# disruptor

LMAX Disruptor是一个高性能的线程间消息传递库。它源于LMAX对并发性、性能和非阻塞算法的研究，如今已成为其Exchange基础设施的核心部分

## 相关文档

[Overview](Overview.md)

## 重点内容

- [处理链路](链路.md)
- [伪共享](伪共享.md)
- [ringbuffer](ringbuffer.md)

## 为什么快

1. ringbuffer无锁队列，使用CAS
2. ringbuffer底层使用数组,预先分配数组内存，避免频繁gc    
3. ringbuffer使用padding消除伪共享
4. ringbuffer内存屏障

## 链路

支持单消费者，多消费者
SingleProducerSequencer
MultiProducerSequencer








