package com.sbr.visualization.controlpanel.reportformmanage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 描述：报表管理模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-23 09:53:25
 */
@Entity
@Table(name = "report_form_manage")
public class ReportFormManage {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 名称
     */
    @NotNull(message = "报表名称不能为空")
    @Size(min = 1, max = 512, message = "报表名称的长度范围为:1-512")
    @JsonProperty(value = "report_form_name")
    private String reportFormName;

    /**
     * 描述
     */
    @Size(min = 0, max = 1024, message = "描述的长度范围为:0-1024")
    @JsonProperty(value = "report_form_describe")
    private String reportFormDescribe;

    /**
     * 报表json
     */
    @JsonProperty(value = "report_form_json")
    private String reportFormJson;

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
     * 报表模型预览URL
     */
    @NotNull(message = "报表模型预览URL不能为空")
    @Size(min = 1, max = 1024, message = "报表模型预览URL的长度范围为:1-1024")
    @JsonProperty(value = "report_form_preview_url")
    private String reportFormPreviewUrl;

    /**
     * 报表模型创建单位id
     */
    @JsonProperty(value = "report_form_create_unit_id")
    private String reportFormCreateUnitId;

    /**
     * 报表模型创建单位
     */
    @JsonProperty(value = "report_form_create_unit")
    private String reportFormCreateUnit;

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
     * 批量删除ID集合
     */
    @JsonIgnore
    @Transient
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public String getReportFormJson() {
        return reportFormJson;
    }

    public void setReportFormJson(String reportFormJson) {
        this.reportFormJson = reportFormJson;
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

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportFormName() {
        return this.reportFormName;
    }

    public void setReportFormName(String reportFormName) {
        this.reportFormName = reportFormName;
    }

    public String getReportFormDescribe() {
        return this.reportFormDescribe;
    }

    public void setReportFormDescribe(String reportFormDescribe) {
        this.reportFormDescribe = reportFormDescribe;
    }

    public String getReportFormPreviewUrl() {
        return this.reportFormPreviewUrl;
    }

    public void setReportFormPreviewUrl(String reportFormPreviewUrl) {
        this.reportFormPreviewUrl = reportFormPreviewUrl;
    }

    public String getReportFormCreateUnitId() {
        return this.reportFormCreateUnitId;
    }

    public void setReportFormCreateUnitId(String reportFormCreateUnitId) {
        this.reportFormCreateUnitId = reportFormCreateUnitId;
    }

    public String getReportFormCreateUnit() {
        return this.reportFormCreateUnit;
    }

    public void setReportFormCreateUnit(String reportFormCreateUnit) {
        this.reportFormCreateUnit = reportFormCreateUnit;
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