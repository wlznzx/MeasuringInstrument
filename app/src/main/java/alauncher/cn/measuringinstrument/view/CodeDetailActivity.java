package alauncher.cn.measuringinstrument.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Arrays;
import java.util.List;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.view.fragment.BaseInfoFragment;
import alauncher.cn.measuringinstrument.view.fragment.CodeBaseInfoFragment;
import alauncher.cn.measuringinstrument.view.fragment.ForceCalibrationFragment;
import butterknife.BindView;

public class CodeDetailActivity extends BaseActivity {

    @BindView(R.id.tab_tl_indicator)
    protected TabLayout mTlIndicator;


    @BindView(R.id.tab_vp)
    protected ViewPager mVp;
    /**************Adapter***************/
    protected TabFragmentPageAdapter tabFragmentPageAdapter;
    /************Params*******************/
    protected List<Fragment> mFragmentList;
    private List<String> mTitleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_code_detail);
    }

    @Override
    protected void initView() {
        CodeBaseInfoFragment codeBaseInfoFragment = null;
        ForceCalibrationFragment forceCalibrationFragment = null;
        BaseInfoFragment launcherFragment = null;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof BaseInfoFragment) {
                codeBaseInfoFragment = (CodeBaseInfoFragment) fragment;
            } else if (fragment instanceof alauncher.cn.measuringinstrument.view.fragment.CodeBaseInfoFragment) {
                forceCalibrationFragment = (ForceCalibrationFragment) fragment;
            } else if (fragment instanceof BaseInfoFragment) {
                launcherFragment = (BaseInfoFragment) fragment;
            }
        }
        if (codeBaseInfoFragment == null)
            codeBaseInfoFragment = new CodeBaseInfoFragment();
        if (forceCalibrationFragment == null)
            forceCalibrationFragment = new ForceCalibrationFragment();
        if (launcherFragment == null)
            launcherFragment = new BaseInfoFragment();


        String[] mTitles = new String[]{getString(R.string.code_base_info),
                getString(R.string.code_force_cailbration),
                getString(R.string.code_step)};
        mTitleList = Arrays.asList(mTitles);


        mFragmentList = Arrays.asList(codeBaseInfoFragment, forceCalibrationFragment, launcherFragment);

        tabFragmentPageAdapter = new TabFragmentPageAdapter(getSupportFragmentManager());
        mVp.setAdapter(tabFragmentPageAdapter);
        mVp.setOffscreenPageLimit(2);
        mTlIndicator.setupWithViewPager(mVp);
    }

    private View tab_icon(String name) {
        @SuppressLint("InflateParams")
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_view_icon_right, null);
        TextView tv = tabView.findViewById(R.id.tabtext);
        tv.setText(name);
        // ImageView im = tabView.findViewById(R.id.tabicon);
        // im.setVisibility(View.VISIBLE);
        // im.setImageResource(R.drawable.ic_arrow_drop_down);
        return tabView;
    }

    public class TabFragmentPageAdapter extends FragmentPagerAdapter {

        TabFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            int size = mFragmentList.size();

            android.util.Log.d("wlDebug", "size = " + size);
            android.util.Log.d("wlDebug", "T size = " + mTitleList.size());
            return size;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }

        public View getTabView(Context mContext, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_header, null);
            TextView textView = view.findViewById(R.id.tv_header);
            return view;
        }
    }
}