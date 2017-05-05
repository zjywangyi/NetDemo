package com.demo.library.net.okhttp;

import android.os.Message;

import com.demo.library.net.NetUtils;
import com.demo.library.net.ResponseFactory;
import com.demo.library.net.okhttp.base.BaseResponseHandler;
import com.demo.library.utils.C;
import com.demo.library.utils.L;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.demo.library.net.okhttp.base.BaseResponseHandler.FAILURE;
import static com.demo.library.net.okhttp.base.BaseResponseHandler.START;
import static com.demo.library.net.okhttp.base.BaseResponseHandler.SUCCESS;

public class RequestTask implements Runnable {

    private static final String TAG = "RequestTask";

    private String mBaseUrl;
    private OkHttpClient mClient;
    private Request mRequest;
    private BaseResponseHandler mHandler;
    private Class<?> mCls;
    private long startTime = 0;

    private ResponseHeaderListener responseHeaderListener;

    public RequestTask(String baseUrl, OkHttpClient client, Request request, BaseResponseHandler handler) {
        this.mBaseUrl = baseUrl;
        this.mClient = client;
        this.mRequest = request;
        this.mHandler = handler;
    }

    public RequestTask(String baseUrl, OkHttpClient client, Request request, BaseResponseHandler handler, Class<?> cls) {
        this.mBaseUrl = baseUrl;
        this.mClient = client;
        this.mRequest = request;
        this.mHandler = handler;
        this.mCls = cls;
    }

    public void setResponseHeaderListener(ResponseHeaderListener listener) {
        responseHeaderListener = listener;
    }

    @Override
    public void run() {

        // start
        L.syso("request start");
        sendMessage(START, -1, null);

        if (!NetUtils.isNetworkAvailable(C.get())) {
            // fail
            L.syso("request url : " + mBaseUrl);
            L.e(TAG, "request error : network is not available");
            fail(HttpConstants.FAILURE_CODE_NETWORK_NO, "network is not available");
            return;
        }

        try {
            startTime = System.currentTimeMillis();
            mClient.newCall(mRequest).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    // fail
                    L.syso("request url : " + mBaseUrl);
                    L.syso("request time : " + (System.currentTimeMillis() - startTime));
                    L.e(TAG, "onFailure : " + e.getMessage());
                    fail(HttpConstants.FAILURE_CODE_DEFAULT, HttpConstants.DEFAULT_ERROR);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    L.syso("request url : " + mBaseUrl);
                    L.syso("request time : " + (System.currentTimeMillis() - startTime));
                    if (response == null) {
                        // fail
                        L.e(TAG, "onResponse : response is null");
                        fail(HttpConstants.FAILURE_CODE_DEFAULT, HttpConstants.DEFAULT_ERROR);
                        return;
                    }

//                    int code = response.code();

                    String responseString = "without response body";
                    if (response.body() != null) {
                        responseString = response.body().string();
                    }

                    L.syso("response : " + responseString);

                    if (response.isSuccessful()) {
                        Object obj;
                        if (mCls != null) {
                            long time = System.currentTimeMillis();
                            obj = ResponseFactory.handleResponse(responseString, mCls);
                            L.syso("parse json time : " + (System.currentTimeMillis() - time));
                        } else {
                            obj = responseString;
                        }

                        if (obj != null) {
                            // success
                            L.syso("response success");

//                            ResponseFactory.handleHeaders(mBaseUrl, response.headers());
                            if (responseHeaderListener != null) {
                                responseHeaderListener.headers(mBaseUrl, response.headers());
                            }

                            sendMessage(SUCCESS, -1, obj);

                        } else {
                            //  parse error
                            L.e(TAG, "response parse error : " + response.toString());
                            fail(HttpConstants.FAILURE_CODE_PARSE_ERR, "parse error");
                        }
                    } else {
                        // fail
                        L.e(TAG, "response fail : " + response.toString());
                        fail(HttpConstants.FAILURE_CODE_DEFAULT, "response fail");
                    }
                }

            });
        } catch (Throwable e) {
            e.printStackTrace();

            // fail
            L.syso("request url : " + mBaseUrl);
            L.e(TAG, "response error : " + e.getMessage());
            fail(HttpConstants.FAILURE_CODE_DEFAULT, "response error");
        }

    }

    private void fail(int code, Object obj) {
        sendMessage(FAILURE, code, obj);
    }

    private void sendMessage(int what, int code, Object obj) {
        if (mHandler != null) {
            Message msg = mHandler.obtainMessage(what);
            msg.arg1 = code;
            msg.obj = obj;

            msg.sendToTarget();
        }
    }
}
