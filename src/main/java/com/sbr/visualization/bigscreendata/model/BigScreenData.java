package com.sbr.visualization.bigscreendata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbr.visualization.filter.model.Filter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName BigScreenData
 * @Description TODO 大屏数据统计实体
 * @Author zxx
 * @Version 1.0
 */
public class BigScreenData {

    /**
     * 数据模型ID
     **/
    @NotNull(message = "数据模型ID不能为空")
    @JsonProperty(value = "data_model_id")
    private String dataModelId;

    /**
     * 维度集合
     **/
    private List<BigAttributeData> x;

    /**
     * 维度集合2,表格列使用
     **/
    private List<BigAttributeData> x2;

    /**
     * 第一个Y轴
     **/
    private List<BigAttributeData> y;

    /**
     * 第二个Y轴
     **/
    private List<BigAttributeData> y2;

    /**
     * 没有y轴数据，文本、雷达图
     **/
    private List<BigAttributeData> value;

    /**
     * 没有y轴数据，文本、雷达图
     **/
    @JsonProperty(value = "max")
    private List<BigAttributeData> max;

    /**
     * color
     **/
    private List<BigAttributeData> color;

    /**
     * 目前桑基图使用 流出节点名称
     **/
    @JsonProperty(value = "source_node")
    private List<BigAttributeData> sourceNode;

    /**
     * 目前桑基图使用 流入节点名称字段
     **/
    @JsonProperty(value = "target_node")
    private List<BigAttributeData> targetNode;

    /**
     * 地图地点的名称、字符
     **/
    private List<BigAttributeData> name;

    /**
     * 地图 起点经度
     **/
    private List<BigAttributeData> lng;

    /**
     * 地图 终点经度
     **/
    private List<BigAttributeData> lng2;

    /**
     * 地图 起点维度
     **/
    private List<BigAttributeData> lat;

    /**
     * 地图 终点维度
     **/
    private List<BigAttributeData> lat2;

    /**
     * 图表类型
     **/
    @JsonProperty(value = "chart_type")
    private String chartType;

    /**
     * 过滤条件集合
     **/
    private List<Filter> filterList;

    /**
     * 单独value,过滤条件
     **/
    @JsonProperty(value = "value_filter_list")
    private List<Filter> valueFilterList;

    /**
     * 联动条件
     **/
    @JsonProperty(value = "biglinkage_data")
    private BiglinkageData biglinkageData;

    /**
     * 关联Url参数
     **/
    @JsonProperty(value = "query_data")
    private BiglinkageData queryData;

    /**
     * 表格类型 normal正常的，crossPivot交叉透视表
     **/
    @JsonProperty(value = "table_type")
    private String tableType;

    /**
     * 分页条数
     **/
    @NotNull(message = "分页条数不能为空")
    private int limit;

    /**
     * 是否开启列表序号
     *
     * true  开启表单序号
     * false 没有表单序号
     *
     **/
    @JsonProperty(value = "table_num")
    private Boolean tableNum;

    /**
     * 表单聚合开关
     *
     * true  不聚合展示所有数据
     * false 聚合
     **/
    @JsonProperty(value = "table_not_aggregate")
    private Boolean tableNotAggregate;

    /**
     *  最大值
     **/
    @JsonProperty(value = "max_value")
    private Integer maxValue;

    /**
     *  最小值
     **/
    @JsonProperty(value = "min_value")
    private Integer minValue;

    /**
     *  隱藏指标名称
     **/
    @JsonProperty(value = "hide_name")
    private boolean hideName;

    /**
     *  单位
     **/
    private String unit;

    /**
     *  小数转百分比
     **/
    @JsonProperty(value = "turn_percentage")
    private boolean turnPercentage;

    public List<BigAttributeData> getLat() {
        return lat;
    }

    public void setLat(List<BigAttributeData> lat) {
        this.lat = lat;
    }

    public List<BigAttributeData> getLat2() {
        return lat2;
    }

    public void setLat2(List<BigAttributeData> lat2) {
        this.lat2 = lat2;
    }

    public List<BigAttributeData> getLng() {
        return lng;
    }

    public void setLng(List<BigAttributeData> lng) {
        this.lng = lng;
    }

    public List<BigAttributeData> getLng2() {
        return lng2;
    }

    public void setLng2(List<BigAttributeData> lng2) {
        this.lng2 = lng2;
    }

    public BiglinkageData getQueryData() {
        return queryData;
    }

    public void setQueryData(BiglinkageData queryData) {
        this.queryData = queryData;
    }

    public List<Filter> getValueFilterList() {
        return valueFilterList;
    }

    public void setValueFilterList(List<Filter> valueFilterList) {
        this.valueFilterList = valueFilterList;
    }

    public boolean isTurnPercentage() {
        return turnPercentage;
    }

    public void setTurnPercentage(boolean turnPercentage) {
        this.turnPercentage = turnPercentage;
    }

    public boolean isHideName() {
        return hideName;
    }

    public void setHideName(boolean hideName) {
        this.hideName = hideName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<BigAttributeData> getMax() {
        return max;
    }

    public void setMax(List<BigAttributeData> max) {
        this.max = max;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Boolean getTableNotAggregate() {
        return tableNotAggregate;
    }

    public void setTableNotAggregate(Boolean tableNotAggregate) {
        this.tableNotAggregate = tableNotAggregate;
    }

    public List<BigAttributeData> getX2() {
        return x2;
    }

    public void setX2(List<BigAttributeData> x2) {
        this.x2 = x2;
    }

    public Boolean getTableNum() {
        return tableNum;
    }

    public void setTableNum(Boolean tableNum) {
        this.tableNum = tableNum;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public BiglinkageData getBiglinkageData() {
        return biglinkageData;
    }

    public void setBiglinkageData(BiglinkageData biglinkageData) {
        this.biglinkageData = biglinkageData;
    }

    public List<BigAttributeData> getName() {
        return name;
    }

    public void setName(List<BigAttributeData> name) {
        this.name = name;
    }

    public List<BigAttributeData> getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(List<BigAttributeData> sourceNode) {
        this.sourceNode = sourceNode;
    }

    public List<BigAttributeData> getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(List<BigAttributeData> targetNode) {
        this.targetNode = targetNode;
    }

    public List<BigAttributeData> getValue() {
        return value;
    }

    public void setValue(List<BigAttributeData> value) {
        this.value = value;
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    public String getDataModelId() {
        return dataModelId;
    }

    public void setDataModelId(String dataModelId) {
        this.dataModelId = dataModelId;
    }

    public List<BigAttributeData> getX() {
        return x;
    }

    public void setX(List<BigAttributeData> x) {
        this.x = x;
    }

    public List<BigAttributeData> getY() {
        return y;
    }

    public void setY(List<BigAttributeData> y) {
        this.y = y;
    }

    public List<BigAttributeData> getY2() {
        return y2;
    }

    public void setY2(List<BigAttributeData> y2) {
        this.y2 = y2;
    }

    public List<BigAttributeData> getColor() {
        return color;
    }

    public void setColor(List<BigAttributeData> color) {
        this.color = color;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
}
