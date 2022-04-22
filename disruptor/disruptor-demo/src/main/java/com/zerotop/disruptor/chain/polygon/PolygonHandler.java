package com.zerotop.disruptor.chain.polygon;

import com.lmax.disruptor.EventHandler;

public class PolygonHandler implements EventHandler {
    private String step;

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public PolygonHandler(String step) {
        this.step = step;
    }

    @Override
    public void onEvent(Object o, long l, boolean b) throws Exception {
        System.out.println(step + "  " + l);
    }
}
