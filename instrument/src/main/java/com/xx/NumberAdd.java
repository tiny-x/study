package com.xx;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-05-31 11:03
 */
public class NumberAdd {

    private static final Logger logger = LoggerFactory.getLogger(NumberAdd.class);

    static {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof LoggerContext) {
            LoggerContext loggerContext = (LoggerContext) loggerFactory;
            Logger logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
            ch.qos.logback.classic.Logger logger0 = (ch.qos.logback.classic.Logger) logger;
            logger0.detachAndStopAllAppenders();
            logger0.setLevel(Level.toLevel("info"));
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(loggerContext);
            encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS}, %green(%-5level), %red([%thread]) %boldMagenta(%logger{72}) - %msg%n");
            encoder.start();
            ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
            consoleAppender.setEncoder(encoder);
            consoleAppender.start();
            logger0.addAppender(consoleAppender);
        }
    }

    /**
     * -javaagent:D:\workspace\idea\study\instrument\target\instrument-1.0.0.jar
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        while (true) {
            Random random = new Random();
            int a = random.nextInt(10);
            int b = random.nextInt(10);
            int add = NumberAdd.add(a, b);
            logger.info("a(" + a + ")\t+\tb(" + b + ")\t=" + add);
            TimeUnit.SECONDS.sleep(3);
        }
    }

    public static int add(int a, int b) {
        return a + b;
    }
}
