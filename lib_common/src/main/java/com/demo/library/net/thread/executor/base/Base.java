package com.demo.library.net.thread.executor.base;

public abstract class Base {

    protected final static int CORE_POOL_SIZE = 5;

    protected final static int MAX_POOL_SIZE = 8;

    public void check(Object obj) {
        if (obj == null) {
            throw new NullPointerException("param must not be null!");
        }
    }

}
