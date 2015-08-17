package com.liubin.socket.mvc.compoent;

import com.coohua.redis.lib.RedisClient;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * Created by liubin on 2015/8/14.
 */
@Repository
public class SingleInstanceContainer {
    RedisClient redisClient;
    SocketInfoRedis socketInfoRedis;

    @PostConstruct
    public void init() throws Exception {
        redisClient = new RedisClient();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("redis.properties");
        redisClient.init(inputStream);
        inputStream.close();
        socketInfoRedis = new SocketInfoRedis();
        socketInfoRedis.init(redisClient);
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public SocketInfoRedis getSocketInfoRedis() {
        return socketInfoRedis;
    }
}
