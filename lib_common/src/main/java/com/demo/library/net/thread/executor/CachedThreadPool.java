package com.demo.library.net.thread.executor;

import com.demo.library.net.thread.executor.base.Base;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CachedThreadPool extends Base {

    private volatile static CachedThreadPool mInstance;

    private ExecutorService mThreadPool;

    private CachedThreadPool() {
    }

    public static CachedThreadPool get() {
        if (mInstance == null) {
            synchronized (CachedThreadPool.class) {
                if (mInstance == null) {
                    mInstance = new CachedThreadPool();
                }
            }
        }

        return mInstance;
    }


    public ExecutorService getThreadPool() {
        if (mThreadPool == null) {
            synchronized (CachedThreadPool.class) {
                if (mThreadPool == null) {
                    mThreadPool = Executors.newCachedThreadPool();
                }
            }
        }

        return mThreadPool;
    }


    public void execute(Runnable task) {
        check(task);

        getThreadPool().execute(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        check(task);

        return getThreadPool().submit(task);
    }

    public Future<?> submit(Runnable task) {
        check(task);

        return getThreadPool().submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        check(task);

        return getThreadPool().submit(task, result);
    }

    public void shutdown() {
        if (mThreadPool != null && !mThreadPool.isShutdown()) {
            mThreadPool.shutdown();
        }
    }

    public List<Runnable> shutdownNow() {
        if (mThreadPool != null && !mThreadPool.isShutdown()) {
            return mThreadPool.shutdownNow();
        }

        return null;
    }

    public boolean isShutdown() {
        return mThreadPool == null || mThreadPool.isShutdown();
    }

    public boolean isTerminated() {
        return mThreadPool == null || mThreadPool.isTerminated();
    }

}
