package com.rp.service.controller;

import com.rp.service.data.UserMessage;
import com.rp.service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * @author rpradhan
 */

@RestController
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private NotificationService notificationService;

    @Autowired
    public RegistrationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/subscribe/{name}")
    public Flux<ServerSentEvent<UserMessage>> pushMessage(@PathVariable String name) {
        logger.info("Received Subscription for [" + name +"]");
        return Flux.from(notificationService.subscribe())
                .filter( message -> name.equals(message.getUserName()))
                .map(message -> ServerSentEvent.<UserMessage>builder()
                        .event("push")
                        .retry(Duration.ofSeconds(10))
                        .data(message).build())
                .doOnError( exception -> logger.error(exception.getMessage()))
                .doOnTerminate(() -> logger.info("Terminated"));
    }

    @ExceptionHandler
    public void exception(Exception ex){
        logger.error("Error" + ex.getMessage());
    }

}
