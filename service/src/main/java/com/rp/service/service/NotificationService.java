package com.rp.service.service;

import com.rp.service.data.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author rpradhan
 */
@Service
public class NotificationService {

    private final List<String> userLogins = Arrays.asList("rishabh", "qazi", "adam", "ali");

    private final List<String> messages = Arrays.asList("Hello", "Hi", "How are you?", "Good");

    public Flux<UserMessage> subscribe() {
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(2));
        Flux<UserMessage> message = Flux.fromStream( Stream.generate( () -> new UserMessage(getRandomUser(), getRandomMessage(), new Date())));
        return Flux.zip(interval, message).map(Tuple2::getT2);
    }


    private String getRandomUser() {
        return userLogins.get(new Random().nextInt(userLogins.size()));
    }

    private String getRandomMessage() {
        return messages.get(new Random().nextInt(messages.size()));
    }
}
