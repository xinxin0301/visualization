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
 * @ClassName BigRandarDataUtil
 * @Description TODO 雷达图和没有Y轴的图数据处理
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
public class BigRandarDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigRandarDataUtil.class);

    private static DataModelAttributeDAO dataModelAttributeDAO = SpringContextUtils.getBean(DataModelAttributeDAO.class);

    private static DataModelDAO dataModelDAO = SpringContextUtils.getBean(DataModelDAO.class);


    /**
     * @param bigScreenData 大屏数据对象
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 没有Y轴的数据处理
     * @Date 13:18 2020/7/16
     * @Param
     **/
    public static InfoJson buidChartDataValue(BigScreenData bigScreenData) throws IOException {
        InfoJson infoJson = new InfoJson();
        DataModel daoOne = dataModelDAO.findOne(bigScreenData.getDataModelId());
        //判断数据模型
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, daoOne)) {
            return infoJson;
        }

        //排序集合
        List<BigAttributeData> sortListAll = new ArrayList<>();
        //维度集合
        List<DataModelAttribute> dimensionalityDataAll = new ArrayList<>();

        //普通，不是y轴的度量参数
        List<DataModelAttribute> measurementData = null;
        List<BigAttributeData> value = bigScreenData.getValue();
        if (value != null && value.size() > 0) {
            //获取度量ID集合
            measurementData = DataBaseUtil.findBigAttributeDataByListId(value);
            sortListAll.addAll(value);
        }
        //取出维度参数
        List<BigAttributeData> x = bigScreenData.getX();
        List<DataModelAttribute> dimensionalityData = null;
        if (x != null && x.size() > 0) {
            dimensionalityData = DataBaseUtil.findBigAttributeDataByListId(x);
            sortListAll.addAll(x);
            dimensionalityDataAll.addAll(dimensionalityData);
        }

        //颜色图例
        List<BigAttributeData> color = bigScreenData.getColor();
        List<DataModelAttribute> colorAttributes = null;
        if (color != null && color.size() > 0) {
            colorAttributes = DataBaseUtil.findBigAttributeDataByListId(color);
            sortListAll.addAll(color);
            dimensionalityDataAll.addAll(colorAttributes);
        }


        //TODO 大屏对象、当前数据模型、度量条件、维度条件、分组条件、WHERE条件、排序条件
        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (daoOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, daoOne, DataBaseUtil.buildMeasureSQL(value), DataBaseUtil.buildVeidooSQL(dimensionalityDataAll),
                    DataBaseUtil.buildGroupBy(dimensionalityDataAll), DataBaseUtil.buildWhereSQL(bigScreenData, daoOne, param), DataBaseUtil.buildSortSQL(sortListAll), param);
        }

        List<Map<String, String>> chartDatas = null;
        try {
            switch (daoOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
                //获取数据，传递数据源和SQL，建立连接执行sql返回结果
                case CommonConstant.MYSQL:
                    chartDatas = DataBaseUtil.getDatas(daoOne.getDatasourceManage(), sqlBuffer.toString(), param);
                    break;
                case CommonConstant.ES:
                    chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, daoOne, value, dimensionalityDataAll, daoOne.getDatasourceManage(), measurementData);
            }

        } catch (Exception e) {
            LOGGER.error("BigRandarDataUtil大屏雷达图数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行SQL错误：" + e.getMessage());
            return infoJson;
        }
        //处理返回数据
        switch (bigScreenData.getChartType()) {
            case CommonConstant.TEXTBOX://文字
                infoJson = getResultTextBooxData(chartDatas);
                break;
            case CommonConstant.RADAR://雷达图
                infoJson = getResultRadarData(chartDatas, dimensionalityData, measurementData, colorAttributes);
        }
        return infoJson;
    }


    /**
     * @Author zxx
     * @Description //TODO 查询ES
     * @Date 16:23 2020/8/6
     * @Param
     * @param bigScreenData
     * @param modelDAOOne
     * @param yAll
     * @param allDataModelAttribute
     * @param datasourceManage
     * @param measureDataList
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
     **/


    /**
     * @param chartDatas 结果
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 文字结果
     * @Date 16:11 2020/7/14
     **/
    private static InfoJson getResultTextBooxData(List<Map<String, String>> chartDatas) {
        InfoJson infoJson = new InfoJson();
        if (chartDatas != null && chartDatas.size() > 0) {
            for (Map<String, String> chartData : chartDatas) {
                chartData.forEach((k, v) -> {
                    infoJson.setData(v);
                });
                break;
            }
        } else {
            infoJson.setData("");
        }
        return infoJson;
    }


    /**
     * @param chartDatas         结果数据
     * @param dimensionsDataList 维度数据模型
     * @param measureDataList    度量数据模型
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 处理雷达图数据
     * @Date 13:38 2020/7/16
     **/
    private static InfoJson getResultRadarData(List<Map<String, String>> chartDatas, List<DataModelAttribute> dimensionsDataList, List<DataModelAttribute> measureDataList, List<DataModelAttribute> colorAttributes) {
        InfoJson infoJson = new InfoJson();
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> indicators = new ArrayList<>();
        List<Map<String, Object>> series = new ArrayList<>();
        //存在图例
        if (colorAttributes != null && colorAttributes.size() > 0) {
            //获取所有外圈X轴数据，去重
            Set<String> textSet = new HashSet<>();
            for (Map<String, String> chartData : chartDatas) {
                for (DataModelAttribute dataModelAttribute : dimensionsDataList) {
                    String name = chartData.get(dataModelAttribute.getRandomAlias());
                    textSet.add(name);
                }
            }

            //最大值
            Double valueIndex = 0.00;
            Map<String, Object> map = new HashMap<>();
            for (DataModelAttribute colorAttribute : colorAttributes) {
                List<String> list = new ArrayList<>(textSet);
                for (int i = 0; i < list.size(); i++) {
                    for (Map<String, String> chartData : chartDatas) {
                        //维度值
                        String dimensionsValue = chartData.get(dimensionsDataList.get(0).getRandomAlias());
                        if (list.get(i).equals(dimensionsValue)) {
                            //度量
                            Double mesureValue = Double.valueOf(chartData.get(measureDataList.get(0).getRandomAlias()));
                            if (mesureValue > valueIndex) {
                                valueIndex = mesureValue;
                            }
                            //颜色
                            Double[] colorDouble = (Double[]) map.get(chartData.get(colorAttribute.getRandomAlias()));
                            if (colorDouble != null) {
                                colorDouble[i] = mesureValue;
                            } else {
                                Double[] colorDouble1 = new Double[textSet.size()];
                                colorDouble1[i] = mesureValue;
                                map.put(chartData.get(colorAttribute.getRandomAlias()), colorDouble1);
                            }
                        }
                    }
                }
                //填空，没值的数据赋0
                for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                    Map<String, Object> seriesMap = new HashMap<>();
                    Double[] value = (Double[]) stringObjectEntry.getValue();
                    for (int i = 0; i < value.length; i++) {
                        if (value[i] == null) {
                            value[i] = 0.00;
                        }
                    }
                    //拼装series结果数据
                    seriesMap.put("name", stringObjectEntry.getKey());
                    seriesMap.put("value", value);
                    series.add(seriesMap);
                }
            }
            //处理indicators结果数据
            for (String xvalue : textSet) {
                Map<String, Object> indicatorsMap = new HashMap<>();
                indicatorsMap.put("text", xvalue);
                indicatorsMap.put("max", valueIndex);
                indicatorsMap.put("min", 0);
                indicators.add(indicatorsMap);
            }
            resultMap.put("indicators", indicators);
            resultMap.put("series", series);
            infoJson.setData(resultMap);

        } else {
            //构建度量series
            Double flag = 0.0;
            for (DataModelAttribute dataModelAttribute : measureDataList) {
                Map<String, Object> measureMap = new HashMap<>();
                List<Double> doubleList = new ArrayList<>();
                for (Map<String, String> chartData : chartDatas) {
                    Double value = 0.0;
                    String dataValue = chartData.get(dataModelAttribute.getRandomAlias());
                    if (dataValue != null) {
                        //替换标识最大值
                        value = Double.valueOf(dataValue);
                    }
                    if (value > flag) {
                        flag = value;
                    }
                    doubleList.add(value);
                }
                measureMap.put("name", dataModelAttribute.getFieldsAlias());
                measureMap.put("type", "radar");
                measureMap.put("value", doubleList);
                series.add(measureMap);
            }
            //构建维度indicators
            for (Map<String, String> chartData : chartDatas) {
                for (DataModelAttribute dataModelAttribute : dimensionsDataList) {
                    Map<String, Object> dimensionsMap = new HashMap<>();
                    String name = chartData.get(dataModelAttribute.getRandomAlias());
                    dimensionsMap.put("text", name);
                    dimensionsMap.put("max", flag);
                    dimensionsMap.put("min", 0);
                    indicators.add(dimensionsMap);
                }
            }
        }
        resultMap.put("indicators", indicators);
        resultMap.put("series", series);
        infoJson.setData(resultMap);
        return infoJson;
    }
}
