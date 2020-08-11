package com.sbr.visualization.datamodel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述：数据模型管理模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-12 15:43:26
 */
@Entity
@Table(name = "data_model")
public class DataModel {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 数据源管理表主键
     */
    @ManyToOne
    @JoinColumn(name = "datasource_manage_id")
    private DatasourceManage datasourceManage;

    /**
     * 数据模型名称
     */
    @NotNull(message = "数据模型名称不能为空")
    @Size(min = 1, max = 256, message = "数据模型名称的长度范围为:1-256")
    @JsonProperty(value = "model_name")
    private String modelName;

    /**
     * 数据模型命令/脚本
     */
    @JsonProperty(value = "model_script")
    private String modelScript;

    /**
     * 关联关系
     */
    @JsonProperty(value = "association")
    private String association;

    /**
     * SQL条件
     */
    @JsonProperty(value = "sql_condition")
    private String sqlCondition;

    /**
     * 虚拟字段----字段属性
     */
    @Transient
    @JsonProperty(value = "field")
    private String field;

    /**
     * 虚拟字段----表名
     */
    @Transient
    @JsonProperty(value = "table_name")
    private String tableName;

    /**
     * SQL条件参数
     */
    @JsonProperty(value = "sql_param")
    private String sqlParam;

    /**
     * SQL展示使用
     */
    @JsonProperty(value = "sql_show")
    private String sqlShow;

    /**
     * SQL字符串,条件占位符SQL
     */
    @JsonProperty(value = "sql_str")
    private String sqlStr;

    /**
     * 索引名称
     */
    @JsonProperty(value = "indexes")
    private String indexes;

    public String getIndexes() {
        return indexes;
    }

    public void setIndexes(String indexes) {
        this.indexes = indexes;
    }

    public String getSqlShow() {
        return sqlShow;
    }

    public void setSqlShow(String sqlShow) {
        this.sqlShow = sqlShow;
    }

    public String getSqlParam() {
        return sqlParam;
    }

    public void setSqlParam(String sqlParam) {
        this.sqlParam = sqlParam;
    }

    public String getSqlCondition() {
        return sqlCondition;
    }

    public void setSqlCondition(String sqlCondition) {
        this.sqlCondition = sqlCondition;
    }

    public String getSqlStr() {
        return sqlStr;
    }

    public void setSqlStr(String sqlStr) {
        this.sqlStr = sqlStr;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getModelScript() {
        return modelScript;
    }

    public void setModelScript(String modelScript) {
        this.modelScript = modelScript;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DatasourceManage getDatasourceManage() {
        return datasourceManage;
    }

    public void setDatasourceManage(DatasourceManage datasourceManage) {
        this.datasourceManage = datasourceManage;
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }


}