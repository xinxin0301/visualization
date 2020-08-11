package com.sbr.visualization.handler;

import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.datasourcemanage.service.DatabaseConnectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName HandlerDatasourceContext
 * @Description TODO 处理器上下文
 * @Author zxx
 * @Version 1.0
 */
@Component
public class HandlerContext {

    @Autowired
    private ApplicationContext applicationContext;

    //存放数据源策略类Bean的map
    public static Map<String, Class<Object>> StrategyBeanMap = new HashMap<>();

    /**
     * @param type 数据源类型
     * @return com.sbr.visualization.datasourcemanage.service.DatabaseConnectService
     * @Author zxx
     * @Description //TODO 获取数据源Ben
     * @Date 9:37 2020/6/16
     **/
    public DatabaseConnectService getDatabaseStrategy(String type) {
        DatabaseConnectService contextBean = null;
        //从容器中获取对应的策略Bean
        Class<Object> objectClass = StrategyBeanMap.get(type);
        if (objectClass != null) {
            contextBean = (DatabaseConnectService) applicationContext.getBean(objectClass);
        }
        return contextBean;
    }

    /**
     * @param type 大屏图类型
     * @return com.sbr.visualization.bigscreendata.service.IBigScreenDataService
     * @Author zxx
     * @Description //TODO 获取大屏数据Ben
     * @Date 16:46 2020/6/16
     **/
    public IBigScreenDataService getBigScreenDataStrategy(String type) {
        IBigScreenDataService contextBean = null;
        //从容器中获取对应的策略Bean
        Class<Object> objectClass = StrategyBeanMap.get(type);
        if (objectClass != null) {
            //获取大屏Ben
            contextBean = (IBigScreenDataService) applicationContext.getBean(objectClass);
        }
        return contextBean;
    }


}
