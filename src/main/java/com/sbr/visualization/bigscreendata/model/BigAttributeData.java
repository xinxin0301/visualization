package com.sbr.visualization.bigscreendata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @ClassName BigAttributeData 大屏属性数据
 * @Description TODO
 * @Author zxx
 * @Version 1.0
 */
public class BigAttributeData {

    /**
     * 数据模型属性ID
     **/
    private String id;

    /**
     * 聚合类型
     **/
    private String aggregator;

    /**
     * 排序
     **/
    private String sort;

    /**
     * 类型 d维度 m度量
     **/
    private String type;

    /**
     * 图表类型
     **/
    @JsonProperty(value = "line_chart_type")
    private String lineChartType;

    /**
     * 第几个Y轴
     **/
    @JsonProperty(value = "y_axis_index")
    private Integer yAxisIndex;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getyAxisIndex() {
        return yAxisIndex;
    }

    public void setyAxisIndex(Integer yAxisIndex) {
        this.yAxisIndex = yAxisIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAggregator() {
        return aggregator;
    }

    public void setAggregator(String aggregator) {
        this.aggregator = aggregator;
    }

    public String getLineChartType() {
        return lineChartType;
    }

    public void setLineChartType(String lineChartType) {
        this.lineChartType = lineChartType;
    }
}
