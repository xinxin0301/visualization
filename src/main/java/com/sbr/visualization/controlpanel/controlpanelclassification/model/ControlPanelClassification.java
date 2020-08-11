package com.sbr.visualization.controlpanel.controlpanelclassification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述：控制面板分类表模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-23 15:01:08
 */
@Entity
@Table(name = "control_panel_classification")
public class ControlPanelClassification {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 控制面板分类名称
     */
    @NotNull(message = "控制面板分类名称不能为空")
    @Size(min = 1, max = 256, message = "控制面板分类名称的长度范围为:1-256")
    @JsonProperty(value = "classification_name")
    private String classificationName;

    /**
     * 显示顺序
     */
    @JsonProperty(value = "sort_index")
    private Integer sortIndex;

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

    public String getClassificationName() {
        return this.classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }


}