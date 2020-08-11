package com.sbr.visualization.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @ClassName Util
 * @Description TODO
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
public class Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    /**
     * @param number
     * @return java.lang.String
     * @Author zxx
     * @Description //TODO 数据添加千分位 1000 1,000
     * @Date 17:32 2020/6/6
     **/
    public static String getThousandsStr(int number) {
        String str = DecimalFormat.getNumberInstance().format(number);//该方法精度只保留小数点后三位
        return str;
    }

    /**
     * @param length 字符串长度
     * @return java.lang.String
     * @Author zxx
     * @Description //TODO 获取随机字符串
     * @Date 15:38 2020/6/28
     **/
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return "SG" + sb.toString();
    }

    /**
     * @Author zxx
     * @Description //TODO 判断字符串是否为数字
     * @Date 11:09 2020/8/10
     * @param str
     * @return boolean
     **/
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
