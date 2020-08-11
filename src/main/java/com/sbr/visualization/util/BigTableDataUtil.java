package com.sbr.visualization.util;

import com.sbr.springboot.context.SpringContextUtils;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigAttributeData;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.dao.DataModelAttributeDAO;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName BigTableDataUtil
 * @Description TODO 表格处理工具类
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
public class BigTableDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigTableDataUtil.class);

    private static DataModelAttributeDAO dataModelAttributeDAO = SpringContextUtils.getBean(DataModelAttributeDAO.class);

    private static DataModelDAO dataModelDAO = SpringContextUtils.getBean(DataModelDAO.class);


    public static InfoJson buidTableDataValue(BigScreenData bigScreenData) throws IOException {
        InfoJson infoJson = new InfoJson();
        //获取数据模型
        DataModel modelDAOOne = dataModelDAO.findOne(bigScreenData.getDataModelId());
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, modelDAOOne)) {
            return infoJson;
        }
        //普通表格
        if ("normal".equals(bigScreenData.getTableType())) {
            infoJson = normalTable(bigScreenData, infoJson, modelDAOOne);
        } else if ("crossPivot".equals(bigScreenData.getTableType())) {//交叉表格
            infoJson = crossPivotTable(bigScreenData, infoJson, modelDAOOne);
        }
        return infoJson;
    }

    /**
     * @param bigScreenData 大屏数据对象
     * @param infoJson      返回数据、状态
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 交叉表格
     * @Date 10:16 2020/7/22
     **/
    private static InfoJson crossPivotTable(BigScreenData bigScreenData, InfoJson infoJson, DataModel modelDAOOne) throws IOException {
        //列、行数据模型结果
        List<DataModelAttribute> x2AdnyAll = new ArrayList<>();
        //排序条件集合
        List<BigAttributeData> sortListAll = new ArrayList<>();

        //表格列
        List<BigAttributeData> x2 = bigScreenData.getX2();
        List<DataModelAttribute> x2DatamodelAttribute = null;
        if (x2 != null && x2.size() > 0) {
            x2DatamodelAttribute = DataBaseUtil.findBigAttributeDataByListId(x2);
            x2AdnyAll.addAll(x2DatamodelAttribute);
            sortListAll.addAll(x2);
        }

        //表格行
        List<BigAttributeData> y = bigScreenData.getY();
        List<DataModelAttribute> yDatamodelAttribute = null;
        if (y != null && y.size() > 0) {
            yDatamodelAttribute = DataBaseUtil.findBigAttributeDataByListId(y);
            x2AdnyAll.addAll(yDatamodelAttribute);
            sortListAll.addAll(y);
        }

        //表格指标，值
        List<BigAttributeData> value = bigScreenData.getValue();
        List<DataModelAttribute> valueDatamodelAttribute = null;
        if (value != null && value.size() > 0) {
            valueDatamodelAttribute = DataBaseUtil.findBigAttributeDataByListId(value);
            sortListAll.addAll(value);

        }

        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            //度量，聚合SQL
            StringBuffer measureSQL = DataBaseUtil.buildMeasureSQL(value);
            //维度SQL
            StringBuffer veidooSQL = DataBaseUtil.buildVeidooSQL(x2AdnyAll);
            //分组SQL
            StringBuffer groupBySQL = DataBaseUtil.buildGroupBy(x2AdnyAll);
            //条件参数集合
            param = new ArrayList<>();
            //获取结果SQL
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, measureSQL, veidooSQL,
                    groupBySQL, DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), DataBaseUtil.buildSortSQL(sortListAll), param);
        }

        List<Map<String, String>> datas = null;
        try {
            switch (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL:
                    datas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                    break;
                case CommonConstant.ES:
                    datas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, value, x2AdnyAll, modelDAOOne.getDatasourceManage(), valueDatamodelAttribute);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("BigTableDataUtil交叉表格处理数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行错误：" + e.getMessage());
            return infoJson;
        }
        //data
        Map<String, Object> resultMap = new HashMap<>();
        //columns
        List<Map<String, Object>> columnsList = new ArrayList<>();
        //rows
        List<Map<String, Object>> rowsList = new ArrayList<>();

        //设置行
        Set<String> valueSet = new HashSet<>();
        Map<String, Object> rows = new HashMap<>();
        int i = 0;
        for (DataModelAttribute dataModelAttribute : yDatamodelAttribute) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dataModelAttribute.getFieldsAlias());
            map.put("name", dataModelAttribute.getFieldsAlias());
            columnsList.add(map);

            for (Map<String, String> data : datas) {
                i++;
                for (DataModelAttribute valuedataModelAttribute : valueDatamodelAttribute) {
                    //设置列
                    for (DataModelAttribute dataModelAttribute1 : x2DatamodelAttribute) {
                        Map<String, Object> rowsMap = new HashMap<>();

                        Map<String, Object> map1 = new HashMap<>();
                        //获取列名称，去重
                        String x2Value = data.get(dataModelAttribute1.getRandomAlias());
                        if (valueSet.contains(x2Value)) {
                            continue;
                        }
                        //设置columns参数值
                        map1.put("id", valuedataModelAttribute.getFieldsAlias() + "@" + i + "");
                        map1.put("name", x2Value);
                        valueSet.add(x2Value);
                        columnsList.add(map1);

                        //设置rows值，判断去重，如果存在获取Map设置对应的指标值，
                        if (valueSet.contains(data.get(dataModelAttribute.getRandomAlias()))) {
                            Map<String, Object> map2 = (Map<String, Object>) rows.get(data.get(dataModelAttribute.getRandomAlias()));
                            String dataValue = data.get(valuedataModelAttribute.getRandomAlias());
                            if (Util.isNumeric(dataValue)) {
                                map2.put(valuedataModelAttribute.getFieldsAlias() + "@" + i + "", Double.valueOf(dataValue));
                            } else {
                                map2.put(valuedataModelAttribute.getFieldsAlias() + "@" + i + "", dataValue);
                            }
                        } else {
                            rowsMap.put(dataModelAttribute.getFieldsAlias(), data.get(dataModelAttribute.getRandomAlias()));
                            String dataValue = data.get(valuedataModelAttribute.getRandomAlias());
                            if (Util.isNumeric(dataValue)) {
                                rowsMap.put(valuedataModelAttribute.getFieldsAlias() + "@" + i + "", Double.valueOf(dataValue));
                            } else {
                                rowsMap.put(valuedataModelAttribute.getFieldsAlias() + "@" + i + "", dataValue);
                            }
                            valueSet.add(data.get(dataModelAttribute.getRandomAlias()));
                            rows.put(data.get(dataModelAttribute.getRandomAlias()), rowsMap);
                        }
                    }
                }
            }
        }

        //遍历获取好的数据，添加结果rows集合
        for (Map.Entry<String, Object> stringObjectEntry : rows.entrySet()) {
            Map<String, Object> value1 = (Map<String, Object>) stringObjectEntry.getValue();
            rowsList.add(value1);
        }

        resultMap.put("fixRow", yDatamodelAttribute.size());//设置行数
        resultMap.put("columns", columnsList);
        resultMap.put("rows", rowsList);
        infoJson.setData(resultMap);
        return infoJson;
    }


    /**
     * @param bigScreenData
     * @param infoJson
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 普通表格
     * @Date 10:15 2020/7/22
     **/
    public static InfoJson normalTable(BigScreenData bigScreenData, InfoJson infoJson, DataModel modelDAOOne) throws IOException {
        //获取表格列
        List<BigAttributeData> x = bigScreenData.getX();

        //根据大屏数据模型，查询数据模型集合
        List<DataModelAttribute> dataModelAttributes = DataBaseUtil.findBigAttributeDataByListId(x);
        List<DataModelAttribute> dimensionsBigAttributeDataByList = null;
        StringBuffer measureSQL = new StringBuffer("");
        StringBuffer guoupBySql = new StringBuffer("");

        //度量大屏属性
        List<BigAttributeData> m = null;
        //度量数据模型属性
        List<DataModelAttribute> measureDataList = null;
        //如果为true，展示所有数据不分组，不聚合
        if (bigScreenData.getTableNotAggregate()) {
            dimensionsBigAttributeDataByList = new ArrayList<>();
            for (BigAttributeData bigAttributeData : x) {
                DataModelAttribute dataModelAttributes1 = dataModelAttributeDAO.findOne(bigAttributeData.getId());
                dimensionsBigAttributeDataByList.add(dataModelAttributes1);
            }
        } else {
            //获取查询数据集合，维度
            if (x != null && x.size() > 0) {
                //根据维度集合，获取维度数据模型
                dimensionsBigAttributeDataByList = DataBaseUtil.findBigAttributeDataByListId(x.stream()
                        .filter(bigAttributeData -> (bigAttributeData.getType().equals("d")))
                        .collect(Collectors.toList()));
                //分組SQL
                guoupBySql = DataBaseUtil.buildGroupBy(dimensionsBigAttributeDataByList);
                //根据度量集合，获取度量SQL
                m = x.stream()
                        .filter(bigAttributeData -> (bigAttributeData.getType().equals("m")))
                        .collect(Collectors.toList());
                //度量集合
                measureDataList = DataBaseUtil.findBigAttributeDataByListId(m);
                measureSQL = DataBaseUtil.buildMeasureSQL(m);
            }
        }

        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            //条件参数
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, measureSQL, DataBaseUtil.buildVeidooSQL(dimensionsBigAttributeDataByList),
                    guoupBySql, DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), DataBaseUtil.buildSortSQL(x), param);
        }

        List<Map<String, String>> chartDatas = null;
        try {
            switch (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL:
                    //获取数据，传递数据源和SQL，建立连接执行sql返回结果
                    chartDatas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                    break;
                case CommonConstant.ES:
                    chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, m, dimensionsBigAttributeDataByList, modelDAOOne.getDatasourceManage(), measureDataList);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("BigTableDataUtil普通表格处理数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行错误：" + e.getMessage());
            return infoJson;
        }

        Map<String, Object> resultMap = new HashMap<>();
        //创建columns
        List<Map<String, Object>> columns = new ArrayList<>();
        Boolean tableNum = bigScreenData.getTableNum();
        for (int i = 0; i < dataModelAttributes.size(); i++) {
            if (tableNum) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", "__sugar_showx_table_number");
                map.put("name", "序号");
                map.put("textAlign", "center");
                columns.add(map);
                tableNum = false;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("id", dataModelAttributes.get(i).getFieldsAlias());
            map.put("name", dataModelAttributes.get(i).getFieldsAlias());
            columns.add(map);
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        Boolean tableNum1 = bigScreenData.getTableNum();
        int j = 0;
        for (int i = 0; i < chartDatas.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            j++;
            if (tableNum1) {
                map.put("__sugar_showx_table_number", j);
            }
            for (DataModelAttribute dataModelAttribute : dataModelAttributes) {
                String value = chartDatas.get(i).get(dataModelAttribute.getRandomAlias());
                if (Util.isNumeric(value)) {
                    map.put(dataModelAttribute.getFieldsAlias(), Double.valueOf(value));
                } else {
                    map.put(dataModelAttribute.getFieldsAlias(), value);
                }
            }
            rows.add(map);
        }
        resultMap.put("columns", columns);
        resultMap.put("rows", rows);
        infoJson.setData(resultMap);
        return infoJson;
    }
}
