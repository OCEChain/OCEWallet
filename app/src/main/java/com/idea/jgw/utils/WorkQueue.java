package com.idea.jgw.utils;

import java.util.LinkedList;

/**
 * 
 * @Project    JavaDemos
 * @Package    com.java.thread
 * @author     chenlin
 * @version    1.0
 * @Date       2011年6月12日
 */
public class WorkQueue {

    private int threadCounts;// 线程池的大小
    private ThreadQueue[] threads;// 用数组实现线程池
    private LinkedList queue;// 任务队列

    public WorkQueue(int threadCounts) {
        this.threadCounts = threadCounts;
        this.threads = new ThreadQueue[threadCounts];
        this.queue = new LinkedList();
        for (int i = 0; i < threadCounts; i++) {
            threads[i] = new ThreadQueue();
            threads[i].start();// 启动所有工作线程
        }
    }

    public void execute(Runnable r) {
        synchronized (queue) {
            queue.addLast(r);
            queue.notify();
        }
    }

    private class ThreadQueue extends Thread {// 工作线程类
        @Override
        public void run() {
            Runnable r;
            while (true) {
                synchronized (queue) {
                    //
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    r = (Runnable) queue.removeFirst();// 有任务时，取出任务
                }
                r.run();
            }

        }
    }

    /**
     * 在静态方法里调用内部类必须是静态的
     */
    static class Mytask implements Runnable {// 任务接口
        public void run() {
            String name = Thread.currentThread().getName();
            try {
                Thread.sleep(100);// 模拟任务执行的时间
            } catch (InterruptedException e) {
            }
            System.out.println(name + " executed OK");
        }
    }

    public static void main(String[] args) {
        WorkQueue wq = new WorkQueue(10);// 10个工作线程  
        Mytask r[] = new Mytask[20];// 20个任务  
        for (int i = 0; i < 20; i++) {  
            r[i] = new Mytask();  
            wq.execute(r[i]);  
        }  
    }


}