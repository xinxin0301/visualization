package com.sbr.visualization.handler;

import java.lang.annotation.*;

@Target(ElementType.TYPE)  //作用在类上
@Retention(RetentionPolicy.RUNTIME)//运行时
@Documented//API
@Inherited //子类可以继承此注解
public @interface HandlerType {
    /**
     * 策略类型
     * @return
     */
    String valueType();
}
