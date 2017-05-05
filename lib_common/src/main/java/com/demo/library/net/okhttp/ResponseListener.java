package com.demo.library.net.okhttp;


public interface ResponseListener {

    boolean onSuccess(String url, Object obj);

    boolean onFailure(int code, String url, String error);

}
