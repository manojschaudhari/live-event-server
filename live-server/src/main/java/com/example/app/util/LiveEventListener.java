package com.example.app.util;

import com.example.app.model.LiveEvent;
import com.example.app.service.LiveEventService;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;

@Component
@RocketMQMessageListener(
        topic = "${rocketmq.producer.topic}",
        consumerGroup = "${rocketmq.producer.group}",
        consumeMode = ConsumeMode.CONCURRENTLY,
        consumeThreadNumber = 100,
        consumeThreadMax = 100
)
public class LiveEventListener implements RocketMQListener<LiveEvent> {

    private final Logger logger = LoggerFactory.getLogger(LiveEventListener.class);

    @Autowired
    LiveEventService liveEventService;

    @Value("${dead.queue.file.name}")
    private String deadQueueFile;

    //-- MQ Message Delegate method for Listener
    public void onMessage(LiveEvent event) {
        logger.info("RECEIVED EVENT ::: {}", event);
        try {
            LiveEvent liveEvent = liveEventService.getFromCache(event.getEventId());
            if (liveEvent != null) {
                liveEvent.setCurrentScore(event.getCurrentScore());
            } else {
                throw new RuntimeException("NULL Message Received.");
            }
        } catch (Exception ex) {
            appendMessageToDeadFileQueue(event.toString());
            logger.error("An error occurred while processing the event :::  ",ex);
        }
    }

    //-- Dead Letter Queue (DLQ) design pattern
    public void appendMessageToDeadFileQueue(String message) {
        appendMessage(message);
    }

    //-- Create Dead Queue File.
    //-- Write message to append only mode.
    public void appendMessage(String message) {

        Path path = Paths.get(deadQueueFile);

        //-- Add a newline character to the message for separation
        String contentToAppend = message + System.lineSeparator();

        try {
            Files.write(
                    path,
                    contentToAppend.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE
            );

        } catch (IOException ex) {
            logger.error("An error occurred while appending to the file :::  ",ex);
        }
    }


}
