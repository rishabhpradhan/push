package com.rp.service.controller;

import com.rp.service.data.UserMessage;
import com.rp.service.push.PushConnectionRegistry;
import com.rp.service.push.PushMessageHandler;
import com.rp.service.push.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.EmitterProcessor;

import java.util.Set;

/**
 * @author rpradhan
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private PushConnectionRegistry<UserMessage> registry;
    private Sender sender;

    @Autowired
    public NotificationController(PushConnectionRegistry registry, PushMessageHandler handler) {
        this.registry = registry;
        this.sender = handler;
    }

    @RequestMapping(value = "/subscriber/all")
    public Set<String> subscribers() {
        return registry.getAllSubscriber();
    }

    @CrossOrigin
    @RequestMapping(value = "/subscribe/{subscriber}")
    public EmitterProcessor<ServerSentEvent<UserMessage>> register(@PathVariable String subscriber) {
        logger.info("Registering subscriber " + subscriber);
        return registry.put(subscriber);
    }

    @CrossOrigin
    @RequestMapping(value = "/unsubscribe/{subscriber}", method = RequestMethod.DELETE)
    public void remove(@PathVariable String subscriber) {
        logger.info("Removing subscriber " + subscriber);
        registry.remove(subscriber);
    }

    @RequestMapping(value = "/push/{subscriber}", method = RequestMethod.POST)
    public void push(@PathVariable String subscriber, @RequestBody String message) {
        logger.info("Pushing data to subscriber [" + subscriber + "]");
        sender.send(subscriber, message);
    }

    @RequestMapping(value = "/push/all")
    public void broadcast(@RequestBody String message) {
        logger.info("Broadcasting message");
        sender.broadcast(message);
    }

}
