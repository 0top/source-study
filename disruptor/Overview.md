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

- Ring Buffer 环形缓冲区：环形缓冲区通常被认为是破坏者的主要方面。然而，从3.0开始，环形缓冲区只负责存储和更新通过中断器移动的数据（事件）。对于一些高级用例，用户甚至可以完全替换它。

- Sequence 序列：破坏者使用序列作为一种手段来识别某个特定组件在哪里。每个消费者（事件处理器）和破坏者本身一样维护一个序列。大多数并发代码依赖于这些序列值的移动，因此该序列支持原子链的许多当前特性。事实上，两者之间唯一真正的区别在于序列包含额外的功能，以防止序列和其他值之间的错误共享。


- Sequencer 序列：序列是破坏者真正的核心。该接口的两个实现（单生产者、多生产者）实现了所有并行算法，用于生产者和消费者之间快速、正确地传递数据。

- Sequence Barrier（序列屏障）：Sequencer生成一个序列屏障，其中包含对Sequencer发布的主序列和任何依赖使用者序列的引用。它包含用于确定消费者是否可以处理任何事件的逻辑。

- Wait Strategy 等待策略：等待策略决定消费者将如何等待生产者将事件放入破坏者中。关于可选无锁的部分提供了更多详细信息。

- Event 事件：从生产者传递到消费者的数据单位。事件没有特定的代码表示，因为它完全由用户定义。

- Event Processor 事件处理器：主事件循环，用于处理来自干扰程序的事件，并拥有消费者序列的所有权。有一个称为BatchEventProcessor的表示，它包含事件循环的有效实现，并将调用已使用的EventHandler接口实现。


- Event Handler 事件处理程序：由用户实现的接口，代表破坏者的消费者。

- Producer 生产者：这是调用破坏者将事件排队的用户代码。这个概念在代码中也没有表示。

## 等待策略

- BusySpinWaitStrategy： 自旋等待，类似Linux Kernel使用的自旋锁。低延迟但同时对CPU资源的占用也多。
- BlockingWaitStrategy： 使用锁和条件变量。CPU资源的占用少，延迟大，默认等待策略。
- SleepingWaitStrategy： 在多次循环尝试不成功后，选择让出- CPU，等待下次调度，多次调度后仍不成功，尝试前睡眠一个纳秒级别的时间再尝试。这种策略平衡了延迟和CPU资源占用，但延迟不均匀。
- YieldingWaitStrategy： 在多次循环尝试不成功后，选择让出CPU，等待下次调。平衡了延迟和CPU资源占用，但延迟也比较均匀。
- PhasedBackoffWaitStrategy： 上面多种策略的综合，CPU资源的占用少，延迟大


