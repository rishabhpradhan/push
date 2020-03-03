package com.rp.service.push;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rpradhan
 */
@Component
public class PushConnectionRegistry<T> {

    private final ConcurrentHashMap<String, EmitterProcessor<ServerSentEvent<T>>> registry = new ConcurrentHashMap<>();

    public EmitterProcessor<ServerSentEvent<T>> get(final String subscriber) {
        return registry.get(subscriber);
    }

    public EmitterProcessor<ServerSentEvent<T>> remove (final String subscriber) {
        final EmitterProcessor<ServerSentEvent<T>> emitter = registry.remove(subscriber);
        return emitter;
    }

    public EmitterProcessor<ServerSentEvent<T>> put ( final String subscriber ) {
        final EmitterProcessor<ServerSentEvent<T>> emitter = EmitterProcessor.create();
        if (registry.get(subscriber) != null) {
            registry.remove(subscriber);
        }
        registry.put(subscriber, emitter);
        return emitter;
    }

    public int size() {
        return registry.size();
    }

    public Set<String> getAllSubscriber() {
        return registry.keySet();
    }

}
