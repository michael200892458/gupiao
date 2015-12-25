package com.liubin.socket.mvc.compoent;

import com.coohua.redis.lib.RedisClient;
import com.liubin.socket.mvc.compoent.strategy.StrategyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Timer;

/**
 * Created by liubin on 2015/8/14.
 */
@Repository
public class AppContext {

    @Autowired
    SingleInstanceContainer singleInstanceContainer;
    @Autowired
    StrategyManager strategyManager;
    @Autowired
    SinaSocketFlushProcessor sinaSocketFlushProcessor;
    @Autowired
    DumpDbProcessor dumpDbProcessor;
    Timer timer = new Timer();

    @PostConstruct
    public void init() {
        try {
            timer.schedule(sinaSocketFlushProcessor, 0, 1000 * 30);
            timer.schedule(strategyManager, 0, 1000 * 30);
            timer.schedule(dumpDbProcessor, 0, 1000L * 3600);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
