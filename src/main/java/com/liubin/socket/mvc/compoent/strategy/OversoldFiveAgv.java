package com.liubin.socket.mvc.compoent.strategy;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.StrategyResult;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
import com.liubin.socket.utils.MailUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2016/1/8.
 * 5日均线过去15天连续7天以上下跌，并且5日均线下跌超过20%，最新收盘价高于5日均线
 * 该策略主要用于寻找5日均线超跌的股票，买入操作在第二天买入，买入后3日内可以考虑做T操作，如果股价创近期新低则卖出
 */
@Repository
public class OversoldFiveAgv implements StrategyInterface {
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
        int nowHour = DateTime.now().getHourOfDay();
        if (nowHour < 9) {
            log.info("nowHour:{}", nowHour);
            return false;
        }
        long lastTime = socketInfoRedis.getLongValue(CommonConstants.LAST_OVERSOLD_FIVE_AVG_TIME_REDIS_KEY);
        if (nowTime - lastTime < 1000L * 3600 * 2) {
            log.info("lastTime:{}", lastTime);
            return false;
        }
        socketInfoRedis.setLongValue(CommonConstants.LAST_OVERSOLD_FIVE_AVG_TIME_REDIS_KEY, nowTime);
        return true;
    }

    @Override
    public StrategyResult check(String code, int day, List<SocketInfoObject> socketInfoObjects) {
        StrategyResult strategyResult = new StrategyResult();
        strategyResult.setCode(code);
        strategyResult.setValid(false);
        if (socketInfoObjects == null || socketInfoObjects.size() < 15 || socketInfoObjects.get(0).getDay() != day) {
            return strategyResult;
        }
        // 第一步找出5日均线连续下跌超过7天结束时间点
        double lastFiveAvg = Integer.MAX_VALUE;
        double maxN = 0;
        double maxPrice = 0;
        double minPrice = Integer.MAX_VALUE;
        for (int i = 0; i < socketInfoObjects.size() - 7; i++) {
            SocketInfoObject socketInfoObject = socketInfoObjects.get(i);
            double avgPrice5 = socketInfoObject.getAvgPrice5();
            if (avgPrice5 > lastFiveAvg) {
                maxN += 1;
                maxPrice = avgPrice5;
            } else {
                maxN = 1;
                maxPrice = avgPrice5;
                minPrice = avgPrice5;
            }
            if (maxN >= 7) {
                break;
            }
            lastFiveAvg = avgPrice5;
        }
        if (maxN < 7 || minPrice <= 0 || maxPrice <= 0) {
            return strategyResult;
        }
        double diff = (maxPrice - minPrice)/maxPrice;
        if (diff < 3) {
            return strategyResult;
        }
        SocketInfoObject nowSocketInfoObject = socketInfoObjects.get(0);
        if (nowSocketInfoObject.getAvgPrice5() > nowSocketInfoObject.getCurrentPrice()) {
            return strategyResult;
        }
        strategyResult.setValid(true);
        strategyResult.setDescription("超跌反弹");
        return strategyResult;
    }
}
