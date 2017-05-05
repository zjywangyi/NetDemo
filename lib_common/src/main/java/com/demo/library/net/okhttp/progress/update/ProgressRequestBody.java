package com.demo.library.net.okhttp.progress.update;

import com.demo.library.net.okhttp.progress.listener.ProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 封装的上传请求体,处理上传进度
 */
public class ProgressRequestBody extends RequestBody {

    // 实际的待封装请求体
    private final RequestBody mRequestBody;

    // 进度回调接口
    private final ProgressListener mProgressListener;

    // 封装完成的BufferedSink
    private BufferedSink bufferedSink;

    /**
     * 构造函数,赋值
     *
     * @param requestBody      带封装的请求体
     * @param progressListener 上传进度回调接口
     */
    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
        this.mRequestBody = requestBody;
        this.mProgressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            // 封装
            bufferedSink = Okio.buffer(sink(sink));
        }
        // 写入
        mRequestBody.writeTo(bufferedSink);
        // 必须调用flush, 否则最后一部分数据可能不会写入
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {

            // 当前写入字节数
            long bytesWritten = 0L;

            // 总字节长度,避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    //获得contentLength的值，后续不再调用
                    contentLength = contentLength();
                }

                //增加当前写入的字节数
                bytesWritten += byteCount;

                // 回调
                if (mProgressListener != null) {
                    mProgressListener.onProgress(bytesWritten, contentLength, bytesWritten == contentLength);
                }
            }
        };
    }
}
