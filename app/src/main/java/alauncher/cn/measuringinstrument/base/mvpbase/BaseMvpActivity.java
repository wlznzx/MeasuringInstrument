package alauncher.cn.measuringinstrument.base.mvpbase;

import android.os.Bundle;
import android.widget.Toast;

import alauncher.cn.measuringinstrument.base.BaseActivity;
import androidx.annotation.Nullable;


/**
 * 日期   ： 2017/8/26
 * 时间   ： 13:51
 * 功能   ：Activity实现Mvp的基类
 */

public abstract class BaseMvpActivity<T extends BasePresenter> extends BaseActivity {

    // protected ActivityComponent build;

    //通过Dagger2注入的其实是Mvp中的 Presenter
    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityComonter();

        mPresenter = initInjector();
        //绑定View
        mPresenter.attachView(this);
        initData();
    }

    /**
     * 加载数据
     */
    protected abstract void initData();

    public void initActivityComonter() {
        /*
        build = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .appComponent(((App) getApplication()).getAppComponent())
                .build();
                */
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 完成注入并返回注入的Presenter对象
     *
     * @return
     */
    protected abstract T initInjector();

    /**
     * 解绑
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }

    }

}
