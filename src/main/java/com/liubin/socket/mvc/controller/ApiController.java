package com.liubin.socket.mvc.controller;

import com.alibaba.fastjson.JSON;
import com.liubin.socket.mvc.service.SocketService;
import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.StatusResult;
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

    @Autowired
    HttpServletRequest request;

    @Autowired
    SocketService socketService;

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
            socketService.delSocketCode(socketCode);
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
}
