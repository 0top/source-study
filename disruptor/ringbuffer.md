## ringbuffer

- 底层使用数组，数组预先定义好，可以避免频繁垃圾回收
- 通过名为cursor的sequence计算下标，sequence单调递增，通过按位与计算下标sequence & (array length－1)，所以disruptor的大小应该是2的倍数
- 通过sequence使用cas获取下一个下标，实现无锁
- 只有一个cursor表示当前生产者发布的下标，采取数据覆盖方式


## cursor

是一个sequence，实质是一个通过padding修饰过得volatile变量，可避免伪共享
cas实现无锁

## Sequencer接口

继承了cursored(游标) 和 sequenced(生产者查询发布)
主要用来生产者发布，消费者查询

abstractSequence实现sequencer接口，主要维护了标识生产者进度的cursor(Sequence对象)，消费者进度的gatingSequences(Sequencep[]对象)

- SingleProducerSequencer
    单生产者

- MultiProducerSequencer
    多线程写入时使用

## Sequence 序列号

    递增序列号，通过cas操作，线程安全，并且使用padding避免伪共享，使用引用

    注： sequence序列号是一直递增的，通过算法计算出对应ringbuffer下标值
    sequence & (array length－1)

## sequenceBarrier
    设置消费依赖
    消费者使用时间处理器序号屏障
    屏障会等待当前序列号为期望序列号后进行数据处理
    sequencer.newBarrier(sequencesToTrack); 最终dependentSequence = cursorSequence = cursor
    即SingleProducerSequencer=已发布最大sequence，生产者只能消费到ringbuffer最新发布后的solt
      MultiProducerSequencer=已申请最大sequence

## producerBarriers

    producerBarrier用于提交生产者写入的entity
    写入时需要通过ConsumerTrackingProducerBarrier 判断当前消费者消费下标

![PreventRingFromWrapping](img/ringbuffer/PreventRingFromWrapping.png)

    上图为一个生产者写入ringbuffer


    多生产者写入 通过ClaimStrategy确定当前生产者是否可以写入
![ProducersNextEntry](img/ringbuffer/ProducersNextEntry.png)


       
        updateGatingSequencesForNextInChain(barrierSequences, processorSequences);
        barrierSequences=ringbuffer的cursor
        processorSequences=当前链路的eventhandler列表生成的batchProcessor列表

        ringBuffer.addGatingSequences(processorSequences);


## 内存屏障

sequence的cursor使用volatile 
sequenceBarrier作为消费者的屏障，通过sequence确定当前是否可以消费

## 单消费者

消费者使用EventHandler

    - 写入顺序

        多线程写操作

        每个线程会获取对应next，当前线程会等待cursor变为期望cursor执行写入操作，从而保证写操作与提交顺序无关，而与抢占的位置的先后顺序有关，抢在靠前顺序的一定会先写入。

        写操作保证是原子的，事务及无锁。


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

## publish发布

事件发布
1. 通过sequence申请下一个cursor
2. sequenceBarrier.waitFor保证链路消费, WaitStrategy.waitFor 等待策略
3. 申请好后，等待可以写入后，写入完成后发布
    发布即通过WaitStrategy的condition通知其他线程有新数据

start其实就是启动新的线程

## sequenceBarrier.waitFor

通过waitFor等待期望消费的下标到达消费的位置，或者是依赖的消费者已消费完成
程序间共用等待策略，通过等待策略唤醒消费者

## 参考

- [disruptor](https://www.jianshu.com/p/bad7b4b44e48)
- [写入 Ringbuffer](https://ifeve.com/disruptor-writing-ringbuffer/)