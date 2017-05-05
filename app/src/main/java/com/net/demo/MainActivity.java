package com.net.demo;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.net.demo.net.AppApis;
import com.net.demo.net.Urls;
import com.demo.library.ui.base.BaseActivity;

public class MainActivity extends BaseActivity {

    private TextView tvTest;
    private TextView tvquery;
    private EditText editText;

    private int data = 1;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        tvTest = getView(R.id.tv_test);
        tvquery=getView(R.id.tv_query);
        editText=getView(R.id.ev_test);
    }

    @Override
    protected void initListener() {
        tvquery.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        if (view.getId() == R.id.tv_query&&editText.getText()!=null) {
            AppApis.getLifeInsurance(editText.getText().toString(), this);
        }
    }

    @Override
    public boolean onSuccess(String url, Object obj) {
        if (super.onSuccess(url, obj)) {
            return true;
        }

        switch (url) {
            case Urls.LIFE:
                tvTest.setText(obj.toString());
                break;
        }

        return false;
    }

    @Override
    public boolean onFailure(int code, String url, String error) {
        return super.onFailure(code, url, error);
    }
}
