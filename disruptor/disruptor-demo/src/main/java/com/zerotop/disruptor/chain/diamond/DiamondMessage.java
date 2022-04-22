package com.zerotop.disruptor.chain.diamond;

public class DiamondMessage {

    private long num;

    private boolean step1;
    private boolean step2;

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public boolean isStep1() {
        return step1;
    }

    public void setStep1(boolean step1) {
        this.step1 = step1;
    }

    public boolean isStep2() {
        return step2;
    }

    public void setStep2(boolean step2) {
        this.step2 = step2;
    }

    @Override
    public String toString() {
        return "Message{" +
                "num=" + num +
                "  step1=" + step1 +
                "  step2=" + step2 +
                '}';
    }
}
