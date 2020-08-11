package com.sbr.visualization.datasourcemanage.service;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;

public interface DatabaseConnectService{

    /**
     * @param datasourceManage 数据源对象
     * @return com.sbr.springboot.json.InfoJson 成功、失败
     * @Author zxx
     * @Description //TODO 验证数据库连接
     * @Date 16:03 2020/6/2
     **/
    InfoJson verificationDatabaseConnect(DatasourceManage datasourceManage);
}
