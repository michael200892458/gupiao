package com.liubin.socket.mvc.compoent.strategy;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SinaSocketFlushProcessor;
import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.SinaSocketInfo;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
import com.liubin.socket.utils.SinaSocketUtils;
import com.liubin.socket.utils.SockInfoUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2015/8/17.
 */
@Component
public class ThreeLinesOfSun {
    Logger log = LogUtils.getSysLog();
    Logger errorLog = LogUtils.getErrorLog();

    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    @PostConstruct
    public void init() {
        socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    public boolean checkThreeLinesOfSun(List<SocketInfoObject> socketInfoObjects) {
        if (socketInfoObjects.size() < 2) {
            return false;
        }
        int day = Integer.parseInt(DateTime.now().toString(CommonConstants.DAY_FORMATTER));
        SocketInfoObject nowSocketInfoObject = socketInfoObjects.get(0);
        SocketInfoObject lastSocketInfoObject = socketInfoObjects.get(1);
        if (nowSocketInfoObject.getDay() != day) {
            return false;
        }
        double roseValue = SockInfoUtils.calcRoseValue(nowSocketInfoObject);
        if (roseValue < 0.03) {
            return false;
        }
        double volumeDiff = (nowSocketInfoObject.getVolume() - lastSocketInfoObject.getVolume())*1.0 / lastSocketInfoObject.getVolume();
        if (volumeDiff < 0.1) {
            return false;
        }
        if (nowSocketInfoObject.getOpenPrice() < nowSocketInfoObject.getAvgPrice5()
                && nowSocketInfoObject.getOpenPrice() < nowSocketInfoObject.getAvgPrice10()
                && nowSocketInfoObject.getOpenPrice() < nowSocketInfoObject.getAvgPrice30()
                && nowSocketInfoObject.getCurrentPrice() > nowSocketInfoObject.getAvgPrice5()
                && nowSocketInfoObject.getCurrentPrice() > nowSocketInfoObject.getAvgPrice10()
                && nowSocketInfoObject.getCurrentPrice() > nowSocketInfoObject.getAvgPrice30()) {
            return true;
        }
        return false;
    }

    public void run() {
        long lastModifiedTime = 0;
        try {
            lastModifiedTime = socketInfoRedis.getLastThreeLinesOfSunTime();
            long startTime = DateTime.now().withTimeAtStartOfDay().getMillis();
            if (lastModifiedTime > startTime) {
                log.info("lastModifiedTime:{}", lastModifiedTime);
                return;
            }
            DateTime now = DateTime.now();
            if (now.getHourOfDay() != 14 || now.getMinuteOfHour() < 48) {
                return;
            }
            socketInfoRedis.setLastThreeLinesOfSunTime(System.currentTimeMillis());
            List<String> codes = socketInfoRedis.getAllCodeList();
            List<String> validCodes = new ArrayList<String>();
            int day = Integer.parseInt(DateTime.now().toString(CommonConstants.DAY_FORMATTER));
            for (String code : codes) {
                List<SocketInfoObject> socketInfoObjects = socketInfoRedis.getSocketInfoObjectListByEndDay(code, day, 3);
                if (checkThreeLinesOfSun(socketInfoObjects)) {
                    validCodes.add(code);
                }
            }
            String content = JSON.toJSONString(validCodes);
            socketInfoRedis.setThreeLinesOfSunCodes(content);
            log.info("codes:{}", content);
        } catch (Exception e) {
            errorLog.error(e);
            socketInfoRedis.setLastThreeLinesOfSunTime(lastModifiedTime);
        }
    }
}
