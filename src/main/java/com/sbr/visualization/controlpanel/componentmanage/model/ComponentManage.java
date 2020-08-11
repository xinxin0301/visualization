package com.sbr.visualization.controlpanel.componentmanage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbr.visualization.controlpanel.componenttypemanage.model.ComponentTypeManage;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述：元组件管理模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-23 14:58:48
 */
@Entity
@Table(name = "component_manage")
public class ComponentManage {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 组件类型表主键
     */
    @ManyToOne
    @JoinColumn(name = "component_type_id")
    private ComponentTypeManage componentTypeManage;

    /**
     * 组件名称
     */
    @NotNull(message = "组件名称不能为空")
    @Size(min = 1, max = 512, message = "组件名称的长度范围为:1-512")
    @JsonProperty(value = "component_name")
    private String componentName;

    /**
     * 组件代码
     */
    @JsonProperty(value = "component_code")
    private String componentCode;

    /**
     * 图表代码
     */
    @JsonProperty(value = "chart_code")
    private String chartCode;

    /**
     * 设置
     */
    @JsonProperty(value = "options")
    private String options;

    /**
     * 显示顺序
     */
    @JsonProperty(value = "sort_index")
    private Integer sortIndex;

    /**
     * 1:可见 2:不可见
     */
    @JsonProperty(value = "is_visible")
    private Integer isVisible;

    /**
     * 面板JSON
     */
    @JsonProperty(value = "panel_json")
    private String panelJson;

    /**
     * 图标
     */
    @JsonProperty(value = "icon")
    private String icon;


    /**
     * 是否继承基础属性1:是 2:否
     */
    @JsonProperty(value = "is_inherit_basics")
    private Integer isInheritBasics;


    /**
     * 是否使用数据属性1:是 2:否
     */
    @JsonProperty(value = "is_use_data")
    private Integer isUseData;

    /**
     * 是否开启下钻1:开启 2:不开启
     */
    @JsonProperty(value = "is_open_drilldown")
    private Integer isOpenDrilldown;

    /**
     * 是否开启联动1:开启 2:不开启
     */
    @JsonProperty(value = "is_open_linkage")
    private Integer isOpenLinkage;

    /**
     *  组件默认配置表单
     */
    @JsonProperty(value = "chart_form_data")
    private String chartFormData;

    public String getChartFormData() {
        return chartFormData;
    }

    public void setChartFormData(String chartFormData) {
        this.chartFormData = chartFormData;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Integer getIsOpenDrilldown() {
        return isOpenDrilldown;
    }

    public void setIsOpenDrilldown(Integer isOpenDrilldown) {
        this.isOpenDrilldown = isOpenDrilldown;
    }

    public Integer getIsOpenLinkage() {
        return isOpenLinkage;
    }

    public void setIsOpenLinkage(Integer isOpenLinkage) {
        this.isOpenLinkage = isOpenLinkage;
    }

    public Integer getIsInheritBasics() {
        return isInheritBasics;
    }

    public void setIsInheritBasics(Integer isInheritBasics) {
        this.isInheritBasics = isInheritBasics;
    }

    public Integer getIsUseData() {
        return isUseData;
    }

    public void setIsUseData(Integer isUseData) {
        this.isUseData = isUseData;
    }

    public String getChartCode() {
        return chartCode;
    }

    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    public Integer getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Integer isVisible) {
        this.isVisible = isVisible;
    }

    public String getPanelJson() {
        return panelJson;
    }

    public void setPanelJson(String panelJson) {
        this.panelJson = panelJson;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ComponentTypeManage getComponentTypeManage() {
        return componentTypeManage;
    }

    public void setComponentTypeManage(ComponentTypeManage componentTypeManage) {
        this.componentTypeManage = componentTypeManage;
    }

    public String getComponentName() {
        return this.componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }


}