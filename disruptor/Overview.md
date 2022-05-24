# disruptor

主要对disruptor原理研究

[官方文档](https://lmax-exchange.github.io/disruptor/user-guide/index.html)

## 简介

disruptor是一款高性能线程消息传递库  
使用ringbuffer无锁队列  
解决了伪共享问题  
可以链式处理数据，支持多链路，join

![avatar](img/models.png)

## 核心概念

- Ring Buffer 环形缓冲区：曾经RingBuffer是Disruptor中的最主要的对象，但从3.0版本开始，其职责被简化为仅仅负责对通过Disruptor进行交换的数据（事件）进行存储和更新。在一些更高级的应用场景中，Ring Buffer 可以由用户的自定义实现来完全替代。

- Sequence 序列：通过顺序递增的序号来编号管理通过其进行交换的数据（事件），对数据(事件)的处理过程总是沿着序号逐个递增处理。一个 Sequence 用于跟踪标识某个特定的事件处理者( RingBuffer/Consumer )的处理进度。虽然一个 AtomicLong 也可以用于标识进度，但定义 Sequence 来负责该问题还有另一个目的，那就是防止不同的 Sequence 之间的CPU缓存伪共享(Flase Sharing)问题。


- Sequencer 序列：Sequencer 是 Disruptor 的真正核心。此接口有两个实现类 SingleProducerSequencer、MultiProducerSequencer ，它们定义在生产者和消费者之间快速、正确地传递数据的并发算法。

- Sequence Barrier（序列屏障）：用于保持对RingBuffer的 main published Sequence 和Consumer依赖的其它Consumer的 Sequence 的引用。 Sequence Barrier 还定义了决定 Consumer 是否还有可处理的事件的逻辑。

- Wait Strategy 等待策略：定义 Consumer 如何进行等待下一个事件的策略。 （注：Disruptor 定义了多种不同的策略，针对不同的场景，提供了不一样的性能表现）

- Event 事件：在 Disruptor 的语义中，生产者和消费者之间进行交换的数据被称为事件(Event)。它不是一个被 Disruptor 定义的特定类型，而是由 Disruptor 的使用者定义并指定。

- Event Processor 事件处理器：EventProcessor 持有特定消费者(Consumer)的 Sequence，并提供用于调用事件处理实现的事件循环(Event Loop)。

- Event Handler 事件处理程序：Disruptor 定义的事件处理接口，由用户实现，用于处理事件，是 Consumer 的真正实现。

- Producer 生产者：即生产者，只是泛指调用 Disruptor 发布事件的用户代码，Disruptor 没有定义特定接口或类型。

## 等待策略

- BusySpinWaitStrategy： 自旋等待，类似Linux Kernel使用的自旋锁。低延迟但同时对CPU资源的占用也多。
- BlockingWaitStrategy： 使用锁和条件变量。CPU资源的占用少，延迟大，默认等待策略。
- SleepingWaitStrategy： 在多次循环尝试不成功后，选择让出- CPU，等待下次调度，多次调度后仍不成功，尝试前睡眠一个纳秒级别的时间再尝试。这种策略平衡了延迟和CPU资源占用，但延迟不均匀。
- YieldingWaitStrategy： 在多次循环尝试不成功后，选择让出CPU，等待下次调。平衡了延迟和CPU资源占用，但延迟也比较均匀。
- PhasedBackoffWaitStrategy： 上面多种策略的综合，CPU资源的占用少，延迟大


