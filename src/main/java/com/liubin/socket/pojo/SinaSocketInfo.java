package com.liubin.socket.pojo;

/**
 * Created by liubin on 2015/8/14.
 */
public class SinaSocketInfo {
    private int day;
    private String code;
    private String name;
    private int openPrice;
    private int lastClosePrice;
    private int currentPrice;
    private int todayMaxPrice;
    private int todayMinPrice;
    private int volume;
    private int turnover;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(int openPrice) {
        this.openPrice = openPrice;
    }

    public int getLastClosePrice() {
        return lastClosePrice;
    }

    public void setLastClosePrice(int lastClosePrice) {
        this.lastClosePrice = lastClosePrice;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getTodayMaxPrice() {
        return todayMaxPrice;
    }

    public void setTodayMaxPrice(int todayMaxPrice) {
        this.todayMaxPrice = todayMaxPrice;
    }

    public int getTodayMinPrice() {
        return todayMinPrice;
    }

    public void setTodayMinPrice(int todayMinPrice) {
        this.todayMinPrice = todayMinPrice;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getTurnover() {
        return turnover;
    }

    public void setTurnover(int turnover) {
        this.turnover = turnover;
    }
}
