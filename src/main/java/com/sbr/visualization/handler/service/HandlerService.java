package com.sbr.visualization.handler.service;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;

/**
 * @Author zxx
 * @Description //TODO 策略接口
 * @Date 9:58 2020/6/16
 **/
public interface HandlerService {
    /**
     * @param datasourceManage 数据源对象
     * @return com.sbr.springboot.json.InfoJson 成功、失败
     * @Author zxx
     * @Description //TODO 验证数据库连接
     * @Date 16:03 2020/6/2
     **/
    InfoJson verificationDatabaseConnect(DatasourceManage datasourceManage);


    /**
     * @param BigScreenData 大屏数据实体
     * @return InfoJson 数据
     * @Author zxx
     * @Description //TODO 大屏策略接口，根据图形类型路由到不同是实现
     * @Date 16:42 2020/6/16
     **/
    InfoJson bigScreenStrategyService(BigScreenData BigScreenData) throws Exception;

}
