package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.BigMapDataUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName BigScreenMapDataServiceImpl
 * @Description TODO 地图
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
@Service
@HandlerType(valueType = CommonConstant.MAP)//自定义注解，地图
public class BigScreenMapDataServiceImpl implements IBigScreenDataService {

    @Override
    public InfoJson analysisChartResult(BigScreenData BigScreenData) throws IOException {
        InfoJson infoJson = BigMapDataUtil.buidMapChartData(BigScreenData);
        return infoJson;
    }
}
