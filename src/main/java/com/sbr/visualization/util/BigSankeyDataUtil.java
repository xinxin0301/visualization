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
 * @ClassName BigSankeyDataUtil
 * @Description TODO 桑基图
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
public class BigSankeyDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BigSankeyDataUtil.class);

    private static DataModelAttributeDAO dataModelAttributeDAO = SpringContextUtils.getBean(DataModelAttributeDAO.class);

    private static DataModelDAO dataModelDAO = SpringContextUtils.getBean(DataModelDAO.class);

    /**
     * @param bigScreenData
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO  桑基图
     * @Date 14:38 2020/7/16
     * @Param
     **/
    public static InfoJson buidSankeyChartData(BigScreenData bigScreenData) throws IOException {
        InfoJson infoJson = new InfoJson();
        DataModel daoOne = dataModelDAO.findOne(bigScreenData.getDataModelId());
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, daoOne)) {
            return infoJson;
        }
        //流出节点
        List<BigAttributeData> sourceNode = bigScreenData.getSourceNode();
        //流入节点
        List<BigAttributeData> targetNode = bigScreenData.getTargetNode();
        //流数据大小
        List<BigAttributeData> value = bigScreenData.getValue();
        //排序集合
        List<BigAttributeData> sortListAll = new ArrayList<>();
        //流出、流入所有数据
        List<DataModelAttribute> attributeListALL = new ArrayList<>();

        //流出
        List<DataModelAttribute> sourceNodeData = null;
        if (sourceNode != null && sourceNode.size() > 0) {
            sourceNodeData = DataBaseUtil.findBigAttributeDataByListId(sourceNode);
            attributeListALL.addAll(sourceNodeData);
            sortListAll.addAll(sourceNode);
        }

        //流入
        List<DataModelAttribute> targetNodeData = null;
        if (targetNode != null && targetNode.size() > 0) {
            targetNodeData = DataBaseUtil.findBigAttributeDataByListId(targetNode);
            attributeListALL.addAll(targetNodeData);
            sortListAll.addAll(targetNode);
        }

        //流数据大小
        List<DataModelAttribute> valueDataList = null;
        if (value != null && value.size() > 0) {
            valueDataList = DataBaseUtil.findBigAttributeDataByListId(value);
            sortListAll.addAll(value);
        }

        //Mysql
        StringBuffer sqlBuffer = null;
        List<String> param = null;
        if (daoOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName().equals(CommonConstant.MYSQL)) {
            param = new ArrayList<>();
            sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, daoOne, DataBaseUtil.buildMeasureSQL(value), DataBaseUtil.buildVeidooSQL(attributeListALL),
                    DataBaseUtil.buildGroupBy(attributeListALL), DataBaseUtil.buildWhereSQL(bigScreenData, daoOne, param), DataBaseUtil.buildSortSQL(sortListAll), param);
        }

        List<Map<String, String>> chartDatas = null;
        try {
            switch (daoOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL:
                    chartDatas = DataBaseUtil.getDatas(daoOne.getDatasourceManage(), sqlBuffer.toString(), param);
                    break;
                case CommonConstant.ES:
                    chartDatas = ElasticsearchUtil.buildElasticsearch(bigScreenData, daoOne, value, attributeListALL, daoOne.getDatasourceManage(), valueDataList);
            }
            //获取数据，传递数据源和SQL，建立连接执行sql返回结果

        } catch (Exception e) {
            LOGGER.error("BigSankeyDataUtil大屏桑基图数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行错误：" + e.getMessage());
            return infoJson;
        }

        //处理返回数据
        switch (bigScreenData.getChartType()) {
            case CommonConstant.SANKEY://桑基图
                infoJson = getResultSankeyData(chartDatas, sourceNodeData, targetNodeData, valueDataList, infoJson);
                break;
        }
        return infoJson;
    }


    /**
     * @param chartDatas     数据结果
     * @param sourceNodeData 流出模型属性
     * @param targetNodeData 流入模型属性
     * @param valueDataList  流数据大小
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 处理桑基图
     * @Date 15:35 2020/7/16
     * @Param
     **/
    private static InfoJson getResultSankeyData(List<Map<String, String>> chartDatas, List<DataModelAttribute> sourceNodeData, List<DataModelAttribute> targetNodeData, List<DataModelAttribute> valueDataList, InfoJson infoJson) {

        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, String>> nodeList = new ArrayList<>();
        List<Map<String, Object>> linksList = new ArrayList<>();
        //去重
        Set<String> nameSet = new HashSet<>();
        for (Map<String, String> chartData : chartDatas) {
            Map<String, Object> linksMap = new HashMap<>();

            for (DataModelAttribute sourceNodeDatum : sourceNodeData) {
                Map<String, String> sourceNodeMap = new HashMap<>();
                String name = chartData.get(sourceNodeDatum.getRandomAlias());
                if (!nameSet.contains(name)) {
                    sourceNodeMap.put("name", name);//处理nodes
                    nameSet.add(name);
                    nodeList.add(sourceNodeMap);
                }
                linksMap.put("source", name); //处理links
            }

            for (DataModelAttribute targetNodeDatum : targetNodeData) {
                Map<String, String> targetNodeMap = new HashMap<>();
                String name = chartData.get(targetNodeDatum.getRandomAlias());
                if (!nameSet.contains(name)) {
                    targetNodeMap.put("name", name);//处理nodes
                    nameSet.add(name);
                    nodeList.add(targetNodeMap);
                }
                linksMap.put("target", name); //处理links
            }

            //处理links
            for (DataModelAttribute dataModelAttribute : valueDataList) {
                Double value = 0.0;
                String dataValue = chartData.get(dataModelAttribute.getRandomAlias());
                if (dataValue != null) {
                    value = Double.valueOf(dataValue);
                }
                linksMap.put("value", value);
            }
            linksList.add(linksMap);
        }

        resultMap.put("nodes", nodeList);
        resultMap.put("links", linksList);
        infoJson.setData(resultMap);
        return infoJson;
    }
}
