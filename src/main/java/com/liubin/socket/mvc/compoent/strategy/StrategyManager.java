package com.liubin.socket.mvc.compoent.strategy;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.RecommendCode;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.StrategyResult;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by liubin on 2015/8/17.
 */
@Component
public class StrategyManager extends TimerTask {

    Logger log = LogUtils.getSysLog();
    Logger errorLog = LogUtils.getErrorLog();

    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    @Autowired
    ErTiJiao erTiJiao;
    @Autowired
    ThreeLinesOfSun threeLinesOfSun;
    @Autowired
    TopCowEscapement topCowEscapement;
    @Autowired
    OversoldFiveAgv oversoldFiveAgv;

    @Autowired
    AvgMoveUp avgMoveUp;



    @PostConstruct
    public void init() throws Exception {
        socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    @Override
    public void run() {
        try {
            long nowTime = System.currentTimeMillis();
            long lastUpdateTime = socketInfoRedis.getLongValue(CommonConstants.LAST_STRATEGY_UPDATE_TIME_REDIS_KEY);
            if (nowTime - lastUpdateTime < 120 * 1000L) {
                log.info("lastStrategyUpdateTime:{}", lastUpdateTime);
                return;
            }
            List<StrategyInterface> strategyList = new ArrayList<StrategyInterface>();
            if (erTiJiao.executeCheck(nowTime)) {
                strategyList.add(erTiJiao);
            }
            if (oversoldFiveAgv.executeCheck(nowTime)) {
                strategyList.add(oversoldFiveAgv);
            }
            if (avgMoveUp.executeCheck(nowTime)) {
                strategyList.add(avgMoveUp);
            }
            socketInfoRedis.setLongValue(CommonConstants.LAST_STRATEGY_UPDATE_TIME_REDIS_KEY, nowTime);
            List<String> codes = socketInfoRedis.getAllCodeList();
            Map<String, String> recommendMap = new HashMap<String, String>();
            int day = Integer.parseInt(DateTime.now().toString(CommonConstants.DAY_FORMATTER));
            for (String code : codes) {
                List<SocketInfoObject> socketInfoObjects = socketInfoRedis.getSocketInfoObjectListByEndDay(code, day, 60);
                if (socketInfoObjects.size() < 1) {
                    continue;
                }
                if (socketInfoObjects.get(0).getDay() != day) {
                    continue;
                }
                List<String> reasons = new ArrayList<String>();
                for (StrategyInterface strategyInterface : strategyList) {
                    StrategyResult strategyResult = strategyInterface.check(code, day, socketInfoObjects);
                    if (strategyResult.isValid()) {
                        reasons.add(strategyResult.getDescription());
                    }
                }
                if (reasons.size() > 0) {
                    RecommendCode recommendCode = new RecommendCode();
                    recommendCode.setCode(code);
                    recommendCode.setReasons(JSON.toJSONString(reasons));
                    recommendMap.put(code, JSON.toJSONString(JSON.toJSONString(reasons)));
                }
            }
            if (recommendMap.size() > 0) {
                socketInfoRedis.setHashValue(CommonConstants.RECOMMEND_CODES_REDIS_KEY, recommendMap);
            }
        } catch (Exception e) {

        }
    }
}
