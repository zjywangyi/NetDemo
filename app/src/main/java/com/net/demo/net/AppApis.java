package com.net.demo.net;

import com.demo.library.net.Https;
import com.demo.library.net.okhttp.RequestParams;
import com.demo.library.net.okhttp.ResponseListener;

public class AppApis {

    private AppApis() {

    }

    public static void cancel(String url) {
        Https.get().cancel(url);
    }

    public static RequestParams getParams() {
        RequestParams params = RequestParams.get();
        params.clear();
        return params;
    }

    public static void test(int a, ResponseListener listener) {
        RequestParams params = getParams();
        params.put("data", a);
        Https.get().post(Urls.TEST, params, listener);//, TestBean.class);
    }

    public static void getLifeInsurance(String certicode, ResponseListener listener) {
        RequestParams params = getParams();
        params.put("certiCode", certicode);
        Https.get().post(Urls.LIFE, params, listener);//, TestBean.class);
    }

}
