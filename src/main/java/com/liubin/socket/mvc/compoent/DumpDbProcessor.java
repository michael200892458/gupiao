package com.liubin.socket.mvc.compoent;

import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.utils.LogUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by liubin on 2015/12/23.
 */
@Repository
public class DumpDbProcessor extends TimerTask {

    Logger log = LogUtils.getSysLog();
    Logger dbLog = LogUtils.getDbLog();

    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    @PostConstruct
    public void init() {
        socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    @Override
    public void run() {
        try {
            long timestamp = System.currentTimeMillis();
            long lastDumpTime = socketInfoRedis.getLastDumpDbTime();
            if (timestamp - lastDumpTime < 24 * 3600 * 1000L) {
                log.info("lastDumpDbTime:{}", lastDumpTime);
                return;
            }
            DateTime dateTime = DateTime.now();
            if (dateTime.getHourOfDay() >= 9 && dateTime.getHourOfDay() <= 15) {
                log.info("nowHour is in 9-15");
                return;
            }
            socketInfoRedis.setLastDumpDbTime(timestamp);
            List<String> codes = socketInfoRedis.getAllCodeList();
            for (String code : codes) {
                List<SocketInfoObject> socketInfoObjects = socketInfoRedis.getAllSocketInfoObject(code);
                for (SocketInfoObject socketInfoObject : socketInfoObjects) {
                    dbLog.info("{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}\t{}", code,
                            socketInfoObject.getDay(),
                            socketInfoObject.getOpenPrice(),
                            socketInfoObject.getLastClosePrice(),
                            socketInfoObject.getCurrentPrice(),
                            socketInfoObject.getTodayMaxPrice(),
                            socketInfoObject.getTodayMinPrice(),
                            socketInfoObject.getVolume(),
                            socketInfoObject.getTurnover());
                }
            }
            log.info("dump db ok!");
        } catch (Exception e) {
            log.error("dumpDb error", e);
        }
    }
}
