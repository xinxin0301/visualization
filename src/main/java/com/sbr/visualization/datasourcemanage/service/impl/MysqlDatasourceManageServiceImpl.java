package com.sbr.visualization.datasourcemanage.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.databasetype.dao.DatabaseTypeManageDAO;
import com.sbr.visualization.databasetype.model.DatabaseTypeManage;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.datasourcemanage.service.DatabaseConnectService;
import com.sbr.visualization.util.DataBaseUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Connection;

/**
 * @ClassName MysqlDatasourceManageServiceImpl
 * @Description TODO
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
@Service
@Component
@HandlerType(valueType = CommonConstant.MYSQL)//自定义注解，标明Mysql连接
public class MysqlDatasourceManageServiceImpl implements DatabaseConnectService {

    @Autowired
    private DatabaseTypeManageDAO databaseTypeManageDAO;

    private Logger logger = LoggerFactory.getLogger(String.valueOf(DatasourceManageServiceImpl.class));

    @Override
    public InfoJson verificationDatabaseConnect(DatasourceManage datasourceManage) {
        InfoJson infoJson = new InfoJson();
        if (datasourceManage.getDatabaseTypeManage() != null && datasourceManage.getDatabaseTypeManage()!=null && StringUtils.isNotEmpty(datasourceManage.getDatabaseTypeManage().getId())) {
            //获取数据库类型
            DatabaseTypeManage databaseTypeManage = databaseTypeManageDAO.findOne(datasourceManage.getDatabaseTypeManage().getId());
            datasourceManage.setDatabaseTypeManage(databaseTypeManage);
            Connection connection = null;
            try {
                connection = DataBaseUtil.databaseConnect(datasourceManage);
                if (connection == null) {
                    infoJson.setSuccess(false);
                    infoJson.setDescription("测试连接失败");
                }
            } catch (Exception e) {
                logger.error("测试连接数据源错误:", e);
                infoJson.setSuccess(false);
                infoJson.setDescription("测试连接数据源失败");
            }

        }
        return infoJson;
    }
}
