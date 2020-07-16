package com.xy.lettuce;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 */
public class LettuceTest {

    private static final Logger logger = LoggerFactory.getLogger(LettuceTest.class);

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

    public static void main(String[] args) {
        RedisURI uri = RedisURI.create("redis://127.0.0.1:6379/");
        RedisClient redisClient = RedisClient.create(uri);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> redisCommands = connection.sync();
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
                long start = System.currentTimeMillis();
                redisCommands.set("name", "luna");
                logger.info("key: [name]; value: [{}], time: [{}ms]", redisCommands.get("name"), (System.currentTimeMillis() - start));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
