### ringbuffer

底层使用数组，数组预先定义好，可以避免频繁垃圾回收

通过名为cursor的sequence指定队列头

只有一个指针表示当前消费下标，消费到数组最后，再重新开始，才去覆盖方式


##

![ringbuffer](img/ringbuffer/ringbuffer1.png)


![ringbuffer](img/ringbuffer/ringbufferconsumer.png)


## Sequencer

- SingleProducerSequencer
    单生产者

- MultiProducerSequencer
    多线程写入时使用

## sequenceBarrier

消费者使用时间处理器序号屏障


## Sequence 序列号

递增序列号，通过cas操作，线程安全，并且使用padding避免伪共享，使用引用


## 单消费者

消费者使用EventHandler

## 多生产者多消费者

 消费者的整体逻辑：多个消费者共同使用同一个Sequence即workSequence，大家都从这个sequence里取得序列号，通过CAS保证线程安全，然后每个消费者拿到序列号nextSequence后去和RingBuffer的cursor比较，即生产者生产到的最大序列号比较，如果自己要取的序号还没有被生产者生产出来，则等待生产者生成出来后再从RingBuffer中取数据，处理数据

消费者使用workHandler，监听委托给线程池

屏障
workpool

根据每个WorkHandler创建对应的WorkProcessor，同一个workpool中的消费者线程共享同一个sequenceBarrier,workSequence，

        WorkerPool<T> workerPool = new WorkerPool<T>(
                ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                consumers);


## 参考

- [写入 Ringbuffer](https://ifeve.com/disruptor-writing-ringbuffer/)