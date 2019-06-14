package top.zanghongmin.thread;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadState {


    public static void main(String[] args) {
        final  ReentrantLock lock=new ReentrantLock();
        final  Object object = new Object();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.unlock();
            }
        });



        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (object){
                    try {
                        object.wait();
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (object){
                    try {
                        Thread.sleep(5000);
                        object.notify();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                LockSupport.park();
            }
        });

        t1.run();
//        t2.run();
//        t3.run();
//        t4.run();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(t1.getState());
        System.out.println(t2.getState());
        System.out.println(t3.getState());
        System.out.println(t4.getState());
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(t1.getState());
        System.out.println(t2.getState());
        System.out.println(t3.getState());
        System.out.println(t4.getState());

    }
}
