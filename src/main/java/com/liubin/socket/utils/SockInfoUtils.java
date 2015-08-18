package com.liubin.socket.utils;

import com.liubin.socket.pojo.SinaSocketInfo;
import com.liubin.socket.pojo.SocketInfoObject;

import java.util.List;

/**
 * Created by liubin on 2015/8/14.
 */
public class SockInfoUtils {
    public static SocketInfoObject calcSocketInfObject(SinaSocketInfo sinaSocketInfo, List<SocketInfoObject> socketInfoObjectList) {
        SocketInfoObject nowSockInfoObject = new SocketInfoObject();
        nowSockInfoObject.setCurrentPrice(sinaSocketInfo.getCurrentPrice());
        nowSockInfoObject.setDay(sinaSocketInfo.getDay());
        nowSockInfoObject.setCurrentPrice(sinaSocketInfo.getCurrentPrice());
        nowSockInfoObject.setOpenPrice(sinaSocketInfo.getOpenPrice());
        nowSockInfoObject.setLastClosePrice(sinaSocketInfo.getLastClosePrice());
        nowSockInfoObject.setVolume(sinaSocketInfo.getVolume());
        nowSockInfoObject.setTurnover(sinaSocketInfo.getTurnover());
        if (socketInfoObjectList.size() >= 4) {
            int avg5 = sumPrice(socketInfoObjectList, 4) + sinaSocketInfo.getCurrentPrice() / 5;
            nowSockInfoObject.setAvgPrice5(avg5);
        }
        if (socketInfoObjectList.size() >= 9) {
            int avg10 = sumPrice(socketInfoObjectList, 9) + sinaSocketInfo.getCurrentPrice() / 10;
            nowSockInfoObject.setAvgPrice10(avg10);
        }
        if (socketInfoObjectList.size() >= 19) {
            int avg20 = sumPrice(socketInfoObjectList, 19) + sinaSocketInfo.getCurrentPrice() / 20;
            nowSockInfoObject.setAvgPrice20(avg20);
        }
        if (socketInfoObjectList.size() >= 29) {
            int avg30 = sumPrice(socketInfoObjectList, 29) + sinaSocketInfo.getCurrentPrice() / 30;
            nowSockInfoObject.setAvgPrice30(avg30);
        }
        if (socketInfoObjectList.size() >= 59) {
            int avg60 = sumPrice(socketInfoObjectList, 59) + sinaSocketInfo.getCurrentPrice() / 60;
            nowSockInfoObject.setAvgPrice60(avg60);
        }
        return nowSockInfoObject;
    }

    public static int sumPrice(List<SocketInfoObject> socketInfoObjectList, int num) {
        if (num < socketInfoObjectList.size()) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < num; i += 1) {
            SocketInfoObject socketInfoObject = socketInfoObjectList.get(i);
            sum += socketInfoObject.getCurrentPrice();
        }
        return sum;
    }

    public static double calcRoseValue(SocketInfoObject socketInfoObject) {
        if (socketInfoObject.getCurrentPrice() == 0 || socketInfoObject.getLastClosePrice() == 0) {
            return 0;
        }
        double lastClosePrice = socketInfoObject.getLastClosePrice();
        double currentPrice = socketInfoObject.getCurrentPrice();
        return (currentPrice - lastClosePrice)/lastClosePrice;
    }

    public static double calcAmplitudeValue(SocketInfoObject socketInfoObject) {
        if (socketInfoObject.getLastClosePrice() == 0 || socketInfoObject.getCurrentPrice() == 0) {
            return 0;
        }
        double maxPrice = socketInfoObject.getTodayMaxPrice();
        double minPrice = socketInfoObject.getTodayMinPrice();
        double lastClosePrice = socketInfoObject.getLastClosePrice();
        return (maxPrice - minPrice) / lastClosePrice;
    }
}
