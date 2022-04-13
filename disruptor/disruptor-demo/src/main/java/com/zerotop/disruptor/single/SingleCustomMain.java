package com.zerotop.disruptor.single;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;

public class SingleCustomMain {
    public static void main(String[] args) throws Exception {
        int bufferSize = 512;

        Disruptor<SingleMessage> disruptor = new Disruptor<SingleMessage>(SingleMessage::new, bufferSize, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith((event, sequence, endOfBatch) ->
                System.out.println("Event: " + event));
        disruptor.start();

        RingBuffer<SingleMessage> ringBuffer = disruptor.getRingBuffer();
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; l < 10; l++)
        {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer) -> event.setNum(buffer.getLong(0)), bb);
            Thread.sleep(100);
        }
    }
}
