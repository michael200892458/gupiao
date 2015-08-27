package com.liubin.socket.mvc.compoent.strategy;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
import com.liubin.socket.utils.MailUtils;
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
public class TopCowEscapement {
    Logger log = LogUtils.getSysLog();
    Logger errorLog = LogUtils.getErrorLog();

    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    @PostConstruct
    public void init() {
        socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    public boolean checkTopCowEscapement(List<SocketInfoObject> socketInfoObjects) {
        if (socketInfoObjects.size() < 30) {
            return false;
        }
        int day = Integer.parseInt(DateTime.now().toString(CommonConstants.DAY_FORMATTER));
        SocketInfoObject nowSocketInfoObject = socketInfoObjects.get(0);
        SocketInfoObject lastSocketInfoObject = socketInfoObjects.get(1);
        if (nowSocketInfoObject.getDay() != day) {
            return false;
        }
        double roseValue = SockInfoUtils.calcRoseValue(nowSocketInfoObject);
        if (roseValue < 0.096) {
            return false;
        }
        double volumeDiff = (nowSocketInfoObject.getVolume() - lastSocketInfoObject.getVolume())*1.0 / lastSocketInfoObject.getVolume();
        if (volumeDiff >= -0.1) {
            return false;
        }
        int n = 0;
        for (int i = 3; i < socketInfoObjects.size(); i++) {
            SocketInfoObject socketInfoObject = socketInfoObjects.get(i);
            roseValue = SockInfoUtils.calcRoseValue(socketInfoObject);
            if (roseValue < 0.096) {
                continue;
            }
            if (socketInfoObject.getTodayMaxPrice() == socketInfoObject.getTodayMinPrice()) {
                n += 1;
            }
        }
        if (n >= 7) {
            return true;
        }
        return false;
    }

    public void run() {
        long lastModifiedTime = 0;
        try {
            lastModifiedTime = socketInfoRedis.getLastTopCowEscapementTime();
            long startTime = DateTime.now().withTimeAtStartOfDay().getMillis();
            if (lastModifiedTime > startTime) {
                log.info("lastModifiedTime:{}", lastModifiedTime);
                return;
            }
            socketInfoRedis.setLastTopCowEscapementTime(System.currentTimeMillis());
            List<String> codes = socketInfoRedis.getAllCodeList();
            List<String> validCodes = new ArrayList<String>();
            int day = Integer.parseInt(DateTime.now().toString(CommonConstants.DAY_FORMATTER));
            for (String code : codes) {
                List<SocketInfoObject> socketInfoObjects = socketInfoRedis.getSocketInfoObjectListByEndDay(code, day, 30);
                if (checkTopCowEscapement(socketInfoObjects)) {
                    validCodes.add(code);
                }
            }
            if (validCodes.size() > 0) {
                String content = JSON.toJSONString(validCodes);
                socketInfoRedis.setTopCowEscapementCodes(content);
                MailUtils.sendMail("TopCowEscapement", content);
                log.info("codes:{}", content);
            } else {
                log.info("validCodes is empty");
            }
        } catch (Exception e) {
            errorLog.error(e);
            socketInfoRedis.setLastTopCowEscapementTime(lastModifiedTime);
        }
    }
}
