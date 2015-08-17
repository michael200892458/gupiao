package com.liubin.socket.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by liubin on 2015/8/14.
 */
public class LogUtils {
    public static Logger accessLog = LogManager.getLogger("accessLog");
    public static Logger sysLog = LogManager.getLogger("sysLog");
    public static Logger errorLog = LogManager.getLogger("errorLog");

    public static Logger getAccessLog() {
        return accessLog;
    }

    public static Logger getSysLog() {
        return sysLog;
    }

    public static Logger getErrorLog() {
        return errorLog;
    }
}
