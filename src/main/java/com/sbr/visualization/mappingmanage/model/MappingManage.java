package com.sbr.visualization.mappingmanage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbr.visualization.mappingdata.model.MappingData;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * 描述：数据映射管理模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-15 16:23:39
 */
@Entity
@Table(name = "mapping_manage")
public class MappingManage {

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
    @NotNull(message = "名称不能为空")
    @Size(min = 1, max = 128, message = "名称的长度范围为:1-128")
    @JsonProperty(value = "mapping_name")
    private String mappingName;

    /**
     * 简介
     */
    @Size(min = 0, max = 512, message = "简介的长度范围为:0-512")
    @JsonProperty(value = "description")
    private String description;

    /**
     * 一对多配置
     */
    @JsonProperty(value = "mappingData")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mappingManage")
    @JsonIgnoreProperties("mappingManage")//解决双向映射，数据无线递归循环问题指定的字段不会被序列化，如下则mappingDataSet的mappingManage字段不会被序列化
    private Set<MappingData> mappingDataSet;

    public Set<MappingData> getMappingDataSet() {
        return mappingDataSet;
    }

    public void setMappingDataSet(Set<MappingData> mappingDataSet) {
        this.mappingDataSet = mappingDataSet;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMappingName() {
        return this.mappingName;
    }

    public void setMappingName(String mappingName) {
        this.mappingName = mappingName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}