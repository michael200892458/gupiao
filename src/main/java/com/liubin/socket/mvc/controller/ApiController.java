package com.liubin.socket.mvc.controller;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.compoent.SinaSocketFlushProcessor;
import com.liubin.socket.mvc.service.SocketService;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.StatusResult;
import com.liubin.socket.utils.LogUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2015/8/18.
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    Logger log = LogUtils.getAccessLog();

    @Autowired
    HttpServletRequest request;

    @Autowired
    SocketService socketService;
    @Autowired
    SinaSocketFlushProcessor sinaSocketFlushProcessor;

    @RequestMapping("/addSocketCode")
    @ResponseBody
    public String addSocketCode() {
        StatusResult statusResult = new StatusResult();
        try {
            String socketCode = request.getParameter("socketCode");
            if (!socketService.addSocketCode(socketCode)) {
                statusResult.setStatus(1);
                statusResult.setMessage("添加失败");
            }
            log.info("添加广告代码: socketCode:{}, result:{}", socketCode, JSON.toJSONString(statusResult));
        } catch (Exception e) {
            statusResult.setStatus(1);
        }
        return JSON.toJSONString(statusResult);
    }

    @RequestMapping("/addSelectedCode")
    @ResponseBody
    public String addSelectedCode() {
        StatusResult statusResult = new StatusResult();
        try {
            String socketCode = request.getParameter("socketCode");
            if (!socketService.addSelectedCode(socketCode)) {
                statusResult.setStatus(1);
                statusResult.setMessage("添加失败");
            }
            log.info("addSelectedCode:{}", socketCode);
        } catch (Exception e) {
            statusResult.setStatus(1);
        }
        return JSON.toJSONString(statusResult);
    }

    @RequestMapping("/delSelectedCode")
    @ResponseBody
    public String delSelectedCode() {
        StatusResult statusResult = new StatusResult();
        try {
            String socketCode = request.getParameter("socketCode");
//            if (!socketService.delSelectedCode(socketCode)) {
//                statusResult.setStatus(1);
//                statusResult.setMessage("删除失败");
//            }
            log.info("delSelectedCode:{}", socketCode);
        } catch (Exception e) {
            statusResult.setStatus(1);
        }
        return JSON.toJSONString(statusResult);
    }

    @RequestMapping("/clearOutOfDateSocketInfo")
    @ResponseBody
    public String clearOutOfDateSocketInfo() {
        StatusResult statusResult = new StatusResult();
        try {
            String socketCode = request.getParameter("socketCode");
//            if (!socketService.clearOutOfDateCode(socketCode)) {
//                statusResult.setStatus(1);
//                statusResult.setMessage("清除失败");
//            }
        } catch (Exception e) {
            statusResult.setStatus(1);
        }
        return JSON.toJSONString(statusResult);
    }

    @RequestMapping("/delSocketCode")
    @ResponseBody
    public String delSocketCode() {
        StatusResult statusResult = new StatusResult();
        try {
            String socketCode = request.getParameter("socketCode");
//            socketService.delSocketCode(socketCode);
        } catch (Exception e) {
            statusResult.setStatus(1);
        }
        return JSON.toJSONString(statusResult);
    }

    @RequestMapping("/getSocketInfoObjects")
    @ResponseBody
    public String getSocketInfoObjects() {
        List<SocketInfoObject> socketInfoObjectList = new ArrayList<SocketInfoObject>();
        try {
            String socketCode = request.getParameter("socketCode");
            socketInfoObjectList = socketService.getSocketInfoObjects(socketCode);
        } catch (Exception e) {
            socketInfoObjectList.clear();
        }
        return JSON.toJSONString(socketInfoObjectList);
    }

    @RequestMapping("/clearInvalidSocketInfo")
    @ResponseBody
    public String clearInvalidSocketInfo() {
        StatusResult statusResult = new StatusResult();
        try {
            String socketCode = request.getParameter("socketCode");
//            if(!socketService.clearInvalidSocketInfo(socketCode)) {
//               statusResult.setStatus(1);
//            }
        } catch (Exception e) {
            statusResult.setStatus(1);
        }
        return JSON.toJSONString(statusResult);
    }

    @RequestMapping("/calcAvgValue")
    @ResponseBody
    public String calcAvgValue() {
        StatusResult statusResult = new StatusResult();
        try {
            String magicCode = request.getParameter("magicCode");
            if (!magicCode.equals("mySocketMagicCodeOfLiuBin")) {
                statusResult.setMessage("不合法用户");
                statusResult.setStatus(-1);
                return JSON.toJSONString(statusResult);
            }
            String socketCode = request.getParameter("socketCode");
            socketService.calcAvgValue(socketCode);
        } catch (Exception e) {
            statusResult.setStatus(1);
        }
        return JSON.toJSONString(statusResult);
    }

    @RequestMapping("/updateAllSocketInfo.do")
    @ResponseBody
    public String updateAllSocketInfo() {
        StatusResult statusResult = new StatusResult();
        try {
            String magicCode = request.getParameter("magicCode");
            if (!magicCode.equals("mySocketMagicCodeOfLiuBin")) {
                statusResult.setMessage("不合法用户");
                statusResult.setStatus(-1);
                return JSON.toJSONString(statusResult);
            }
            sinaSocketFlushProcessor.updateAllSocketsInfo();
        } catch (Exception e) {
            statusResult.setStatus(1);
        }
        return JSON.toJSONString(statusResult);
    }
}
