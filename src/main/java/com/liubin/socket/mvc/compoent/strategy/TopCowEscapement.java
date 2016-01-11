package com.liubin.socket.mvc.compoent.strategy;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.StrategyResult;
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
public class TopCowEscapement implements StrategyInterface {
    Logger log = LogUtils.getSysLog();
    Logger errorLog = LogUtils.getErrorLog();

    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    @PostConstruct
    public void init() {
        socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    @Override
    public boolean executeCheck(long nowTime) {
        return false;
    }

    @Override
    public StrategyResult check(String code, int day, List<SocketInfoObject> socketInfoObjects) {
        StrategyResult strategyResult = new StrategyResult();
        strategyResult.setValid(false);
        strategyResult.setCode(code);
        if (socketInfoObjects.size() < 30) {
            return strategyResult;
        }
        SocketInfoObject nowSocketInfoObject = socketInfoObjects.get(0);
        SocketInfoObject lastSocketInfoObject = socketInfoObjects.get(1);
        if (nowSocketInfoObject.getDay() != day) {
            return strategyResult;
        }
        double roseValue = SockInfoUtils.calcRoseValue(nowSocketInfoObject);
        if (roseValue < 0.096) {
            return strategyResult;
        }
        double volumeDiff = (nowSocketInfoObject.getVolume() - lastSocketInfoObject.getVolume())*1.0 / lastSocketInfoObject.getVolume();
        if (volumeDiff >= -0.1) {
            return strategyResult;
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
            strategyResult.setValid(true);
            strategyResult.setDescription("越顶擒牛");
            return strategyResult;
        }
        return strategyResult;
    }
}
