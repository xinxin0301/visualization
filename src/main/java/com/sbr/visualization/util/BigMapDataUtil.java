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
 * @ClassName BigMapDataUtil
 * @Description TODO 地图工具类
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
public class BigMapDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigMapDataUtil.class);

    private static DataModelAttributeDAO dataModelAttributeDAO = SpringContextUtils.getBean(DataModelAttributeDAO.class);

    private static DataModelDAO dataModelDAO = SpringContextUtils.getBean(DataModelDAO.class);


    /**
     * @param bigScreenData 大屏实体对象
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 处理地图数据
     * @Date 17:09 2020/7/16
     **/
    public static InfoJson buidMapChartData(BigScreenData bigScreenData) throws IOException {
        InfoJson infoJson = new InfoJson();

        DataModel dataModel = dataModelDAO.findOne(bigScreenData.getDataModelId());
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, dataModel)) {
            return infoJson;
        }
        //排序集合
        List<BigAttributeData> sortListAll = new ArrayList<>();
        //获取地图地点
        List<BigAttributeData> name = bigScreenData.getName();
        //取值字段
        List<BigAttributeData> value = bigScreenData.getValue();

        //获取地图地点模型属性
        List<DataModelAttribute> dataModelAttributeList = null;
        if (name != null && name.size() > 0) {
            dataModelAttributeList = DataBaseUtil.findBigAttributeDataByListId(name);
            sortListAll.addAll(name);
        }
        //获取取值模型属性
        List<DataModelAttribute> valueDataList = null;
        if (value != null && value.size() > 0) {
            valueDataList = DataBaseUtil.findBigAttributeDataByListId(value);
            sortListAll.addAll(value);
        }

        //获取数据源
        DatasourceManage datasourceManage = dataModel.getDatasourceManage();
        //TODO 大屏对象、当前数据模型、度量条件、维度条件、分组条件、WHERE条件、排序条件
        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, dataModel, DataBaseUtil.buildMeasureSQL(value), DataBaseUtil.buildVeidooSQL(dataModelAttributeList),
                    DataBaseUtil.buildGroupBy(dataModelAttributeList), DataBaseUtil.buildWhereSQL(bigScreenData, dataModel, param), DataBaseUtil.buildSortSQL(sortListAll), param);
        }

        List<Map<String, String>> chartDatas = null;
        try {
            switch (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName()) {
                //获取数据，传递数据源和SQL，建立连接执行sql返回结果
                case CommonConstant.MYSQL:
                    chartDatas = DataBaseUtil.getDatas(dataModel.getDatasourceManage(), sqlBuffer.toString(), param);
                    break;
                case CommonConstant.ES:
                    chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, dataModel, value, dataModelAttributeList, datasourceManage, valueDataList);
            }
        } catch (Exception e) {
            LOGGER.error("BigMapDataUtil大屏地图数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行SQL错误：" + e.getMessage());
            return infoJson;
        }

        //处理返回数据
        switch (bigScreenData.getChartType()) {
            case CommonConstant.MAP://地图
                infoJson = getResultMapData(chartDatas, dataModelAttributeList, valueDataList, infoJson);
                break;
        }

        return infoJson;
    }


    /**
     * @param chartDatas             结果数据
     * @param dataModelAttributeList 地图名称模型属性
     * @param valueDataList          取值模型属性
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 处理地图数据
     * @Date 17:21 2020/7/16
     * @Param
     **/
    private static InfoJson getResultMapData(List<Map<String, String>> chartDatas, List<DataModelAttribute> dataModelAttributeList, List<DataModelAttribute> valueDataList, InfoJson infoJson) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> mapData = new ArrayList<>();
        if (chartDatas != null && chartDatas.size() > 0) {
            for (Map<String, String> chartData : chartDatas) {
                Map<String, Object> map = new HashMap<>();
                for (DataModelAttribute dataModelAttribute : dataModelAttributeList) {
                    map.put("name", chartData.get(dataModelAttribute.getRandomAlias()));
                }
                for (DataModelAttribute dataModelAttribute : valueDataList) {
                    Double value = 0.0;
                    String dataValue = chartData.get(dataModelAttribute.getRandomAlias());
                    if (dataValue != null) {
                        value = Double.valueOf(dataValue);
                    }
                    map.put("value", value);
                }
                mapData.add(map);
            }
        }
        resultMap.put("mapData", mapData);
        resultMap.put("valueName", valueDataList.get(0).getFieldsAlias());
        infoJson.setData(resultMap);
        return infoJson;
    }
}
