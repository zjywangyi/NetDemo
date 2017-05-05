package com.demo.library.net.okhttp;

import okhttp3.Headers;

public interface ResponseHeaderListener {

    void headers(String url, Headers headers);

}
