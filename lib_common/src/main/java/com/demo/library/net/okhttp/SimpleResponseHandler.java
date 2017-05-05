package com.demo.library.net.okhttp;

import com.demo.library.net.okhttp.base.BaseResponseHandler;

public class SimpleResponseHandler extends BaseResponseHandler {

    public SimpleResponseHandler(String baseUrl, ResponseListener listener) {
        super(baseUrl, listener);
    }

}
