package com.demo.library.ui.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.library.utils.L;

/**
 * 若把初始化内容放到initData实现,就是采用Lazy方式加载的Fragment
 * 若不需要Lazy加载则initData方法内留空,初始化内容放到initViews即可
 * -
 * -注1: 如果是与ViewPager一起使用，调用的是setUserVisibleHint。
 * ------可以调用mViewPager.setOffscreenPageLimit(size),若设置了该属性 则viewpager会缓存指定数量的Fragment
 * -注2: 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
 * -注3: 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果 需要先hide再show
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    protected boolean isVisible;                //是否可见状态
    private boolean isPrepared;                 //标志位，View已经初始化完成。
    private boolean isFirstLoad = true;         //是否第一次加载

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isFirstLoad = true;
        L.setTag(this.getClass().getSimpleName());
        root = inflater.inflate(getContentViewResId(), container, false);
        initView(root);
        initListener(root);
        isPrepared = true;
        lazyLoad();

        return root;
    }


    @Override
    public void onClick(View view) {
    }

    /**
     * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
     * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }


    protected void onVisible() {
        lazyLoad();
    }

    protected void onInvisible() {
    }


    protected void lazyLoad() {
        if (!isPrepared || !isVisible || !isFirstLoad) {
            return;
        }
        isFirstLoad = false;
        initData();
    }


    /**
     * 布局文件id
     *
     * @return
     */
    protected abstract int getContentViewResId();

    /**
     * 初始化
     *
     * @param root
     */
    protected abstract void initView(View root);

    protected void initListener(View root) {
    }

    protected void initData() {
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


}