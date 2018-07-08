package com.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.net.SocketAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Client {

    private static final Logger logger;

    static {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        configure(loggerContext);
        logger = LoggerFactory.getLogger(Client.class);
    }

    public static void main(String[] args) throws Exception {
        int i = 0;
        while (true) {
            TimeUnit.SECONDS.sleep(2);
            logger.info("编程式配置 " + (i++));
        }
        //StatusPrinter.print((LoggerContext) LoggerFactory.getILoggerFactory());
    }

    public static void configure(LoggerContext lc) {
        StatusManager sm = lc.getStatusManager();
        if(sm != null)  {
            sm.add(new InfoStatus("Setting up default configuration.", lc));
        }
        PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setContext(lc);
        pl.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        pl.start();

        // console
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
        ca.setContext(lc);
        ca.setName("console");
        ca.setEncoder(pl);
        ca.start();

        // socket
        SocketAppender sa = new SocketAppender();
        sa.setPort(8999);
        sa.setRemoteHost("127.0.0.1");
        sa.setName("socket");
        sa.setContext(lc);
        // 重连接时间
        sa.setReconnectionDelay(Duration.buildBySeconds(2));
        sa.start();

        ch.qos.logback.classic.Logger logger = lc.getLogger(Client.class);
        logger.setAdditive(false);
        logger.addAppender(ca);
        logger.addAppender(sa);
    }
}
