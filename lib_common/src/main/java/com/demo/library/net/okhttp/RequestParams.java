package com.demo.library.net.okhttp;

import com.demo.library.utils.L;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class RequestParams {

    private LinkedHashMap<String, String> params;

    private RequestParams(RequestParams rp) {
        params = (LinkedHashMap) rp.getParams().clone();
    }

    private static class Init {
        private static RequestParams p = new RequestParams();
    }

    private RequestParams() {
        if (params == null) {
            params = new LinkedHashMap<>();
        }
    }

    public static RequestParams get() {
        return Init.p;
    }


    public void put(String key, String value) {
        params.put(key, value);
    }

    public void put(String key, Object value) {
        params.put(key, value.toString());
    }

    public String remove(String key) {
        return params.remove(key);
    }

    public void clear() {
        params.clear();
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }

    public String get(String key) {
        try {
            return params.get(key);
        } catch (Throwable e) {
            e.printStackTrace();

            return "";
        }
    }

    public RequestParams clone() {
        return new RequestParams(Init.p);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }

            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        return sb.toString();
    }


    public String toQueryString(String url) {
//        String paramStr = toString();

        StringBuilder sb = new StringBuilder();

        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }

            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }


        // TODO:sign
        if (sb.length() > 0) {

            url += url.contains("?") ? "&" : "?";
            url += sb.toString();
        }

        return url;
    }

    public RequestBody toRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();

        for (HashMap.Entry<String, String> entry : params.entrySet()) {
            try {
                builder.addEncoded(entry.getKey(), URLEncoder.encode(valueToString(entry.getValue()), "UTF-8"));
            } catch (Throwable e) {
                e.printStackTrace();
                L.syso("key:" + entry.getKey() + "\nvalue:" + entry.getValue());
            }
        }

        return builder.build();
    }

    private String valueToString(String value) {
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

}
