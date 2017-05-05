package com.demo.library.net.okhttp.progress.handler;

import android.os.Message;

import com.demo.library.net.okhttp.ResponseListener;
import com.demo.library.net.okhttp.base.BaseResponseHandler;
import com.demo.library.net.okhttp.progress.ProgressInfo;
import com.demo.library.net.okhttp.progress.listener.ProgressListener;

public class ProgressHandler extends BaseResponseHandler implements ProgressListener {

    public ProgressHandler(String baseUrl, ResponseListener responseListener, ProgressListener progressListener) {
        super(baseUrl, responseListener, progressListener);
    }

    @Override
    public void onProgress(long currentBytes, long contentLength, boolean done) {
        Message msg = obtainMessage(UPDATE);

        ProgressInfo info;
        if (msg.obj == null) {
            info = new ProgressInfo(currentBytes, contentLength, done);
        } else {
            info = (ProgressInfo) msg.obj;

            info.setCurrentBytes(currentBytes);
            info.setContentLength(contentLength);
            info.setDone(done);
        }
        msg.obj = info;

        sendMessage(msg);
    }
}
