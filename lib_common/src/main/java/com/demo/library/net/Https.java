package com.demo.library.net;

import android.text.TextUtils;

import com.demo.library.net.okhttp.HttpConfig;
import com.demo.library.net.okhttp.RequestParams;
import com.demo.library.net.okhttp.RequestTask;
import com.demo.library.net.okhttp.ResponseHeaderListener;
import com.demo.library.net.okhttp.ResponseListener;
import com.demo.library.net.okhttp.SimpleResponseHandler;
import com.demo.library.net.okhttp.base.BaseResponseHandler;
import com.demo.library.net.okhttp.progress.down.DownTask;
import com.demo.library.net.okhttp.progress.handler.ProgressHandler;
import com.demo.library.net.okhttp.progress.listener.DownloadListener;
import com.demo.library.net.okhttp.progress.listener.UploadListener;
import com.demo.library.net.okhttp.progress.update.ProgressRequestBody;
import com.demo.library.utils.L;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Https {

    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final static MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");
    private final static MediaType ALL = MediaType.parse("*/*");
    private final static MediaType FILE = MediaType.parse("application/octet-stream");
    private final static MediaType MEDIA = MediaType.parse("image/jpeg");

    private volatile static Https mInstance;

    private OkHttpClient mOkHttpClient;

    private HttpConfig mHttpConfig;

    private ExecutorService mThreadPool;

    private Headers.Builder mHeadersBuilder;

    private Https() {
        mOkHttpClient = getOkHttpClient();

//        init(HttpConfig.createDefault());
//
//        mOkHttpClient.sslSocketFactory(null);
    }

    public static Https get() {
        if (mInstance == null) {
            synchronized (Https.class) {
                if (mInstance == null) {
                    mInstance = new Https();
                }
            }
        }

        return mInstance;
    }

    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (Https.class) {
                if (mOkHttpClient == null) {
                    HttpConfig httpConfig = HttpConfig.createDefault();

                    if (httpConfig == null) {
                        throw new IllegalArgumentException("OkHttp can not be initialized with null");
                    }

                    this.mHttpConfig = httpConfig;

                    this.mThreadPool = mHttpConfig.getThreadPool();
                    this.mHeadersBuilder = mHttpConfig.getHeaderBuilder();

                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
//                    builder.sslSocketFactory(null);
                    builder.connectTimeout(mHttpConfig.getConnectTimeOut(), TimeUnit.SECONDS);
                    builder.writeTimeout(mHttpConfig.getWriteTimeOut(), TimeUnit.SECONDS);
                    builder.readTimeout(mHttpConfig.getReadTimeOut(), TimeUnit.SECONDS);

                    // 请求不重复
                    builder.retryOnConnectionFailure(false);
                    // 请求支持重定向
                    builder.followRedirects(true);

                    mOkHttpClient = builder.build();
                }
            }
        }

        return mOkHttpClient;
    }


//    /**
//     * init
//     *
//     * @param httpConfig the config of Http Request.
//     */
//    public void init(HttpConfig httpConfig) {
//        if (httpConfig == null) {
//            throw new IllegalArgumentException("OkHttp can not be initialized with null");
//        }
//
//        this.mHttpConfig = httpConfig;
//
//        this.mThreadPool = mHttpConfig.getThreadPool();
//        this.mHeadersBuilder = mHttpConfig.getHeaderBuilder();
//
//        // set all timeout
//        getOkHttpClient().setConnectTimeout(mHttpConfig.getConnectTimeOut(), TimeUnit.SECONDS);
//        getOkHttpClient().setWriteTimeout(mHttpConfig.getWriteTimeOut(), TimeUnit.SECONDS);
//        getOkHttpClient().setReadTimeout(mHttpConfig.getReadTimeOut(), TimeUnit.SECONDS);
//
//        // 请求不重复
//        getOkHttpClient().setRetryOnConnectionFailure(false);
//        // 请求支持重定向
//        getOkHttpClient().setFollowRedirects(true);
//    }

    public HttpConfig getConfig() {
        return mHttpConfig;
    }

//    /**
//     * Set the socket factory used to secure HTTPS connections.
//     * <p/>
//     * <p>If unset, a lazily created SSL socket factory will be used.</p>
//     *
//     * @param sslSocketFactory
//     */
//    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
//        getOkHttpClient().setSslSocketFactory(sslSocketFactory);
//    }

    /**
     * Add a header for all requests
     *
     * @param name  the name of header.
     * @param value the value of header.
     */
    public void addHeader(String name, String value) {
        getHeadersBuilder().add(name, value);
    }

    /**
     * Remove a header for all requests.
     *
     * @param name the name of header.
     */
    public void removeHeader(String name) {
        getHeadersBuilder().removeAll(name);
    }

    public Headers.Builder getHeadersBuilder() {
        return mHeadersBuilder;
    }

    /**
     * Create a Request.Builder
     *
     * @param tag     the tag of this HTTP request.
     * @param fullUrl the full URL of this HTTP request.
     * @return a new Request.Builder
     */
    private Request.Builder createRequestBuilder(String tag, String fullUrl) {
        return new Request.Builder().url(fullUrl).tag(tag).headers(getHeadersBuilder().build());
    }

    /**
     * get POST Request.Builder
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @return the POST Request.Builder
     */
    private Request.Builder getPostBuilder(String url, RequestParams requestParams) {
        Request.Builder builder = createRequestBuilder(url, url);

        if (requestParams != null) {
            builder.post(requestParams.toRequestBody());
            L.syso("request params : " + requestParams.toString());
            releaseParams(requestParams);
//        } else {
//            builder.post(RequestBody.create(ALL, ""));
        }

        return builder;
    }

    /**
     * get POST Request.Builder
     *
     * @param url         the URL of HTTP request.
     * @param requestBody the Request Body.
     * @return the POST Request.Builder
     */
    private Request.Builder getPostBuilder(String url, RequestBody requestBody) {
        Request.Builder builder = createRequestBuilder(url, url);

        if (requestBody != null) {
            builder.post(requestBody);
//        } else {
//            builder.post(RequestBody.create(ALL, ""));
        }

        return builder;
    }

    /**
     * get GET Request.Builder
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @return the GET Request.Builder
     */
    private Request.Builder getGetBuilder(String url, RequestParams requestParams) {
        Request.Builder builder;

        if (requestParams != null) {
            builder = createRequestBuilder(url, requestParams.toQueryString(url));
            L.syso("request params : " + requestParams.toString());
            releaseParams(requestParams);
        } else {
            builder = createRequestBuilder(url, url);
        }

        return builder.get();
    }

    /**
     * Release Request Params
     *
     * @param params
     */
    private void releaseParams(RequestParams params) {
        if (params != null) {
            params.clear();
//            params = null;
        }
    }

    /**
     * Release all resource
     */
    public void release() {
        try {
            if (mHttpConfig != null) {
                mHttpConfig.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mHttpConfig = null;
        }

        try {
            if (mThreadPool != null) {
                mThreadPool.shutdownNow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mThreadPool = null;
        }

        try {
            if (mHeadersBuilder != null) {
                mHeadersBuilder = null;
            }

            if (mOkHttpClient != null) {
                mOkHttpClient = null;
            }

            if (mInstance != null) {
                mInstance = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancels all scheduled tasks tagged with tag.
     * Requests that are already complete cannot be canceled.
     *
     * @param url the tag of HTTP request which used the request url.
     */
    public void cancel(String url) {
        if (mOkHttpClient != null && !TextUtils.isEmpty(url)) {
            if (!cancelCalls(mOkHttpClient.dispatcher().runningCalls(), url)) {
                cancelCalls(mOkHttpClient.dispatcher().queuedCalls(), url);
            }
        }
    }

    private boolean cancelCalls(List<Call> calls, String tag) {
        if (calls != null && calls.size() > 0) {
            for (Call call : calls) {
                if (call.request().tag().equals(tag)) {
                    call.cancel();
                    return true;
                }
            }
        }

        return false;
    }


    //**************************  GET  **********************************

    /**
     * Perform HTTP GET request with a RequestParams.
     *
     * @param url      the URL of HTTP request.
     * @param listener the callback of the response.
     */
    public void get(String url, ResponseListener listener) {
        submitRequest(url, getGetBuilder(url, null).build(), listener);
    }

    /**
     * Perform HTTP GET request with a RequestParams.
     *
     * @param url      the URL of HTTP request.
     * @param listener the callback of the response.
     * @param cls      the Json class which need to be parsed
     */
    public void get(String url, ResponseListener listener, Class<?> cls) {
        submitRequest(url, getGetBuilder(url, null).build(), listener, cls);
    }

    /**
     * Perform HTTP GET request with a RequestParams.
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @param listener      the callback of the response.
     */
    public void get(String url, RequestParams requestParams, ResponseListener listener) {
        submitRequest(url, getGetBuilder(url, requestParams).build(), listener);
    }

    /**
     * Perform HTTP GET request with a RequestParams.
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @param listener      the callback of the response.
     * @param cls           the Json class which need to be parsed
     */
    public void get(String url, RequestParams requestParams, ResponseListener listener, Class<?> cls) {
        submitRequest(url, getGetBuilder(url, requestParams).build(), listener, cls);
    }

    /**
     * Perform HTTP GET request with a RequestParams.
     *
     * @param url     the URL of HTTP request.
     * @param handler the handler of the response.
     */
    public void get(String url, BaseResponseHandler handler) {
        submitRequest(url, getGetBuilder(url, null).build(), handler);
    }

    /**
     * Perform HTTP GET request with a RequestParams.
     *
     * @param url     the URL of HTTP request.
     * @param handler the handler of the response.
     * @param cls     the Json class which need to be parsed
     */
    public void get(String url, BaseResponseHandler handler, Class<?> cls) {
        submitRequest(url, getGetBuilder(url, null).build(), handler, cls);
    }

    /**
     * Perform HTTP GET request with a RequestParams.
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @param handler       the handler of the response.
     */
    public void get(String url, RequestParams requestParams, BaseResponseHandler handler) {
        submitRequest(url, getGetBuilder(url, requestParams).build(), handler);
    }

    /**
     * Perform HTTP GET request with a RequestParams.
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @param handler       the handler of the response.
     * @param cls           the Json class which need to be parsed
     */
    public void get(String url, RequestParams requestParams, BaseResponseHandler handler, Class<?> cls) {
        submitRequest(url, getGetBuilder(url, requestParams).build(), handler, cls);
    }


    //**************************  POST  *********************************

    /**
     * Perform HTTP POST request with a RequestParams.
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     */
    public void post(String url, RequestParams requestParams) {
        submitRequest(url, getPostBuilder(url, requestParams).build());
    }

    /**
     * Perform HTTP POST request with a JSON String
     *
     * @param url      the URL of HTTP request.
     * @param json     the param of Request Body.
     * @param listener the callback of the response.
     */
    public void post(String url, String json, ResponseListener listener) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        post(url, requestBody, listener);
    }

    /**
     * Perform HTTP POST request with a JSON String
     *
     * @param url      the URL of HTTP request.
     * @param json     the param of Request Body.
     * @param listener the callback of the response.
     * @param cls      the Json class which need to be parsed
     */
    public void post(String url, String json, ResponseListener listener, Class<?> cls) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        post(url, requestBody, listener, cls);
    }

    /**
     * Perform HTTP POST request with a JSON String
     *
     * @param url     the URL of HTTP request.
     * @param json    the param of Request Body.
     * @param handler the handler of the response.
     */
    public void post(String url, String json, BaseResponseHandler handler) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        post(url, requestBody, handler);
    }

    /**
     * Perform HTTP POST request with a JSON String
     *
     * @param url     the URL of HTTP request.
     * @param json    the param of Request Body.
     * @param handler the handler of the response.
     * @param cls     the Json class which need to be parsed
     */
    public void post(String url, String json, BaseResponseHandler handler, Class<?> cls) {
        RequestBody requestBody = RequestBody.create(JSON, json);
        post(url, requestBody, handler, cls);
    }

    /**
     * Perform HTTP POST request with a RequestParams.
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @param listener      the callback of the response.
     */
    public void post(String url, RequestParams requestParams, ResponseListener listener) {
        submitRequest(url, getPostBuilder(url, requestParams).build(), listener);
    }

    /**
     * Perform HTTP POST request with a RequestParams.
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @param listener      the callback of the response.
     * @param cls           the Json class which need to be parsed
     */
    public void post(String url, RequestParams requestParams, ResponseListener listener, Class<?> cls) {
        submitRequest(url, getPostBuilder(url, requestParams).build(), listener, cls);
    }

    public void post(String url, RequestParams requestParams, ResponseListener listener, Class<?> cls, ResponseHeaderListener responseHeaderListener) {
        submitRequest(url, getPostBuilder(url, requestParams).build(), listener, cls, responseHeaderListener);
    }

    /**
     * Perform HTTP POST request with a RequestParams.
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @param handler       the handler of the response.
     */
    public void post(String url, RequestParams requestParams, BaseResponseHandler handler) {
        submitRequest(url, getPostBuilder(url, requestParams).build(), handler);
    }

    /**
     * Perform HTTP POST request with a RequestParams.
     *
     * @param url           the URL of HTTP request.
     * @param requestParams the param of Request Body.
     * @param handler       the handler of the response.
     * @param cls           the Json class which need to be parsed
     */
    public void post(String url, RequestParams requestParams, BaseResponseHandler handler, Class<?> cls) {
        submitRequest(url, getPostBuilder(url, requestParams).build(), handler, cls);
    }

    /**
     * Perform HTTP POST request with a RequestBody.
     *
     * @param url         the URL of HTTP request.
     * @param requestBody the Request Body.
     * @param listener    the callback of the response.
     */
    public void post(String url, RequestBody requestBody, ResponseListener listener) {
        submitRequest(url, getPostBuilder(url, requestBody).build(), listener);
    }

    /**
     * Perform HTTP POST request with a RequestBody.
     *
     * @param url         the URL of HTTP request.
     * @param requestBody the Request Body.
     * @param listener    the callback of the response.
     * @param cls         the Json class which need to be parsed
     */
    public void post(String url, RequestBody requestBody, ResponseListener listener, Class<?> cls) {
        submitRequest(url, getPostBuilder(url, requestBody).build(), listener, cls);
    }

    /**
     * Perform HTTP POST request with a RequestBody.
     *
     * @param url         the URL of HTTP request.
     * @param requestBody the Request Body.
     * @param handler     the handler of the response.
     */
    public void post(String url, RequestBody requestBody, BaseResponseHandler handler) {
        submitRequest(url, getPostBuilder(url, requestBody).build(), handler);
    }

    /**
     * Perform HTTP POST request with a RequestBody.
     *
     * @param url         the URL of HTTP request.
     * @param requestBody the Request Body.
     * @param handler     the handler of the response.
     * @param cls         the Json class which need to be parsed
     */
    public void post(String url, RequestBody requestBody, BaseResponseHandler handler, Class<?> cls) {
        submitRequest(url, getPostBuilder(url, requestBody).build(), handler, cls);
    }

    //**************************  UPDATE  **********************************

    /**
     * @param url            the URL of update file
     * @param filePath       the local path of update file
     * @param requestParams  the param of update
     * @param updateListener the callback of the update response.
     */
    public void upload(String url, String fileKey, String filePath, RequestParams requestParams, UploadListener updateListener) {
        File file = new File(filePath);
        if (!file.exists()) {
            L.e("update file not exists");
            return;
        }

        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (requestParams != null && !requestParams.getParams().isEmpty()) {
            for (HashMap.Entry<String, String> entry : requestParams.getParams().entrySet()) {
                multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
            }
            releaseParams(requestParams);
        }

        multipartBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + fileKey + "\"; filename=\"" + file.getName() + "\""),
                RequestBody.create(FILE, file));

        ProgressHandler handler = new ProgressHandler(url, updateListener, updateListener);
        Request request = createRequestBuilder(url, url).post(new ProgressRequestBody(multipartBuilder.build(), handler)).build();

        submitRequest(url, request, handler);
    }


    //**************************  DOWN  *********************************

    /**
     * @param url              the URL of download file
     * @param filePath         the local path of download file save to
     * @param requestParams    the param of download
     * @param downloadListener the callback of the download response.
     */
    public void down(String url, String filePath, RequestParams requestParams, DownloadListener downloadListener) {
//        File file = new File(filePath);
//        if (file.exists()) {
//            file.delete();
//        }

        ProgressHandler handler = new ProgressHandler(url, downloadListener, downloadListener);
        DownTask task = new DownTask(filePath, getOkHttpClient(), getPostBuilder(url, requestParams).build(), handler, handler);
        submit(task);
    }

    /**
     * @param url              the URL of download file
     * @param filePath         the local path of download file save to
     * @param downloadListener the callback of the download response.
     */
    public void down(String url, String filePath, DownloadListener downloadListener) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        RequestParams requestParams = null;
        ProgressHandler handler = new ProgressHandler(url, downloadListener, downloadListener);
        DownTask task = new DownTask(filePath, getOkHttpClient(), getPostBuilder(url, requestParams).build(), handler, handler);
        submit(task);
    }


    //**************************  SUBMIT  *******************************

    /**
     * Submit a Runnable task to thread pool.
     *
     * @param url     the URL of HTTP request.
     * @param request the HTTP request
     */
    public void submitRequest(String url, Request request) {
        RequestTask task = new RequestTask(url, getOkHttpClient(), request, null);
        submit(task);
    }

    /**
     * Submit a Runnable task to thread pool.
     *
     * @param url      the URL of HTTP request.
     * @param request  the HTTP request
     * @param listener the callback of the response.
     */
    public void submitRequest(String url, Request request, ResponseListener listener) {
        submitRequest(url, request, new SimpleResponseHandler(request.tag().toString(), listener));
    }

    /**
     * Submit a Runnable task to thread pool.
     *
     * @param url      the URL of HTTP request.
     * @param request  the HTTP request
     * @param listener the callback of the response.
     * @param cls      the Json class which need to be parsed
     */
    public void submitRequest(String url, Request request, ResponseListener listener, Class<?> cls) {
        submitRequest(url, request, new SimpleResponseHandler(request.tag().toString(), listener), cls);
    }

    /**
     * Submit a Runnable task to thread pool.
     *
     * @param url     the URL of HTTP request.
     * @param request the HTTP request
     * @param handler the handler of the response.
     */
    public void submitRequest(String url, Request request, BaseResponseHandler handler) {
        RequestTask task = new RequestTask(url, getOkHttpClient(), request, handler);
        submit(task);
    }

    /**
     * Submit a Runnable task to thread pool.
     *
     * @param url     the URL of HTTP request.
     * @param request the HTTP request
     * @param handler the handler of the response.
     * @param cls     the Json class which need to be parsed
     */
    public void submitRequest(String url, Request request, BaseResponseHandler handler, Class<?> cls) {
        RequestTask task = new RequestTask(url, getOkHttpClient(), request, handler, cls);
        submit(task);
    }

    public void submitRequest(String url, Request request, ResponseListener listener, Class<?> cls, ResponseHeaderListener responseHeaderListener) {
        RequestTask task = new RequestTask(url, getOkHttpClient(), request, new SimpleResponseHandler(request.tag().toString(), listener), cls);
        task.setResponseHeaderListener(responseHeaderListener);
        submit(task);
    }

    /**
     * Submit a Runnable task to thread pool.
     *
     * @param task the task which need to be submitted.
     */
    public void submit(Runnable task) {
        mThreadPool.submit(task);
    }

    //**************************  END  **********************************


}
