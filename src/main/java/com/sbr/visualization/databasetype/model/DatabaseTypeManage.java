package com.sbr.visualization.databasetype.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述：数据库类型管理模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-11 14:48:01
 */
@Entity
@Table(name = "database_type_manage")
public class DatabaseTypeManage {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 数据库类型名称 如MySQL，SQLserver，Oracle，db2
     */
    @NotNull(message = "数据库类型名称不能为空")
    @Size(min = 1, max = 256, message = "数据库类型名称的长度范围为:1-256")
    @JsonProperty(value = "database_type_name")
    private String databaseTypeName;

    /**
     * 数据库类型 如1：关系型数据，2：非关系型数据库
     */
    @NotNull(message = "数据库类型不能为空")
    @JsonProperty(value = "database_type")
    private Integer databaseType;

    public Integer getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(Integer databaseType) {
        this.databaseType = databaseType;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatabaseTypeName() {
        return this.databaseTypeName;
    }

    public void setDatabaseTypeName(String databaseTypeName) {
        this.databaseTypeName = databaseTypeName;
    }


}