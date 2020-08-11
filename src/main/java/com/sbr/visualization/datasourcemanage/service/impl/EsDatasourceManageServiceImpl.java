package com.sbr.visualization.datasourcemanage.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.config.EsConfig;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.datasourcemanage.service.DatabaseConnectService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @ClassName EsDatasourceManageServiceImpl
 * @Description TODO
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
@Service
@Component
@HandlerType(valueType = CommonConstant.ES)//自定义注解，标明es连接
public class EsDatasourceManageServiceImpl implements DatabaseConnectService {

    @Autowired
    private EsConfig esConfig;


    @Override
    public InfoJson verificationDatabaseConnect(DatasourceManage datasourceManage) {
        InfoJson infoJson = new InfoJson();
        //连接ES
        if (datasourceManage.getDatabaseTypeManage() != null) {
            RestHighLevelClient esHighInit = esConfig.getEsHighInit(datasourceManage);
            //连接失败
            if (esHighInit == null) {
                infoJson.setSuccess(false);
                infoJson.setDescription("测试连接失败");
            }
        }
        return infoJson;
    }
}
