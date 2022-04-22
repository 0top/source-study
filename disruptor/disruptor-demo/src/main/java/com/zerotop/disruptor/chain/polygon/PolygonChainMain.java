package com.zerotop.disruptor.chain.polygon;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.zerotop.disruptor.chain.diamond.DiamondMessage;

import java.nio.ByteBuffer;

public class PolygonChainMain {
    public static void main(String[] args) throws Exception {
        int bufferSize = 512;

        Disruptor<DiamondMessage> disruptor = new Disruptor<DiamondMessage>(DiamondMessage::new, bufferSize, DaemonThreadFactory.INSTANCE);

        PolygonHandler aChain1 = new PolygonHandler(" chain A 1   done");
        PolygonHandler aChain2 = new PolygonHandler(" chain A 1-2 done ");
        PolygonHandler bChain1 = new PolygonHandler(" chain B 1   done");
        PolygonHandler bChain2 = new PolygonHandler(" chain B 1-2 done ");
        PolygonHandler end = new PolygonHandler(" --------------------- chain A-B join done");


        // 前两个并行步骤执行顺序随机
        disruptor.handleEventsWith(aChain1, bChain1);
        disruptor.after(aChain1).handleEventsWith(aChain2);
        disruptor.after(bChain1).handleEventsWith(bChain2);

        disruptor.after(aChain2, bChain2).then(end);
        disruptor.start();

        RingBuffer<DiamondMessage> ringBuffer = disruptor.getRingBuffer();
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; l < 2; l++)
        {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer) -> event.setNum(buffer.getLong(0)), bb);
            Thread.sleep(100);
        }
    }
}
