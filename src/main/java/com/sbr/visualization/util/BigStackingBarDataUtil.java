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

/**
 * @ClassName BigStackingBarDataUtil
 * @Description TODO    堆叠图
 * @Author zxx
 * @Version 1.0
 */
public class BigStackingBarDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigStackingBarDataUtil.class);

    private static DataModelAttributeDAO dataModelAttributeDAO = SpringContextUtils.getBean(DataModelAttributeDAO.class);

    private static DataModelDAO dataModelDAO = SpringContextUtils.getBean(DataModelDAO.class);


    public static InfoJson buidStackingBarChartData(BigScreenData bigScreenData) throws IOException {
        InfoJson infoJson = new InfoJson();

        //获取数据模型
        DataModel modelDAOOne = dataModelDAO.findOne(bigScreenData.getDataModelId());
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, modelDAOOne)) {
            return infoJson;
        }
        //排序集合
        List<BigAttributeData> sortListAll = new ArrayList<>();
        //維度集合
        List<DataModelAttribute> dimensionListAll = new ArrayList<>();

        List<BigAttributeData> x = bigScreenData.getX();
        List<BigAttributeData> y = bigScreenData.getY();
        List<BigAttributeData> color = bigScreenData.getColor();

        //维度
        List<DataModelAttribute> xValueList = null;
        if (x != null && x.size() > 0) {
            xValueList = new ArrayList<>();
            for (BigAttributeData bigAttributeData : x) {
                xValueList.add(dataModelAttributeDAO.findOne(bigAttributeData.getId()));
            }
            sortListAll.addAll(x);
            dimensionListAll.addAll(xValueList);
        }

        //度量
        List<DataModelAttribute> yValueList = null;
        if (y != null && y.size() > 0) {
            yValueList = new ArrayList<>();
            for (BigAttributeData bigAttributeData : y) {
                yValueList.add(dataModelAttributeDAO.findOne(bigAttributeData.getId()));
            }
            sortListAll.addAll(y);
        }

        //图例
        List<DataModelAttribute> colorValueList = null;
        if (color != null && color.size() > 0) {
            colorValueList = new ArrayList<>();
            for (BigAttributeData bigAttributeData : color) {
                colorValueList.add(dataModelAttributeDAO.findOne(bigAttributeData.getId()));
            }
            sortListAll.addAll(color);
            dimensionListAll.addAll(colorValueList);
        }

        //Mysql
        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, DataBaseUtil.buildMeasureSQL(y), DataBaseUtil.buildVeidooSQL(dimensionListAll),
                    DataBaseUtil.buildGroupBy(dimensionListAll), DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), DataBaseUtil.buildSortSQL(sortListAll), param);
        }
        List<Map<String, String>> chartDatas = null;
        try {
            switch (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL:
                    chartDatas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                    break;
                case CommonConstant.ES:
                    chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, y, dimensionListAll, modelDAOOne.getDatasourceManage(), yValueList);
            }
            //获取数据，传递数据源和SQL，建立连接执行sql返回结果

        } catch (Exception e) {
            LOGGER.error("BigStackingBarDataUtil大屏堆叠图数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行错误：" + e.getMessage());
            return infoJson;
        }

        //处理返回数据
        switch (bigScreenData.getChartType()) {
            case CommonConstant.STACKING_BAR://堆叠柱圖
                infoJson = getResultStackingBarData(chartDatas, xValueList, colorValueList, yValueList, infoJson);
                break;
        }
        return infoJson;
    }

    /**
     * @param chartDatas     结果数据
     * @param xValueList     X轴维度模型属性
     * @param colorValueList 图例维度模型属性
     * @param yValueList     Y轴度量模型属性
     * @param infoJson       返回信息
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 处理堆叠柱图数据
     * @Date 14:22 2020/8/10
     * @Param
     **/
    private static InfoJson getResultStackingBarData(List<Map<String, String>> chartDatas, List<DataModelAttribute> xValueList, List<DataModelAttribute> colorValueList, List<DataModelAttribute> yValueList, InfoJson infoJson) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> categoriesList = new ArrayList<>();
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<Map<String, Object>> seriesList = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (Map<String, String> chartData : chartDatas) {
            Map<String, Object> map = new HashMap<>();
            String xName = "";
            for (DataModelAttribute dataModelAttribute : xValueList) {
                xName += chartData.get(dataModelAttribute.getRandomAlias()) + "-";
                map.put(dataModelAttribute.getRandomAlias(), chartData.get(dataModelAttribute.getRandomAlias()));
            }
            //截取-
            String subStrValue = xName.substring(0, xName.length() - 1);
            if (!set.contains(subStrValue)) {
                categoriesList.add(subStrValue);
                mapList.add(map);
                set.add(subStrValue);
            }
        }
        //存在图列
        if (colorValueList != null && colorValueList.size() > 0) {
            for (Map<String, String> chartData : chartDatas) {
                String colorName = "";
                //图例
                for (DataModelAttribute dataModelAttribute : colorValueList) {
                    colorName += chartData.get(dataModelAttribute.getRandomAlias()) + "-";
                }
                //Y轴
                for (DataModelAttribute dataModelAttribute : yValueList) {
                    List<Double> doubleList = new ArrayList<>(mapList.size());
                    Map<String, Object> map1 = new HashMap<>();

                    boolean dataFlag = true;
                    int i = 0;
                    //维度Map，遍历判断是否有对应的数据值
                    for (Map<String, Object> map : mapList) {
                        i++;
                        Double dataValue = null;
                        boolean flag = true;
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            String entryValue = chartData.get(entry.getKey());
                            if (entryValue == null || !entryValue.equals(entry.getValue())) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            dataValue = Double.valueOf(chartData.get(dataModelAttribute.getRandomAlias()));
                        }
                        //图例名称
                        String colorNameValue = colorName + dataModelAttribute.getFieldsName();
                        if (seriesList != null && seriesList.size() > 0) {
                            for (Map<String, Object> objectMap : seriesList) {
                                String name = (String) objectMap.get("name");
                                if (dataValue == null) {
                                    break;
                                } else if (name.equals(colorNameValue)) {
                                    // 如果当前名称存在，取出集合将值进行替换
                                    List<Double> data = (List<Double>) objectMap.get("data");
                                    //完成替换
                                    data.set(i - 1, dataValue);
                                    dataFlag = false;
                                }
                            }
                        }
                        doubleList.add(dataValue);
                        map1.put("name", colorNameValue);
                        map1.put("data", doubleList);
                        map1.put("type", "bar");
                    }
                    if (dataFlag) {
                        seriesList.add(map1);
                    }
                }
            }
        } else {
            //不存在图例
            for (DataModelAttribute dataModelAttribute : yValueList) {
                Map<String, Object> map = new HashMap<>();
                List<Double> doubleList = new ArrayList<>();
                for (Map<String, String> chartData : chartDatas) {
                    String value = chartData.get(dataModelAttribute.getRandomAlias());
                    doubleList.add(Double.valueOf(value));
                }
                map.put("name", dataModelAttribute.getFieldsName());
                map.put("data", doubleList);
                map.put("type", "bar");
                seriesList.add(map);
            }
        }

        resultMap.put("categories", categoriesList);
        resultMap.put("series", seriesList);
        infoJson.setData(resultMap);
        return infoJson;
    }

}
