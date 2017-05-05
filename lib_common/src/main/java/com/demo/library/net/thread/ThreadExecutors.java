package com.demo.library.net.thread;

import com.demo.library.net.thread.executor.CachedThreadPool;
import com.demo.library.net.thread.executor.ScheduledThreadPool;
import com.demo.library.net.thread.executor.base.Base;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class ThreadExecutors extends Base {

    private volatile static ThreadExecutors mInstance;

    private ExecutorService mThreadPool;

    private ScheduledExecutorService mScheduledExecutor;

    private ThreadExecutors() {
    }

    public static ThreadExecutors get() {
        if (mInstance == null) {
            synchronized (ThreadExecutors.class) {
                if (mInstance == null) {
                    mInstance = new ThreadExecutors();
                }
            }
        }

        return mInstance;
    }


    public void setThreadPool(ExecutorService threadPool) {
        this.mThreadPool = threadPool;
    }

    public void setScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
        this.mScheduledExecutor = scheduledExecutor;
    }

    public ExecutorService getThreadPool() {
        if (mThreadPool == null) {
            mThreadPool = CachedThreadPool.get().getThreadPool();
        }

        return mThreadPool;
    }

    public ScheduledExecutorService getScheduledExecutor() {
        if (mScheduledExecutor == null) {
            mScheduledExecutor = ScheduledThreadPool.get().getScheduledExecutor();
        }

        return mScheduledExecutor;
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


    public void executeSchedule(Runnable task) {
        check(task);

        getScheduledExecutor().execute(task);
    }

    public <T> Future<T> submitSchedule(Callable<T> task) {
        check(task);

        return getScheduledExecutor().submit(task);
    }

    public Future<?> submitSchedule(Runnable task) {
        check(task);

        return getScheduledExecutor().submit(task);
    }

    public <T> Future<T> submitSchedule(Runnable task, T result) {
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

    public List<Runnable> shutdownScheduleNow() {
        if (mScheduledExecutor != null && !mScheduledExecutor.isShutdown()) {
            return mScheduledExecutor.shutdownNow();
        }

        return null;
    }

    public boolean isShutdownSchedule() {
        return mScheduledExecutor == null || mScheduledExecutor.isShutdown();
    }

    public boolean isTerminatedSchedule() {
        return mScheduledExecutor == null || mScheduledExecutor.isTerminated();
    }


    public void shutdown() {
        if (mThreadPool != null && !mThreadPool.isShutdown()) {
            mThreadPool.shutdown();
        }

        if (mScheduledExecutor != null && !mScheduledExecutor.isShutdown()) {
            mScheduledExecutor.shutdown();
        }
    }

}
