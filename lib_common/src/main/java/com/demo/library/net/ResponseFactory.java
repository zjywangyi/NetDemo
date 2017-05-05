package com.demo.library.net;

import android.text.TextUtils;

import com.demo.library.utils.GsonUtils;

public class ResponseFactory {


    private ResponseFactory() {
    }


    public static Object handleResponse(String json, Class<?> cls) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        if (cls == null) {
            return json;
        }

        if (String.class.equals(cls)) {
            return json;
        }

        return GsonUtils.get().fromJson(json, cls);
    }

}
