package com.liubin.socket.mvc.compoent.strategy;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SinaSocketFlushProcessor;
import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.SinaSocketInfo;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.StrategyResult;
import com.liubin.socket.utils.*;
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
public class ThreeLinesOfSun implements StrategyInterface {
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
        strategyResult.setCode(code);
        strategyResult.setValid(false);
        if (socketInfoObjects == null || socketInfoObjects.size() < 2) {
            return strategyResult;
        }
        SocketInfoObject nowSocketInfoObject = socketInfoObjects.get(0);
        SocketInfoObject lastSocketInfoObject = socketInfoObjects.get(1);
        if (nowSocketInfoObject.getDay() != day) {
            return strategyResult;
        }
        double roseValue = SockInfoUtils.calcRoseValue(nowSocketInfoObject);
        if (roseValue < 0.03) {
            return strategyResult;
        }
        double volumeDiff = (nowSocketInfoObject.getVolume() - lastSocketInfoObject.getVolume())*1.0 / lastSocketInfoObject.getVolume();
        if (volumeDiff < 0.1) {
            return strategyResult;
        }
        if (roseValue >= 0.03 && roseValue < 0.05) {
            if (volumeDiff < 0.3) {
                return strategyResult;
            }
        } else if (roseValue >= 0.05 && roseValue < 0.07) {
            if (volumeDiff < 0.5) {
                return strategyResult;
            }
        } else if (roseValue >= 0.07) {
            if (volumeDiff < 0.7) {
                return strategyResult;
            }
        }
        if (nowSocketInfoObject.getOpenPrice() < nowSocketInfoObject.getAvgPrice5()
                && nowSocketInfoObject.getOpenPrice() < nowSocketInfoObject.getAvgPrice10()
                && nowSocketInfoObject.getOpenPrice() < nowSocketInfoObject.getAvgPrice30()
                && nowSocketInfoObject.getCurrentPrice() > nowSocketInfoObject.getAvgPrice5()
                && nowSocketInfoObject.getCurrentPrice() > nowSocketInfoObject.getAvgPrice10()
                && nowSocketInfoObject.getCurrentPrice() > nowSocketInfoObject.getAvgPrice30()) {
            strategyResult.setDescription("一阳穿三线");
            strategyResult.setValid(true);
            return strategyResult;
        }
        return strategyResult;
    }
}
