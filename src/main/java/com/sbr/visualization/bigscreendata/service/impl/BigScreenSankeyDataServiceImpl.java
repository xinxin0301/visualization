package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.BigSankeyDataUtil;
import org.springframework.stereotype.Service;

/**
 * @ClassName BigScreenSankeyDataServiceImpl
 * @Description TODO 桑基图
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
@Service
@HandlerType(valueType = CommonConstant.SANKEY)//自定义注解，桑基图
public class BigScreenSankeyDataServiceImpl implements IBigScreenDataService {

    @Override
    public InfoJson analysisChartResult(BigScreenData BigScreenData) throws Exception {
        InfoJson infoJson = BigSankeyDataUtil.buidSankeyChartData(BigScreenData);
        return infoJson;
    }
}
