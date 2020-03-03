package com.rp.service.push;

import com.rp.service.data.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.EmitterProcessor;

import java.time.Duration;
import java.util.Date;
import java.util.Set;

/**
 * @author rpradhan
 */
@Service
public class PushMessageHandler implements Sender {

    private static final Logger logger = LoggerFactory.getLogger(PushMessageHandler.class);

    private PushConnectionRegistry registry;

    private static final String event = "push";

    @Autowired
    public PushMessageHandler( final PushConnectionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void send(String subscriber, String message) {
        EmitterProcessor<ServerSentEvent<UserMessage>> emitter =  registry.get(subscriber);
        if (emitter == null) {
            logger.info("Could not find subscriber [" +subscriber +"]");
        }
        else if(emitter.isTerminated() || emitter.isCancelled()) {
            logger.info("Removing subscriber [" +subscriber +"]");
            registry.remove(subscriber);
        } else {
            ServerSentEvent<UserMessage> sse = ServerSentEvent.<UserMessage>builder()
                    .data(StringUtils.isEmpty(message) ? new UserMessage(subscriber, "", new Date()) : new UserMessage(subscriber, message, new Date()))
                    .event(PushMessageHandler.event)
                    .retry(Duration.ofSeconds(100)).build();

            emitter.onNext(sse);
        }
    }

    @Override
    public void broadcast(String message) {
        Set<String> subscribers = registry.getAllSubscriber();
        for(String subscriber : subscribers) {
            EmitterProcessor<ServerSentEvent<UserMessage>> emitter =  registry.get(subscriber);
            if (emitter == null) {
                logger.info("Could not find subscriber [" +subscriber +"]");
            } else if(emitter.isTerminated() || emitter.isCancelled()) {
                logger.info("Removing subscriber [" + subscriber + "]");
                registry.remove(subscriber);
            } else {
                ServerSentEvent<UserMessage> sse = ServerSentEvent.<UserMessage>builder()
                        .data(StringUtils.isEmpty(message) ? new UserMessage(subscriber, " ", new Date()) : new UserMessage(subscriber, message, new Date()))
                        .event(PushMessageHandler.event)
                        .retry(Duration.ofSeconds(100)).build();

                emitter.onNext(sse);
                logger.info("message sent to [" + subscriber + "]");
            }
        }
    }
}
