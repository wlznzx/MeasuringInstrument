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
import alauncher.cn.measuringinstrument.view.fragment.AuthorityDetailFragment;
import alauncher.cn.measuringinstrument.view.fragment.AuthorityUserFragment;
import alauncher.cn.measuringinstrument.view.fragment.CodeBaseInfoFragment;
import alauncher.cn.measuringinstrument.view.fragment.CodeStepFragment2;
import alauncher.cn.measuringinstrument.view.fragment.ForceCalibrationFragment;
import alauncher.cn.measuringinstrument.view.fragment.MeasureConfigurationFragment;
import alauncher.cn.measuringinstrument.view.fragment.WorkpieceFragment;
import butterknife.BindView;

public class AuthorityDetailActivity extends BaseActivity implements ForceCalibrationFragment.RefreshInterface {

    @BindView(R.id.tab_tl_indicator)
    protected TabLayout mTlIndicator;

    @BindView(R.id.tab_vp)
    protected ViewPager mVp;
    /************** Adapter ***************/
    protected TabFragmentPageAdapter tabFragmentPageAdapter;
    /************ Params *******************/
    protected List<Fragment> mFragmentList;
    private List<String> mTitleList;

    private long authorityGroupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_authority_detail);
    }

    @Override
    protected void initView() {
        authorityGroupID = getIntent().getLongExtra("pID", -1);
        AuthorityUserFragment authorityUserFragment = null;
        AuthorityDetailFragment authorityDetailFragment = null;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof AuthorityUserFragment) {
                authorityUserFragment = (AuthorityUserFragment) fragment;
            } else if (fragment instanceof AuthorityDetailFragment) {
                authorityDetailFragment = (AuthorityDetailFragment) fragment;
            }
        }
        if (authorityUserFragment == null)
            authorityUserFragment = new AuthorityUserFragment();
            authorityUserFragment.setAuthorityGroupID(authorityGroupID);
        if (authorityDetailFragment == null) {
            authorityDetailFragment = new AuthorityDetailFragment();
            authorityDetailFragment.setAuthorityGroupID(authorityGroupID);
        }

        String[] mTitles = new String[]{getString(R.string.user),
                getString(R.string.authority)};
        mTitleList = Arrays.asList(mTitles);

        mFragmentList = Arrays.asList(authorityUserFragment, authorityDetailFragment);

        tabFragmentPageAdapter = new TabFragmentPageAdapter(getSupportFragmentManager());
        mVp.setAdapter(tabFragmentPageAdapter);
        mVp.setOffscreenPageLimit(1);
        mTlIndicator.setupWithViewPager(mVp);
    }

    private View tab_icon(String name) {
        @SuppressLint("InflateParams")
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_view_icon_right, null);
        TextView tv = tabView.findViewById(R.id.tabtext);
        tv.setText(name);
        return tabView;
    }

    @Override
    public void onTriggerConditionChanged() {
        // if (mCodeStepFragment2 != null) mCodeStepFragment2.onTriggerConditionChanged();
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
