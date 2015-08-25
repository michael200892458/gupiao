package com.liubin.socket.mvc.controller;

import com.liubin.socket.mvc.service.SocketService;
import com.liubin.socket.pojo.RecommendCode;
import com.liubin.socket.pojo.SocketCode;
import com.liubin.socket.utils.LogUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2015/8/18.
 */
@Controller
@RequestMapping("/")
public class PageController {

    Logger errorLog = LogUtils.getErrorLog();

    @Autowired
    SocketService socketService;

    @RequestMapping("/")
    public String index(ModelMap modelMap) {
        try {
            List<RecommendCode> recommendCodes = socketService.getRecommendCodes();
            List<SocketCode> selectCodes = socketService.getSelectedCodes();
            modelMap.addAttribute("recommendCodes", recommendCodes);
            modelMap.addAttribute("selectedCodes", selectCodes);
        } catch (Exception e) {
            errorLog.error(e);
        }
        return "/socket";
    }

}
