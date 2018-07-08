package com.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.net.SimpleSocketServer;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    public static void main(String[] args) throws Exception {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        configure(loggerContext);

        SimpleSocketServer simpleSocketServer = new SimpleSocketServer(loggerContext, 8999);
        simpleSocketServer.start();

        Logger logger = LoggerFactory.getLogger("com.logback.Client");
        logger.info("server test: {}", "hahaha");
    }

    public static void configure(LoggerContext lc) throws Exception {
        StatusManager sm = lc.getStatusManager();
        if (sm != null) {
            sm.add(new InfoStatus("Setting up default configuration.", lc));
        }
        PatternLayoutEncoder pl = new PatternLayoutEncoder();
        pl.setContext(lc);
        pl.setPattern("console: %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        pl.start();

        // console
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
        ca.setContext(lc);
        ca.setName("console");
        ca.setEncoder(pl);
        ca.start();

        PatternLayoutEncoder pl2 = new PatternLayoutEncoder();
        pl2.setContext(lc);
        pl2.setPattern("file: %d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        pl2.start();

        FileAppender<ILoggingEvent> fa = new FileAppender<>();
        fa.setContext(lc);
        fa.setFile("/Users/yefei/Documents/tmp/2.log");
        fa.setName("file");
        fa.setEncoder(pl2);
        fa.start();

        ch.qos.logback.classic.Logger logger = lc.getLogger("com.logback.Client");
        logger.setAdditive(false);
        logger.addAppender(ca);
        logger.addAppender(fa);
    }
}
