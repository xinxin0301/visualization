package com.sbr.visualization.controlpanel.designmode.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbr.visualization.controlpanel.componenttypemanage.model.ComponentTypeManage;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 描述：设计模型模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-02 10:54:32
 */
@Entity
@Table(name = "design_model")
public class DesignModel {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 父节点ID
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private DesignModel parent;

    /**
     * 名称
     */
    @NotNull(message = "模型名称不能为空")
    @Size(min = 1, max = 512, message = "模型名称的长度范围为:1-512")
    @JsonProperty(value = "model_name")
    private String modelName;

    /**
     * 描述
     */
    @Size(min = 0, max = 1024, message = "描述的长度范围为:1-1024")
    @JsonProperty(value = "model_describe")
    private String modelDescribe;

    /**
     * 模型json
     **/
    @JsonProperty(value = "model_json")
    private String modelJson;

    /**
     * 排序
     */
    @JsonProperty(value = "sort_index")
    private Integer sortIndex;

    /**
     * 是否共享
     */
    @JsonProperty(value = "share_flag")
    private Integer shareFlag;

    /**
     * 类型 1：大屏 2：报表
     */
    @JsonProperty(value = "model_type")
    private Integer modelType;

    /**
     * 模型创建单位id
     */
    @Size(min = 0, max = 32, message = "创建单位ID的长度范围为:0-32")
    @JsonProperty(value = "model_create_unit_id")
    private String modelCreateUnitId;

    /**
     * 模型创建单位
     */
    @Size(min = 0, max = 512, message = "创建单位的长度范围为:0-512")
    @JsonProperty(value = "model_create_unit")
    private String modelCreateUnit;

    /**
     * 模型预览URL
     */
    @Size(min = 0, max = 1024, message = "预览URL的长度范围为:0-1024")
    @JsonProperty(value = "model_preview_url")
    private String modelPreviewUrl;

    /**
     * 创建时间
     */
    @JsonProperty(value = "create_time")
    private Date createTime;

    /**
     * 创建人
     */
    @JsonProperty(value = "create_user")
    private String createUser;

    /**
     * 修改时间
     */
    @JsonProperty(value = "update_time")
    private Date updateTime;

    /**
     * 修改人
     */
    @JsonProperty(value = "update_user")
    private String updateUser;

    /**
     * ID集合
     */
    @JsonIgnore
    @Transient
    private List<String> ids;


    /**
     * 子节点
     */
    @JsonIgnore
    @Transient
    private List<DesignModel> typeChildren = new ArrayList<DesignModel>();

    @JsonIgnore
    @OneToMany(mappedBy = "parent")
    private Set<DesignModel> children;

    public DesignModel getParent() {
        return parent;
    }

    public void setParent(DesignModel parent) {
        this.parent = parent;
    }

    public List<DesignModel> getTypeChildren() {
        return typeChildren;
    }

    public void setTypeChildren(List<DesignModel> typeChildren) {
        this.typeChildren = typeChildren;
    }

    public Set<DesignModel> getChildren() {
        return children;
    }

    public void setChildren(Set<DesignModel> children) {
        this.children = children;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getModelJson() {
        return modelJson;
    }

    public void setModelJson(String modelJson) {
        this.modelJson = modelJson;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public Integer getShareFlag() {
        return shareFlag;
    }

    public void setShareFlag(Integer shareFlag) {
        this.shareFlag = shareFlag;
    }

    public Integer getModelType() {
        return modelType;
    }

    public void setModelType(Integer modelType) {
        this.modelType = modelType;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelDescribe() {
        return this.modelDescribe;
    }

    public void setModelDescribe(String modelDescribe) {
        this.modelDescribe = modelDescribe;
    }

    public String getModelCreateUnitId() {
        return this.modelCreateUnitId;
    }

    public void setModelCreateUnitId(String modelCreateUnitId) {
        this.modelCreateUnitId = modelCreateUnitId;
    }

    public String getModelCreateUnit() {
        return this.modelCreateUnit;
    }

    public void setModelCreateUnit(String modelCreateUnit) {
        this.modelCreateUnit = modelCreateUnit;
    }

    public String getModelPreviewUrl() {
        return this.modelPreviewUrl;
    }

    public void setModelPreviewUrl(String modelPreviewUrl) {
        this.modelPreviewUrl = modelPreviewUrl;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }


}