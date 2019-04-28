package alauncher.cn.measuringinstrument.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;


import java.lang.reflect.Field;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.mvpbase.BaseView;
import butterknife.ButterKnife;


public abstract class BaseActivity extends Activity implements BaseView {
    protected ViewGroup bar_layout = null;
    private static final String TAG = "BaseActivity";

    protected BaseActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "set no title ?");
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无ActionBar，单在继承AppCompat?Activity时无效，继承Activity时才有效
        //保持竖屏
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            // 透明导航栏
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        this.mActivity = this;
        //初始化布局
        initLayout();
        ButterKnife.bind(this);
        //初始化View
        initView();

    }

    /**
     * 初始化布局
     */
    protected abstract void initLayout();

    /**
     * 初始化View
     */
    protected abstract void initView();

    /**
     * 获取状态栏的高度
     *
     * @return 状态栏的高度
     */
    protected int getStatusBarHeight() {
        try {
            //通过反射获取到类
            Class<?> aClass = Class.forName("com.android.internal.R$dimen");
            //创建对象
            Object o = aClass.newInstance();
            //拿取属性
            Field status_bar_height = aClass.getField("status_bar_height");
            //获取值
            Object o1 = status_bar_height.get(o);
            int height = Integer.parseInt(o1.toString());
            return getResources().getDimensionPixelOffset(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 系统版本4.4或以上才可以设置沉浸式状态栏
     * <p>
     * 设置状态栏的高度
     */
    protected void setStatus() {
//        bar_layout = (ViewGroup) findViewById(R.id.bar_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置导航栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            /*
            final int statusBarHeight = getStatusBarHeight();
            bar_layout.post(new Runnable() {
                @Override
                public void run() {
                    //标题栏高度
                    int height = bar_layout.getHeight();
                    android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) bar_layout.getLayoutParams();
                    //导航栏高度+通知栏高度
                    params.height = height + statusBarHeight;
                    bar_layout.setLayoutParams(params);
                }
            });
            */
        }
    }


    /**
     * 设置进入 动画
     *
     * @param intent
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    protected void openActivty(Class<?> mclass) {
        Intent intent = new Intent(this, mclass);
        startActivity(intent);
    }

    /**
     * 设置退出 动画
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fixInputMethodManagerLeak(this);
    }


    /**
     * 解决InputMethodManager内存泄露现象
     */
    private static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f;
        Object obj_get;
        for (String param : arr) {
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get
                            .getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        /*if (QLog.isColorLevel()) {
                            QLog.d(ReflecterHelper.class.getSimpleName(), QLog.CLR, "fixInputMethodManagerLeak break, context is not suitable, get_context=" + v_get.getContext()+" dest_context=" + destContext);
                        }*/
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void showToast(String msg) {
        // ToastUtils.showToast(msg);
    }
}
