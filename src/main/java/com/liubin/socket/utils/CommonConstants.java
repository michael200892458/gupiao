package com.liubin.socket.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by liubin on 2015/8/14.
 */
public class CommonConstants {
    public static final String HTTP_METHOD_GET = "get";
    public static final String HTTP_METHOD_POST = "post";

    public static final String SELECTED_CODE_LIST_REDIS_KEY = "selectedCodeList";
    public static final String CODE_LIST_REDIS_KEY = "codeList";
    public static final String LAST_SINA_SOCKET_MODIFIED_TIME_REDIS_KEY = "lastSinaSocketModifiedTime";
    public static final String LAST_ER_TI_JIAO_MODIFIED_TIME_REDIS_KEY = "lastErTiJiaoModifiedTime";
    public static final String LAST_THREE_LINES_OF_SUN_REDIS_KEY = "lastThreeLinesOfSunTime";
    public static final String LAST_TOP_COW_ESCAPEMENT_REDIS_KEY = "lastTopCowEscapementTime";
    public static final String LAST_DUMP_DB_TIME_REDIS_KEY = "lastDumpDbTime";
    public static final String ER_TI_JIAO_CODES_REDIS_KEY = "erTiJiaoCodes";
    public static final String THREE_LINES_OF_SUN_CODES_REDIS_KEY = "threeLinesOfSunCodes";
    public static final String TOP_COW_ESCAPEMENT_CODES_REDIS_KEY = "topCowEscapementCodes";
    public static final String SINA_SOCKET_URL_PREFIX = "http://hq.sinajs.cn/list=";
    public static final int CODES_NUM_PER_REQUEST = 100;
    public static final int SAVE_SOCKET_DAYS = 365*5;

    public static DateTimeFormatter DAY_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");
}
