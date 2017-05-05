package com.demo.library.net.okhttp.progress.down;

import android.os.Message;

import com.demo.library.net.NetUtils;
import com.demo.library.net.okhttp.HttpConstants;
import com.demo.library.net.okhttp.base.BaseResponseHandler;
import com.demo.library.net.okhttp.progress.listener.ProgressListener;
import com.demo.library.utils.C;
import com.demo.library.utils.L;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.demo.library.net.okhttp.base.BaseResponseHandler.FAILURE;
import static com.demo.library.net.okhttp.base.BaseResponseHandler.START;
import static com.demo.library.net.okhttp.base.BaseResponseHandler.SUCCESS;

public class DownTask implements Runnable {

    private final static int MAX_BUFFER_SIZE = 1024;

    private String mPath;
    private OkHttpClient mClient;
    private Request mRequest;
    private BaseResponseHandler mHandler;
    private ProgressListener mProgressListener;

    public DownTask(String filePath, OkHttpClient client, Request request, BaseResponseHandler handler, ProgressListener progressListener) {
        this.mPath = filePath;
        this.mClient = client;
        this.mRequest = request;
        this.mHandler = handler;
        this.mProgressListener = progressListener;
    }

    @Override
    public void run() {

        // start
        L.syso("down start");
        sendMessage(START, -1, null);

        if (!NetUtils.isNetworkAvailable(C.get())) {
            // fail
            L.e("down error : network is not available");
            sendMessage(FAILURE, HttpConstants.FAILURE_CODE_NETWORK_NO, "network is not available");
            return;
        }

        try {

            mClient.newCall(mRequest).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    // fail
                    L.e("onFailure : " + e.getMessage());
                    sendMessage(FAILURE, HttpConstants.FAILURE_CODE_DEFAULT, e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response == null) {
                        // fail
                        L.e("onResponse : response is null");
                        sendMessage(FAILURE, HttpConstants.FAILURE_CODE_DEFAULT, HttpConstants.DEFAULT_ERROR);
                        return;
                    }

                    int code = response.code();

                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        long contentLength = body.contentLength();
                        InputStream is = body.byteStream();
                        FileOutputStream fos = new FileOutputStream(mPath);
                        byte[] buf = new byte[MAX_BUFFER_SIZE];
                        int len = 0;
                        long currentBytes = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            currentBytes += len;

                            if (mProgressListener != null) {
                                mProgressListener.onProgress(currentBytes, contentLength, currentBytes == contentLength);
                            }
                        }

                        try {
                            is.close();
                        } catch (Exception e) {
                            // ignore
                        } finally {
                            is = null;
                        }

                        try {
                            fos.flush();
                            fos.close();
                        } catch (Exception e) {
                            // ignore
                        } finally {
                            fos = null;
                        }

                        // success
                        L.syso("down success");
                        sendMessage(SUCCESS, -1, "success");
                    } else {
                        // fail
                        L.e("down fail : " + response.toString());
                        sendMessage(FAILURE, code, response.toString());
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();

            // fail
            L.e("down error : " + e.getMessage());
            sendMessage(FAILURE, HttpConstants.FAILURE_CODE_DEFAULT, e.getMessage());
        }
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
