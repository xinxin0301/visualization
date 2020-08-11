package com.sbr.visualization.handler.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.databasetype.dao.DatabaseTypeManageDAO;
import com.sbr.visualization.databasetype.model.DatabaseTypeManage;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.datasourcemanage.service.DatabaseConnectService;
import com.sbr.visualization.handler.HandlerContext;
import com.sbr.visualization.handler.service.HandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName HandlerDatasourceServiceImpl
 * @Description TODO 策略模式实现类
 * @Author zxx
 * @Version 1.0
 */
@Component
public class HandlerServiceImpl implements HandlerService {

    @Autowired
    private HandlerContext handlerContext;

    @Autowired
    private DatabaseTypeManageDAO databaseTypeManageDAO;

    /**
     * @param datasourceManage 数据源对象
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 调用数据源连接接口
     * @Date 16:48 2020/6/16
     **/
    @Override
    public InfoJson verificationDatabaseConnect(DatasourceManage datasourceManage) {
        //获取数据库类型
        DatabaseTypeManage databaseTypeManage = databaseTypeManageDAO.findOne(datasourceManage.getDatabaseTypeManage().getId());
        //获取service
        DatabaseConnectService databaseStrategy = handlerContext.getDatabaseStrategy(databaseTypeManage.getDatabaseTypeName());
        //调用指定的接口
        InfoJson infoJson = databaseStrategy.verificationDatabaseConnect(datasourceManage);
        return infoJson;
    }

    /**
     * @param BigScreenData 大屏对象
     * @return InfoJson 返回結果
     * @Author zxx
     * @Description //TODO 调用大屏可视化分析接口
     * @Date 16:48 2020/6/16
     **/
    @Override
    public InfoJson bigScreenStrategyService(BigScreenData BigScreenData) throws Exception {
        //根据ChartType找到具体的实现
        IBigScreenDataService bigScreenDataStrategy = handlerContext.getBigScreenDataStrategy(BigScreenData.getChartType());
        return bigScreenDataStrategy.analysisChartResult(BigScreenData);
    }
}
