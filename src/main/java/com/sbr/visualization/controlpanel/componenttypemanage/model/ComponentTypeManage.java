package com.sbr.visualization.controlpanel.componenttypemanage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 描述：组件类型管理模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-23 15:31:49
 */
@Entity
@Table(name = "component_type_manage")
public class ComponentTypeManage {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 类型名称
     */
    @NotNull(message = "类型名称不能为空")
    @Size(min = 1, max = 256, message = "类型名称的长度范围为:1-256")
    @JsonProperty(value = "component_type_name")
    private String componentTypeName;

    /**
     * 上级类型id
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ComponentTypeManage parent;

    /**
     * 显示顺序
     */
    @JsonProperty(value = "sort_index")
    private Integer sortIndex;

    /**
     * 子节点
     */
    @JsonIgnore
    @Transient
    private List<ComponentTypeManage> typeChildren = new ArrayList<ComponentTypeManage>();

    @JsonIgnore
    @OneToMany(mappedBy = "parent")
    private Set<ComponentTypeManage> children;

    /**
     * 是否允许使用 1:是 2:否
     */
    @JsonProperty(value = "is_visible")
    private Integer isVisible;

    /**
     * 图标
     */
    @JsonProperty(value = "icon")
    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Integer isVisible) {
        this.isVisible = isVisible;
    }

    public Set<ComponentTypeManage> getChildren() {
        return children;
    }

    public void setChildren(Set<ComponentTypeManage> children) {
        this.children = children;
    }

    public ComponentTypeManage getParent() {
        return parent;
    }

    public void setParent(ComponentTypeManage parent) {
        this.parent = parent;
    }

    public List<ComponentTypeManage> getTypeChildren() {
        return typeChildren;
    }

    public void setTypeChildren(List<ComponentTypeManage> typeChildren) {
        this.typeChildren = typeChildren;
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

    public String getComponentTypeName() {
        return this.componentTypeName;
    }

    public void setComponentTypeName(String componentTypeName) {
        this.componentTypeName = componentTypeName;
    }
}