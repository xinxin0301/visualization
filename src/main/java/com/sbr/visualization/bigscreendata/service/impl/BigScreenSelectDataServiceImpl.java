package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigAttributeData;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.DataBaseUtil;
import com.sbr.visualization.util.ElasticsearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@HandlerType(valueType = CommonConstant.SELECT)//全局查询条件
public class BigScreenSelectDataServiceImpl implements IBigScreenDataService {


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


        //维度集合
        List<DataModelAttribute> dimensionalityModelAttributeList = new ArrayList<>();
        //排序集合
        List<BigAttributeData> sortListAll = new ArrayList<>();

        //取值字段
        List<BigAttributeData> value = bigScreenData.getValue();
        //名称字段
        List<BigAttributeData> name = bigScreenData.getName();


        //获取名称模型属性
        List<DataModelAttribute> nameModelAttributeList = null;
        if (name != null && name.size() > 0) {
            nameModelAttributeList = DataBaseUtil.findBigAttributeDataByListId(name);
            sortListAll.addAll(name);
            dimensionalityModelAttributeList.addAll(nameModelAttributeList);
        }
        //获取值模型属性
        List<DataModelAttribute> valueModelAttributeList = null;
        if (value != null && value.size() > 0) {
            valueModelAttributeList = DataBaseUtil.findBigAttributeDataByListId(value);
            sortListAll.addAll(value);
            dimensionalityModelAttributeList.addAll(valueModelAttributeList);
        }


        //获取数据源
        DatasourceManage datasourceManage = modelDAOOne.getDatasourceManage();
        //TODO 大屏对象、当前数据模型、度量条件、维度条件、分组条件、WHERE条件、排序条件
        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, DataBaseUtil.buildMeasureSQL(null), DataBaseUtil.buildVeidooSQL(dimensionalityModelAttributeList),
                    DataBaseUtil.buildGroupBy(dimensionalityModelAttributeList), DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), DataBaseUtil.buildSortSQL(sortListAll), param);
        }
        List<Map<String, String>> chartDatas = null;
        switch (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName()) {
            //获取数据，传递数据源和SQL，建立连接执行sql返回结果
            case CommonConstant.MYSQL:
                chartDatas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                break;
            case CommonConstant.ES:
                chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, null, dimensionalityModelAttributeList, datasourceManage, null);
                break;
        }

        List<Map<String, Object>> data = new ArrayList<>();
        if (chartDatas != null) {
            for (Map<String, String> chartData : chartDatas) {
                Map<String, Object> valueMap = new HashMap<>();
                if (nameModelAttributeList != null) {
                    for (DataModelAttribute dataModelAttribute : nameModelAttributeList) {
                        valueMap.put("label", chartData.get(dataModelAttribute.getRandomAlias()).toString());
                    }
                    for (DataModelAttribute dataModelAttribute : valueModelAttributeList) {
                        valueMap.put("value", chartData.get(dataModelAttribute.getRandomAlias()).toString());
                    }
                    data.add(valueMap);
                } else {
                    for (DataModelAttribute dataModelAttribute : valueModelAttributeList) {
                        valueMap.put("label", chartData.get(dataModelAttribute.getRandomAlias()).toString());
                        valueMap.put("value", chartData.get(dataModelAttribute.getRandomAlias()).toString());
                        data.add(valueMap);
                    }

                }
            }
        }
        infoJson.setData(data);
        return infoJson;
    }
}
