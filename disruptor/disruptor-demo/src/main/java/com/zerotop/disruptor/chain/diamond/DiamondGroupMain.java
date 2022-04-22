package com.zerotop.disruptor.chain.diamond;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;

public class DiamondGroupMain {
    public static void main(String[] args) throws Exception {
        int bufferSize = 512;

        Disruptor<DiamondMessage> disruptor = new Disruptor<DiamondMessage>(DiamondMessage::new, bufferSize, DaemonThreadFactory.INSTANCE);

        // 前两个并行步骤执行顺序随机
        disruptor.handleEventsWith(
                     (event, sequence, endOfBatch) -> { event.setStep1(true); System.out.println("chain1 -- sub step1: " + event);},
                     (event, sequence, endOfBatch) -> { event.setStep2(true); System.out.println("chain1 -- sub step2: " + event);}
                )
                .then(
                        (event, sequence, endOfBatch) -> System.out.println("chain2: " + event)
                );
        disruptor.start();

        RingBuffer<DiamondMessage> ringBuffer = disruptor.getRingBuffer();
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; l < 5; l++)
        {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer) -> event.setNum(buffer.getLong(0)), bb);
            Thread.sleep(100);
        }
    }
}
