package com.rp.service.push;

/**
 * @author rpradhan
 */
public interface Sender {

    void send(String subscriber, String message);

    void broadcast(String message);
}
