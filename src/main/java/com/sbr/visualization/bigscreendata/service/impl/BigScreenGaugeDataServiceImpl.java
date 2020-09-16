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

import java.io.IOException;
import java.util.*;

@Service
@HandlerType(valueType = CommonConstant.GAUGE)//定义注解，配置仪表盘
public class BigScreenGaugeDataServiceImpl implements IBigScreenDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigScreenGaugeDataServiceImpl.class);

    @Autowired
    private DataModelDAO dataModelDAO;

    @Autowired
    private DataModelAttributeDAO dataModelAttributeDAO;

    /**
     * @return com.sbr.springboot.json.InfoJson
     * @Author 张鑫鑫
     * @Description //TODO 配置仪表盘
     * @Param [BigScreenData]
     **/
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
        //Y轴大屏属性
        List<BigAttributeData> yListAll = new ArrayList<>();
        //排序集合
        List<BigAttributeData> sortListAll = new ArrayList<>();
        //获取度量
        List<BigAttributeData> value = bigScreenData.getValue();
        //度量
        List<DataModelAttribute> yValueList = null;
        if (value != null && value.size() > 0) {
            yValueList = DataBaseUtil.findBigAttributeDataByListId(value);
            sortListAll.addAll(value);
            yListAll.addAll(value);
            yDataModelAttributeListAll.addAll(yValueList);
        }

        Double maxValue = 0.0;
        if (bigScreenData.getMaxValue() != null) {
            maxValue = Double.valueOf(bigScreenData.getMaxValue());
        } else {
            List<BigAttributeData> sortListAll1 = new ArrayList<>();
            //获取最大值
            List<BigAttributeData> maxValueData = bigScreenData.getMax();
            //最大值
            List<DataModelAttribute> maxValueList = null;
            if (maxValueData != null && maxValueData.size() > 0) {
                maxValueList = new ArrayList<>();
                for (BigAttributeData bigAttributeData : maxValueData) {
                    maxValueList.add(dataModelAttributeDAO.findOne(bigAttributeData.getId()));
                }
                sortListAll1.addAll(maxValueData);
            }
            //Mysql
            StringBuffer sqlBuffer = null;
            List<String> param = null;
            if (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
                param = new ArrayList<>();
                sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, DataBaseUtil.buildMeasureSQL(maxValueData), null,
                        null, DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), DataBaseUtil.buildSortSQL(sortListAll1), param);
            }
            List<Map<String, String>> chartDatas = null;
            switch (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL:
                    chartDatas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                    break;
                case CommonConstant.ES:
                    chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, maxValueData, null, modelDAOOne.getDatasourceManage(), maxValueList);
                    break;
            }
            if (chartDatas != null) {
                maxValue = Double.valueOf(chartDatas.get(0).get(maxValueList.get(0).getRandomAlias()));
            }
        }

        //Mysql
        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, DataBaseUtil.buildMeasureSQL(yListAll), null,
                    null, DataBaseUtil.buildWhereSQLAndValue(bigScreenData, modelDAOOne, param), DataBaseUtil.buildSortSQL(sortListAll), param);
        }
        List<Map<String, String>> chartDatas = null;
        switch (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
            case CommonConstant.MYSQL:
                chartDatas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                break;
            case CommonConstant.ES:
                chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, value, null, modelDAOOne.getDatasourceManage(), yDataModelAttributeListAll);
                break;
        }

        Map<String, Object> resultMap = new LinkedHashMap<>();
        if (chartDatas != null && chartDatas.size() > 0) {
            resultMap.put("hideName", bigScreenData.isHideName());
            resultMap.put("max", maxValue);
            resultMap.put("min", bigScreenData.getMinValue());
            resultMap.put("name", DataBaseUtil.buildShowName(value, yValueList.get(0)));
            resultMap.put("unit", bigScreenData.getUnit());
            resultMap.put("value", chartDatas.get(0).get(yValueList.get(0).getRandomAlias()));
        }
        infoJson.setData(resultMap);
        return infoJson;
    }
}