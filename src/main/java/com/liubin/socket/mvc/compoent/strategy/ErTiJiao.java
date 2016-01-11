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
import org.omg.CORBA.COMM_FAILURE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2015/8/17.
 */
@Component
public class ErTiJiao implements StrategyInterface {
    Logger log = LogUtils.getSysLog();
    Logger errorLog = LogUtils.getErrorLog();

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
        if (nowHour < 9) {
            log.info("nowHour:{}", nowHour);
            return false;
        }
        long lastTime = socketInfoRedis.getLongValue(CommonConstants.LAST_ER_TI_JIAO_MODIFIED_TIME_REDIS_KEY);
        if (nowTime - lastTime < 1000L * 3600 * 2) {
            log.info("lastTime:{}", lastTime);
            return false;
        }
        socketInfoRedis.setLongValue(CommonConstants.LAST_ER_TI_JIAO_MODIFIED_TIME_REDIS_KEY, nowTime);
        return true;
    }

    @Override
    public StrategyResult check(String code, int day, List<SocketInfoObject> socketInfoObjects) {
        StrategyResult strategyResult = new StrategyResult();
        strategyResult.setValid(false);
        strategyResult.setCode(code);
        if (socketInfoObjects.size() < 2) {
            return strategyResult;
        }
        SocketInfoObject nowSocketInfoObject = socketInfoObjects.get(0);
        SocketInfoObject lastSocketInfoObject = socketInfoObjects.get(1);
        if (SockInfoUtils.calcRoseValue(nowSocketInfoObject) < 0.05) {
            return strategyResult;
        }
        if (SockInfoUtils.calcRoseValue(lastSocketInfoObject) > -0.04) {
            return strategyResult;
        }
        // 均线多头排列
        if (nowSocketInfoObject.getCurrentPrice() > nowSocketInfoObject.getAvgPrice5()
                && nowSocketInfoObject.getAvgPrice5() > nowSocketInfoObject.getAvgPrice10()
                && nowSocketInfoObject.getAvgPrice10() > nowSocketInfoObject.getAvgPrice20()
                && nowSocketInfoObject.getAvgPrice20() > nowSocketInfoObject.getAvgPrice30()
                && nowSocketInfoObject.getAvgPrice30() > nowSocketInfoObject.getAvgPrice60()
                && nowSocketInfoObject.getAvgPrice60() > 0) {
            strategyResult.setValid(true);
            strategyResult.setDescription("二踢脚");
            return strategyResult;
        }
        return strategyResult;
    }
}
