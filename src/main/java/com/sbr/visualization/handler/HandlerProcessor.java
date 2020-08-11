package com.sbr.visualization.handler;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName HandlerProcessor
 * @Description TODO 策略核心功能,获取所有策略注解的类型 并将对应的class初始化到HandlerContext中
 * @Author zxx
 * @Version 1.0
 */
@Component
public class HandlerProcessor implements ApplicationContextAware {

    /**
     * 获取所有的策略Beanclass 加入HandlerContext属性中
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //获取所有策略注解的Bean
        Map<String, Object> strategyMap = applicationContext.getBeansWithAnnotation(HandlerType.class);
        strategyMap.forEach((k, v) -> {
            Class<Object> strategyClass = (Class<Object>) v.getClass();
            String valueType = strategyClass.getAnnotation(HandlerType.class).valueType();
            //将class加入map中,type作为key
            HandlerContext.StrategyBeanMap.put(valueType, strategyClass);
        });
    }
}
