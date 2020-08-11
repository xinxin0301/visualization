package com.sbr.visualization.datasourcemanage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sbr.visualization.databasetype.model.DatabaseTypeManage;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 描述：数据源管理模型
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-11 15:07:07
 */
@Entity
@Table(name = "datasource_manage")
public class DatasourceManage {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "com.sbr.ms.springdata.keygenerator.KeyGenerator")
    @JsonProperty(value = "id")
    private String id;

    /**
     * 数据库类型管理表主键
     */
    @ManyToOne
    @JoinColumn(name = "database_type_manage_id")
    private DatabaseTypeManage databaseTypeManage;

    /**
     * 数据源名称
     */
    @NotNull(message = "数据源名称不能为空")
    @Size(min = 1, max = 256, message = "数据源名称的长度范围为:1-256")
    @JsonProperty(value = "datasource_name")
    private String datasourceName;

    /**
     * 数据源描述
     */
    @Size(min = 0, max = 512, message = "数据源描述的长度范围为:1-512")
    @JsonProperty(value = "datasource_describe")
    private String datasourceDescribe;

    /**
     * 数据库地址
     */
    @NotNull(message = "数据库地址不能为空")
    @Size(min = 1, max = 512, message = "数据库地址的长度范围为:1-512")
    @JsonProperty(value = "database_address")
    private String databaseAddress;

    /**
     * 端口
     */
    @NotNull(message = "端口不能为空")
    @JsonProperty(value = "port")
    private Integer port;

    /**
     * 数据库名/path
     */
    @NotNull(message = "数据库名不能为空")
    @Size(min = 1, max = 512, message = "数据库名的长度范围为:1-512")
    @JsonProperty(value = "database_name")
    private String databaseName;

    /**
     * 用户名
     */
    @Size(min = 0, max = 32, message = "用户名的长度范围为:0-32")
    @JsonProperty(value = "username")
    private String username;

    /**
     * 密码
     */
    @Size(min = 0, max = 32, message = "密码的长度范围为:0-32")
    @JsonProperty(value = "pwd")
    private String pwd;


    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DatabaseTypeManage getDatabaseTypeManage() {
        return databaseTypeManage;
    }

    public void setDatabaseTypeManage(DatabaseTypeManage databaseTypeManage) {
        this.databaseTypeManage = databaseTypeManage;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getDatasourceDescribe() {
        return datasourceDescribe;
    }

    public void setDatasourceDescribe(String datasourceDescribe) {
        this.datasourceDescribe = datasourceDescribe;
    }

    public String getDatabaseAddress() {
        return this.databaseAddress;
    }

    public void setDatabaseAddress(String databaseAddress) {
        this.databaseAddress = databaseAddress;
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }


}