package com.liubin.socket.mvc.compoent;

import com.coohua.redis.lib.RedisClient;
import com.liubin.socket.mvc.compoent.strategy.StrategyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Timer;

/**
 * Created by liubin on 2015/8/14.
 */
@Component
public class AppContext {

    @Autowired
    SingleInstanceContainer singleInstanceContainer;
    @Autowired
    StrategyManager strategyManager;
    @Autowired
    SinaSocketFlushProcessor sinaSocketFlushProcessor;
    Timer timer = new Timer();

    @PostConstruct
    public void init() {
        timer.schedule(sinaSocketFlushProcessor, 0, 1000*60);
        timer.schedule(strategyManager, 0, 1000*60);
    }

}
