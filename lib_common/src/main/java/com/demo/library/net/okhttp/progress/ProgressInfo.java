package com.demo.library.net.okhttp.progress;

import java.io.Serializable;

public class ProgressInfo implements Serializable {

    // 当前字节长度
    private long currentBytes;

    // 总字节长度
    private long contentLength;

    // 是否读取完成
    private boolean done;

    public ProgressInfo(long currentBytes, long contentLength, boolean done) {
        this.currentBytes = currentBytes;
        this.contentLength = contentLength;
        this.done = done;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }

    public void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "ProgressInfo{" +
                "currentBytes=" + currentBytes +
                ", contentLength=" + contentLength +
                ", done=" + done +
                '}';
    }
}
