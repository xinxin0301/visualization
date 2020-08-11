package com.sbr.visualization.datamodelattribute.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.filter.model.Filter;
import com.sbr.visualization.mappingmanage.model.MappingManage;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述：数据模型属性表
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-03 11:05:32
 */
@Entity
@Table(name = "data_model_attribute")
public class DataModelAttribute {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 数据模型表主键
     */
    @ManyToOne
    @JoinColumn(name = "data_model_id")
    private DataModel dataModel;

    /**
     * 数据映射管理主键
     */
    @OneToOne
    @NotFound(action= NotFoundAction.IGNORE)//如果该关联没有值不会报错，返回null
    @JoinColumn(name = "mapping_manage_id")
    private MappingManage mappingManage;

    /**
     * 字段名称
     */
    @NotNull(message = "字段名称不能为空")
    @Size(min = 1, max = 32, message = "字段名称的长度范围为:1-32")
    @JsonProperty(value = "fields_name")
    private String fieldsName;

    /**
     * 字段类型
     */
    @NotNull(message = "字段类型不能为空")
    @Size(min = 0, max = 32, message = "字段类型的长度范围为:0-32")
    @JsonProperty(value = "fields_type")
    private String fieldsType;

    /**
     * 字段别名
     */
    //@NotNull(message = "字段别名不能为空")
    @Size(min = 0, max = 128, message = "字段别名的长度范围为:0-32")
    @JsonProperty(value = "fields_alias")
    private String fieldsAlias;


    /**
     * 表名
     */
    @NotNull(message = "表名不能为空")
    @Size(min = 1, max = 128, message = "表名的长度范围为:0-32")
    @JsonProperty(value = "table_name")
    private String tableName;


    /**
     * 表名别名
     */
    //@NotNull(message = "表名別名不能为空")
    @Size(min = 0, max = 512, message = "表名別名的长度范围为:0-32")
    @JsonProperty(value = "table_name_alias")
    private String tableNameAlias;


    /**
     * 是否隐藏 1:隐藏 2:不隐藏
     */
    @JsonProperty(value = "is_hide")
    private Integer isHide;


    /**
     * 类型 1:维度 2:度量
     */
    @JsonProperty(value = "model_type")
    private Integer modelType;


    /**
     * 排序
     */
    @JsonProperty(value = "sort_index")
    private Integer sortIndex;

    /**
     * 随机别名
     */
    @JsonProperty(value = "randomAlias")
    private String randomAlias;

    /**
     * 聚合方式
     */
    @JsonProperty(value = "polymerization_type")
    private String polymerizationType;

    /**
     * 虚拟字段----条件过滤
     */
    @Transient
    private Filter filter;

    /**
     * 图表类型
     */
//    @NotNull(message = "图表类型不能为空")
    @Size(min = 0, max = 32, message = "图表类型的长度范围为:0-32")
    @JsonProperty(value = "chart_type")
    private String chartType;

    /**
     * 是否是新建计算1:维度2:度量
     */
    @JsonProperty(value = "is_new_calculation")
    private Integer isNewCalculation;

    /**
     * 新建计算维度表达式
     */
    private String expression;

    public Integer getIsNewCalculation() {
        return isNewCalculation;
    }

    public void setIsNewCalculation(Integer isNewCalculation) {
        this.isNewCalculation = isNewCalculation;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getPolymerizationType() {
        return polymerizationType;
    }

    public void setPolymerizationType(String polymerizationType) {
        this.polymerizationType = polymerizationType;
    }

    public String getRandomAlias() {
        return randomAlias;
    }

    public void setRandomAlias(String randomAlias) {
        this.randomAlias = randomAlias;
    }

    public MappingManage getMappingManage() {
        return mappingManage;
    }

    public void setMappingManage(MappingManage mappingManage) {
        this.mappingManage = mappingManage;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public String getFieldsName() {
        return fieldsName;
    }

    public void setFieldsName(String fieldsName) {
        this.fieldsName = fieldsName;
    }

    public String getFieldsType() {
        return fieldsType;
    }

    public void setFieldsType(String fieldsType) {
        this.fieldsType = fieldsType;
    }

    public String getFieldsAlias() {
        return fieldsAlias;
    }

    public void setFieldsAlias(String fieldsAlias) {
        this.fieldsAlias = fieldsAlias;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableNameAlias() {
        return tableNameAlias;
    }

    public void setTableNameAlias(String tableNameAlias) {
        this.tableNameAlias = tableNameAlias;
    }

    public Integer getIsHide() {
        return isHide;
    }

    public void setIsHide(Integer isHide) {
        this.isHide = isHide;
    }

    public Integer getModelType() {
        return modelType;
    }

    public void setModelType(Integer modelType) {
        this.modelType = modelType;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }
}