package com.zerotop.disruptor.mutiConsumer;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MutiCustomMain {
    public static void main(String[] args) throws Exception {
        int bufferSize = 512;

        Disruptor<Message> disruptor = new Disruptor<Message>(Message::new, bufferSize, DaemonThreadFactory.INSTANCE);

        disruptor.handleEventsWith((event, sequence, endOfBatch) ->
                System.out.println("Event: " + event))
                .handleEventsWith((event, sequence, endOfBatch) ->
                        System.out.println("Event2: " + event));
        disruptor.start();

        //创建ringbuffer
        RingBuffer<Message> ringBuffer = RingBuffer.create(ProducerType.MULTI,
                new EventFactory<Message>() {
                    @Override
                    public Message newInstance() {
                        return new Message();
                    }
                },
                16 * 16,
                new BlockingWaitStrategy());

        //通过ringbuffer 创建一个屏障
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        //创建多个消费者
        Consumer[] consumers = new Consumer[5];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer(i);
        }

        //构建多消费者工作池
        WorkerPool<Message> workerPool = new WorkerPool<Message>(
                ringBuffer,
                sequenceBarrier,
                new FatalExceptionHandler(),
                consumers);

        //将sequence添加到gatingsequence中追踪
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());

        workerPool.start(Executors.newFixedThreadPool(5));

        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; l < 10; l++)
        {
            bb.putLong(0, l);
            ringBuffer.publishEvent((event, sequence, buffer) -> event.setNum(buffer.getLong(0)), bb);
            Thread.sleep(100);
        }
    }
}
