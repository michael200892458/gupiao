package com.liubin.socket.utils;

import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2014/11/18.
 */
@Repository
public class MailUtils {
    private final String MAIL_SMTP = "smtp.exmail.qq.com";
    private final String MAIL_USER = "data@coohua.com";
    private final String MAIL_PASSWORD = "coohua#008";

    private List<String> receiverList = new ArrayList<String>();

    @PostConstruct
    public void init() {
        receiverList.add("liubin@coohua.com");
    }

    public void sendMail(String subject, String content) {
        for (String to : receiverList) {
            Mail.send(MAIL_SMTP, MAIL_USER, to, subject, content, MAIL_USER, MAIL_PASSWORD);
        }
    }

    public List<String> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<String> receiverList) {
        this.receiverList = receiverList;
    }
}
