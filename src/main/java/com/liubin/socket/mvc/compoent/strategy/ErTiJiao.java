package com.liubin.socket.mvc.compoent.strategy;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
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
public class ErTiJiao {
    Logger log = LogUtils.getSysLog();
    Logger errorLog = LogUtils.getErrorLog();

    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    @PostConstruct
    public void init() throws Exception {
        socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    public boolean checkErTiJiao(List<SocketInfoObject> socketInfoObjects) {
        if (socketInfoObjects.size() < 2) {
            return false;
        }
        SocketInfoObject nowSocketInfoObject = socketInfoObjects.get(0);
        SocketInfoObject lastSocketInfoObject = socketInfoObjects.get(1);
        if (SockInfoUtils.calcRoseValue(nowSocketInfoObject) < 0.05) {
            return false;
        }
        if (SockInfoUtils.calcRoseValue(lastSocketInfoObject) > -0.04) {
            return false;
        }
        // 均线多头排列
        if (nowSocketInfoObject.getCurrentPrice() > nowSocketInfoObject.getAvgPrice5()
                && nowSocketInfoObject.getAvgPrice5() > nowSocketInfoObject.getAvgPrice10()
                && nowSocketInfoObject.getAvgPrice10() > nowSocketInfoObject.getAvgPrice20()
                && nowSocketInfoObject.getAvgPrice20() > nowSocketInfoObject.getAvgPrice30()
                && nowSocketInfoObject.getAvgPrice30() > nowSocketInfoObject.getAvgPrice60()) {
            return true;
        }
        return false;
    }

    public void run() {
        long lastModifiedTime = socketInfoRedis.getLastErTiJiaoModifiedTime();
        try {
            long startTime = DateTime.now().withTimeAtStartOfDay().getMillis();
            if (lastModifiedTime > startTime) {
                log.info("lastModifiedTime:{}", lastModifiedTime);
                return;
            }
            socketInfoRedis.setLastErTiJiaoModifiedTime(System.currentTimeMillis());
            List<String> codes = socketInfoRedis.getAllCodeList();
            List<String> validCodes = new ArrayList<String>();
            int day = Integer.parseInt(DateTime.now().toString(CommonConstants.DAY_FORMATTER));
            for (String code : codes) {
                List<SocketInfoObject> socketInfoObjects = socketInfoRedis.getSocketInfoObjectListByEndDay(code, day, 2);
                if (checkErTiJiao(socketInfoObjects)) {
                    validCodes.add(code);
                }
            }
            String content = JSON.toJSONString(validCodes);
            socketInfoRedis.setErTiJiaoCodes(content);
            log.info("codes:{}", content);
        } catch (Exception e) {
            socketInfoRedis.setLastErTiJiaoModifiedTime(lastModifiedTime);
            errorLog.error(e);
        }
    }
}
