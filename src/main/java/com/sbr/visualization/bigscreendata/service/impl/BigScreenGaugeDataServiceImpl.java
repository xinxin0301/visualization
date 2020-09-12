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
    public InfoJson analysisChartResult(BigScreenData bigScreenData) throws IOException {
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
        //获取最大值
        List<BigAttributeData> maxValue = bigScreenData.getMax();
        //度量
        List<DataModelAttribute> yValueList = null;
        if (value != null && value.size() > 0) {
            yValueList = DataBaseUtil.findBigAttributeDataByListId(value);
            sortListAll.addAll(value);
            yListAll.addAll(value);
            yDataModelAttributeListAll.addAll(yValueList);
        }
        //最大值
        List<DataModelAttribute> maxValueList = null;
        if (maxValue != null && maxValue.size() > 0) {
            maxValueList = DataBaseUtil.findBigAttributeDataByListId(maxValue);
            yListAll.addAll(maxValue);
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
        try {
            switch (modelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL:
                    chartDatas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                    break;
                case CommonConstant.ES:
                    chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, value, null, modelDAOOne.getDatasourceManage(), yDataModelAttributeListAll);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("BigScreenGaugeDataServiceImpl大屏仪表盘数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行错误：" + e.getMessage());

        }

        Map<String, Object> resultMap = new LinkedHashMap<>();
        if (chartDatas != null && chartDatas.size() > 0) {
            resultMap.put("hideName", bigScreenData.isHideName());
            if (maxValue != null && maxValue.size() > 0) {
                resultMap.put("max", chartDatas.get(0).get(maxValueList.get(0).getRandomAlias()));
            } else {
                resultMap.put("max", bigScreenData.getMaxValue());
            }
            resultMap.put("min", bigScreenData.getMinValue());
            resultMap.put("name", DataBaseUtil.buildShowName(value, yValueList.get(0)));
            resultMap.put("unit", bigScreenData.getUnit());
            resultMap.put("value", chartDatas.get(0).get(yValueList.get(0).getRandomAlias()));
        }
        infoJson.setData(resultMap);
        return infoJson;
    }
}