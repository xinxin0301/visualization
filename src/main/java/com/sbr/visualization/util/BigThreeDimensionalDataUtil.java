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
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BigThreeDimensionalDataUtil
 * @Description TODO 3D柱图数据处理
 * @Author zxx
 * @Version 1.0
 */
public class BigThreeDimensionalDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigThreeDimensionalDataUtil.class);

    private static DataModelAttributeDAO dataModelAttributeDAO = SpringContextUtils.getBean(DataModelAttributeDAO.class);

    private static DataModelDAO dataModelDAO = SpringContextUtils.getBean(DataModelDAO.class);


    /**
     * @param bigScreenData 大屏实体对象
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 3D柱图数据处理
     * @Date 10:17 2020/7/27
     **/
    public static InfoJson buidChartDataValue(BigScreenData bigScreenData) throws IOException {
        InfoJson infoJson = new InfoJson();
        //获取数据模型
        DataModel modelDAOOne = dataModelDAO.findOne(bigScreenData.getDataModelId());
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, modelDAOOne)) {
            return infoJson;
        }

        //维度集合
        List<DataModelAttribute> dimensionsDataAll = new ArrayList<>();
        //排序集合
        List<BigAttributeData> sortList = new ArrayList<>();

        List<BigAttributeData> x = bigScreenData.getX();
        List<BigAttributeData> y = bigScreenData.getY();
        List<BigAttributeData> value = bigScreenData.getValue();
        List<BigAttributeData> color = bigScreenData.getColor();

        //获取x数据模型
        List<DataModelAttribute> xDataModelAttributes = null;
        if (x != null && x.size() > 0) {
            xDataModelAttributes = DataBaseUtil.findBigAttributeDataByListId(x);
            dimensionsDataAll.addAll(xDataModelAttributes);
            sortList.addAll(x);
        }
        //获取y数据模型
        List<DataModelAttribute> yDataModelAttributes = null;
        if (y != null && y.size() > 0) {
            yDataModelAttributes = DataBaseUtil.findBigAttributeDataByListId(y);
            dimensionsDataAll.addAll(yDataModelAttributes);
            sortList.addAll(y);
        }
        //颜色图列例
        List<DataModelAttribute> colorDataModelAttributes = null;
        if (color != null && color.size() > 0) {
            colorDataModelAttributes = DataBaseUtil.findBigAttributeDataByListId(color);
            dimensionsDataAll.addAll(colorDataModelAttributes);
            sortList.addAll(color);
        }

        //获取z轴数据模型
        List<DataModelAttribute> valueDataModelAttributes = null;
        if (value != null && value.size() > 0) {
            valueDataModelAttributes = DataBaseUtil.findBigAttributeDataByListId(value);
            sortList.addAll(value);
        }


        List<Map<String, String>> datas = null;
        try {
            switch (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL:
                    datas = buildMySql(bigScreenData, modelDAOOne, value, dimensionsDataAll, modelDAOOne.getDatasourceManage(), sortList);
                    break;
                case CommonConstant.ES:
                    datas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, value, dimensionsDataAll, modelDAOOne.getDatasourceManage(), valueDataModelAttributes);
            }

        } catch (Exception e) {
            LOGGER.error("BigThreeDimensionalDataUtil3D柱图数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行SQL错误：" + e.getMessage());
            return infoJson;
        }

        Map<String, Object> resultMap = new HashMap<>();
        //X轴结果
        List<String> xCategories = new ArrayList<>();
        //Y轴结果
        List<String> yCategories = new ArrayList<>();
        //Z轴结果
        List<Map<String, Object>> series = new ArrayList<>();

        //包含图例
        if (colorDataModelAttributes != null && colorDataModelAttributes.size() > 0) {
            for (Map<String, String> data : datas) {
                Map<String, Object> map = new HashMap<>();
                List<List<String>> valueList = new ArrayList<>();
                List<String> ListString = new ArrayList<>();
                //X轴数据
                String xValue = data.get(xDataModelAttributes.get(0).getRandomAlias());
                if (!xCategories.contains(xValue)) {
                    xCategories.add(xValue);
                }
                ListString.add(xValue);
                //Y轴数据
                String yValue = data.get(yDataModelAttributes.get(0).getRandomAlias());
                if (!yCategories.contains(yValue)) {
                    yCategories.add(yValue);
                }
                ListString.add(yValue);
                //图例数据
                String colorValue = data.get(colorDataModelAttributes.get(0).getRandomAlias());
                ListString.add(data.get(valueDataModelAttributes.get(0).getRandomAlias()));
                valueList.add(ListString);
                map.put("name", colorValue);
                map.put("type", "bar3D");
                map.put("data", valueList);
                series.add(map);
            }
        } else {
            //没有图例
            for (DataModelAttribute valueDataModelAttribute : valueDataModelAttributes) {
                Map<String, Object> map1 = new HashMap<>();
                List<List<String>> dataList = new ArrayList<>();
                map1.put("name", valueDataModelAttribute.getFieldsAlias());
                map1.put("type", "bar3D");
                map1.put("data", dataList);
                for (Map<String, String> data : datas) {
                    //Z轴数据
                    List<String> valueList = new ArrayList<>();
                    //X轴数据
                    for (DataModelAttribute xDataModelAttribute : xDataModelAttributes) {
                        String xvalue = data.get(xDataModelAttribute.getRandomAlias());
                        if (!xCategories.contains(xvalue)) {
                            xCategories.add(xvalue);
                        }
                        valueList.add(xvalue);
                    }
                    //Y轴数据
                    for (DataModelAttribute yDataModelAttribute : yDataModelAttributes) {
                        String yvalue = data.get(yDataModelAttribute.getRandomAlias());
                        if (!yCategories.contains(yvalue)) {
                            yCategories.add(yvalue);
                        }
                        valueList.add(yvalue);
                    }
                    valueList.add(data.get(valueDataModelAttribute.getRandomAlias()));
                    dataList.add(valueList);
                }
                series.add(map1);
            }
        }
        resultMap.put("xCategories", xCategories);
        resultMap.put("yCategories", yCategories);
        resultMap.put("series", series);
        infoJson.setData(resultMap);
        return infoJson;
    }


    private static List<Map<String, String>> buildMySql(BigScreenData bigScreenData, DataModel modelDAOOne, List<BigAttributeData> value, List<DataModelAttribute> dimensionsDataAll, DatasourceManage datasourceManage, List<BigAttributeData> sortList) throws Exception {
        //维度SQL
        StringBuffer veidooSQL = DataBaseUtil.buildVeidooSQL(dimensionsDataAll);
        //度量SQL
        StringBuffer measureSQL = DataBaseUtil.buildMeasureSQL(value);
        //排序SQL
        StringBuffer sortSQL = DataBaseUtil.buildSortSQL(sortList);
        //分组SQL
        StringBuffer groupBySQL = DataBaseUtil.buildGroupBy(dimensionsDataAll);

        //条件参数集合
        List<String> param = new ArrayList<>();
        //获取结果SQL
        StringBuffer sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, measureSQL, veidooSQL,
                groupBySQL, DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), sortSQL, param);
        List<Map<String, String>> chartDatas = DataBaseUtil.getDatas(datasourceManage, sqlBuffer.toString(), param);
        return chartDatas;
    }


}
