package top.zanghongmin.lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 流程：在调用await()方法前线程必须获得重入锁（第17行代码），调用await()方法后线程会释放当前占用的锁。
 * 同理在调用signal()方法时当前线程也必须获得相应重入锁（代码32行），调用signal()方法后系统会从condition.await()等待队列中唤醒一个线程。
 * 当线程被唤醒后，它就会尝试重新获得与之绑定的重入锁，一旦获取成功将继续执行。所以调用signal()方法后一定要释放当前占用的锁（代码41行），
 * 这样被唤醒的线程才能有获得锁的机会，才能继续执行。
 */
public class ReentrantLocktTest {
    public static ReentrantLock lock=new ReentrantLock();
    public static Condition condition =lock.newCondition();
    public static void main(String[] args) {
        new Thread(){
            @Override
            public void run() {
                lock.lock();//请求锁
                try{
                    System.out.println(Thread.currentThread().getName()+"==》进入等待");
                    condition.await();//设置当前线程进入等待
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    lock.unlock();//释放锁
                }
                System.out.println(Thread.currentThread().getName()+"==》继续执行");
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                lock.lock();//请求锁
                try{
                    System.out.println(Thread.currentThread().getName()+"=》进入");
                    Thread.sleep(2000);//休息2秒
                    condition.signal();//随机唤醒等待队列中的一个线程
                    System.out.println(Thread.currentThread().getName()+"休息结束");
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }finally{
                    lock.unlock();//释放锁
                }
            }
        }.start();
    }
}