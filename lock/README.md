一、 锁synchronized和Lock

| tips | synchronized | Lock 
| :---: | :---: | :---: |
|存在层次|Java的关键字，在jvm层面上 | 是一个类
|锁的释放 | 1、以获取锁的线程执行完同步代码，释放锁 2、线程执行发生异常，jvm会让线程释放锁 | 在finally中必须释放锁，不然容易造成线程死锁
|锁的获取 | 假设A线程获得锁，B线程等待。如果A线程阻塞，B线程会一直等待 | 分情况而定，Lock有多个锁获取的方式，lock、trylock等
|锁状态  | 无法判断 | 可以判断
|锁类型  | 可重入 不可中断 非公平 | 可重入 可中断 公平或非公平都有
|性能 | 少量同步时性能比Lock好 | 大量同步时性能比synchronized好


 
 
二、 可重入锁ReentrantLock 
    
```
await和signal
 
```

二、 LockSupport（park/unpark）

```
https://www.cnblogs.com/qingquanzi/p/8228422.html
concurrent包是基于AQS (AbstractQueuedSynchronizer)框架的，AQS框架借助于两个类：

    Unsafe（提供CAS操作）
    LockSupport（提供park/unpark操作）
    因此，LockSupport非常重要。
两个重点
（1）操作对象
归根结底，LockSupport.park()和LockSupport.unpark(Thread thread)调用的是Unsafe中的native代码：
//LockSupport中
public static void park() {
        UNSAFE.park(false, 0L);
    }

//LockSupport中
public static void unpark(Thread thread) {
        if (thread != null)
            UNSAFE.unpark(thread);
    }

Unsafe类中的对应方法：
    //park
    public native void park(boolean isAbsolute, long time);
    
    //unpack
    public native void unpark(Object var1);

park函数是将当前调用Thread阻塞，而unpark函数则是将指定线程Thread唤醒。
与Object类的wait/notify机制相比，park/unpark有两个优点：

以thread为操作对象更符合阻塞线程的直观定义
操作更精准，可以准确地唤醒某一个线程（notify随机唤醒一个线程，notifyAll唤醒所有等待的线程），增加了灵活性。

（2）关于“许可”
在上面的文字中，我使用了阻塞和唤醒，是为了和wait/notify做对比。
其实park/unpark的设计原理核心是“许可”：park是等待一个许可，unpark是为某线程提供一个许可。
如果某线程A调用park，那么除非另外一个线程调用unpark(A)给A一个许可，否则线程A将阻塞在park操作上。
有一点比较难理解的，是unpark操作可以再park操作之前。
也就是说，先提供许可。当某线程调用park时，已经有许可了，它就消费这个许可，然后可以继续运行。这其实是必须的。
考虑最简单的生产者(Producer)消费者(Consumer)模型：Consumer需要消费一个资源，于是调用park操作等待；Producer则生产资源，然后调用unpark给予Consumer使用的许可。
非常有可能的一种情况是，Producer先生产，这时候Consumer可能还没有构造好（比如线程还没启动，或者还没切换到该线程）。那么等Consumer准备好要消费时，
显然这时候资源已经生产好了，可以直接用，那么park操作当然可以直接运行下去。如果没有这个语义，那将非常难以操作。
但是这个“许可”是不能叠加的，“许可”是一次性的。
比如线程B连续调用了三次unpark函数，当线程A调用park函数就使用掉这个“许可”，如果线程A再次调用park，则进入等待状态。

https://www.jianshu.com/p/e3afe8ab8364

Unsafe.park和Unsafe.unpark的底层实现原理
在Linux系统下，是用的Posix线程库pthread中的mutex（互斥量），condition（条件变量）来实现的。
mutex和condition保护了一个_counter的变量，当park时，这个变量被设置为0，当unpark时，这个变量被设置为1。

作者：SinX竟然被占用了
链接：https://www.jianshu.com/p/e3afe8ab8364
来源：简书
简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。
 
```
