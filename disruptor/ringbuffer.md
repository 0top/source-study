### ringbuffer

底层使用数组，数组预先定义好，可以避免频繁垃圾回收

通过名为cursor的sequence指定队列头


##

![ringbuffer](img/ringbuffer/ringbuffer1.png)


![ringbuffer](img/ringbuffer/ringbufferconsumer.png)


## Sequence 序列号

递增序列号，通过cas操作，线程安全，并且使用padding避免伪共享，使用引用


## 单消费者

消费者使用EventHandler

## 多生产者多消费者

消费者使用workHandler，监听委托给线程池

屏障
workpool

根据每个WorkHandler创建对应的WorkProcessor，同一个workpool中的消费者线程共享同一个sequenceBarrier,workSequence，

        WorkerPool<T> workerPool = new WorkerPool<T>(
                ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                consumers);
