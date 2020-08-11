package com.sbr.visualization.bigscreendata.service.impl;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.bigscreendata.service.IBigScreenDataService;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.handler.HandlerType;
import com.sbr.visualization.util.BigTableDataUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @ClassName BigScreenTableDataServiceImpl
 * @Description TODO 表格处理
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
@Service
@HandlerType(valueType = CommonConstant.TABLE)//自定义注解，表格
public class BigScreenTableDataServiceImpl implements IBigScreenDataService {

    @Override
    public InfoJson analysisChartResult(BigScreenData BigScreenData) throws IOException {
        InfoJson infoJson = BigTableDataUtil.buidTableDataValue(BigScreenData);
        return infoJson;
    }
}
