package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.BigScreenPieDataUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName BigScreenLineDataServiceImpl
 * @Description TODO 折线图实现
 * @Author zxx
 * @Version 1.0
 */
@Service
@HandlerType(valueType = CommonConstant.LINE)//自定义注解，配置折线图
public class BigScreenLineDataServiceImpl implements IBigScreenDataService {

    @Override
    public InfoJson analysisChartResult(BigScreenData BigScreenData) throws IOException {
        InfoJson infoJson = BigScreenPieDataUtil.buidChartData(BigScreenData);
        return infoJson;
    }
}
