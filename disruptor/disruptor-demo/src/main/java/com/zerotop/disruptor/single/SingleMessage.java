package com.zerotop.disruptor.single;

public class SingleMessage {
    private long num;

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "SingleMessage{" +
                "num=" + num +
                '}';
    }
}
