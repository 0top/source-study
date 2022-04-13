package com.zerotop.disruptor.handlerchain;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.zerotop.disruptor.muti.MutiMessage;

import java.nio.ByteBuffer;

public class MutiCustomMain {
    public static void main(String[] args) throws Exception {
        int bufferSize = 512;

        Disruptor<MutiMessage> disruptor = new Disruptor<MutiMessage>(MutiMessage::new, bufferSize, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith((event, sequence, endOfBatch) ->
                System.out.println("Event: " + event))
                .handleEventsWith((event, sequence, endOfBatch) ->
                        System.out.println("Event2: " + event));
        disruptor.start();

        RingBuffer<MutiMessage> ringBuffer = disruptor.getRingBuffer();
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; l < 10; l++)
        {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer) -> event.setNum(buffer.getLong(0)), bb);
            Thread.sleep(100);
        }
    }
}
