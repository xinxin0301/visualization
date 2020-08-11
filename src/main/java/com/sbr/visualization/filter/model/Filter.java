package com.sbr.visualization.filter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbr.visualization.controlpanel.componentmanage.model.ComponentManage;
import com.sbr.visualization.datamodel.model.DataModel;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 描述：过滤器模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-30 11:18:23
 */
@Entity
@Table(name = "filter")
public class Filter {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 字段名称
     */
    @JsonProperty(value = "field_name")
    private String fieldName;

    /**
     * 表名
     */
    @JsonProperty(value = "table_name")
    private String tableName;

    /**
     * 字段ID
     */
    @JsonProperty(value = "field_id")
    private String fieldId;

    /**
     * 字段类型
     */
    @JsonProperty(value = "type")
    private String type;

    /**
     * 文本筛选
     */
    @JsonProperty(value = "text_match")
    private String textMatch;

    /**
     * 列表筛选
     */
    @JsonProperty(value = "list_match")
    private String listMatch;

    /**
     * 时间过滤
     */
    @JsonProperty(value = "date")
    private String date;

    /**
     * 描述
     */
    @JsonProperty(value = "description")
    private String description;

    /**
     * 使用类型 1:数据模型 2:数据大屏
     */
    @JsonProperty(value = "use_type")
    private Integer useType;

    /**
     * 数据模型
     */
    @ManyToOne
    @JoinColumn(name = "data_model_id")
    private DataModel dataModel;

    /**
     * 元组件
     */
    @ManyToOne
    @JoinColumn(name = "component_id")
    private ComponentManage componentManage;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public Integer getUseType() {
        return useType;
    }

    public void setUseType(Integer useType) {
        this.useType = useType;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public ComponentManage getComponentManage() {
        return componentManage;
    }

    public void setComponentManage(ComponentManage componentManage) {
        this.componentManage = componentManage;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTextMatch() {
        return this.textMatch;
    }

    public void setTextMatch(String textMatch) {
        this.textMatch = textMatch;
    }

    public String getListMatch() {
        return this.listMatch;
    }

    public void setListMatch(String listMatch) {
        this.listMatch = listMatch;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}