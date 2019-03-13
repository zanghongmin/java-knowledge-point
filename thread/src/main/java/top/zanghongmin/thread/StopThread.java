package top.zanghongmin.thread;

import java.io.IOException;
import java.net.ServerSocket;

public class StopThread extends Thread {


    public static void main(String[] args) throws Exception {

        final Object lock = new Object();
        //定义第一个线程，首先该线程拿到锁，而后等待3s,之后释放锁
        try {
            Thread t0 = new Thread() {
                public void run() {
                    try {
                        synchronized (lock) {
                            System.out.println("thread->" + getName()  + " acquire lock.");
                            sleep(3*1000);
                            System.out.println("thread->" + getName() + " 等待3s");
                            System.out.println("thread->" + getName()  + " release lock.");
                        }
                    } catch (Throwable ex) {

                        System.out.println("Caught in run: " + ex);
                        ex.printStackTrace();
                    }
                }
            };

            //定义第二个线程，等待拿到锁对象
            Thread t1 = new Thread() {
                public void run() {
                    synchronized (lock) {
                        System.out.println("thread->" + getName()  + " acquire lock.");
                    }
                }
            };

            //线程一先运行，先拿到lock
            t0.start();
            //而后主线程等待100ms,为了做延迟
            Thread.sleep(100);
            //停止线程一
            t0.stop();  //如果调用这个方法，那么t0线程抛出了ThreadDeath error并且t0线程释放了它所占有的锁
            //这时候在开启线程二
            t1.start();


        } catch (Throwable t) {
            System.out.println("Caught in main: " + t);
            t.printStackTrace();
        }

    }

    // 从上面的程序验证结果来看，thread.stop()确实是不安全的。
    // 它的不安全主要是：释放该线程所持有的所有的锁。一般任何进行加锁的代码块，
    // 都是为了保护数据的一致性，如果在调用thread.stop()后导致了该线程所持有的所有锁的突然释放(不可控制)，
    // 那么被保护数据就有可能呈现不一致性，其他线程在使用这些被破坏的数据时，有可能导致一些很奇怪的应用程序错误。

//    Java中多线程锁释放的条件：
//
//            1）执行完同步代码块，就会释放锁。（synchronized）
//            2）在执行同步代码块的过程中，遇到异常而导致线程终止，锁也会被释放。（exception）
//            3）在执行同步代码块的过程中，执行了锁所属对象的wait()方法，这个线程会释放锁，进入对象的等待池。(wait)
//
//    从上面的三点我就可以看到stop方法释放锁是在第二点的，通过抛出异常来释放锁，通过证明，这种方式是不安全的，不可靠的。



}
