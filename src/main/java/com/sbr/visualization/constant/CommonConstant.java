package com.sbr.visualization.constant;

import com.sbr.visualization.bigscreendata.service.impl.BigScreenPieDataServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author zxx
 * @Description //TODO 公共常量类
 * @Date 16:12 2020/6/11
 **/
public class CommonConstant {

    /**
     * Mysql
     */
    public static final String MYSQL = "MySQL";

    /**
     * Oracle
     */
    public static final String ORACLE = "Oracle";

    /**
     * Elasticsearch
     */
    public static final String ES = "Elasticsearch";



    /*                                    大屏统计图类型                                         */

    /**
     * 折线图
     */
    public static final String LINE = "line";

    /**
     * 柱状图
     */
    public static final String BAR = "bar";

    /**
     * 饼图
     */
    public static final String PIE = "pie";

    /**
     * 文本
     */
    public static final String TEXTBOX = "textbox";

    /**
     * 雷达图
     */
    public static final String RADAR = "radar";

    /**
     * 桑基图
     */
    public static final String SANKEY = "sankey";

    /**
     * 地图
     */
    public static final String MAP = "map";

    /**
     * 表格
     */
    public static final String TABLE = "table";

    /**
     * 3D-柱图
     */
    public static final String THREE_DIMENSIONAL= "3d-bar";

    /**
     * 堆叠柱图
     */
    public static final String STACKING_BAR = "stackBar";

    /**
     * 堆叠柱图
     */
    public static final String GAUGE = "gauge";

    /**
     * 水球图
     */
    public static final String LIQUID_FILL = "liquid-fill";

    /**
     * 字符云
     */
    public static final String WORDCLOUD = "wordCloud";


    /**
     * 3D飞线地图
     */
    public static final String THREE_MAP_LINE = "3d-map-line";

    /*                               度量參數                                       */
    public static Map<String,Object> measureMap = new HashMap<>();

    static {
        //计数、去重计数、平均值、求和、最大值、最小值
        measureMap.put("COUNT", "COUNT");
        measureMap.put("DISTINCT", "DISTINCT");
        measureMap.put("AVG", "AVG");
        measureMap.put("SUM", "SUM");
        measureMap.put("MAX", "MAX");
        measureMap.put("MIN", "MIN");
    }
}
