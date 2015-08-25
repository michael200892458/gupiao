package com.liubin.socket.mvc.compoent.redis;

import com.alibaba.fastjson.JSON;
import com.coohua.redis.lib.RedisClient;
import com.liubin.socket.pojo.SocketCode;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.proto.SocketInfo;
import com.liubin.socket.utils.CommonConstants;
import com.liubin.socket.utils.LogUtils;
import com.liubin.socket.utils.ProtoUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by liubin on 2015/8/14.
 */
public class SocketInfoRedis {

    Logger errorLog = LogUtils.getErrorLog();

    RedisClient redisClient;

    public void init(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public List<String> getAllCodeList() {
        List<String> codes = new ArrayList<String>();
        try {
            Map<String, String> valueMap = redisClient.hgetAll(CommonConstants.CODE_LIST_REDIS_KEY);
            for(Map.Entry<String, String> entry : valueMap.entrySet()) {
                codes.add(entry.getKey());
            }
        } catch (Exception e) {
            errorLog.error(e);
        }
        return codes;
    }

    public SocketCode getSocketCode(String code) {
        try {
            SocketCode socketCode = new SocketCode();
            socketCode.setSocketCode(code);
//            socketCode.setName(new String(redisClient.hget(CommonConstants.CODE_LIST_REDIS_KEY, code).getBytes("gbk"), "utf-8"));
            socketCode.setName(redisClient.hget(CommonConstants.CODE_LIST_REDIS_KEY, code));
            return socketCode;
        } catch (Exception e) {
            errorLog.error(e);
        }
        return null;
    }

    public List<String> getSelectedCodeList() {
        List<String> codes = new ArrayList<String>();
        try {
            Map<String, String> valueMap = redisClient.hgetAll(CommonConstants.SELECTED_CODE_LIST_REDIS_KEY);
            for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                codes.add(entry.getKey());
            }
        } catch (Exception e) {
            errorLog.error(e);
        }
        return codes;
    }

    public void addSelectedCode(String code) {
        try {
            redisClient.hset(CommonConstants.SELECTED_CODE_LIST_REDIS_KEY, code, "1");
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public void delSelectedCode(String code) {
        try {
            redisClient.hdel(CommonConstants.SELECTED_CODE_LIST_REDIS_KEY, code);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public void addCode(String code, String name) {
        try {
            int timestamp = (int)(System.currentTimeMillis()/1000);
            redisClient.hset(CommonConstants.CODE_LIST_REDIS_KEY, code, name);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public boolean isExist(String code) {
        try {
            String ret = redisClient.hget(CommonConstants.CODE_LIST_REDIS_KEY, code);
            if (ret != null) {
                return true;
            }
        } catch (Exception e) {
            errorLog.error(e);
        }
        return false;
    }

    public void delCode(String code) {
        try {
            redisClient.del(code);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public void setSocketInfo(String code, SocketInfoObject socketInfoObject) {
        try {
            SocketInfo.SocketInfoField field = ProtoUtils.toSocketInfoField(socketInfoObject.getDay());
            SocketInfo.SocketInfoValue value = ProtoUtils.toSocketInfoValue(socketInfoObject);
            redisClient.hset(code.getBytes(), field.toByteArray(), value.toByteArray());
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public List<SocketInfoObject> getSocketInfoObjectList(String code, int startDay, int endDay) {
        List<SocketInfoObject> socketInfoObjects = new ArrayList<SocketInfoObject>();
        try {
            Map<byte[], byte[]> valueMap = redisClient.hgetAll(code.getBytes());
            if (valueMap == null) {
                return socketInfoObjects;
            }
            for (Map.Entry<byte[], byte[]> entry : valueMap.entrySet()) {
                SocketInfo.SocketInfoField socketInfoField = SocketInfo.SocketInfoField.parseFrom(entry.getKey());
                SocketInfo.SocketInfoValue socketInfoValue = SocketInfo.SocketInfoValue.parseFrom(entry.getValue());
                if (socketInfoField.getDay() < startDay && socketInfoField.getDay() > endDay) {
                    continue;
                }
                SocketInfoObject socketInfoObject = ProtoUtils.toSockInfoObject(socketInfoField, socketInfoValue);
                socketInfoObjects.add(socketInfoObject);
            }
            Collections.sort(socketInfoObjects, new Comparator<SocketInfoObject>() {
                @Override
                public int compare(SocketInfoObject o1, SocketInfoObject o2) {
                    // 从大到小排序
                    return o2.getDay() - o1.getDay();
                }
            });
        } catch (Exception e) {
            errorLog.error(e);
        }
        return socketInfoObjects;
    }

    public List<SocketInfoObject> getAllSocketInfoObject(String code) {
        List<SocketInfoObject> socketInfoObjects = new ArrayList<SocketInfoObject>();
        try {
            Map<byte[], byte[]> valueMap = redisClient.hgetAll(code.getBytes());
            if (valueMap == null) {
                return socketInfoObjects;
            }
            for (Map.Entry<byte[], byte[]> entry : valueMap.entrySet()) {
                SocketInfo.SocketInfoField socketInfoField = SocketInfo.SocketInfoField.parseFrom(entry.getKey());
                SocketInfo.SocketInfoValue socketInfoValue = SocketInfo.SocketInfoValue.parseFrom(entry.getValue());
                SocketInfoObject socketInfoObject = ProtoUtils.toSockInfoObject(socketInfoField, socketInfoValue);
                socketInfoObjects.add(socketInfoObject);
            }
            Collections.sort(socketInfoObjects, new Comparator<SocketInfoObject>() {
                @Override
                public int compare(SocketInfoObject o1, SocketInfoObject o2) {
                    // 从大到小排序
                    return o2.getDay() - o1.getDay();
                }
            });
        } catch (Exception e) {
            errorLog.error(e);
        }
        return socketInfoObjects;
    }

    public List<SocketInfoObject> getSocketInfoObjectListByEndDay(String code, int endDay, int num) {
        List<SocketInfoObject> socketInfoObjects = new ArrayList<SocketInfoObject>();
        try {
            Map<byte[], byte[]> valueMap = redisClient.hgetAll(code.getBytes());
            if (valueMap == null) {
                return socketInfoObjects;
            }
            for (Map.Entry<byte[], byte[]> entry : valueMap.entrySet()) {
                SocketInfo.SocketInfoField socketInfoField = SocketInfo.SocketInfoField.parseFrom(entry.getKey());
                SocketInfo.SocketInfoValue socketInfoValue = SocketInfo.SocketInfoValue.parseFrom(entry.getValue());
                if (socketInfoField.getDay() > endDay) {
                    continue;
                }
                SocketInfoObject socketInfoObject = ProtoUtils.toSockInfoObject(socketInfoField, socketInfoValue);
                socketInfoObjects.add(socketInfoObject);
            }
            Collections.sort(socketInfoObjects, new Comparator<SocketInfoObject>() {
                @Override
                public int compare(SocketInfoObject o1, SocketInfoObject o2) {
                    // 从大到小排序
                    return o2.getDay() - o1.getDay();
                }
            });
        } catch (Exception e) {
            errorLog.error(e);
        }
        if (socketInfoObjects.size() <= num) {
            return socketInfoObjects;
        }
        return socketInfoObjects.subList(0, num);
    }

    public void clearOutOfDateSocketInfoObject(String code) {
        try {
            Map<byte[], byte[]> valueMap = redisClient.hgetAll(code.getBytes());
            if (valueMap == null) {
                return;
            }
            List<byte[]> needDelFields = new ArrayList<byte[]>();
            int endDay = Integer.parseInt(DateTime.now().minusDays(CommonConstants.SAVE_SOCKET_DAYS).toString(CommonConstants.DAY_FORMATTER));
            for (Map.Entry<byte[], byte[]> entry : valueMap.entrySet()) {
                SocketInfo.SocketInfoField socketInfoField = SocketInfo.SocketInfoField.parseFrom(entry.getKey());
                if (socketInfoField.getDay() < endDay) {
                    needDelFields.add(entry.getKey());
                }
            }
            if (needDelFields.size() > 0) {
                redisClient.hdel(code.getBytes(), needDelFields);
            }
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public void clearInvalidSocketInfoObject(String code) {
        try {
            Map<byte[], byte[]> valueMap = redisClient.hgetAll(code.getBytes());
            if (valueMap == null) {
                return;
            }
            List<byte[]> needDelFields = new ArrayList<byte[]>();
            for (Map.Entry<byte[], byte[]> entry : valueMap.entrySet()) {
                SocketInfo.SocketInfoField socketInfoField = SocketInfo.SocketInfoField.parseFrom(entry.getKey());
                SocketInfo.SocketInfoValue socketInfoValue = SocketInfo.SocketInfoValue.parseFrom(entry.getValue());
                if (socketInfoValue.getVolume() == 0) {
                    needDelFields.add(entry.getKey());
                }
            }
            if (needDelFields.size() > 0) {
                redisClient.hdel(code.getBytes(), needDelFields);
            }
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public void set(String key, String value) {
        try {
            redisClient.set(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getLastSinaSocketLastModifiedTime() {
        return getLongValue(CommonConstants.LAST_SINA_SOCKET_MODIFIED_TIME_REDIS_KEY);
    }

    public void setLastSinaSocketLastModifiedTime(long value) {
        setLongValue(CommonConstants.LAST_SINA_SOCKET_MODIFIED_TIME_REDIS_KEY, value);
    }

    public long getLastErTiJiaoModifiedTime() {
        return getLongValue(CommonConstants.LAST_ER_TI_JIAO_MODIFIED_TIME_REDIS_KEY);
    }

    public void setLastErTiJiaoModifiedTime(long value) {
        setLongValue(CommonConstants.LAST_ER_TI_JIAO_MODIFIED_TIME_REDIS_KEY, value);
    }

    public long getLastThreeLinesOfSunTime() {
        return getLongValue(CommonConstants.LAST_THREE_LINES_OF_SUN_REDIS_KEY);
    }

    public void setLastThreeLinesOfSunTime(long value) {
        setLongValue(CommonConstants.LAST_THREE_LINES_OF_SUN_REDIS_KEY, value);
    }

    public long getLastTopCowEscapementTime() {
        return getLongValue(CommonConstants.LAST_TOP_COW_ESCAPEMENT_REDIS_KEY);
    }

    public void setLastTopCowEscapementTime(long value) {
        setLongValue(CommonConstants.LAST_TOP_COW_ESCAPEMENT_REDIS_KEY, value);
    }

    public String getErTiJiaoCodes() {
        try {
            return redisClient.get(CommonConstants.ER_TI_JIAO_CODES_REDIS_KEY);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return null;
    }

    public void setErTiJiaoCodes(String value) {
        try {
            redisClient.set(CommonConstants.ER_TI_JIAO_CODES_REDIS_KEY, value);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public String getThreeLinesOfSunCodes() {
        try {
            return redisClient.get(CommonConstants.THREE_LINES_OF_SUN_CODES_REDIS_KEY);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return null;
    }

    public void setThreeLinesOfSunCodes(String value) {
        try {
            redisClient.set(CommonConstants.THREE_LINES_OF_SUN_CODES_REDIS_KEY, value);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public String getTopCowEscapementCodes() {
        try {
            return redisClient.get(CommonConstants.TOP_COW_ESCAPEMENT_CODES_REDIS_KEY);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return null;
    }

    public void setTopCowEscapementCodes(String value) {
        try {
            redisClient.set(CommonConstants.TOP_COW_ESCAPEMENT_CODES_REDIS_KEY, value);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    protected long getLongValue(String key) {
        try {
            String value = redisClient.get(key);
            if (value == null) {
                return 0;
            }
            return Long.parseLong(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void setLongValue(String key, long value) {
        try {
            if (StringUtils.isBlank(key)) {
                throw new RuntimeException("key is null");
            }
            redisClient.set(key, String.valueOf(value));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
