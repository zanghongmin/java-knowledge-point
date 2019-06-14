package top.zanghongmin.lock;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SynchronizedExample {

    public static void main(String[] args) {

//        ExecutorService executorService = Executors.newCachedThreadPool();
//        executorService.execute(() -> SynchronizedExample.func1());
//        executorService.execute(() -> SynchronizedExample.func2());

    }


    public static synchronized  void func1() {

            for (int i = 0; i < 10000; i++) {
                System.out.println(i + " ");
            }
    }

    public static synchronized  void func2() {

        for (int i = 0; i < 10; i++) {
            System.out.print(i + " ");
        }
    }



}
