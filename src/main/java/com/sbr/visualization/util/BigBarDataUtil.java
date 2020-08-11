package com.sbr.visualization.util;

import com.sbr.springboot.context.SpringContextUtils;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigAttributeData;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.dao.DataModelAttributeDAO;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BigBarDataUtil
 * @Description TODO 柱状图
 * @Author zxx
 * @Version 1.0
 */
public class BigBarDataUtil {


    private static final Logger LOGGER = LoggerFactory.getLogger(BigBarDataUtil.class);

    private static DataModelAttributeDAO dataModelAttributeDAO = SpringContextUtils.getBean(DataModelAttributeDAO.class);

    private static DataModelDAO dataModelDAO = SpringContextUtils.getBean(DataModelDAO.class);


    public static InfoJson buidChartData(BigScreenData bigScreenData) throws IOException {
        InfoJson infoJson = new InfoJson();

        //获取数据模型
        DataModel modelDAOOne = dataModelDAO.findOne(bigScreenData.getDataModelId());
        if (DataBaseUtil.buidCheckModel(bigScreenData, infoJson, modelDAOOne)) {
            return infoJson;
        }

        //维度集合
        List<DataModelAttribute> dimensionsDataAll = new ArrayList<>();
        //排序集合
        List<BigAttributeData> sortList = new ArrayList<>();

        //X轴/类别轴
        List<BigAttributeData> x = bigScreenData.getX();
        //Y轴/值轴
        List<BigAttributeData> y = bigScreenData.getY();
        //颜色图例
        List<BigAttributeData> color = bigScreenData.getColor();

        //X轴,顺序问题，遍历查询
        List<DataModelAttribute> xDataModelAttributes = new ArrayList<>();
        if (x != null && x.size() > 0) {
            for (BigAttributeData bigAttributeData : x) {
                DataModelAttribute dataModelAttribute = dataModelAttributeDAO.findOne(bigAttributeData.getId());
                dimensionsDataAll.add(dataModelAttribute);
                xDataModelAttributes.add(dataModelAttribute);
            }
            sortList.addAll(x);
        }

        //color
        List<DataModelAttribute> colorDataModelAttributes = null;
        if (color != null && color.size() > 0) {
            colorDataModelAttributes = DataBaseUtil.findBigAttributeDataByListId(color);
            dimensionsDataAll.addAll(colorDataModelAttributes);
            sortList.addAll(color);
        }

        //Y轴值
        List<DataModelAttribute> yDataModelAttributes = null;
        if (y != null && y.size() > 0) {
            yDataModelAttributes = DataBaseUtil.findBigAttributeDataByListId(y);
            sortList.addAll(y);
        }

        //维度SQL
        StringBuffer barVeidooSQL = DataBaseUtil.buildVeidooSQL(dimensionsDataAll);
        //度量SQL
        StringBuffer barMeasureSQL = DataBaseUtil.buildMeasureSQL(y);
        //排序SQL
        StringBuffer barSortSQL = DataBaseUtil.buildSortSQL(sortList);
        //分组SQL
        StringBuffer barGroupBySQL = DataBaseUtil.buildGroupBy(dimensionsDataAll);

        //条件参数集合
        List<String> param = new ArrayList<>();
        //获取结果SQL
        StringBuffer sqlBuffer = DataBaseUtil.buidSQL(bigScreenData, modelDAOOne, barMeasureSQL, barVeidooSQL,
                barGroupBySQL, DataBaseUtil.buildWhereSQL(bigScreenData, modelDAOOne, param), barSortSQL, param);
        List<Map<String, String>> datas = null;
        try {
            datas = DataBaseUtil.getDatas(modelDAOOne.getDatasourceManage(), sqlBuffer.toString(), param);
        } catch (Exception e) {
            LOGGER.error("BigBarDataUtil柱状图数据处理错误:", e);
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            infoJson.setDescription("执行SQL错误：" + e.getMessage());
            return infoJson;
        }

        Map<String, Object> resultMap = new HashMap<>();
        List<String> categories = new ArrayList<>();
        List<Map<String, Object>> series = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        for (Map<String, String> data : datas) {
            String xValue = "";
            for (DataModelAttribute xDataModelAttribute : xDataModelAttributes) {
                xValue += data.get(xDataModelAttribute.getRandomAlias()) + "-";
            }
            for (DataModelAttribute yDataModelAttribute : yDataModelAttributes) {
                Map<String, Object> map1 = (Map<String, Object>) map.get(yDataModelAttribute.getRandomAlias());
                if (map1 != null) {
                    map1.put("name", yDataModelAttribute.getFieldsAlias());
                    map1.put("type", "bar");
                    List<Double> doubleList = (List<Double>) map1.get("data");
                    doubleList.add(Double.valueOf(data.get(yDataModelAttribute.getRandomAlias())));
                } else {
                    Map<String, Object> map2 = new HashMap<>();
                    List<Double> doubleList = new ArrayList<>();
                    map2.put("name", yDataModelAttribute.getFieldsAlias());
                    map2.put("type", "bar");
                    map2.put("data", doubleList);
                    doubleList.add(Double.valueOf(data.get(yDataModelAttribute.getRandomAlias())));
                    map.put(yDataModelAttribute.getRandomAlias(), map2);
                }
            }
            categories.add(xValue.substring(0, xValue.length() - 1));
        }

        for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
            series.add((Map<String, Object>) stringObjectEntry.getValue());
        }
        resultMap.put("categories", categories);
        resultMap.put("series", series);
        infoJson.setData(resultMap);
        return infoJson;
    }

}
