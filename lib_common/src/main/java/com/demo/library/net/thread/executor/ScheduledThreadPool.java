package com.demo.library.net.thread.executor;

import com.demo.library.net.thread.executor.base.Base;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPool extends Base {

    private volatile static ScheduledThreadPool mInstance;

    private ScheduledExecutorService mScheduledExecutor;

    private ScheduledThreadPool() {
    }

    public static ScheduledThreadPool get() {
        if (mInstance == null) {
            synchronized (ScheduledThreadPool.class) {
                if (mInstance == null) {
                    mInstance = new ScheduledThreadPool();
                }
            }
        }

        return mInstance;
    }


    public ScheduledExecutorService getScheduledExecutor() {
        if (mScheduledExecutor == null) {
            synchronized (ScheduledThreadPool.class) {
                if (mScheduledExecutor == null) {
                    mScheduledExecutor = Executors.newScheduledThreadPool(CORE_POOL_SIZE);
                }
            }
        }

        return mScheduledExecutor;
    }


    public void execute(Runnable task) {
        check(task);

        getScheduledExecutor().execute(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        check(task);

        return getScheduledExecutor().submit(task);
    }

    public Future<?> submit(Runnable task) {
        check(task);

        return getScheduledExecutor().submit(task);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        check(task);

        return getScheduledExecutor().submit(task, result);
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        check(callable);

        return getScheduledExecutor().schedule(callable, delay, unit);
    }

    public ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        check(task);

        return getScheduledExecutor().schedule(task, delay, unit);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        check(task);

        return getScheduledExecutor().scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long initialDelay, long delay, TimeUnit unit) {
        check(task);

        return getScheduledExecutor().scheduleWithFixedDelay(task, initialDelay, delay, unit);
    }

    public void shutdown() {
        if (mScheduledExecutor != null && !mScheduledExecutor.isShutdown()) {
            mScheduledExecutor.shutdown();
        }
    }

    public List<Runnable> shutdownNow() {
        if (mScheduledExecutor != null && !mScheduledExecutor.isShutdown()) {
            return mScheduledExecutor.shutdownNow();
        }

        return null;
    }

    public boolean isShutdown() {
        return mScheduledExecutor == null || mScheduledExecutor.isShutdown();
    }

    public boolean isTerminated() {
        return mScheduledExecutor == null || mScheduledExecutor.isTerminated();
    }

}
