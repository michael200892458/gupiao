package com.liubin.socket.pojo;

/**
 * Created by liubin on 2016/1/11.
 */
public class StrategyResult {
    private boolean isValid;
    private String code;
    private String description;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
