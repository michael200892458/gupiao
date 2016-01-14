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
 * Created by liubin on 2016/1/13.
 * 选择最近21个交易日股价由最高价下跌超过60%的股票
 */
@Repository
public class CrashSocketSelector implements StrategyInterface {
    Logger log = LogUtils.getSysLog();

    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    @PostConstruct
    public void init() throws Exception {
        socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    @Override
    public boolean executeCheck(long nowTime) {
        int nowHour = DateTime.now().getHourOfDay();
        if (nowHour <= 9) {
            log.info("nowHour:{}", nowHour);
            return false;
        }
        long lastTime = socketInfoRedis.getLongValue(CommonConstants.LAST_CRASH_SOCKET_SELECTOR_TIME_REDIS_KEY);
        if (nowTime - lastTime < 3600L * 1000) {
            return false;
        }
        return true;
    }

    @Override
    public StrategyResult check(String code, int day, List<SocketInfoObject> socketInfoObjects) {
        StrategyResult strategyResult = new StrategyResult();
        strategyResult.setValid(false);
        strategyResult.setCode(code);
        if (socketInfoObjects == null || socketInfoObjects.size() < 21) {
            return strategyResult;
        }
        int maxPrice = 0;
        int maxIdx = -1;
        int minPrice = Integer.MAX_VALUE;
        int minIdx = -1;
        for (int i = 0; i < 21; i++) {
            SocketInfoObject socketInfoObject = socketInfoObjects.get(i);
            int price = socketInfoObject.getCurrentPrice();
            if (price == 0) {
                continue;
            }
            // 对于高送转的股票, 只从除权后的日期开始算
            if (i > 0) {
                int lastPrice = socketInfoObjects.get(i-1).getCurrentPrice();
                double diff = 1.0 * (price - lastPrice) / price;
                if (diff > 0.2) {
                    break;
                }

            }
            if (price < minPrice) {
                minPrice = price;
                minIdx = i;
                maxIdx = i;
                maxPrice = price;
            } else if (price > maxPrice) {
                maxIdx = i;
                maxPrice = price;
            }
        }
        if (minIdx == -1 || maxIdx == -1) {
            return strategyResult;
        }
        double diff = 1.0*(maxPrice - minPrice)/maxPrice;
        if (diff > 0.45) {
            strategyResult.setValid(true);
            strategyResult.setDescription("[近期严重超跌, 最大跌幅:" + diff + "]");
        } else {
            strategyResult.setDescription("跌幅:" + diff);
        }

        return strategyResult;
    }
}
