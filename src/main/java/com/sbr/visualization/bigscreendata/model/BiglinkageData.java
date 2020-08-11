package com.sbr.visualization.bigscreendata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @ClassName BiglinkageData
 * @Description TODO 大屏联动对象
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
public class BiglinkageData {

    private String value;

    /**
     * 数据模型ID
     **/
    @JsonProperty(value = "data_model_attribute_id")
    private String dataModelAttributeId;

    /**
     * 连接类型
     **/
    @JsonProperty(value = "link_type")
    private String linkType;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDataModelAttributeId() {
        return dataModelAttributeId;
    }

    public void setDataModelAttributeId(String dataModelAttributeId) {
        this.dataModelAttributeId = dataModelAttributeId;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }
}
