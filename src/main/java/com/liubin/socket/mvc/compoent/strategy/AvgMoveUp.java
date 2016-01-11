package com.liubin.socket.mvc.compoent.strategy;

import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.StrategyResult;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by liubin on 2016/1/11.
 * 均线上涨, 5日均线连续5日上涨, 10日均线连续4天上涨, 股价连续10天大于60日均线; 在连续5日内，10日均线斜率 < 5日均线的斜率 < 10日均线的斜率*1.2
 */
@Repository
public class AvgMoveUp implements StrategyInterface {
    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    Logger log = LogUtils.getSysLog();

    @PostConstruct
    public void init() {
        socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    @Override
    public boolean executeCheck(long nowTime) {
        int nowHour = DateTime.now().getHourOfDay();
        if (nowHour <= 9) {
            log.info("nowHour:{}", nowHour);
            return false;
        }
        long lastTime = socketInfoRedis.getLongValue(CommonConstants.LAST_AVG_MOVE_UP_TIME_REDIS_KEY);
        if (nowTime - lastTime < 1000L * 2 * 3600) {
            log.info("lastTime:{}", lastTime);
            return false;
        }
        socketInfoRedis.setLongValue(CommonConstants.LAST_AVG_MOVE_UP_TIME_REDIS_KEY, nowTime);
        return true;
    }

    @Override
    public StrategyResult check(String code, int day, List<SocketInfoObject> socketInfoObjects) {
        StrategyResult strategyResult = new StrategyResult();
        strategyResult.setCode(code);
        strategyResult.setValid(false);
        if (socketInfoObjects.size() < 11) {
            return strategyResult;
        }
        boolean isValid = true;
        for (int i = 0; i < 10; i++) {
            SocketInfoObject socketInfoObject = socketInfoObjects.get(i);
            SocketInfoObject preDaySocketInfoObject = socketInfoObjects.get(i+1);
            if (i < 5 && socketInfoObject.getAvgPrice5() < preDaySocketInfoObject.getAvgPrice5()) {
                isValid = false;
                break;
            }
            if (i < 4 && socketInfoObject.getAvgPrice10() < preDaySocketInfoObject.getAvgPrice10()) {
                isValid = false;
                break;
            }
            if (socketInfoObject.getCurrentPrice() < socketInfoObject.getAvgPrice60()) {
                isValid = false;
                break;
            }
        }
        SocketInfoObject todaySocketInfoObject = socketInfoObjects.get(0);
        SocketInfoObject pre5SocketInfoObject = socketInfoObjects.get(4);
        if (!isValid || todaySocketInfoObject.getCurrentPrice() <= 1 || pre5SocketInfoObject.getCurrentPrice() <= 1) {
            return strategyResult;
        }
        double k5 = 1.0*(todaySocketInfoObject.getAvgPrice5() - pre5SocketInfoObject.getAvgPrice5())/pre5SocketInfoObject.getAvgPrice5();
        double k10 = 1.0*(todaySocketInfoObject.getAvgPrice10() - pre5SocketInfoObject.getAvgPrice10())/pre5SocketInfoObject.getAvgPrice10();
        if (k5 < k10 || k5 > k10*1.2) {
            return strategyResult;
        }
        strategyResult.setDescription("均线上涨");
        return strategyResult;
    }
}
