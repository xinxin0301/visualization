package com.sbr.visualization.bigscreendata.service.impl;


import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigAttributeData;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.DataBaseUtil;
import com.sbr.visualization.util.ElasticsearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 3D 飞线地图
 */
@Service
@HandlerType(valueType = CommonConstant.THREE_MAP_LINE)//自定义注解，3D飞线地图
public class BigScreenThreeMapLineServiceImpl implements IBigScreenDataService {

    @Autowired
    private DataModelDAO dataModelDAO;

    @Override
    public InfoJson analysisChartResult(BigScreenData bigScreenData) throws Exception {

        InfoJson infoJson = new InfoJson();
        //获取数据模型
        DataModel modelDAOOne = dataModelDAO.findOne(bigScreenData.getDataModelId());
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, modelDAOOne)) {
            return infoJson;
        }

        //起点经度、维度
        List<DataModelAttribute> originAll = new ArrayList<>();
        //终点经度、维度
        List<DataModelAttribute> destinationAll = new ArrayList<>();

        //维度集合
        List<DataModelAttribute> dimensionsDataAll = new ArrayList<>();
        //排序集合
        List<BigAttributeData> sortList = new ArrayList<>();

        //起点维度
        List<BigAttributeData> lat = bigScreenData.getLat();
        //终点维度
        List<BigAttributeData> lat2 = bigScreenData.getLat2();
        //起点经度
        List<BigAttributeData> lng = bigScreenData.getLng();
        //终点经度
        List<BigAttributeData> lng2 = bigScreenData.getLng2();
        //飞线分类
        List<BigAttributeData> name = bigScreenData.getName();

        //起点经度
        List<DataModelAttribute> lngDataModelAttributeList = null;
        if (lng != null && lng.size() > 0) {
            lngDataModelAttributeList = DataBaseUtil.findBigAttributeDataByListId(lng);
            dimensionsDataAll.addAll(lngDataModelAttributeList);
            originAll.addAll(lngDataModelAttributeList);
            sortList.addAll(lng);
        }
        //起点维度
        List<DataModelAttribute> latDataModelAttributeList = null;
        if (lat != null && lat.size() > 0) {
            latDataModelAttributeList = DataBaseUtil.findBigAttributeDataByListId(lat);
            dimensionsDataAll.addAll(latDataModelAttributeList);
            originAll.addAll(latDataModelAttributeList);
            sortList.addAll(lat);
        }


        //终点经度
        List<DataModelAttribute> lng2DataModelAttributeList = null;
        if (lng2 != null && lng2.size() > 0) {
            lng2DataModelAttributeList = DataBaseUtil.findBigAttributeDataByListId(lng2);
            dimensionsDataAll.addAll(lng2DataModelAttributeList);
            destinationAll.addAll(lng2DataModelAttributeList);
            sortList.addAll(lng2);
        }

        //终点维度
        List<DataModelAttribute> lat2DataModelAttributeList = null;
        if (lat2 != null && lat2.size() > 0) {
            lat2DataModelAttributeList = DataBaseUtil.findBigAttributeDataByListId(lat2);
            dimensionsDataAll.addAll(lat2DataModelAttributeList);
            destinationAll.addAll(lat2DataModelAttributeList);
            sortList.addAll(lat2);
        }

        //飞线分类
        List<DataModelAttribute> nameDataModelAttributeList = null;
        if (name != null && name.size() > 0) {
            nameDataModelAttributeList = DataBaseUtil.findBigAttributeDataByListId(name);
            dimensionsDataAll.addAll(nameDataModelAttributeList);
            sortList.addAll(name);
        }

        //TODO 大屏对象、当前数据模型、度量条件、维度条件、分组条件、WHERE条件、排序条件
        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, DataBaseUtil.buildMeasureSQL(null), DataBaseUtil.buildVeidooSQL(dimensionsDataAll),
                    DataBaseUtil.buildGroupBy(dimensionsDataAll), DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), DataBaseUtil.buildSortSQL(sortList), param);
        }
        List<Map<String, String>> chartDatas = null;
        switch (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
            //获取数据，传递数据源和SQL，建立连接执行sql返回结果
            case CommonConstant.MYSQL:
                chartDatas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                break;
            case CommonConstant.ES:
                chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, null, dimensionsDataAll, modelDAOOne.getDatasourceManage(), null);
                break;
        }

        List<Map<String, Object>> dataMap = new ArrayList<>();

        for (Map<String, String> chartData : chartDatas) {
            Map<String, Object> map = new LinkedHashMap<>();
            List<List<List<Double>>> data = new ArrayList<>();

            List<List<Double>> dataResult = new ArrayList<>();
            //处理起点经度、维度
            List<Double> latData = new ArrayList<>();
            for (DataModelAttribute dataModelAttribute : originAll) {
                latData.add(Double.valueOf(chartData.get(dataModelAttribute.getRandomAlias())));
            }
            dataResult.add(latData);

            //处理终点经度、维度
            List<Double> lngData = new ArrayList<>();
            for (DataModelAttribute dataModelAttribute : destinationAll) {
                lngData.add(Double.valueOf(chartData.get(dataModelAttribute.getRandomAlias())));
            }
            dataResult.add(lngData);

            //处理飞线分类
            if (nameDataModelAttributeList != null && nameDataModelAttributeList.size() > 0) {
                for (DataModelAttribute dataModelAttribute : nameDataModelAttributeList) {
                    map.put("name", chartData.get(dataModelAttribute.getRandomAlias()));
                }
            }

            data.add(dataResult);
            map.put("data", data);
            dataMap.add(map);
        }
        infoJson.setData(dataMap);
        return infoJson;
    }

}
