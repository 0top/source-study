package com.zerotop.disruptor.mutiConsumer;

public class MutiMessage {

    private long num;

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "MutiMessage{" +
                "num=" + num +
                '}';
    }
}
