package top.zanghongmin.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;


public class Something {



    private Buffer mBuf = new Buffer();

    public void produce() {
        System.out.println("produce1: " + Thread.currentThread().getName());
        synchronized (this) {
            System.out.println("produce2: " + Thread.currentThread().getName());
            while (mBuf.isFull()) {
                try {
                    System.out.println("produce3: " + Thread.currentThread().getName());
                    wait();
                    System.out.println("produce4: " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mBuf.add();
            notifyAll();
        }
        System.out.println("produce6: " + Thread.currentThread().getName());
    }

    public void consume() {
        System.out.println("consume1: " + Thread.currentThread().getName());
        synchronized (this) {
            System.out.println("consume2: " + Thread.currentThread().getName());
            while (mBuf.isEmpty()) {
                try {
                    System.out.println("consume3: " + Thread.currentThread().getName());
                    wait();
                    System.out.println("consume4: " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mBuf.remove();
            notifyAll();
        }
        System.out.println("consume6: " + Thread.currentThread().getName());
    }

    private class Buffer {
        private static final int MAX_CAPACITY = 1;
        private List innerList = new ArrayList<>(MAX_CAPACITY);

        void add() {
            if (isFull()) {
                throw new IndexOutOfBoundsException();
            } else {
                innerList.add(new Object());
            }
            System.out.println(Thread.currentThread().getName() + " add");

        }

        void remove() {
            if (isEmpty()) {
                throw new IndexOutOfBoundsException();
            } else {
                innerList.remove(MAX_CAPACITY - 1);
            }
            System.out.println(Thread.currentThread().getName() + " remove");
        }

        boolean isEmpty() {
            return innerList.isEmpty();
        }

        boolean isFull() {
            return innerList.size() == MAX_CAPACITY;
        }
    }

    public static void main(String[] args) {
        Something sth = new Something();
        Runnable runProduce = new Runnable() {
            int count = 4;

            @Override
            public void run() {
                while (count-- > 0) {
                    sth.produce();
                }
            }
        };
        Runnable runConsume = new Runnable() {
            int count = 4;

            @Override
            public void run() {
                while (count-- > 0) {
                    sth.consume();
                }
            }
        };
//        for (int i = 0; i < 2; i++) {
//            new Thread(runConsume).start();
//        }
//        for (int i = 0; i < 1; i++) {
//            new Thread(runProduce).start();
//        }

        ConcurrentSkipListSet<String> uuids = new ConcurrentSkipListSet<String>();
        uuids.add("1");
        uuids.add("1");
        uuids.add("1");
        System.out.println(uuids);
    }
}
