package com.sbr.visualization.util;

import com.sbr.common.finder.Filter;
import com.sbr.common.finder.Finder;
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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName BigScreenPieDataUtil
 * @Description TODO 大屏普通图数据处理，包含Y轴
 * @Author zxx
 * @Version 1.0
 */
public class BigScreenPieDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigScreenPieDataUtil.class);

    private static DataModelAttributeDAO dataModelAttributeDAO = SpringContextUtils.getBean(DataModelAttributeDAO.class);

    private static DataModelDAO dataModelDAO = SpringContextUtils.getBean(DataModelDAO.class);

    /**
     * @param bigScreenData 大屏实体对象
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 图标公共方法，包含Y轴数据处理
     * @Date 15:18 2020/6/18
     **/
    public static InfoJson buidChartData(BigScreenData bigScreenData) throws IOException {
        InfoJson infoJson = new InfoJson();

        //获取数据模型
        DataModel modelDAOOne = dataModelDAO.findOne(bigScreenData.getDataModelId());
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, modelDAOOne)) {
            return infoJson;
        }

        //TODO 拼装所有度量条件集合
        //获取度量参数
        List<BigAttributeData> yAll = new ArrayList<>();
        List<BigAttributeData> y = bigScreenData.getY();
        List<BigAttributeData> y2 = bigScreenData.getY2();

        //两个度量放入单个集合
        if (y != null && y2 != null) {
            yAll.addAll(y);
            yAll.addAll(y2);
        } else {
            if (y2 != null) {
                yAll.addAll(y2);
            }
            if (y != null) {
                yAll.addAll(y);
            }
        }

        //判断参数是否合法，并写入度量ID，为方便查询度量使用
        List<String> yIdsList = null;
        if (yAll != null) {
            yIdsList = new ArrayList<>();
            for (BigAttributeData bigAttributeData : yAll) {
                //写入度量ID
                yIdsList.add(bigAttributeData.getId());
            }
        }

        //TODO 获取度量条件SQL，并且获取所有度量数据
        //获取当前所有度量
        List<DataModelAttribute> measureDataList = null;
        if (yIdsList != null && yIdsList.size() > 0) {
            Finder measureFinder = new Finder();
            measureFinder.appendFilter("id", yIdsList, Filter.OperateType.OPERATE_IN);
            measureDataList = dataModelAttributeDAO.findByFinder(measureFinder);
            if (measureDataList == null) {
                infoJson.setSuccess(false);
                infoJson.setDescription("当前度量不存在！");
                return infoJson;
            }
        }


        //TODO 取出颜色
        List<BigAttributeData> colorList = bigScreenData.getColor();
        List<DataModelAttribute> colorDimensionsDataList = null;
        //StringBuffer groupBy = null;
        if (colorList != null && colorList.size() > 0) {
            //构建Group by 条件,如果colorList不为空的话，只分组colorList参数
            List<String> collect = colorList.stream().map(BigAttributeData::getId).collect(Collectors.toList());
            //dimensionsIds.addAll(collect);//颜色也属于维度字段拼接查询条件
            Finder finder = new Finder();
            finder.appendFilter("id", collect, Filter.OperateType.OPERATE_IN);
            //获取Color当前所有维度
            colorDimensionsDataList = dataModelAttributeDAO.findByFinder(finder);
            //groupBy = DataBaseUtil.buildGroupBy(colorDimensionsDataList);
        }


        //TODO 查询当前维度
        //维度参数
        List<BigAttributeData> x = bigScreenData.getX();
        List<DataModelAttribute> dimensionsDataList = new ArrayList<>();
        if (x != null && x.size() > 0) {
            for (BigAttributeData bigAttributeData : x) {
                dimensionsDataList.add(dataModelAttributeDAO.findOne(bigAttributeData.getId()));
            }
        }

        //如果有color放入全部维度结合，拼装SQL
        List<DataModelAttribute> allDataModelAttribute = new ArrayList<>();
        allDataModelAttribute.addAll(dimensionsDataList);
        if (colorDimensionsDataList != null) {
            allDataModelAttribute.addAll(colorDimensionsDataList);
        }
        if (dimensionsDataList == null) {
            infoJson.setSuccess(false);
            infoJson.setDescription("当前维度不存在！");
            return infoJson;
        }
        //TODO 根据数据模型ID，获取数据模型属性，只为获取数据库表名,拼接SQL，查询SQL获取结果
        List<DataModelAttribute> modelAttributes = dataModelAttributeDAO.findByDataModelId(modelDAOOne.getId());
        //获取数据源
        DatasourceManage datasourceManage = modelDAOOne.getDatasourceManage();

        List<Map<String, String>> chartDatas = null;


        try {
            switch (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL:
                    chartDatas = buildMySql(bigScreenData, modelDAOOne, yAll, allDataModelAttribute, datasourceManage);
                    break;
                case CommonConstant.ES:
                    chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, yAll, allDataModelAttribute, datasourceManage, measureDataList);
            }
        } catch (Exception e) {
            LOGGER.error("BigScreenPieDataUtil大屏数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行错误：" + e.getMessage());
            return infoJson;
        }

        switch (bigScreenData.getChartType()) {
            case CommonConstant.BAR://柱状图
                infoJson = getResultBarAndLineData(chartDatas, dimensionsDataList, measureDataList, colorDimensionsDataList, yAll);
                break;
            case CommonConstant.LINE://折线图
                infoJson = getResultBarAndLineData(chartDatas, dimensionsDataList, measureDataList, colorDimensionsDataList, yAll);
                break;
            case CommonConstant.PIE://饼图
                infoJson = getResultPieData(chartDatas, dimensionsDataList, measureDataList, bigScreenData.getChartType());
                break;
        }
        return infoJson;
    }

    /**
     * @param bigScreenData
     * @param modelDAOOne
     * @param yAll
     * @param allDataModelAttribute
     * @param datasourceManage
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
     * @Author zxx
     * @Description //TODO Mysql
     * @Date 15:16 2020/8/5
     **/
    private static List<Map<String, String>> buildMySql(BigScreenData bigScreenData, DataModel modelDAOOne, List<BigAttributeData> yAll, List<DataModelAttribute> allDataModelAttribute, DatasourceManage datasourceManage) throws Exception {
        List<Map<String, String>> chartDatas;//度量条件
        StringBuffer measureSQL = DataBaseUtil.buildMeasureSQL(yAll);
        //维度条件
        StringBuffer dimensionsSQL = DataBaseUtil.buildVeidooSQL(allDataModelAttribute);
        //如果等于null，说名没有color参数，重新拼接分组参数
        StringBuffer groupBy = DataBaseUtil.buildGroupBy(allDataModelAttribute);
        //排序条件
        StringBuffer sortSQL = DataBaseUtil.buildSortSQL(yAll);
        //WHERE条件
        List<String> param = new ArrayList<>();
        String whereSQL = DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param);
        //TODO 获取维度条件SQL，并获取所有维度数据
        StringBuffer sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, measureSQL, dimensionsSQL, groupBy, whereSQL, sortSQL, param);
        //获取数据，传递数据源和SQL，建立连接执行sql返回结果
        chartDatas = DataBaseUtil.getDatas(datasourceManage, sqlBuffer.toString(), param);
        return chartDatas;
    }


    /**
     * @param chartDatas         结果数据
     * @param dimensionsDataList 维度数据
     * @param measureDataList    度量数据
     * @param chartType          图表类型
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 根据数据结果、度量、维度、图标类型、拼接饼图数据格式
     * @Date 15:16 2020/6/18
     **/
    private static InfoJson getResultPieData(List<Map<String, String>> chartDatas, List<DataModelAttribute> dimensionsDataList, List<DataModelAttribute> measureDataList, String chartType) {
        InfoJson infoJson = new InfoJson();
        List<Map<String, Object>> resultList = new ArrayList<>();
        chartDatas.forEach(map -> {
            Map resultMap = new HashMap();
            String name = "";
            //获取维度值,设置维度值
            for (DataModelAttribute dataModelAttribute : dimensionsDataList) {
                name += "-" + map.get(dataModelAttribute.getRandomAlias());
            }
            //截取开头-
            resultMap.put("name", name.substring(1));

            //如果有别用采用别名取值
            for (DataModelAttribute dataModelAttribute : measureDataList) {
                Double value = 0.0;
                String result = map.get(dataModelAttribute.getRandomAlias());
                if (result != null) {
                    value = Double.valueOf(result);
                }
                resultMap.put("value", value);
            }
            resultList.add(resultMap);
        });
        infoJson.setData(resultList);
        return infoJson;
    }

    /**
     * @param chartDatas              结果数据
     * @param dimensionsDataList      维度数据集合
     * @param measureDataList         度量数据集合
     * @param yAll                    度量集合
     * @param colorDimensionsDataList 颜色数据模型集合
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 处理柱状图、折线图数据格式
     * @Date 15:08 2020/6/18
     **/
    public static InfoJson getResultBarAndLineData(List<Map<String, String>> chartDatas, List<DataModelAttribute> dimensionsDataList, List<DataModelAttribute> measureDataList, List<DataModelAttribute> colorDimensionsDataList, List<BigAttributeData> yAll) {
        InfoJson infoJson = new InfoJson();
        Map<String, Object> resultMap = new HashMap();
        //拼装维度结果数据
        List<String> categoriesResult = BigScreenPieDataUtil.categoriesResult(chartDatas, dimensionsDataList, colorDimensionsDataList);
        //拼装度量结果
        List<Map<String, Object>> mapList = BigScreenPieDataUtil.measureResulr(chartDatas, measureDataList, colorDimensionsDataList, yAll, categoriesResult, dimensionsDataList);
        resultMap.put("categories", categoriesResult);
        resultMap.put("series", mapList);
        infoJson.setData(resultMap);
        return infoJson;
    }


    /**
     * @param chartDatas              结果数据
     * @param modelAttributes1        度量数据
     * @param colorDimensionsDataList 图例参数
     * @param yAll                    大屏度量集合
     * @param categoriesResult        维度结果
     * @param dimensionsDataList      维度参数
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author zxx
     * @Description //TODO 构建度量数据(柱状图、折线图)
     * @Date 10:20 2020/6/18
     **/
    public static List<Map<String, Object>> measureResulr(List<Map<String, String>> chartDatas, List<DataModelAttribute> modelAttributes1, List<DataModelAttribute> colorDimensionsDataList, List<BigAttributeData> yAll, List<String> categoriesResult, List<DataModelAttribute> dimensionsDataList) {
        List<Map<String, Object>> mapList = new ArrayList<>();

        if (colorDimensionsDataList != null && colorDimensionsDataList.size() > 0) {
           //TODO 此图图例没有添加--------------------------------------------------------------------------------------
            Map<String, Set<String>> setMap = new HashMap<>();
            Map<String, List<Map<String, String>>> listMap = new HashMap<>();
            for (DataModelAttribute dataModelAttribute : colorDimensionsDataList) {
                for (Map<String, String> chartData : chartDatas) {
                    String name = chartData.get(dataModelAttribute.getRandomAlias());
                    if (name != null) {
                        Set<String> strings = setMap.get(dataModelAttribute.getRandomAlias());
                        if (strings != null) {
                            strings.add(name);
                        } else {
                            Set<String> set = new HashSet();
                            set.add(name);
                            setMap.put(dataModelAttribute.getRandomAlias(), set);
                        }

                        List<Map<String, String>> mapList1 = listMap.get(name);
                        if (mapList1 != null) {
                            mapList1.add(chartData);
                        } else {
                            List<Map<String, String>> listStr = new ArrayList<>();
                            listStr.add(chartData);
                            listMap.put(name, listStr);
                        }
                    }

                }
            }

            //遍历维度结果
            for (String string : categoriesResult) {
                String[] split = string.split("-");
                //图例参数
                for (DataModelAttribute modelAttribute : colorDimensionsDataList) {
                    List<String> strings = new ArrayList<>(setMap.get(modelAttribute.getRandomAlias()));
                    //图例结果
                    for (String str : strings) {
                        for (Map<String, String> chartData : chartDatas) {
                            //维度参数
                            for (int i = 0; i < dimensionsDataList.size(); i++) {
                                String randomAlias = dimensionsDataList.get(i).getRandomAlias();
                                String value = chartData.get(randomAlias);
                                if (value.equals(split[i])) {
                                    String name = chartData.get(modelAttribute.getRandomAlias());
                                    if (name.equals(str)) {
                                        for (DataModelAttribute dataModelAttribute : modelAttributes1) {
                                            String name1 = name + "-" + dataModelAttribute.getFieldsAlias();
                                            String valueData = chartData.get(dataModelAttribute.getRandomAlias());
                                        }
                                    } else {
                                        for (DataModelAttribute dataModelAttribute : modelAttributes1) {
                                            String name1 = name + "-" + dataModelAttribute.getFieldsAlias();
                                            String valueData = chartData.get(dataModelAttribute.getRandomAlias());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

















            /*//获取所有维度数据Map
            Map<String, List<Map<String, String>>> resultMap = new HashMap<>();
            //遍历维度
            for (DataModelAttribute dataModelAttribute : dimensionsDataList) {
                for (Map<String, String> chartData : chartDatas) {
                    String name = chartData.get(dataModelAttribute.getRandomAlias());
                    List<Map<String, String>> mapList2 = resultMap.get(name);
                    if (mapList2 != null && mapList2.size() > 0) {
                        mapList2.add(chartData);
                    } else {
                        List<Map<String, String>> mapList1 = new ArrayList<>();
                        mapList1.add(chartData);
                        resultMap.put(name, mapList1);
                    }
                }
                break;
            }

            for (String str : categoriesResult) {
                //获取维度数据集合
                List<Map<String, String>> mapList1 = resultMap.get(str);
                for (Map<String, String> map : mapList1) {
                    String name = "";
                    for (DataModelAttribute dataModelAttribute : colorDimensionsDataList) {
                        if (name.equals("")) {
                            name += map.get(dataModelAttribute.getRandomAlias());
                        } else {
                            name += "-" + map.get(dataModelAttribute.getRandomAlias());
                        }
                    }
                    for (DataModelAttribute dataModelAttribute : modelAttributes1) {
                        Map<String, Object> dataMap = new HashMap<>();
                        List<String> lists = new ArrayList();
                        String resultName = name + "-" + dataModelAttribute.getFieldsAlias();
                        for (BigAttributeData bigAttributeData : yAll) {
                            if (dataModelAttribute.getId().equals(bigAttributeData.getId())) {
                                dataMap.put("type", bigAttributeData.getLineChartType());
                                dataMap.put("yAxisIndex", bigAttributeData.getyAxisIndex());
                                break;
                            }
                        }

                        dataMap.put("name", resultName);

                        for (int i = 0; i < categoriesResult.size(); i++) {
                            String value = map.get(dataModelAttribute.getRandomAlias());
                            lists.add(value);
                        }
                        dataMap.put("data", lists);
                        mapList.add(dataMap);
                    }
                }
            }

            System.out.println("222");*/







        /*Map<String, List<Map<String, String>>> map1 = new HashMap<>();
        if (colorDimensionsDataList != null && colorDimensionsDataList.size() > 0) {
            //遍历维度数据
            for (DataModelAttribute dataModelAttribute : dimensionsDataList) {
                //遍历结果数据
                for (Map<String, String> stringMap : chartDatas) {
                    //根据维度别名，取数据值
                    String name = stringMap.get(dataModelAttribute.getRandomAlias());
                    //根据值取Map集合，如果有的话将当前对象放进去，如果没有的话创建新的集合放入数据，并放入Map key为维度名称
                    List<Map<String, String>> mapList1 = map1.get(name);
                    if (mapList1 != null && mapList1.size() > 0) {
                        mapList1.add(stringMap);
                    } else {
                        List<Map<String, String>> mapList2 = new ArrayList<>();
                        mapList2.add(stringMap);
                        map1.put(name, mapList2);
                    }
                }
            }

            for (String str : categoriesResult) {
                //获取维度数据集合
                List<Map<String, String>> mapList1 = map1.get(str);
                for (Map<String, String> map : mapList1) {
                    String name = "";
                    for (DataModelAttribute dataModelAttribute : colorDimensionsDataList) {
                        if (name.equals("")) {
                            name += map.get(dataModelAttribute.getRandomAlias());
                        } else {
                            name += "-" + map.get(dataModelAttribute.getRandomAlias());
                        }
                    }

                    for (DataModelAttribute dataModelAttribute : modelAttributes1) {
                        Map<String, Object> dataMap = new HashMap<>();
                        List<String> lists = new ArrayList();
                        String resultName = name + "-" + dataModelAttribute.getFieldsAlias();
                        for (BigAttributeData bigAttributeData : yAll) {
                            if (dataModelAttribute.getId().equals(bigAttributeData.getId())) {
                                dataMap.put("type", bigAttributeData.getLineChartType());
                                dataMap.put("yAxisIndex", bigAttributeData.getyAxisIndex());
                                break;
                            }
                        }
                        dataMap.put("name", resultName);

                        for (int i = 0; i < categoriesResult.size(); i++) {
                            String value = map.get(dataModelAttribute.getRandomAlias());
                            lists.add(value);
                        }
                        dataMap.put("data", lists);
                        mapList.add(dataMap);
                    }
                }
            }*/


//            for (String str : categoriesResult) {
//                chartDatas.forEach(map -> {
//
//                });
//            }
//
//
//            //拼接色例名称
//            String name = "";
//            for (DataModelAttribute dataModelAttribute : colorDimensionsDataList) {
//                if (name.equals("")) {
//                    name += map.get(dataModelAttribute.getRandomAlias());
//                } else {
//                    name += "-" + map.get(dataModelAttribute.getRandomAlias());
//                }
//            }
//
//
//            for (String str : categoriesResult) {
//                for (DataModelAttribute dataModelAttribute : dimensionsDataList) {
//                    String value = map.get(dataModelAttribute.getRandomAlias());
//                    if (str.equals(value)) {
//
//                    }
//                }
//            }
//
//
//            //拼接度量名称
//            for (DataModelAttribute dataModelAttribute : modelAttributes1) {
//                Map<String, Object> dataMap = new HashMap<>();
//                List<String> lists = new ArrayList();
//                String resultName = name + "-" + dataModelAttribute.getFieldsAlias();
//                for (BigAttributeData bigAttributeData : yAll) {
//                    if (dataModelAttribute.getId().equals(bigAttributeData.getId())) {
//                        dataMap.put("type", bigAttributeData.getLineChartType());
//                        dataMap.put("yAxisIndex", bigAttributeData.getyAxisIndex());
//                        break;
//                    }
//                }
//                dataMap.put("name", resultName);
//                lists.add(map.get(dataModelAttribute.getRandomAlias()));
//                dataMap.put("data", lists);
//                mapList.add(dataMap);
//            }
















            /*chartDatas.forEach(map -> {
                //拼接色例名称
                String name = "";
                for (DataModelAttribute dataModelAttribute : colorDimensionsDataList) {
                    if (name.equals("")) {
                        name += map.get(dataModelAttribute.getRandomAlias());
                    } else {
                        name += "-" + map.get(dataModelAttribute.getRandomAlias());
                    }
                }


                for (String str : categoriesResult) {
                    for (DataModelAttribute dataModelAttribute : dimensionsDataList) {
                        String value = map.get(dataModelAttribute.getRandomAlias());
                        if (str.equals(value)) {

                        }
                    }
                }


                //拼接度量名称
                for (DataModelAttribute dataModelAttribute : modelAttributes1) {
                    Map<String, Object> dataMap = new HashMap<>();
                    List<String> lists = new ArrayList();
                    String resultName = name + "-" + dataModelAttribute.getFieldsAlias();
                    for (BigAttributeData bigAttributeData : yAll) {
                        if (dataModelAttribute.getId().equals(bigAttributeData.getId())) {
                            dataMap.put("type", bigAttributeData.getLineChartType());
                            dataMap.put("yAxisIndex", bigAttributeData.getyAxisIndex());
                            break;
                        }
                    }
                    dataMap.put("name", resultName);
                    lists.add(map.get(dataModelAttribute.getRandomAlias()));
                    dataMap.put("data", lists);
                    mapList.add(dataMap);
                }
            });*/
        } else {
            for (int i = 0; i < modelAttributes1.size(); i++) {
                Map<String, Object> resultMap = new HashMap();
                //循环创建集合，有多少度量就需要有多少集合
                List<Double> lists = new ArrayList();
                //根据度量字段，判断数据对应的字段取值循环放入集合
                int finalI = i;
                chartDatas.forEach(map -> {
                    map.forEach((k, v) -> {
                        if (k != null && v != null) {
                            if (k.equals(modelAttributes1.get(finalI).getRandomAlias())) {
                                lists.add(Double.valueOf(v));
                            }
                        } else {
                            lists.add(0.0);
                        }
                    });
                });
                String type = "";
                Integer yAxisIndex = 0;
                for (BigAttributeData bigAttributeData : yAll) {
                    if (modelAttributes1.get(i).getId().equals(bigAttributeData.getId())) {
                        type = bigAttributeData.getLineChartType();
                        yAxisIndex = bigAttributeData.getyAxisIndex();
                    }
                }
                resultMap.put("data", lists);
                resultMap.put("type", type);
                resultMap.put("yAxisIndex", yAxisIndex);
                resultMap.put("name", modelAttributes1.get(i).getFieldsAlias());
                mapList.add(resultMap);
            }
        }
        return mapList;
    }


    /**
     * @param chartDatas          结果数据
     * @param dataModelAttributes 所有维度
     * @return java.util.List<java.lang.String>
     * @Author zxx
     * @Description //TODO 获取维度数据集合（柱状图、折线图）
     * @Date 9:51 2020/6/18
     * @Param
     **/
    public static List<String> categoriesResult(List<Map<String, String>> chartDatas, List<DataModelAttribute> dataModelAttributes, List<DataModelAttribute> colorDimensionsDataList) {
        List<String> resultStrList = new ArrayList<>();
        chartDatas.forEach(map -> {
            StringBuffer strbuffer = new StringBuffer();
            map.forEach((k, v) -> {
                //如果颜色不为空，排除查询数据出来的维度拼接
                if (colorDimensionsDataList != null && colorDimensionsDataList.size() > 0) {
                    for (DataModelAttribute dataModelAttribute : colorDimensionsDataList) {
                        if (k.equals(dataModelAttribute.getRandomAlias())) {
                            continue;
                        }
                    }
                }
                //设置数据值,维度值-拼接
                for (DataModelAttribute dataModelAttribute : dataModelAttributes) {
                    if (k.equals(dataModelAttribute.getRandomAlias())) {
                        if (resultStrList.contains(v)) {
                            continue;
                        }
                        strbuffer.append(v + "-");
                    }
                }
            });
            if (StringUtils.isNotEmpty(strbuffer.toString())) {
                resultStrList.add(strbuffer.toString().substring(0, strbuffer.toString().length() - 1));
            }
        });
        return resultStrList;
    }


}
