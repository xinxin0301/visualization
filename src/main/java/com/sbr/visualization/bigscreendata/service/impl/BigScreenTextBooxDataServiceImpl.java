package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.BigRandarDataUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName BigScreenTextBooxDataServiceImpl
 * @Description TODO 文本
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
@Service
@HandlerType(valueType = CommonConstant.TEXTBOX)//自定义注解，文本
public class BigScreenTextBooxDataServiceImpl implements IBigScreenDataService {

    @Override
    public InfoJson analysisChartResult(BigScreenData BigScreenData) throws IOException {
        InfoJson infoJson = BigRandarDataUtil.buidChartDataValue(BigScreenData);
        return infoJson;
    }

}
