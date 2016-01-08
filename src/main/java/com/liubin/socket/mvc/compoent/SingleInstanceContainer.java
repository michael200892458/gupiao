package com.liubin.socket.mvc.compoent;

import com.coohua.redis.lib.RedisClient;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.utils.LogUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * Created by liubin on 2015/8/14.
 */
@Repository
public class SingleInstanceContainer {
    SocketInfoRedis socketInfoRedis;
    Logger errorLog = LogUtils.getErrorLog();
    Logger log = LogUtils.getSysLog();

    @PostConstruct
    public void init() throws Exception {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("redis.properties");
            socketInfoRedis = new SocketInfoRedis();
            socketInfoRedis.init(inputStream);
            socketInfoRedis.getLastDumpDbTime();
            log.info("init SingleInstanceContainer ok!");
        } catch (Exception e) {
            errorLog.error("init error", e);
            throw e;
        }

    }

    public SocketInfoRedis getSocketInfoRedis() {
        return socketInfoRedis;
    }
}
