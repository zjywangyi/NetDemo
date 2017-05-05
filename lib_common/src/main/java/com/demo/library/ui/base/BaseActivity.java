package com.demo.library.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.demo.library.net.okhttp.ResponseListener;
import com.demo.library.utils.L;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, ResponseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentViewResId());

        initView();
        initListener();
        initData();
    }


    @Override
    public void onClick(View view) {
    }


    protected abstract int getContentViewResId();

    protected abstract void initView();

    protected void initListener() {
    }

    protected void initData() {
    }


    protected <E extends View> E getView(int resViewId) {
        try {
            return (E) findViewById(resViewId);
        } catch (ClassCastException e) {
            L.e("Could not cast View to concrete class", e);

            throw e;
        }
    }

    protected <E extends View> E getView(View rootView, int resViewId) {
        try {
            if (rootView != null) {
                return (E) rootView.findViewById(resViewId);
            } else {
                throw new NullPointerException("root view is null, can not find view");
            }
        } catch (Throwable e) {
            L.e("Could not cast View to concrete class", e);

            throw e;
        }
    }


    @Override
    public boolean onSuccess(String url, Object obj) {
        if (this == null || this.isFinishing() || obj == null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onFailure(int code, String url, String error) {
        if (this == null || this.isFinishing()) {
            return true;
        }

        return false;
    }

}