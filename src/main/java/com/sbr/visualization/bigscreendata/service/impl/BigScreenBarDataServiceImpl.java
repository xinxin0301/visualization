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
 * @ClassName BigScreenHistogramDataServiceImpl
 * @Description TODO 柱状图
 * @Author zxx
 * @Version 1.0
 */
@Service
@HandlerType(valueType = CommonConstant.BAR)//定义注解，配置柱状图自
public class BigScreenBarDataServiceImpl implements IBigScreenDataService {

    @Override
    public InfoJson analysisChartResult(BigScreenData BigScreenData) throws IOException {
        InfoJson infoJson = BigScreenPieDataUtil.buidChartData(BigScreenData);
        return infoJson;
    }


}
