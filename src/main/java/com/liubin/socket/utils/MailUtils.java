package com.liubin.socket.utils;

import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liubin on 2014/11/18.
 */
public class MailUtils {
    private static final String MAIL_SMTP = "smtp.exmail.qq.com";
    private static final String MAIL_USER = "data@coohua.com";
    private static final String MAIL_PASSWORD = "coohua#008";
    private static final String TO = "michael_dlut@qq.com";

    public static void sendMail(String subject, String content) {
        Mail.send(MAIL_SMTP, MAIL_USER, TO, subject, content, MAIL_USER, MAIL_PASSWORD);
    }
}
