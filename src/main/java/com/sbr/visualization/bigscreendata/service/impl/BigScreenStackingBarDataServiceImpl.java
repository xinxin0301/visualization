package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.BigStackingBarDataUtil;
import org.springframework.stereotype.Service;

/**
 * @ClassName BigScreenStackingBarDataServiceImpl
 * @Description TODO 堆叠柱狀圖
 * @Author zxx
 * @Version 1.0
 */
@Service
@HandlerType(valueType = CommonConstant.STACKING_BAR)//自定义注解，堆叠柱状图
public class BigScreenStackingBarDataServiceImpl implements IBigScreenDataService {

    @Override
    public InfoJson analysisChartResult(BigScreenData BigScreenData) throws Exception {
        InfoJson infoJson = BigStackingBarDataUtil.buidStackingBarChartData(BigScreenData);
        return infoJson;
    }
}
