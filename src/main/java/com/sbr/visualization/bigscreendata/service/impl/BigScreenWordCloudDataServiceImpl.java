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
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.DataBaseUtil;
import com.sbr.visualization.util.ElasticsearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author 张鑫鑫
 * @Description //TODO 字符云
 * @Date 13:42 2020/9/9
 * @Param
 * @return
 **/
@Service
@HandlerType(valueType = CommonConstant.WORDCLOUD)//自定义注解，字符云
public class BigScreenWordCloudDataServiceImpl implements IBigScreenDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigScreenWordCloudDataServiceImpl.class);

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

        //排序集合
        List<BigAttributeData> sortListAll = new ArrayList<>();
        //获取度量
        List<BigAttributeData> value = bigScreenData.getValue();
        //获取字符
        List<BigAttributeData> name = bigScreenData.getName();
        //度量
        List<DataModelAttribute> yValueList = null;
        if (value != null && value.size() > 0) {
            yValueList = DataBaseUtil.findBigAttributeDataByListId(value);
            sortListAll.addAll(value);
        }
        //获取字符属性模型
        List<DataModelAttribute> nameModelAttributeList = null;
        if (name != null && name.size() > 0) {
            nameModelAttributeList = DataBaseUtil.findBigAttributeDataByListId(name);
            sortListAll.addAll(name);
        }

        //获取数据源
        DatasourceManage datasourceManage = modelDAOOne.getDatasourceManage();
        //TODO 大屏对象、当前数据模型、度量条件、维度条件、分组条件、WHERE条件、排序条件
        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, DataBaseUtil.buildMeasureSQL(value), DataBaseUtil.buildVeidooSQL(nameModelAttributeList),
                    DataBaseUtil.buildGroupBy(nameModelAttributeList), DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), DataBaseUtil.buildSortSQL(sortListAll), param);
        }

        List<Map<String, String>> chartDatas = null;
        switch (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName()) {
            //获取数据，传递数据源和SQL，建立连接执行sql返回结果
            case CommonConstant.MYSQL:
                chartDatas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
                break;
            case CommonConstant.ES:
                chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, modelDAOOne, value, nameModelAttributeList, datasourceManage, yValueList);
                break;
        }

        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> data = new ArrayList<>();
        if (chartDatas != null && chartDatas.size() > 0) {
            for (Map<String, String> chartData : chartDatas) {
                Map<String, Object> map = new LinkedHashMap<>();
                String nameValue = "";
                Double doubleValue = 0.0;
                //维度
                for (DataModelAttribute dataModelAttribute : nameModelAttributeList) {
                    nameValue += chartData.get(dataModelAttribute.getRandomAlias()) + "-";
                }
                //度量
                for (DataModelAttribute dataModelAttribute : yValueList) {
                    doubleValue = Double.valueOf(chartData.get(dataModelAttribute.getRandomAlias()));
                }
                map.put("name", nameValue.substring(0, nameValue.length() - 1));
                map.put("value", doubleValue);
                data.add(map);
            }
        }
        infoJson.setData(data);
        return infoJson;
    }
}