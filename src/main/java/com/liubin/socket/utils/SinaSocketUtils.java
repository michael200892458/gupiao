package com.liubin.socket.utils;

import com.liubin.socket.pojo.SinaSocketInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2015/8/14.
 */
public class SinaSocketUtils {
    static Logger errorLog = LogUtils.getErrorLog();
    public static List<SinaSocketInfo> getSinaSockets(List<String> codes) {
        List<SinaSocketInfo> sinaSocketInfoList = null;
        try {
            String codeStr = StringUtils.join(codes, ",");
            String url = CommonConstants.SINA_SOCKET_URL_PREFIX + codeStr;
            String content = HttpUtils.getResponse(url);
            sinaSocketInfoList = SinaSocketInfoParser.parseSinaSocketInfoList(content);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return sinaSocketInfoList;
    }
}
