package top.zanghongmin.thread;

import java.io.IOException;
import java.net.ServerSocket;

public class InterruptedThread extends Thread {


    public static void main(String[] args) throws Exception {

//        Thread newThread = new Thread(blockThread);
//        newThread.start();
//        Thread.sleep(3000);
//        System.out.println("Asking thread to stop...");
//        newThread.interrupt();// 等中断信号量设置后再调用
//        Thread.sleep(3000);
//        System.out.println("Stopping application...");
//
//        newThread = new Thread(notblockThread);
//        newThread.start();
//        Thread.sleep(3000);
//        System.out.println("Asking thread to stop...");
//        newThread.interrupt();// 等中断信号量设置后再调用
//        Thread.sleep(3000);
//        System.out.println("Stopping application...");


        Thread newThread = new Thread(IOblockThread);
        newThread.start();
        Thread.sleep(3000);
        System.out.println("Asking thread to stop...");
        newThread.interrupt();// 等中断信号量设置后再调用、
        //socket.close(); //不能中断ServerSocket的阻塞线程，除非close
        Thread.sleep(3000);
        System.out.println("Stopping application...");
    }


    //非阻塞线程
    static Runnable notblockThread = new Runnable() {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Thread is running...");
                long time = System.currentTimeMillis();
                // 使用while循环模拟 sleep
                while ((System.currentTimeMillis() - time < 1000)) {
                }
            }
        }
    };
    //阻塞线程
    static Runnable blockThread = new Runnable() {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Thread running...");
                try {
                    /*
                     * 如果线程阻塞，将不会去检查中断信号量stop变量，所 以thread.interrupt()
                     * 会使阻塞线程从阻塞的地方抛出异常，让阻塞线程从阻塞状态逃离出来，并
                     * 进行异常块进行 相应的处理
                     * wait,sleep,join可中断
                     */
                    Thread.sleep(1000);// 线程阻塞，如果线程收到中断操作信号将抛出异常
                } catch (InterruptedException e) {
                    System.out.println("Thread interrupted...");
                    /*
                     * 如果线程在调用 Object.wait()方法，或者该类的 join() 、sleep()方法
                     * 过程中受阻，则其中断状态将被清除
                     */
                    System.out.println(Thread.currentThread().isInterrupted());// false

                    //中不中断由自己决定，如果需要真真中断线程，则需要重新设置中断位，如果
                    //不需要，则不用调用
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Thread exiting under request...");
        }
    };
    //I/O线程
    static volatile ServerSocket socket;
    static Runnable IOblockThread = new Runnable() {
        public void run() {
            try {
                socket = new ServerSocket(8888);
            } catch (IOException e) {
                System.out.println("Could not create the socket...");
                return;
            }
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Waiting for connection...");
                try {
                    socket.accept();
                } catch (IOException e) {
                    System.out.println("accept() failed or interrupted...");
                    Thread.currentThread().interrupt();//重新设置中断标示位
                }
            }
            System.out.println("Thread exiting under request...");
        }
    };

}
