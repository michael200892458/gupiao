package com.liubin.socket.utils;

import com.liubin.socket.pojo.SocketInfoObject;
import com.liubin.socket.pojo.proto.SocketInfo;

/**
 * Created by liubin on 2015/8/14.
 */
public class ProtoUtils {
    public static SocketInfo.SocketInfoField toSocketInfoField(int day) {
        SocketInfo.SocketInfoField.Builder builder = SocketInfo.SocketInfoField.newBuilder();
        builder.setDay(day);
        return builder.build();
    }

    public static SocketInfo.SocketInfoValue toSocketInfoValue(SocketInfoObject socketInfoObject) {
        SocketInfo.SocketInfoValue.Builder builder = SocketInfo.SocketInfoValue.newBuilder();
        builder.setCurrentPrice(socketInfoObject.getCurrentPrice());
        builder.setLastClosePrice(socketInfoObject.getLastClosePrice());
        builder.setOpenPrice(socketInfoObject.getOpenPrice());
        builder.setTodayMaxPrice(socketInfoObject.getTodayMaxPrice());
        builder.setTodayMinPrice(socketInfoObject.getTodayMinPrice());
        builder.setTurnover(socketInfoObject.getTurnover());
        builder.setVolume(socketInfoObject.getVolume());
        builder.setAvgPrice5(socketInfoObject.getAvgPrice5());
        builder.setAvgPrice10(socketInfoObject.getAvgPrice10());
        builder.setAvgPrice20(socketInfoObject.getAvgPrice20());
        builder.setAvgPrice30(socketInfoObject.getAvgPrice30());
        builder.setAvgPrice60(socketInfoObject.getAvgPrice60());
        return builder.build();
    }

    public static SocketInfoObject toSockInfoObject(SocketInfo.SocketInfoField socketInfoField, SocketInfo.SocketInfoValue socketInfoValue) {
        SocketInfoObject socketInfoObject = new SocketInfoObject();
        socketInfoObject.setDay(socketInfoField.getDay());
        socketInfoObject.setCurrentPrice(socketInfoValue.getCurrentPrice());
        socketInfoObject.setOpenPrice(socketInfoValue.getOpenPrice());
        socketInfoObject.setLastClosePrice(socketInfoValue.getLastClosePrice());
        socketInfoObject.setTodayMaxPrice(socketInfoValue.getTodayMaxPrice());
        socketInfoObject.setTodayMinPrice(socketInfoValue.getTodayMinPrice());
        socketInfoObject.setVolume(socketInfoValue.getVolume());
        socketInfoObject.setTurnover(socketInfoValue.getTurnover());
        socketInfoObject.setAvgPrice5(socketInfoValue.getAvgPrice5());
        socketInfoObject.setAvgPrice10(socketInfoValue.getAvgPrice10());
        socketInfoObject.setAvgPrice20(socketInfoValue.getAvgPrice20());
        socketInfoObject.setAvgPrice30(socketInfoValue.getAvgPrice30());
        socketInfoObject.setAvgPrice60(socketInfoValue.getAvgPrice60());
        return socketInfoObject;
    }
}
