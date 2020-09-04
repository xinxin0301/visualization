package com.sbr.visualization.bigscreendata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @ClassName TableCol
 * @Description TODO 表格维度列设置
 * @Author zxx
 * @Version 1.0
 */
public class TableCol {
    /**
     * 表头备注
     **/
    private String remark;

    /**
     *  对齐方式
     **/
    @JsonProperty(value = "text_align")
    private String textAlign;

    /**
     *  宽度
     **/
    private String width;

    /**
     *  自动换行
     **/
    @JsonProperty(value = "auto_wrap")
    private boolean autoWrap;

    /**
     *  表头背景色
     **/
    @JsonProperty(value = "header_bg_color")
    private String headerBgColor;

    /**
     *  列背景色
     **/
    @JsonProperty(value = "bg_color")
    private String bgColor;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(String textAlign) {
        this.textAlign = textAlign;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public boolean isAutoWrap() {
        return autoWrap;
    }

    public void setAutoWrap(boolean autoWrap) {
        this.autoWrap = autoWrap;
    }

    public String getHeaderBgColor() {
        return headerBgColor;
    }

    public void setHeaderBgColor(String headerBgColor) {
        this.headerBgColor = headerBgColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }
}
