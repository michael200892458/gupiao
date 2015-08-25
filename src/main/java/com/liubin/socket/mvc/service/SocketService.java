package com.liubin.socket.mvc.service;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SingleInstanceContainer;
import com.liubin.socket.mvc.compoent.redis.SocketInfoRedis;
import com.liubin.socket.pojo.RecommendCode;
import com.liubin.socket.pojo.SocketCode;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
import com.sun.org.apache.bcel.internal.classfile.Code;
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

    public boolean addSelectedCode(String code) {
        try {
            if (!checkCode(code)) {
                return false;
            }
            if (!socketInfoRedis.isExist(code)) {
                return false;
            }
            socketInfoRedis.addSelectedCode(code);
            return true;
        } catch (Exception e) {
            errorLog.error(e);
        }
        return false;
    }

    public boolean delSelectedCode(String code) {
        try {
            if (!checkCode(code)) {
                return false;
            }
            socketInfoRedis.delSelectedCode(code);
            return true;
        } catch (Exception e) {
            errorLog.error(e);
        }
        return true;
    }

    public boolean clearOutOfDateCode(String code) {
        try {
            if (!checkCode(code)) {
                return false;
            }
            socketInfoRedis.clearOutOfDateSocketInfoObject(code);
            return true;
        } catch (Exception e) {
            errorLog.error(e);
        }
        return false;
    }

    public boolean clearInvalidSocketInfo(String code) {
        try {
            if (!checkCode(code)) {
                return false;
            }
            socketInfoRedis.clearInvalidSocketInfoObject(code);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return false;
    }

    public void calcAvgValue(String code) {
        try {
            if (!checkCode(code)) {
                return;
            }
            List<SocketInfoObject> socketInfoObjectList = socketInfoRedis.getAllSocketInfoObject(code);
            if (socketInfoObjectList.size() < 5) {
                return;
            }
            int n = socketInfoObjectList.size();
            SocketInfoObject[] socketInfoObjects = new SocketInfoObject[n];
            int[] sumPrice = new int[n];
            int sumValue = 0;
            for (int i = n - 1; i >= 0; i--) {
                socketInfoObjects[ n - i - 1] = socketInfoObjectList.get(i);
                sumValue += socketInfoObjectList.get(i).getCurrentPrice();
                sumPrice[n - i - 1] = sumValue;
            }
            for (int i = 5 ; i < n; i++) {
                int sum5Value = sumPrice[i] - sumPrice[i - 5];
                int sum10Value = 0;
                int sum20Value = 0;
                int sum30Value = 0;
                int sum60Value = 0;
                if (i >= 10) {
                    sum10Value = sumPrice[i] - sumPrice[i - 10];
                }
                if (i >= 20) {
                    sum20Value = sumPrice[i] - sumPrice[i - 20];
                }
                if (i >= 30) {
                    sum30Value = sumPrice[i] - sumPrice[i - 30];
                }
                if (i >= 60) {
                    sum60Value = sumPrice[i] - sumPrice[i - 60];
                }
                SocketInfoObject socketInfoObject = socketInfoObjects[i];
                socketInfoObject.setAvgPrice5(sum5Value/5);
                socketInfoObject.setAvgPrice10(sum10Value/10);
                socketInfoObject.setAvgPrice20(sum20Value/20);
                socketInfoObject.setAvgPrice30(sum30Value/30);
                socketInfoObject.setAvgPrice60(sum60Value/60);
                socketInfoRedis.setSocketInfo(code, socketInfoObject);
            }
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public List<SocketCode> getSelectedCodes() {
        List<SocketCode> socketCodes = new ArrayList<SocketCode>();
        try {
            List<String> codes = socketInfoRedis.getSelectedCodeList();
            for (String code : codes) {
                SocketCode socketCode = socketInfoRedis.getSocketCode(code);
                if (socketCode != null) {
                    socketCodes.add(socketCode);
                }
            }
        } catch (Exception e) {
            errorLog.error(e);
        }
        return socketCodes;
    }

    public boolean addSocketCode(String code) {
        try {
            if(!checkCode(code)) {
                return false;
            }
            socketInfoRedis.addCode(code, "1");
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
            socketInfoObjects = socketInfoRedis.getSocketInfoObjectListByEndDay(code, day, 90);
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

    protected boolean checkCode(String code) {
        if(!StringUtils.startsWith(code, "sh") && !StringUtils.startsWith(code, "sz")) {
            return false;
        }
        if (StringUtils.length(code) != 8) {
            return false;
        }
        return true;
    }
}
