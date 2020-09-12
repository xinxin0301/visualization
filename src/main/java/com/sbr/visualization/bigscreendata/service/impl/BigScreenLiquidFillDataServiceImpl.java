package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigAttributeData;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.dao.DataModelAttributeDAO;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.DataBaseUtil;
import com.sbr.visualization.util.ElasticsearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author 张鑫鑫
 * @Description //TODO 水球图
 * @Date 13:42 2020/9/9
 * @Param
 * @return
 **/
@Service
@HandlerType(valueType = CommonConstant.LIQUID_FILL)//自定义注解，水球图
public class BigScreenLiquidFillDataServiceImpl implements IBigScreenDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigScreenLiquidFillDataServiceImpl.class);

    @Autowired
    private DataModelDAO dataModelDAO;

    @Autowired
    private DataModelAttributeDAO dataModelAttributeDAO;

    @Override
    public InfoJson analysisChartResult(BigScreenData bigScreenData) throws Exception {
        InfoJson infoJson = new InfoJson();

        //获取数据模型
        DataModel modelDAOOne = dataModelDAO.findOne(bigScreenData.getDataModelId());
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, modelDAOOne)) {
            return infoJson;
        }
        //度量模型属性总集合
        List<DataModelAttribute> yDataModelAttributeListAll = new ArrayList<>();
        //全部度量集合
        List<BigAttributeData> yListAll = new ArrayList<>();
        //排序集合
        List<BigAttributeData> sortListAll = new ArrayList<>();
        //获取度量
        List<BigAttributeData> valueData = bigScreenData.getValue();
        //获取最大值
        List<BigAttributeData> maxValueData = bigScreenData.getMax();

        //度量
        List<DataModelAttribute> yValueList = null;
        if (valueData != null && valueData.size() > 0) {
            yValueList = new ArrayList<>();
            yValueList = DataBaseUtil.findBigAttributeDataByListId(valueData);
            sortListAll.addAll(valueData);
            yListAll.addAll(valueData);
            yDataModelAttributeListAll.addAll(yValueList);
        }

        //最大值
        List<DataModelAttribute> maxValueList = null;
        if (maxValueData != null && maxValueData.size() > 0) {
            maxValueList = new ArrayList<>();
            for (BigAttributeData bigAttributeData : maxValueData) {
                maxValueList.add(dataModelAttributeDAO.findOne(bigAttributeData.getId()));
            }
            yListAll.addAll(maxValueData);
            sortListAll.addAll(maxValueData);
            yDataModelAttributeListAll.addAll(maxValueList);
        }


        //Mysql
        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, DataBaseUtil.buildMeasureSQL(yListAll), null,
                    null, DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), DataBaseUtil.buildSortSQL(sortListAll), param);
        }
        List<Map<String, String>> chartDatas = null;
        switch (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
            case CommonConstant.MYSQL:
                chartDatas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                break;
            case CommonConstant.ES:
                chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, yListAll, null, modelDAOOne.getDatasourceManage(), yDataModelAttributeListAll);
                break;
        }
        Map<String, Object> resultMap = new HashMap<>();
        Double dataValue = 0.0;
        Double maxValue = 0.0;
        if (maxValueData != null && maxValueData.size() > 0) {
            maxValue = Double.valueOf(chartDatas.get(0).get(maxValueList.get(0).getRandomAlias()));
        } else {
            maxValue = Double.valueOf(bigScreenData.getMaxValue());
        }
        if (chartDatas != null) {
            dataValue = Double.valueOf(chartDatas.get(0).get(yValueList.get(0).getRandomAlias()));
        }
        //小数转百分比
        if (bigScreenData.isTurnPercentage()) {
            dataValue = dataValue * 100;
        }
        resultMap.put("min", 0.0);
        resultMap.put("max", maxValue);
        resultMap.put("value", dataValue);
        infoJson.setData(resultMap);
        return infoJson;
    }
}