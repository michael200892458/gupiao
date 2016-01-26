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
 * Created by liubin on 2016/1/21.
 * 5日均线连续下跌21天
 */
@Repository
public class ContinueDeclineSocket implements StrategyInterface {
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
        long lastTime = socketInfoRedis.getLongValue(CommonConstants.LAST_CONTINUE_DECLINE_TIME_REDIS_KEY);
        if (nowTime - lastTime < 1000L * 2 * 3600) {
            log.info("lastTime:{}", lastTime);
            return false;
        }
        socketInfoRedis.setLongValue(CommonConstants.LAST_CONTINUE_DECLINE_TIME_REDIS_KEY, nowTime);
        return true;
    }

    @Override
    public StrategyResult check(String code, int day, List<SocketInfoObject> socketInfoObjects) {
        StrategyResult strategyResult = new StrategyResult();
        strategyResult.setCode(code);
        strategyResult.setValid(false);
        if (socketInfoObjects.size() < 21) {
            return strategyResult;
        }
        int lastAvg5Price = 0;
        int num = 0;
        for (int i = 0; i < socketInfoObjects.size(); i++) {
            SocketInfoObject socketInfoObject = socketInfoObjects.get(i);
            if (socketInfoObject.getAvgPrice5() > lastAvg5Price) {
                num += 1;
            } else {
                break;
            }
            lastAvg5Price = socketInfoObject.getAvgPrice5();
        }
        if (num < 21) {
            return strategyResult;
        }
        strategyResult.setValid(true);
        strategyResult.setDescription("股票5日均线连续" + num + "天下跌");
        return null;
    }
}
