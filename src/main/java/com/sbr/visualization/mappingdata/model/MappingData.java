package com.sbr.visualization.mappingdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbr.visualization.mappingmanage.model.MappingManage;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述：映射数据表模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-15 16:23:30
 */
@Entity
@Table(name = "mapping_data")
public class MappingData {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 原始值
     */
    @NotNull(message = "原始值不能为空")
    @Size(min = 1, max = 256, message = "原始值的长度范围为:1-256")
    @JsonProperty(value = "original_data")
    private String originalData;

    /**
     * 映射值
     */
    @NotNull(message = "映射值不能为空")
    @Size(min = 1, max = 256, message = "映射值的长度范围为:1-256")
    @JsonProperty(value = "mapping_data")
    private String mappingData;

    /**
     * 映射管理表主键
     */
    @ManyToOne
    @JoinColumn(name = "mapping_manage_id")
    private MappingManage mappingManage;

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

    public String getOriginalData() {
        return this.originalData;
    }

    public void setOriginalData(String originalData) {
        this.originalData = originalData;
    }

    public String getMappingData() {
        return this.mappingData;
    }

    public void setMappingData(String mappingData) {
        this.mappingData = mappingData;
    }




}