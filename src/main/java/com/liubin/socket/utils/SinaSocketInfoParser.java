package com.liubin.socket.utils;

import com.liubin.socket.pojo.SinaSocketInfo;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2015/8/14.
 */
public class SinaSocketInfoParser {
    public static List<SinaSocketInfo> parseSinaSocketInfoList(String content) {
        List<SinaSocketInfo> sinaSocketInfoList = new ArrayList<SinaSocketInfo>();
        if (StringUtils.isBlank(content)) {
            return sinaSocketInfoList;
        }
        try {
            String[] lines = StringUtils.split(content, "\n");
            for (String line : lines) {
                SinaSocketInfo sinaSocketInfo = parseSinaSocketInfo(line);
                if (sinaSocketInfo != null) {
                    sinaSocketInfoList.add(sinaSocketInfo);
                }
            }
            return sinaSocketInfoList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SinaSocketInfo parseSinaSocketInfo(String line) {
        if (StringUtils.isBlank(line)) {
            return null;
        }
        try {
            String[] tokens = StringUtils.split(line, "=");
            if (tokens.length != 2) {
                return null;
            }
            DateTime now = DateTime.now();
            int day = Integer.parseInt(now.toString(CommonConstants.DAY_FORMATTER));
            String code = tokens[0].split("_")[2];
            String value = StringUtils.strip(tokens[1], "\"");
            String valueStr = value.split("\"")[0];
            String[] subTokens = StringUtils.split(valueStr, ",");
            String name = StringUtils.trim(subTokens[0]);
            double openPrice = Double.parseDouble(StringUtils.trim(subTokens[1]));
            double lastClosePrice = Double.parseDouble(StringUtils.trim(subTokens[2]));
            double currentPrice = Double.parseDouble(StringUtils.trim(subTokens[3]));
            double todayMaxPrice = Double.parseDouble(StringUtils.trim(subTokens[4]));
            double todayMinPrice = Double.parseDouble(StringUtils.trim(subTokens[5]));
            int volume = Integer.parseInt(StringUtils.trim(subTokens[8]));
            double turnover = Double.parseDouble(StringUtils.trim(subTokens[9]));
            SinaSocketInfo sinaSocketInfo = new SinaSocketInfo();
            sinaSocketInfo.setCode(code);
            sinaSocketInfo.setName(name);
            sinaSocketInfo.setOpenPrice((int)(100*openPrice));
            sinaSocketInfo.setLastClosePrice((int)(100*lastClosePrice));
            sinaSocketInfo.setCurrentPrice((int)(100*currentPrice));
            sinaSocketInfo.setTodayMaxPrice((int)(100*todayMaxPrice));
            sinaSocketInfo.setTodayMinPrice((int)(100*todayMinPrice));
            sinaSocketInfo.setVolume(volume);
            sinaSocketInfo.setTurnover((int)(turnover/10000));
            sinaSocketInfo.setDay(day);
            if (openPrice == 0) {
                return null;
            }
            return sinaSocketInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
