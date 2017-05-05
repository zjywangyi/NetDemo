package com.demo.library.net.okhttp;

import com.demo.library.net.thread.ThreadExecutors;

import java.util.concurrent.ExecutorService;

import okhttp3.Headers;

public class HttpConfig {

    private ExecutorService mThreadPool;

    private Headers.Builder mHeaderBuilder;

    private int mConnectTimeOut;

    private int mReadTimeOut;

    private int mWriteTimeOut;


    public ExecutorService getThreadPool() {
        return mThreadPool;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.mThreadPool = threadPool;
    }

    public Headers.Builder getHeaderBuilder() {
        return mHeaderBuilder;
    }

    public void setHeaderBuilder(Headers.Builder headerBuilder) {
        this.mHeaderBuilder = headerBuilder;
    }

    public int getConnectTimeOut() {
        return mConnectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.mConnectTimeOut = connectTimeOut;
    }

    public int getReadTimeOut() {
        return mReadTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.mReadTimeOut = readTimeOut;
    }

    public int getWriteTimeOut() {
        return mWriteTimeOut;
    }

    public void setWriteTimeOut(int writeTimeOut) {
        this.mWriteTimeOut = writeTimeOut;
    }


    public HttpConfig(Builder builder) {
        this.mThreadPool = builder.threadPool;
        this.mHeaderBuilder = builder.headerBuilder;
        this.mConnectTimeOut = builder.connectTimeOut;
        this.mReadTimeOut = builder.readTimeOut;
        this.mWriteTimeOut = builder.writeTimeOut;
    }

    public static HttpConfig createDefault() {
        return new Builder().build();
    }

    public void release() {
        try {
            if (mHeaderBuilder != null) {
                mHeaderBuilder = null;
            }

            if (mThreadPool != null) {
                mThreadPool.shutdownNow();
                mThreadPool = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class Builder {
        private ExecutorService threadPool;

        private Headers.Builder headerBuilder;

        private int connectTimeOut;

        private int readTimeOut;

        private int writeTimeOut;

        public Builder() {
            this.connectTimeOut = HttpConstants.DEFAULT_CONNECT_TIME_OUT;
            this.readTimeOut = HttpConstants.DEFAULT_READ_TIME_OUT;
            this.writeTimeOut = HttpConstants.DEFAULT_WRITE_TIME_OUT;
            this.threadPool = ThreadExecutors.get().getThreadPool();

            this.headerBuilder = new Headers.Builder();
            this.headerBuilder.add("User-Agent", HttpConstants.USER_AGENT);
        }

        public Builder ThreadPool(ExecutorService threadPool) {
            this.threadPool = threadPool;
            return this;
        }

        public Builder HeaderBuilder(Headers.Builder headerBuilder) {
            this.headerBuilder = headerBuilder;
            return this;
        }

        public Builder ConnectTimeOut(int connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
            return this;
        }

        public Builder ReadTimeOut(int readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        public Builder WriteTimeOut(int writeTimeOut) {
            this.writeTimeOut = writeTimeOut;
            return this;
        }

        public HttpConfig build() {
            return new HttpConfig(this);
        }

    }

}
