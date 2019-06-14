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
![线程状态转换图](threadstate.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/40)

三、线程安全及实现方法
```
了解了什么是线程安全之后，紧接着的一个问题就是我们应该如何实现线程安全，这听起来似乎是一件由代码如何编写来决定的事情，确实，如何实现线程安全与代码编写有很大的关系，但虚拟机提供的同步和锁机制也起到了非常重要的作用。
1. 互斥同步
互斥同步（Mutual Exclusion＆Synchronization）是常见的一种并发正确性保障手段。 同步是指在多个线程并发访问共享数据时，保证共享数据在同一个时刻只被一个（或者是一些，使用信号量的时候）线程使用。 而互斥是实现同步的一种手段，临界区（Critical Section）、 互斥量（Mutex）和信号量（Semaphore）都是主要的互斥实现方式。 因此，在这4个字里面，互斥是因，同步是果；互斥是方法，同步是目的。
1.1 synchronized
在Java中，最基本的互斥同步手段就是synchronized关键字，synchronized关键字经过编译之后，会在同步块的前后分别形成monitorenter和monitorexit这两个字节码指令，这两个字节码都需要一个reference类型的参数来指明要锁定和解锁的对象。 如果Java程序中的synchronized明确指定了对象参数，那就是这个对象的reference；如果没有明确指定，那就根据synchronized修饰的是实例方法还是类方法，去取对应的对象实例或Class对象来作为锁对象。
根据虚拟机规范的要求，在执行monitorenter指令时，首先要尝试获取对象的锁。 如果这个对象没被锁定，或者当前线程已经拥有了那个对象的锁，把锁的计数器加1，相应的，在执行monitorexit指令时会将锁计数器减1，当计数器为0时，锁就被释放。 如果获取对象锁失败，那当前线程就要阻塞等待，直到对象锁被另外一个线程释放为止。在虚拟机规范对monitorenter和monitorexit的行为描述中，有两点是需要特别注意的：
    synchronized同步块对同一条线程来说是可重入的，不会出现自己把自己锁死的问题；
    同步块在已进入的线程执行完之前，会阻塞后面其他线程的进入；
Java的线程是映射到操作系统的原生线程之上的，如果要阻塞或唤醒一个线程，都需要操作系统来帮忙完成，这就需要从用户态转换到核心态中，因此状态转换需要耗费很多的处理器时间。对于代码简单的同步块（如被synchronized修饰的getter()或setter()方法），状态转换消耗的时间有可能比用户代码执行的时间还要长。 所以synchronized是Java语言中一个重量级（Heavyweight）的操作，有经验的程序员都会在确实必要的情况下才使用这种操作。
 而虚拟机本身也会进行一些优化，譬如在通知操作系统阻塞线程之前加入一段自旋等待过程，避免频繁地切入到核心态之中。
1.2 ReentrantLock
除了synchronized之外，我们还可以使用java.util.concurrent（简称J.U.C）包中的重入锁（ReentrantLock）来实现同步，在基本用法上，ReentrantLock与synchronized很相似，他们都具备一样的线程重入特性，只是代码写法上有点区别，ReentrantLock表现为API层面的互斥锁（lock()和unlock()方法配合try/finally语句块来完成），synchronized则表现为原生语法层面的互斥锁。 
不过，相比synchronized，ReentrantLock增加了一些高级功能，主要有以下3项：等待可中断、 可实现公平锁，以及锁可以绑定多个条件。
    等待可中断是指当持有锁的线程长期不释放锁的时候，正在等待的线程可以选择放弃等待，改为处理其他事情，可中断特性对处理执行时间非常长的同步块很有帮助。
    公平锁是指多个线程在等待同一个锁时，必须按照申请锁的时间顺序来依次获得锁；而非公平锁则不保证这一点，在锁被释放时，任何一个等待锁的线程都有机会获得锁。synchronized中的锁是非公平的，ReentrantLock默认情况下也是非公平的，但可以通过带布尔值的构造函数要求使用公平锁。
    锁绑定多个条件是指一个ReentrantLock对象可以同时绑定多个Condition对象，而在synchronized中，锁对象的wait()和notify()或notifyAll()方法可以实现一个隐含的条件，如果要和多于一个的条件关联的时候，就不得不额外地添加一个锁，而ReentrantLock则无须这样做，只需要多次调用newCondition()方法即可。
如果需要使用上述功能，选用ReentrantLock是一个很好的选择，那如果是基于性能考虑呢？关于synchronized和ReentrantLock的性能问题，Brian Goetz对这两种锁在JDK 1.5与单核处理器，以及JDK 1.5与双Xeon处理器环境下做了一组吞吐量对比的实验，实验结果如下图所示：
从图中可以看出，多线程环境下synchronized的吞吐量下降得非常严重，而ReentrantLock则能基本保持在同一个比较稳定的水平上。 与其说ReentrantLock性能好，还不如说synchronized还有非常大的优化余地。 后续的技术发展也证明了这一点，JDK 1.6中加入了很多针对锁的优化措施，JDK 1.6发布之后，人们就发现synchronized与ReentrantLock的性能基本上是完全持平了。
 因此，如果读者的程序是使用JDK 1.6或以上部署的话，性能因素就不再是选择ReentrantLock的理由了，虚拟机在未来的性能改进中肯定也会更加偏向于原生的synchronized，所以还是提倡在synchronized能实现需求的情况下，优先考虑使用synchronized来进行同步。
2. 非阻塞同步
互斥同步最主要的问题就是进行线程阻塞和唤醒所带来的性能问题，因此这种同步也称为阻塞同步（Blocking Synchronization）。 
从处理问题的方式上说，互斥同步属于一种悲观的并发策略，总是认为只要不去做正确的同步措施（例如加锁），那就肯定会出现问题，无论共享数据是否真的会出现竞争，
它都要进行加锁（这里讨论的是概念模型，实际上虚拟机会优化掉很大一部分不必要的加锁）、 用户态核心态转换、 维护锁计数器和检查是否有被阻塞的线程需要唤醒等操作。
 随着硬件指令集的发展，我们有了另外一个选择：基于冲突检测的乐观并发策略，通俗地说，就是先进行操作，如果没有其他线程争用共享数据，那操作就成功了；
 如果共享数据有争用，产生了冲突，那就再采取其他的补偿措施（最常见的补偿措施就是不断地重试，直到成功为止），这种乐观的并发策略的许多实现都不需要把线程挂起，因此这种同步操作称为非阻塞同步（Non-Blocking Synchronization）。
为什么使用乐观并发策略需要“硬件指令集的发展”才能进行呢？
因为我们需要操作和冲突检测这两个步骤具备原子性，靠什么来保证呢？如果这里再使用互斥同步来保证就失去意义了，所以我们只能靠硬件来完成这件事情，硬件保证一个从语义上看起来需要多次操作的行为只通过一条处理器指令就能完成，这类指令常用的有：
    测试并设置（Test-and-Set）
    获取并增加（Fetch-and-Increment）
    交换（Swap）
    比较并交换（Compare-and-Swap，下文称CAS）
    加载链接/条件存储（Load-Linked/Store-Conditional，下文称LL/SC）。
其中，前面的3条是20世纪就已经存在于大多数指令集之中的处理器指令，后面的两条是现代处理器新增的，而且这两条指令的目的和功能是类似的。 在IA64、 x86指令集中有cmpxchg指令完成CAS功能，在sparc-TSO也有casa指令实现，而在ARM和PowerPC架构下，则需要使用一对ldrex/strex指令来完成LL/SC的功能。
CAS指令需要有3个操作数，分别是内存位置（在Java中可以简单理解为变量的内存地址，用V表示）、 旧的预期值（用A表示）和新值（用B表示）。 CAS指令执行时，当且仅当V符合旧预期值A时，处理器用新值B更新V的值，否则它就不执行更新，但是无论是否更新了V的值，都会返回V的旧值，上述的处理过程是一个原子操作。在JDK 1.5之后，Java程序中才可以使用CAS操作，
该操作由sun.misc.Unsafe类里面的compareAndSwapInt()和compareAndSwapLong()等几个方法包装提供，虚拟机在内部对这些方法做了特殊处理，即时编译出来的结果就是一条平台相关的处理器CAS指令，没有方法调用的过程，或者可以认为是无条件内联进去了。
由于Unsafe类不是提供给用户程序调用的类（Unsafe.getUnsafe()的代码中限制了只有启动类加载器（Bootstrap ClassLoader）加载的Class才能访问它），因此，如果不采用反射手段，我们只能通过其他的Java API来间接使用它，如J.U.C包里面的整数原子类，其中的compareAndSet()和getAndIncrement()等方法都使用了Unsafe类的CAS操作。
我们不妨拿一段使用volatile关键字没有解决的问题代码来看看如何使用CAS操作来避免阻塞同步，代码如下面所示。 我们曾经通过这段20个线程自增10000次的代码来证明volatile变量不具备原子性，那么如何才能让它具备原子性呢？把“race++”操作或increase()方法用同步块包裹起来当然是一个办法，但是如果改成如下所示的代码，那效率将会提高许多。
使用AtomicInteger代替int后，程序输出了正确的结果，一切都要归功于incrementAndGet()方法的原子性。它的实现其实非常简单，如下代码所示：
    /**
    *Atomically increment by one the current value.
    */
    public final int incrementAndGet(){
        for(;){
            int current=get();
            int next=current+1;
            if(compareAndSet(current,next))
                return next;
        }
    }
incrementAndGet()方法在一个无限循环中，不断尝试将一个比当前值大1的新值赋给自己。 如果失败了，那说明在执行“获取-设置”操作的时候值已经有了修改，于是再次循环进行下一次操作，直到设置成功为止。
尽管CAS看起来很美，但显然这种操作无法涵盖互斥同步的所有使用场景，并且CAS从语义上来说并不是完美的，存在这样的一个逻辑漏洞：如果一个变量V初次读取的时候是A值，并且在准备赋值的时候检查到它仍然为A值，那我们就能说它的值没有被其他线程改变过了吗？如果在这段期间它的值曾经被改成了B，后来又被改回为A，那CAS操作就会误认为它从来没有被改变过。这个漏洞称为CAS操作的“ABA”问题。
 J.U.C包为了解决这个问题，提供了一个带有标记的原子引用类“AtomicStampedReference”，它可以通过控制变量值的版本来保证CAS的正确性。 不过目前来说这个类比较“鸡肋”，大部分情况下ABA问题不会影响程序并发的正确性，如果需要解决ABA问题，改用传统的互斥同步可能会比原子类更高效。
3. 无同步方案
要保证线程安全，并不是一定就要进行同步，两者没有因果关系。 同步只是保证共享数据争用时的正确性的手段，如果一个方法本来就不涉及共享数据，那它自然就无须任何同步措施去保证正确性，因此会有一些代码天生就是线程安全的，这里简单地介绍其中的两类：可重入代码和线程本地存储。
3.1 可重入代码(Reentrant Code)
这种代码也叫做纯代码(Pure Code)，可以在代码执行的任何时刻中断它，转而去执行另外一段代码（包括递归调用它本身），而在控制权返回后，原来的程序不会出现任何错误。 相对线程安全来说，可重入性是更基本的特性，它可以保证线程安全，即所有的可重入的代码都是线程安全的，但是并非所有的线程安全的代码都是可重入的。
可重入代码有一些共同的特征，例如不依赖存储在堆上的数据和公用的系统资源、 用到的状态量都由参数中传入、 不调用非可重入的方法等。 我们可以通过一个简单的原则来判断代码是否具备可重入性：如果一个方法，它的返回结果是可以预测的，只要输入了相同的数据，就都能返回相同的结果，那它就满足可重入性的要求，当然也就是线程安全的。
3.2 线程本地存储(Thread Local Storage)
如果一段代码中所需要的数据必须与其他代码共享，那就看看这些共享数据的代码是否能保证在同一个线程中执行？如果能保证，我们就可以把共享数据的可见范围限制在同一个线程之内，这样，无须同步也能保证线程之间不出现数据争用的问题。符合这种特点的应用并不少见，大部分使用消费队列的架构模式（如“生产者-消费者”模式）都会将产品的消费过程尽量在一个线程中消费完，
其中最重要的一个应用实例就是经典Web交互模型中的“一个请求对应一个服务器线程”（Thread-per-Request）的处理方式，这种处理方式的广泛应用使得很多Web服务端应用都可以使用线程本地存储来解决线程安全问题。
Java语言中，如果一个变量要被多线程访问，可以使用volatile关键字声明它为“易变的”；如果一个变量要被某个线程独享，Java中就没有类似C++中__declspec(thread)这样的关键字，不过还是可以通过java.lang.ThreadLocal类来实现线程本地存储的功能。 每一个线程的Thread对象中都有一个ThreadLocalMap对象，这个对象存储了一组以ThreadLocal.threadLocalHashCode为键，
以本地线程变量为值的K-V值对，ThreadLocal对象就是当前线程的ThreadLocalMap的访问入口，每一个ThreadLocal对象都包含了一个独一无二的threadLocalHashCode值，使用这个值就可以在线程K-V值对中找回对应的本地线程变量。
```

四、web线程安全 servlet、struts2、springMVC、spring Bean线程安全
```
1. servlet，非线程安全
    Servlet体系结构是建立在Java多线程机制之上的，它的生命周期是由Web容器负责的。当客户端第一次请求某个Servlet时，Servlet容器将会根据web.xml配置文件实例化这个Servlet类。
    当有新的客户端请求该Servlet时，一般不会再实例化该Servlet类，也就是有多个线程在使用这个实例。因此对于servlet的成员变量，存在线程安全性问题
    如何保证servlet线程安全?
    1.1 实现 SingleThreadModel 接口 该接口指定了系统如何处理对同一个Servlet的调用。如果一个Servlet被这个接口指定,那么在这个Servlet中的service方法将不会有两个线程被同时执行，当然也就不存在线程安全的问题。
    1.2 同步对共享数据的操作 使用synchronized 关键字能保证一次只有一个线程可以访问被保护的区段，在本论文中的Servlet可以通过同步块操作来保证线程的安全.
    1.3 避免使用实例变量 本实例中的线程安全问题是由实例变量造成的，只要在Servlet里面的任何方法里面都不使用实例变量，那么该Servlet就是线程安全的。

2. springMVC，非线程安全，controller是单例，但它不依赖成员变量传参，因此基本没影响；单例不会创建对象和垃圾回收；
    spring的Controller默认是Singleton的，这意味着每个request过来，系统都会用原有的instance去处理，这样导致了两个结果:一是我们不用每次创建Controller，
    二是减少了对象创建和垃圾收集的时间;由于只有一个Controller的instance，当多个线程调用它的时候，它里面的instance变量就不是线程安全的了。
    如何保证 spring mvc 线程安全?
    2.1 Controller使用ThreadLocal成员变量。
    2.2 将spring mvc 的 Controller中声明 scope="prototype"，每次都创建新的controller .

3. struts2，线程安全，一个action请求，对应一个实例；
    Struts 2 Action对象为每一个请求产生一个实例，因此没有线程安全问题。所以我们可以在Struts2的Action里面去定义属性。但是Struts2由于 Action和普通的Java类没有任何区别
    （也就是不用像Struts1里面那样去实现一个Struts的接口，有兴趣的朋友可以自己去了解），所以我们可以用Spring去管理Struts2的Action，这个时候我们就要注意了，因为当我们在spring里面去定义bean的时候，
    spring默认用的是单例模式。所以在这个时候，你就要修改Spring的配置文件---即修改scope为prototype。
    为什么struts1中并没有考虑到线程问题，因为所有的代码都是写在execute的方法中，所有变量都是定义在里面，所以没有线程安全问题。 
    而现在的struts2就不一样了。struts2的action中就像一个POJO一样，定义了很多的类变量。这就有线程安全问题了。此时，就使用scope=prototype来指定是个原型模式，
    而不是单例，这样就解决了线程安全问题。每个线程都是一个新的实例.
4. Spring 单例Bean和Java 单例模式
    区别：Spring的的单例是基于BeanFactory也就是spring容器，单例Bean在此Spring容器内是单个的，Java的单例是基于JVM，每个JVM内一个单例。

```

五、线程池ThreadPoolExecutor

```
ThreadPoolExecutor作为java.util.concurrent包对外提供基础实现，以内部线程池的形式对外提供管理任务执行，线程调度，线程池管理等等服务；
    参数名 	                     作用
corePoolSize 	            核心线程池大小
maximumPoolSize 	        最大线程池大小
keepAliveTime 	            线程池中超过corePoolSize数目的空闲线程最大存活时间；可以allowCoreThreadTimeOut(true)使得核心线程有效时间
TimeUnit 	                keepAliveTime时间单位
workQueue 	                阻塞任务队列
threadFactory 	            新建线程工厂
RejectedExecutionHandler 	当提交任务数超过maxmumPoolSize+workQueue之和时，任务会交给RejectedExecutionHandler来处理

重点讲解：其中比较容易让人误解的是：corePoolSize，maximumPoolSize，workQueue之间关系。
    1.当线程池小于corePoolSize时，新提交任务将创建一个新线程执行任务，即使此时线程池中存在空闲线程。
    2.当线程池达到corePoolSize时，新提交任务将被放入workQueue中，等待线程池中任务调度执行
    3.当workQueue已满，且maximumPoolSize>corePoolSize时，新提交任务会创建新线程执行任务
    4.当提交任务数超过maximumPoolSize时，新提交任务由RejectedExecutionHandler处理
    5.当线程池中超过corePoolSize线程，空闲时间达到keepAliveTime时，关闭空闲线程
    6.当设置allowCoreThreadTimeOut(true)时，线程池中corePoolSize线程空闲时间达到keepAliveTime也将关闭

构造方法中的字段含义如下：
    corePoolSize：核心线程数量，当有新任务在execute()方法提交时，会执行以下判断：
        如果运行的线程少于 corePoolSize，则创建新线程来处理任务，即使线程池中的其他线程是空闲的；
        如果线程池中的线程数量大于等于 corePoolSize 且小于 maximumPoolSize，则只有当workQueue满时才创建新的线程去处理任务；
        如果设置的corePoolSize 和 maximumPoolSize相同，则创建的线程池的大小是固定的，这时如果有新任务提交，若workQueue未满，则将请求放入workQueue中，等待有空闲的线程去从workQueue中取任务并处理；
        如果运行的线程数量大于等于maximumPoolSize，这时如果workQueue已经满了，则通过handler所指定的策略来处理任务；
    所以，任务提交时，判断的顺序为 corePoolSize –> workQueue –> maximumPoolSize ->  handler。
    maximumPoolSize：最大线程数量；
    workQueue：等待队列，当任务提交时，如果线程池中的线程数量大于等于corePoolSize的时候，把该任务封装成一个Worker对象放入等待队列；
    workQueue：保存等待执行的任务的阻塞队列，当提交一个新的任务到线程池以后, 线程池会根据当前线程池中正在运行着的线程的数量来决定对该任务的处理方式，主要有以下几种处理方式:
        直接切换：这种方式常用的队列是SynchronousQueue，但现在还没有研究过该队列，这里暂时还没法介绍；
        使用无界队列：一般使用基于链表的阻塞队列LinkedBlockingQueue。如果使用这种方式，那么线程池中能够创建的最大线程数就是corePoolSize，而maximumPoolSize就不会起作用了（后面也会说到）。当线程池中所有的核心线程都是RUNNING状态时，这时一个新的任务提交就会放入等待队列中。
        使用有界队列：一般使用ArrayBlockingQueue。使用该方式可以将线程池的最大线程数量限制为maximumPoolSize，这样能够降低资源的消耗，但同时这种方式也使得线程池对线程的调度变得更困难，因为线程池和队列的容量都是有限的值，所以要想使线程池处理任务的吞吐率达到一个相对合理的范围，又想使线程调度相对简单，并且还要尽可能的降低线程池对资源的消耗，就需要合理的设置这两个数量。
            如果要想降低系统资源的消耗（包括CPU的使用率，操作系统资源的消耗，上下文环境切换的开销等）, 可以设置较大的队列容量和较小的线程池容量, 但这样也会降低线程处理任务的吞吐量。
            如果提交的任务经常发生阻塞，那么可以考虑通过调用 setMaximumPoolSize() 方法来重新设定线程池的容量。
            如果队列的容量设置的较小，通常需要将线程池的容量设置大一点，这样CPU的使用率会相对的高一些。但如果线程池的容量设置的过大，则在提交的任务数量太多的情况下，并发量会增加，那么线程之间的调度就是一个要考虑的问题，因为这样反而有可能降低处理任务的吞吐量。
    keepAliveTime：线程池维护线程所允许的空闲时间。当线程池中的线程数量大于corePoolSize的时候，如果这时没有新的任务提交，核心线程外的线程不会立即销毁，而是会等待，直到等待的时间超过了keepAliveTime；
    threadFactory：它是ThreadFactory类型的变量，用来创建新线程。默认使用Executors.defaultThreadFactory() 来创建线程。使用默认的ThreadFactory来创建线程时，会使新创建的线程具有相同的NORM_PRIORITY优先级并且是非守护线程，同时也设置了线程的名称。
    handler：它是RejectedExecutionHandler类型的变量，表示线程池的饱和策略。如果阻塞队列满了并且没有空闲的线程，这时如果继续提交任务，就需要采取一种策略处理该任务。线程池提供了4种策略：
        AbortPolicy：直接抛出异常，这是默认策略；
        CallerRunsPolicy：用调用者所在的线程来执行任务；
        DiscardOldestPolicy：丢弃阻塞队列中靠最前的任务，并执行当前任务；
        DiscardPolicy：直接丢弃任务；
        
Executors提供的线程池配置方案
Executors方法提供的线程服务，都是通过参数设置来实现不同的线程池机制。     
     1、构造一个固定线程数目的线程池，配置的corePoolSize与maximumPoolSize大小相同，同时使用了一个无界LinkedBlockingQueue存放阻塞任务，因此多余的任务将存在再阻塞队列，不会由RejectedExecutionHandler处理     
     public static ExecutorService newFixedThreadPool(int nThreads) {  
         return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()); }
     
     2、构造一个缓冲功能的线程池，配置corePoolSize=0，maximumPoolSize=Integer.MAX_VALUE，keepAliveTime=60s,以及一个无容量的阻塞队列 SynchronousQueue，因此任务提交之后，将会创建新的线程执行；线程空闲超过60s将会销毁     
     public static ExecutorService newCachedThreadPool() {  
         return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()); }
    3、构造一个只支持一个线程的线程池，配置corePoolSize=maximumPoolSize=1，无界阻塞队列LinkedBlockingQueue；保证任务由一个线程串行执行     
     public static ExecutorService newSingleThreadExecutor() {  
         return new FinalizableDelegatedExecutorService (new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));  
     }     
     4、构造有定时功能的线程池，配置corePoolSize，无界延迟阻塞队列DelayedWorkQueue；有意思的是：maximumPoolSize=Integer.MAX_VALUE，由于DelayedWorkQueue是无界队列，所以这个值是没有意义的     
     public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {  
         return new ScheduledThreadPoolExecutor(corePoolSize);  
     }       
     public static ScheduledExecutorService newScheduledThreadPool(  
                 int corePoolSize, ThreadFactory threadFactory) {  
         return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);  
     }       
     // detail
     public ScheduledThreadPoolExecutor(int corePoolSize,  
                                  ThreadFactory threadFactory) {  
         super(corePoolSize, Integer.MAX_VALUE, 0, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), threadFactory);  
     }
     
总结：
     1、用ThreadPoolExecutor自定义线程池，看线程是的用途，如果任务量不大，可以用无界队列，如果任务量非常大，要用有界队列，防止OOM
     2、如果任务量很大，还要求每个任务都处理成功，要对提交的任务进行阻塞提交，重写拒绝机制，改为阻塞提交。保证不抛弃一个任务
     
     // 重写RejectedExecutionHandler，阻塞提交任务，不抛弃任务
     private class CustomRejectedExecutionHandler implements RejectedExecutionHandler {    
         @Override    
         public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {    
             try {  
                                 // 核心改造点，由blockingqueue的offer改成put阻塞方法  
                 executor.getQueue().put(r);  
             } catch (InterruptedException e) {  
                 e.printStackTrace();  
             }  
         }    
     }
     
     3、最大线程数一般设为2N+1最好，N是CPU核数
     4、核心线程数，看应用，如果是任务，一天跑一次，设置为0，合适，因为跑完就停掉了，如果是常用线程池，看任务量，是保留一个核心还是几个核心线程数
     5、如果要获取任务执行结果，用CompletionService，但是注意，获取任务的结果的要重新开一个线程获取，如果在主线程获取，就要等任务都提交后才获取，就会阻塞大量任务结果，队列过大OOM，所以最好异步开个线程获取结果
   


```


六、ScheduledThreadPoolExecutor原理

```
https://blog.csdn.net/luanmousheng/article/details/77816412

核心点：DelayedWorkQueue和take方法
DelayedWorkQueue：队列内存放要运行的Runnable任务，任务按照距离最近运行时间排序，越早运行排在队头。队列使用堆排序
DelayedWorkQueue中的Runnable任务实现Delayed接口，使用getDelay表示当前任务还要多久运行
        public long getDelay(TimeUnit unit) {
            return unit.convert(time - now(), NANOSECONDS);
        }
take：线程池来取任务时，获取队头的Runnable任务，任务delay时间小于0，直接返回给线程进行运行，大于0时当前线程等待available.awaitNanos(delay);
获取任务并运行后，设置下一次运行任务到队列中
            if (ScheduledFutureTask.super.runAndReset()) {
                setNextRunTime();
                reExecutePeriodic(outerTask);
            }
             

```

七、生产消费者模型

```
生产消费者模型A：“LinkedBlockingQueue + Executors.newCachedThreadPool()”方式

package com.xxl.util.core.skill.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生产消费者模型，“FIFO队列，线程池，异步”，“Executors.newCachedThreadPool() + LinkedBlockingQueue”方式实现
 * 特点：启动时初始化指定数量Producer、Consumer线程；Producer负责push数据到queue中，Consumer负责从queue中pull数据并处理；
 * @author xuxueli 2015-9-1 18:05:56
 */
public class ThreadPoolQueueHelper {
    private static Logger logger = LoggerFactory.getLogger(ThreadPoolLinkedHelper.class);

    private ExecutorService executor = Executors.newCachedThreadPool();
    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>(0xfff8);

    public ThreadPoolQueueHelper(){
        // producer thread, can be replaced by method "pushData()"
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    pushData("data-" + System.currentTimeMillis());
                    try {
                        TimeUnit.SECONDS.sleep(3L);
                    } catch (InterruptedException e) {
                        logger.info("ThreadPoolQueueHelper producer error：", e);
                    }
                }
            }
        });
        // consumer thread
        for (int i = 0; i < 2; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String originData;
                        try {
                            originData = queue.poll(5L, TimeUnit.SECONDS);
                            logger.info("poll:{}", originData);
                            if (originData == null) {
                                TimeUnit.SECONDS.sleep(5L);
                            }
                        } catch (InterruptedException e) {
                            logger.info("ThreadPoolQueueHelper consumer error：", e);
                        }
                    }
                }
            });
        }
    }

    private static ThreadPoolQueueHelper helper = new ThreadPoolQueueHelper();
    public static ThreadPoolQueueHelper getInstance(){
        return helper;
    }

    public static boolean pushData(String originData){
        boolean status = false;
        if (originData != null && originData.trim().length()>0) {
            status = getInstance().queue.offer(originData);
        }
        logger.info("offer data:{}, status:{}", originData, status);
        return status;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            pushData("data" + i);
        }
    }

}

原理：该模型中每个消息对应阻塞队列中一条数据；生产者线程负责向阻塞队列Push消息，消费者线程将会监听阻塞队列并消费队列中消息；

分析：

    1、Executors.newCachedThreadPool()：构造一个缓冲功能的线程池，配置corePoolSize=0，maximumPoolSize=Integer.MAX_VALUE，keepAliveTime=60s,以及一个无容量的阻塞队列 SynchronousQueue，因此任务提交之后，将会创建新的线程执行；线程空闲超过60s将会销毁 ；
    2、LinkedBlockingQueue：构造了一个容量为0xfff8的线程安全的阻塞队列。
    3、pushData()：提供给生产者线程使用的方法，Push数据到共享阻塞队列；

生产消费者模型B：“ThreadPoolExecutor”方式

package com.xxl.util.core.skill.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生产消费者模型，“FIFO队列，线程池，异步”，“ThreadPoolExecutor”方式实现
 * 特点：启动时初始化ThreadPool(内部线程使用LinkedBlockingQueue维护)，以及Producer线程；Producer负责为每条数据在ThreadPool中创建新的线程，每个线程run方法即消费者逻辑方法；
 * @author xuxueli 2015-9-1 16:57:16
 */
public class ThreadPoolLinkedHelper {
    private static Logger logger = LoggerFactory.getLogger(ThreadPoolLinkedHelper.class);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 60L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE),
                new ThreadPoolExecutor.CallerRunsPolicy());

    public ThreadPoolLinkedHelper(){
        // producer thread, can be replaced by method "pushData()"
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    pushData("data-" + System.currentTimeMillis());
                    try {
                        TimeUnit.SECONDS.sleep(3L);
                    } catch (InterruptedException e) {
                        logger.info("ThreadPoolQueueHelper producer error：", e);
                    }
                }
            }
        });
        // consumer thread, is replaced by each thread's run method
    }

    private static ThreadPoolLinkedHelper helper = new ThreadPoolLinkedHelper();
    public static ThreadPoolLinkedHelper getInstance(){
        return helper;
    }

    public static void pushData(final String originData) {
        logger.info("producer data:" + originData);
        getInstance().executor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("consumer data:" + originData);
            }
        });
    }

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            pushData("data" + i);
        }
    }

}

原理：该模型中每个消息将会生成一个线程并托管给线程池维护，线程池将会异步执行内部托管的线程，从而实现消费功能；

分析：

    1、ThreadPoolExecutor：构造一个缓冲功能的线程池，配置corePoolSize=0，maximumPoolSize=Integer.MAX_VALUE，keepAliveTime=60s,以及一个容量Integer.MAX_VALUE的阻塞队列，因此任务提交之后，将会创建新的线程执行；线程空闲超过60s将会销毁；ThreadPoolExecutor.CallerRunsPolicy，当线程池满新线程不在入队列，将会直接执行run方法；
    2、pushData(): 提供给生产者线程使用的方法, 每个消息生成一个生产线程并交给线程池维护；

```

