package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.springboot.rest.exception.RestIllegalArgumentException;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.BigScreenPieDataUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName BigScreenPieDataServiceImpl
 * @Description TODO 饼图
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
@Service
@HandlerType(valueType = CommonConstant.PIE)//自定义注解，配置饼图
public class BigScreenPieDataServiceImpl implements IBigScreenDataService {

    @Override
    public InfoJson analysisChartResult(BigScreenData BigScreenData) throws IOException {
        if (BigScreenData.getY().size()> 1) {
            throw new RestIllegalArgumentException("度量参数只能有一个！");
        }
        InfoJson infoJson = BigScreenPieDataUtil.buidChartData(BigScreenData);
        return infoJson;
    }
}

