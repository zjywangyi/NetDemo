package com.demo.library.net.okhttp.progress.listener;

public interface ProgressListener {

    void onProgress(long currentBytes, long contentLength, boolean done);

}
