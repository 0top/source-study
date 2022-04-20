package com.zerotop.disruptor.mutiConsumer;

import com.lmax.disruptor.WorkHandler;

import java.time.temporal.ChronoUnit;

public class Consumer implements WorkHandler<Message> {
    private int consumerId;

    public Consumer(int consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public void onEvent(Message message) throws Exception {
        System.out.println("消费者：" + consumerId + "  消费：" + message.toString());
    }
}
