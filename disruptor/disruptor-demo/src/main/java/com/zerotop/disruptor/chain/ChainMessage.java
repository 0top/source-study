package com.zerotop.disruptor.chain;

public class ChainMessage {

    private long num;

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Message{" +
                "num=" + num +
                '}';
    }
}
