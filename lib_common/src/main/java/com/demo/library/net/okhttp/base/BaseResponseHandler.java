package com.demo.library.net.okhttp.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.demo.library.net.okhttp.ResponseListener;
import com.demo.library.net.okhttp.progress.ProgressInfo;
import com.demo.library.net.okhttp.progress.listener.ProgressListener;
import com.demo.library.utils.L;

public abstract class BaseResponseHandler extends Handler {

    public final static int START = 0x1;

    public final static int UPDATE = START << 1;

    public final static int SUCCESS = UPDATE << 1;

    public final static int FAILURE = SUCCESS << 1;

//    private final static int CANCEL = FAILURE << 1;

//    protected final WeakReference<ResponseListener> mResponseListener;

//    protected WeakReference<ProgressListener> mProgressListener;

    protected ResponseListener mResponse;
    protected ProgressListener mProgress;

    protected String mBaseUrl;

    public BaseResponseHandler(String baseUrl, ResponseListener responseListener) {
        super(Looper.getMainLooper());
        this.mBaseUrl = baseUrl;
//        this.mResponseListener = new WeakReference<ResponseListener>(responseListener);
        mResponse = responseListener;
    }

    public BaseResponseHandler(String baseUrl, ResponseListener responseListener, ProgressListener progressListener) {
        this(baseUrl, responseListener);
//        this.mProgressListener = new WeakReference<ProgressListener>(progressListener);
        this.mProgress = progressListener;
    }


    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case START:
                break;
            case UPDATE:
//                ProgressListener progressListener;
//                if (mProgressListener != null && (progressListener = mProgressListener.get()) != null) {
//                    // 获取进度实体类
//                    ProgressInfo info = (ProgressInfo) msg.obj;
//
//                    progressListener.onProgress(info.getCurrentBytes(), info.getContentLength(), info.isDone());
//                } else {
//                    L.syso("listener is null; baseUrl : " + mBaseUrl);
//                }

                if (mProgress != null) {
                    try {
                        ProgressInfo info = (ProgressInfo) msg.obj;

                        if (info != null) {
                            mProgress.onProgress(info.getCurrentBytes(), info.getContentLength(), info.isDone());
                        }
                    } catch (Exception e) {
                        L.e("BaseResponseHandler", "error", e);
                    }
                }
                break;
            case SUCCESS:
//                ResponseListener listener;
//                if (mResponseListener != null && (listener = mResponseListener.get()) != null) {
//                    listener.onSuccess(mBaseUrl, msg.obj);
//                }

                if (mResponse != null) {
                    mResponse.onSuccess(mBaseUrl, msg.obj);
                }
                break;
            case FAILURE:
//                ResponseListener responseListener;
//                if (mResponseListener != null && (responseListener = mResponseListener.get()) != null) {
//                    String error = null;
//                    if (msg.obj != null) {
//                        error = msg.obj.toString();
//                    }
//
//                    responseListener.onFailure(msg.arg1, mBaseUrl, error);
//                }

                if (mResponse != null) {
                    String error = null;
                    if (msg.obj != null) {
                        error = msg.obj.toString();
                    }
                    mResponse.onFailure(msg.arg1, mBaseUrl, error);
                }
                break;
//                case CANCEL:
//                    break;
        }
    }

}
