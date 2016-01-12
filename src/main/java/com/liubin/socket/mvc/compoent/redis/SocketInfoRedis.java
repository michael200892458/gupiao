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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by liubin on 2015/8/14.
 */
public class SocketInfoRedis {

    Logger errorLog = LogUtils.getErrorLog();

    JedisPool jedisPool;

    public void init(InputStream inputStream) throws Exception {
        try {
            Properties properties = new Properties();
            properties.load(inputStream);

            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(500);
            config.setBlockWhenExhausted(false);      // 当连接池满的时候不阻塞
            config.setTestOnBorrow(true);
            config.setTestWhileIdle(true);
            config.setMaxWaitMillis(1000);
            config.setMaxIdle(50);
            String host = properties.getProperty("host");
            int port = Integer.parseInt(properties.getProperty("port"));
            String password = properties.getProperty("password");
            jedisPool = new JedisPool(config, host, port, 2000, password);
            Jedis jedis = jedisPool.getResource();
            jedisPool.returnResource(jedis);
            inputStream.close();
        } catch (Exception e) {
            errorLog.error("init error", e);
        }

    }

    public List<String> getAllCodeList() {
        List<String> codes = new ArrayList<String>();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<String, String> valueMap = jedis.hgetAll(CommonConstants.CODE_LIST_REDIS_KEY);
            for(Map.Entry<String, String> entry : valueMap.entrySet()) {
                codes.add(entry.getKey());
            }
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return codes;
    }

    public SocketCode getSocketCode(String code) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            SocketCode socketCode = new SocketCode();
            socketCode.setSocketCode(code);
            socketCode.setName(jedis.hget(CommonConstants.CODE_LIST_REDIS_KEY, code));
            return socketCode;
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public List<String> getSelectedCodeList() {
        List<String> codes = new ArrayList<String>();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<String, String> valueMap = jedis.hgetAll(CommonConstants.SELECTED_CODE_LIST_REDIS_KEY);
            for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                codes.add(entry.getKey());
            }
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return codes;
    }

    public void addSelectedCode(String code) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(CommonConstants.SELECTED_CODE_LIST_REDIS_KEY, code, "1");
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void delSelectedCode(String code) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(CommonConstants.SELECTED_CODE_LIST_REDIS_KEY, code);
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void addCode(String code, String name) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            int timestamp = (int)(System.currentTimeMillis()/1000);
            jedis.hset(CommonConstants.CODE_LIST_REDIS_KEY, code, name);
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean isExist(String code) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String ret = jedis.hget(CommonConstants.CODE_LIST_REDIS_KEY, code);
            if (ret != null) {
                return true;
            }
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    public void delCode(String code) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(code);
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setSocketInfo(String code, SocketInfoObject socketInfoObject) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<SocketInfoObject> socketInfoObjects = getSocketInfoObjectListByEndDay(code, socketInfoObject.getDay() - 1, 1);
            if (socketInfoObjects != null && socketInfoObjects.size() > 0) {
                SocketInfoObject lastSocketInfoObject = socketInfoObjects.get(0);
                // 如果所有的指标都是相同，表示股票没有开盘.
                if (lastSocketInfoObject.getTurnover() == socketInfoObject.getTurnover()
                        && lastSocketInfoObject.getVolume() == socketInfoObject.getVolume()
                        && lastSocketInfoObject.getOpenPrice() == socketInfoObject.getOpenPrice()
                        && lastSocketInfoObject.getLastClosePrice() == socketInfoObject.getLastClosePrice()
                        && lastSocketInfoObject.getTodayMaxPrice() == socketInfoObject.getTodayMaxPrice()
                        && lastSocketInfoObject.getTodayMinPrice() == socketInfoObject.getTodayMinPrice()) {
                    return;
                }
            }
            SocketInfo.SocketInfoField field = ProtoUtils.toSocketInfoField(socketInfoObject.getDay());
            SocketInfo.SocketInfoValue value = ProtoUtils.toSocketInfoValue(socketInfoObject);
            jedis.hset(code.getBytes(), field.toByteArray(), value.toByteArray());
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<SocketInfoObject> getSocketInfoObjectList(String code, int startDay, int endDay) {
        List<SocketInfoObject> socketInfoObjects = new ArrayList<SocketInfoObject>();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<byte[], byte[]> valueMap = jedis.hgetAll(code.getBytes());
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
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return socketInfoObjects;
    }

    public List<SocketInfoObject> getAllSocketInfoObject(String code) {
        List<SocketInfoObject> socketInfoObjects = new ArrayList<SocketInfoObject>();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<byte[], byte[]> valueMap = jedis.hgetAll(code.getBytes());
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
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return socketInfoObjects;
    }

    /**
     * 获取一只股票股价数据, 日期按大到小排序
     * @param code
     * @param endDay
     * @param num
     * @return
     */
    public List<SocketInfoObject> getSocketInfoObjectListByEndDay(String code, int endDay, int num) {
        List<SocketInfoObject> socketInfoObjects = new ArrayList<SocketInfoObject>();
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<byte[], byte[]> valueMap = jedis.hgetAll(code.getBytes());
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
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        if (socketInfoObjects.size() <= num) {
            return socketInfoObjects;
        }
        return socketInfoObjects.subList(0, num);
    }

    public void clearOutOfDateSocketInfoObject(String code) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<byte[], byte[]> valueMap = jedis.hgetAll(code.getBytes());
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
                for (byte[] value : needDelFields) {
                    jedis.hdel(code.getBytes(), value);
                }
            }
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void clearInvalidSocketInfoObject(String code) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<byte[], byte[]> valueMap = jedis.hgetAll(code.getBytes());
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
            for (byte[] value : needDelFields) {
                jedis.hdel(code.getBytes(), value);
            }
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
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

    public long getLastDumpDbTime() {
        return getLongValue(CommonConstants.LAST_DUMP_DB_TIME_REDIS_KEY);
    }

    public void setLastDumpDbTime(long value) {
        setLongValue(CommonConstants.LAST_DUMP_DB_TIME_REDIS_KEY, value);
    }

    public long getLastOversoldFiveAvgTime() {
        return getLongValue(CommonConstants.LAST_OVERSOLD_FIVE_AVG_TIME_REDIS_KEY);
    }

    public void setLastOversoldFiveAvgTime(long value) {
        setLongValue(CommonConstants.LAST_OVERSOLD_FIVE_AVG_TIME_REDIS_KEY, value);
    }

    public String getErTiJiaoCodes() {
        try {
            return get(CommonConstants.ER_TI_JIAO_CODES_REDIS_KEY);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return null;
    }

    public void setErTiJiaoCodes(String value) {
        try {
            set(CommonConstants.ER_TI_JIAO_CODES_REDIS_KEY, value);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public String getThreeLinesOfSunCodes() {
        try {
            return get(CommonConstants.THREE_LINES_OF_SUN_CODES_REDIS_KEY);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return null;
    }

    public void setThreeLinesOfSunCodes(String value) {
        try {
            set(CommonConstants.THREE_LINES_OF_SUN_CODES_REDIS_KEY, value);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public String getTopCowEscapementCodes() {
        try {
            return get(CommonConstants.TOP_COW_ESCAPEMENT_CODES_REDIS_KEY);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return null;
    }

    public void setTopCowEscapementCodes(String value) {
        try {
            set(CommonConstants.TOP_COW_ESCAPEMENT_CODES_REDIS_KEY, value);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public String getOversoldFiveAvg() {
        try {
            return get(CommonConstants.OVERSOLD_FIVE_AVG_CODES_REDIS_KEY);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return null;
    }

    public void setOversoldFiveAvg(String value) {
        try {
            set(CommonConstants.OVERSOLD_FIVE_AVG_CODES_REDIS_KEY, value);
        } catch (Exception e) {
            errorLog.error(e);
        }
    }

    public long getLongValue(String key) {
        try {
            String value = get(key);
            if (value == null) {
                return 0;
            }
            return Long.parseLong(value);
        } catch (Exception e) {
            throw new RuntimeException("get longValue error, key=" + key + ", ex:" + e.getMessage(), e);
        }
    }

    public void setLongValue(String key, long value) {
        try {
            if (StringUtils.isBlank(key)) {
                throw new RuntimeException("key is null");
            }
            set(key, String.valueOf(value));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            errorLog.error(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<String, String> ret = jedis.hgetAll(key);
            if (ret == null) {
                return new HashMap<String, String>();
            }
            return ret;
        } catch (Exception e) {
            errorLog.error(e);
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setHashValue(String key, Map<String, String> valueMap) {
        setHashValue(key, valueMap, 0);
    }

    public void setHashValue(String key, Map<String, String> valueMap, int expireTime) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hmset(key, valueMap);
            if (expireTime > 0) {
                jedis.expire(key, expireTime);
            }
        } catch (Exception e) {
            errorLog.error(e);
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } catch (Exception e) {
            errorLog.error(e);
            throw new RuntimeException(e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
