# disruptor

主要对disruptor原理研究

LMAX Disruptor是一个高性能的线程间消息传递库。它源于LMAX对并发性、性能和非阻塞算法的研究，如今已成为其Exchange基础设施的核心部分

## 相关文档

[Overview](Overview.md)

## 重点内容

- [事件驱动](事件驱动.md)
- [伪共享](伪共享.md)
- [ringbuffer](ringbuffer.md)

## 为什么快

1. 乐观锁
2. 底层使用数组，消除伪共享
3. ringbuffer内存屏障

## 单生产者

## 多生产者










