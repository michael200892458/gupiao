package com.liubin.socket.mvc.compoent.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

/**
 * Created by liubin on 2015/8/17.
 */
@Component
public class StrategyManager extends TimerTask {

    @Autowired
    ErTiJiao erTiJiao;
    @Autowired
    ThreeLinesOfSun threeLinesOfSun;
    @Autowired
    TopCowEscapement topCowEscapement;

    @Override
    public void run() {
        erTiJiao.run();
        threeLinesOfSun.run();
        topCowEscapement.run();
    }
}
