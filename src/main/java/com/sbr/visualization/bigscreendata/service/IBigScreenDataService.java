package com.sbr.visualization.bigscreendata.service;

import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.bigscreendata.model.BigScreenData;

public interface IBigScreenDataService {

    /**
     * @param BigScreenData 大屏实体类
     * @return InfoJson
     * @Author zxx
     * @Description //TODO 统计分析图
     * @Date 16:48 2020/6/16
     **/
    InfoJson analysisChartResult(BigScreenData BigScreenData) throws Exception;
}
