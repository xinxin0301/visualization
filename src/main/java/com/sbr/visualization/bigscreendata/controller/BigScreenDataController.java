package com.sbr.visualization.bigscreendata.controller;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.handler.service.HandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @ClassName BigScreenData
 * @Description TODO 大屏数据统计
 * @Author zxx
 * @Version 1.0
 */
@RestController
@RequestMapping(value = "/visualization/api")
public class BigScreenDataController {

    @Autowired//注入策略接口，然后去分配具体的实现
    private HandlerService handlerService;


    /**
     * @Author zxx
     * @Description //TODO 可视化分析图
     * @Date 9:21 2020/6/17
     * @param bigScreenData 大屏参数对象
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     **/
    @PostMapping(value = "/v1/chart-datas")
    public InfoJson visualizedChartsData(@Valid @RequestBody BigScreenData bigScreenData) throws Exception {
        InfoJson infoJson = handlerService.bigScreenStrategyService(bigScreenData);
        return infoJson;
    }


}
