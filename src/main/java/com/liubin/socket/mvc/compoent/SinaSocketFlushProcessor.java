package com.liubin.socket.mvc.compoent;

import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.SinaSocketInfo;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
import com.liubin.socket.utils.SinaSocketUtils;
import com.liubin.socket.utils.SockInfoUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by liubin on 2015/8/14.
 */
@Component
public class SinaSocketFlushProcessor extends TimerTask {

    Logger log = LogUtils.getSysLog();
    Logger errorLog = LogUtils.getErrorLog();
    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    @PostConstruct
    public void init(SocketInfoRedis socketInfoRedis) {
        this.socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    @Override
    public void run() {
        long lastModifiedTime = socketInfoRedis.getLastSinaSocketLastModifiedTime();
        try {
            DateTime now = DateTime.now();
            if (now.getDayOfWeek() > 5) {
                return;
            }
            long nowTimestamp = System.currentTimeMillis();
            long startTime = now.withHourOfDay(15).getMillis();
            if (now.getHourOfDay() == 14 && now.getMinuteOfHour() >= 40) {
                if (nowTimestamp - lastModifiedTime < 1000L*3600) {
                    log.info("the last modified time:{}", lastModifiedTime);
                }
            } else if (nowTimestamp < startTime || lastModifiedTime > startTime) {
                log.info("the last modified time:{}", lastModifiedTime);
                return;
            }
            socketInfoRedis.setLastErTiJiaoModifiedTime(nowTimestamp);
            updateAllSocketsInfo();
            log.info("update sockInfo ok!");
        } catch (Exception e) {
            errorLog.error(e);
            socketInfoRedis.setLastSinaSocketLastModifiedTime(lastModifiedTime);
        }
    }

    public void updateAllSocketsInfo() {
        try {
            int today = Integer.parseInt(DateTime.now().toString(CommonConstants.DAY_FORMATTER));
            List<String> codes = socketInfoRedis.getAllCodeList();
            int reqNum = (codes.size() + CommonConstants.CODES_NUM_PER_REQUEST - 1)/CommonConstants.CODES_NUM_PER_REQUEST;
            for (int i = 0; i < reqNum; i++) {
                int startIdx = i * CommonConstants.CODES_NUM_PER_REQUEST;
                int endIdx = startIdx + CommonConstants.CODES_NUM_PER_REQUEST;
                if (endIdx > codes.size()) {
                    endIdx = codes.size();
                }
                List<SinaSocketInfo> sinaSocketInfoList = SinaSocketUtils.getSinaSockets(codes.subList(startIdx, endIdx));
                for (SinaSocketInfo sinaSocketInfo : sinaSocketInfoList) {
                    // 停牌的股票不处理
                    if (sinaSocketInfo.getCurrentPrice() == 0) {
                        continue;
                    }
                    List<SocketInfoObject> socketInfoObjectList = socketInfoRedis.getSocketInfoObjectListByEndDay(sinaSocketInfo.getCode(), today - 1, 60);
                    SocketInfoObject socketInfoObject = SockInfoUtils.calcSocketInfObject(sinaSocketInfo, socketInfoObjectList);
                    socketInfoRedis.setSocketInfo(sinaSocketInfo.getCode(), socketInfoObject);
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            errorLog.error(e);
        }
    }
}
