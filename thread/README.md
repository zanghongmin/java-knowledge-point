一、  停止线程
```
   Thread.interrupt() :  中断线程（可控制的关闭线程）
       1. 中断非阻塞线程
       2. 中断阻塞线程(wait、sleep、join阻塞中断,synchronized段以及Lock.lock()阻塞不可中断)
       3. 中断I/O阻塞线程（如实现interruptibleChannel接口的IO通过可中断,ServerSocket不可中断）
            
       * 没有任何语言方面的需求一个被中断的线程应该终止。中断一个线程只是为了引起该线程的注意，被中断线程可以决定如何应对中断。
       * 对于处于wait、sleep、join等操作的线程，如果被调用interrupt()后，会抛出InterruptedException，然后线程的中断标志位会由true重置为false，因为线程为了处理异常已经重新处于就绪状态。
       * 不可中断的操作，包括进入synchronized段以及Lock.lock()，inputSteam.read()等，调用interrupt()对于这几个问题无效，因为它们都不抛出中断异常。如果拿不到资源，它们会无限期阻塞下去。

   Thread.stop() : 不安全的方法（不控制的关闭线程）
      1. 即刻抛出ThreadDeath异常，在线程的run()方法内，任何一点都有可能抛出ThreadDeath Error，包括在catch或finally语句中。    
      2. 会释放该线程所持有的所有的锁，而这种释放是不可控制的，非预期的。
      进入synchronized段的，stop也会停止该线程，那么synchronized通常都是保护数据一致性的，那么stop会突然释放掉锁，可能造成数据不一致的
   
   volatile变量 :  线程内循环判断变量状态，退出线程
      1. 当线程内阻塞时，不能退出，如wait、sleep、join   
```
  
二、 线程状态、常用方法

```
    NEW：新建
    RUNNABLE：运行
    WAITING：无限期等待，等得其他线程显式地唤醒
        没有设置Timeout参数的Object.wait()；没有设置Timeout参数的Thread.wait()。
    TIMED_WAITING：限期等待，在一定时间之后会由系统自动唤醒。
        设置Timeout参数的Object.wait()；设置Timeout参数的Thread.wait()；Thread.sleep()方法。
    BLOCKED：阻塞，等待获取一个排它锁，等待进入一个同步区域。
    TERMINATED：结束
    
    object.wait()
        在其他线程调用此对象的notify()或者notifyAll()方法，或超过指定时间量前，当前线程T等待(线程T必须拥有该对象的锁)。
        线程T被放置在该对象的休息区中，并释放锁。
        在被唤醒、中断、超时的情况下，从对象的休息区中删除线程T，并重新进行线程调度。
        一旦线程T获得该对象的锁，该对象上的所有同步申明都被恢复到调用wait()方法时的状态，然后线程T从wait()方法返回。
        如果当前线程在等待之前或在等待时被任何线程中断，则会抛出 InterruptedException。在按上述形式恢复此对象的锁定状态时才会抛出此异常。在抛出此异常时，当前线程的中断状态被清除。
        只有该对象的锁被释放，并不会释放当前线程持有的其他同步资源。
    object.notify()
        唤醒在此对象锁上等待的单个线程。此方法只能由拥有该对象锁的线程来调用。
    Thread.sleep()
        在指定的毫秒数内让当前正在执行的线程休眠（暂停执行），此操作受到系统计时器和调度程序精度和准确性的影响。    
        监控状态依然保持、会自动恢复到可运行状态，不会释放对象锁。如果任何线程中断了当前线程。当抛出InterruptedException异常时，当前线程的中断状态被清除。让出CPU分配的执行时间。
    thread.join()：在一个线程对象上调用，使当前线程等待这个线程对象对应的线程结束。
    Thread.yield()：暂停当前正在执行的线程对象，并执行其他线程。
    thread.interrupt()：中断线程，停止其正在进行的一切。中断一个不处于活动状态的线程不会有任何作用。如果线程在调用Object类的wait()方法、或者join()、sleep()方法过程中受阻，则其中断状态将被清除，并收到一个InterruptedException。
    Thread.interrupted()：检测当前线程是否已经中断，并且清除线程的中断状态(回到非中断状态)。
    thread.isAlive()：如果线程已经启动且尚未终止，则为活动状态。
    thread.setDaemon()：需要在start()方法调用之前调用。当正在运行的线程都是后台线程时，Java虚拟机将退出。否则当主线程退出时，其他线程仍然会继续执行。

```
![线程状态转换图](threadstate.jpg)
