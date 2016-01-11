package com.liubin.socket.mvc.compoent.strategy;

import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.StrategyResult;

import java.util.List;

/**
 * Created by liubin on 2016/1/11.
 */
public interface StrategyInterface {
    public boolean executeCheck(long nowTime);
    public StrategyResult check(String code, int day, List<SocketInfoObject> socketInfoObjects);
}
