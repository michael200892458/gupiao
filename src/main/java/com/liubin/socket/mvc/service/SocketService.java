package com.liubin.socket.mvc.service;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.RecommendCode;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 2015/8/18.
 */
@Service
public class SocketService {

    Logger log = LogUtils.getSysLog();
    Logger errorLog = LogUtils.getErrorLog();

    SocketInfoRedis socketInfoRedis;
    @Autowired
    SingleInstanceContainer singleInstanceContainer;

    @Autowired
    public void init() throws Exception {
        socketInfoRedis = singleInstanceContainer.getSocketInfoRedis();
    }

    public boolean addSocketCode(String code) {
        try {
            if(!StringUtils.startsWith(code, "sh") && !StringUtils.startsWith(code, "sz")) {
                return false;
            }
            if (StringUtils.length(code) != 8) {
                return false;
            }
            socketInfoRedis.addCode(code);
            return true;
        } catch (Exception e) {
            errorLog.error(e);
        }
        return false;
    }

    public void delSocketCode(String code) {
        try {
            socketInfoRedis.delCode(code);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public List<SocketInfoObject> getSocketInfoObjects(String code) {
        List<SocketInfoObject> socketInfoObjects = new ArrayList<SocketInfoObject>();
        try {
            int day = Integer.parseInt(DateTime.now().toString(CommonConstants.DAY_FORMATTER));
            socketInfoObjects = socketInfoRedis.getSocketInfoObjectListByEndDay(code, day, 60);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return socketInfoObjects;
    }

    public List<RecommendCode> getRecommendCodes() {
        List<RecommendCode> recommendCodes = new ArrayList<RecommendCode>();
        try {
            Map<String, String> retMap = new HashMap<String, String>();
            String erTiJiaoCodesStr = socketInfoRedis.getErTiJiaoCodes();
            mergeRecommendCodes(erTiJiaoCodesStr, "二踢脚 ", retMap);
            String threeLinesOfSunCodesStr = socketInfoRedis.getThreeLinesOfSunCodes();
            mergeRecommendCodes(threeLinesOfSunCodesStr, "一阳穿三线", retMap);
            String topCowEscapementCodesStr = socketInfoRedis.getTopCowEscapementCodes();
            mergeRecommendCodes(topCowEscapementCodesStr, "越顶擒牛", retMap);
            for(Map.Entry<String, String> entry : retMap.entrySet()) {
                RecommendCode recommendCode = new RecommendCode();
                recommendCode.setCode(entry.getKey());
                recommendCode.setReasons(entry.getValue());
                recommendCodes.add(recommendCode);
            }
        } catch (Exception e) {
            errorLog.error(e);
        }
        return recommendCodes;
    }

    public void mergeRecommendCodes(String listStr, String reason, Map<String, String> retMap) {
        Map<String, String> valueMap = new HashMap<String, String>();
        if (StringUtils.isBlank(listStr)) {
            return ;
        }
        List<String> codes = JSON.parseArray(listStr, String.class);
        for (String code : codes) {
            valueMap.put(code, reason);
            String value = retMap.get(code);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(reason);
            if (value != null) {
                stringBuilder.append(value);
            }
            retMap.put(code, stringBuilder.toString());
        }
    }
}
