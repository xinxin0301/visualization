package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.BigThreeDimensionalDataUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName BigScreenThreeDimensionalDataServiceImpl
 * @Description TODO 3D柱图
 * @Author zxx
 * @Version 1.0
 */
@Service
@HandlerType(valueType = CommonConstant.THREE_DIMENSIONAL)//自定义注解，3D柱图
public class BigScreenThreeDimensionalDataServiceImpl implements IBigScreenDataService {
    @Override
    public InfoJson analysisChartResult(BigScreenData BigScreenData) throws IOException {
        InfoJson infoJson = BigThreeDimensionalDataUtil.buidChartDataValue(BigScreenData);
        return infoJson;
    }
}
