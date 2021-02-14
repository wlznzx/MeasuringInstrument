package alauncher.cn.measuringinstrument;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.AuthorityBean;
import alauncher.cn.measuringinstrument.bean.AuthorityGroupBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.AuthorityBeanDao;
import alauncher.cn.measuringinstrument.utils.DialogUtils;
import alauncher.cn.measuringinstrument.view.AccoutManagementActivity;
import alauncher.cn.measuringinstrument.view.AuthorityManagementActivity;
import alauncher.cn.measuringinstrument.view.CalibrationActivity;
import alauncher.cn.measuringinstrument.view.Code2Activity;
import alauncher.cn.measuringinstrument.view.Data2Activity;
import alauncher.cn.measuringinstrument.view.LoginActivity;
import alauncher.cn.measuringinstrument.view.Measuring2Activity;
import alauncher.cn.measuringinstrument.view.ParameterManagement2Activity;
import alauncher.cn.measuringinstrument.view.SPCStatistical2Activity;
import alauncher.cn.measuringinstrument.view.Statistical2Activity;
import alauncher.cn.measuringinstrument.view.SystemManagementActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;

public class MainActivity extends BaseOActivity {

    @BindView(R.id.rv)
    RecyclerView rv;

    private AuthorityGroupBean mAuthorityGroupBean;

    private List<MainInfo> mMainInfo = new ArrayList();
    private List<MainInfo> useMainInfo = new ArrayList();

    private MainLayoutAdapter mMainLayoutAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化所有的
        {
            mMainInfo.add(new MainInfo(R.string.measuring, R.drawable.equalizer, 0));
            mMainInfo.add(new MainInfo(R.string.data_query, R.drawable.find_in_page, 1));
            mMainInfo.add(new MainInfo(R.string.parameter_management, R.drawable.functions, 2));
            mMainInfo.add(new MainInfo(R.string.calibration, R.drawable.straighten, 3));
            mMainInfo.add(new MainInfo(R.string.user_management, R.drawable.account_box, 4));
            mMainInfo.add(new MainInfo(R.string.authority_management, R.drawable.authority, 5));
            mMainInfo.add(new MainInfo(R.string.program_management, R.drawable.code, 6));
            mMainInfo.add(new MainInfo(R.string.system_management, R.drawable.phonelink_setup, 7));
            mMainInfo.add(new MainInfo(R.string.spc_analysis, R.drawable.show_chart, 8));
            mMainInfo.add(new MainInfo(R.string.statistical_report, R.drawable.assignment, 9));
            mMainInfo.add(new MainInfo(R.string.logout, R.drawable.logout_96, 10));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        android.util.Log.d("alauncher", "mUser = " + mUser.toString());
        mAuthorityGroupBean = App.getDaoSession().getAuthorityGroupBeanDao().load(Long.valueOf(mUser.getUseAuthorityGroupID()));
        useMainInfo.clear();
        for (MainInfo info : mMainInfo) {
            AuthorityBean _bean = null;
            if (mAuthorityGroupBean != null) {
                _bean = App.getDaoSession().getAuthorityBeanDao().queryBuilder()
                        .where(AuthorityBeanDao.Properties.Id.eq(String.valueOf(info.id))
                                , AuthorityBeanDao.Properties.GroupID.eq(mAuthorityGroupBean.getId())).unique();
            }
            if (_bean == null) {
                // 默认打开权限;
            } else {
                if (!_bean.getAuthorized()) {
                    continue;
                }
            }
            useMainInfo.add(info);
        }
        mMainLayoutAdapter.setDatas(useMainInfo);
        mMainLayoutAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {

//        List<MainInfo> _datas = new ArrayList();
//        _datas.add(new MainInfo(R.string.measuring, R.drawable.equalizer,0));
//        try {
//            if (App.getDaoSession().getUserDao().load(App.handlerAccout).getLimit() < 2) {
//                _datas.add(new MainInfo(R.string.data_query, R.drawable.find_in_page,0));
//                _datas.add(new MainInfo(R.string.parameter_management, R.drawable.functions,1));
//                _datas.add(new MainInfo(R.string.calibration, R.drawable.straighten,2));
//                _datas.add(new MainInfo(R.string.user_management, R.drawable.account_box,3));
//                _datas.add(new MainInfo(R.string.authority_management, R.drawable.authority,4));
//                _datas.add(new MainInfo(R.string.program_management, R.drawable.code,5));
//                _datas.add(new MainInfo(R.string.system_management, R.drawable.phonelink_setup,6));
//                // _datas.add(new MainInfo(R.string.store, R.drawable.archive));
//                _datas.add(new MainInfo(R.string.spc_analysis, R.drawable.show_chart,7));
//                _datas.add(new MainInfo(R.string.statistical_report, R.drawable.assignment,8));
//                _datas.add(new MainInfo(R.string.logout, R.drawable.logout_96,9));
//            }
//        } catch (NullPointerException e) {
//
//        }
        mMainLayoutAdapter = new MainLayoutAdapter(useMainInfo);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        rv.addItemDecoration(new RecyclerItemDecoration(24, 3));
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(mMainLayoutAdapter);

        actionIV.setImageResource(R.drawable.power_button);
        actionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        actionTitleTV.setOnClickListener(new View.OnClickListener() {
            final static int COUNTS = 5;//点击次数
            final static long DURATION = 2 * 1000;//规定有效时间
            long[] mHits = new long[COUNTS];

            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                //实现左移，然后最后一个位置更新距离开机的时间，如果最后一个时间和最开始时间小于DURATION，即连续5次点击
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                    // String tips = "您已在[" + DURATION + "]ms内连续点击【" + mHits.length + "】次了！！！";
                    // Toast.makeText(MainActivity.this, tips, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    ComponentName cn = new ComponentName("com.android.launcher3", "com.android.launcher3.Launcher");
                    intent.setComponent(cn);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(MainActivity.this, "未找到主界面.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void logout() {
        final AlertDialog builder = new AlertDialog.Builder(MainActivity.this)
                .create();
        builder.show();
        if (builder.getWindow() == null) {
            return;
        }
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = builder.findViewById(R.id.tv_msg);
        Button cancel = builder.findViewById(R.id.btn_cancle);
        Button sure = builder.findViewById(R.id.btn_sure);
        if (msg == null || cancel == null || sure == null) {
            return;
        }
        msg.setText("确认注销当前账号？");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }

    class MainInfo {
        public int strID;
        public int drawableID;
        public int id;

        public MainInfo(int strID, int drawableID, int pid) {
            this.strID = strID;
            this.drawableID = drawableID;
            this.id = pid;
        }
    }

    class MainLayoutAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<MainInfo> datas;

        public MainLayoutAdapter(List<MainInfo> pDatas) {
            datas = pDatas;
        }

        public void setDatas(List<MainInfo> datas) {
            this.datas = datas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(MainActivity.this, parent, R.layout.main_layout_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setImageResource(R.id.main_item_iv, datas.get(position).drawableID);
            holder.setText(R.id.main_item_tv, datas.get(position).strID);
            holder.setOnClickListener(R.id.main_item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthorityBean _bean = null;
                    // android.util.Log.d("alauncher","mAuthorityGroupBean = " + mAuthorityGroupBean.toString());
                    if (mAuthorityGroupBean != null) {
                        _bean = App.getDaoSession().getAuthorityBeanDao().queryBuilder()
                                .where(AuthorityBeanDao.Properties.Id.eq(String.valueOf(datas.get(position).id))
                                        , AuthorityBeanDao.Properties.GroupID.eq(mAuthorityGroupBean.getId())).unique();
                    }
                    if (_bean == null) {
                        // 默认打开权限;
                    } else {
                        if (!_bean.getAuthorized()) {
                            DialogUtils.showDialog(MainActivity.this,
                                    getResources().getString(R.string.un_authorized),
                                    getResources().getString(R.string.un_authorized_tips));
                            return;
                        }
                    }
                    switch (datas.get(position).id) {
                        // 进入子菜单，鉴权；
                        case 0:
                            openActivty(Measuring2Activity.class, datas.get(position).strID);
                            break;
                        case 1:
                            openActivty(Data2Activity.class, datas.get(position).strID);
                            break;
                        case 2:
                            openActivty(ParameterManagement2Activity.class, datas.get(position).strID);
                            break;
                        case 3:
                            openActivty(CalibrationActivity.class, datas.get(position).strID);
                            break;
                        case 4:
                            openActivty(AccoutManagementActivity.class, datas.get(position).strID);
                            break;
                        case 5:
                            openActivty(AuthorityManagementActivity.class, datas.get(position).strID);
                            break;
                        case 6:
                            openActivty(Code2Activity.class, datas.get(position).strID);
                            break;
                        case 7:
                            openActivty(SystemManagementActivity.class, datas.get(position).strID);
                            break;
                        case 8:
                            openActivty(SPCStatistical2Activity.class, datas.get(position).strID);
                            break;
                        case 9:
                            openActivty(Statistical2Activity.class, datas.get(position).strID);
                            break;
                        case 10:
                            // openActivty(Statistical2Activity.class, datas.get(position).strID);
                            exitDialog();
                            break;
                        case 11:
//                            logout();
                        default:
                            break;
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    public class RecyclerItemDecoration extends RecyclerView.ItemDecoration {
        private int itemSpace;
        private int itemNum;

        /**
         * @param itemSpace item间隔
         * @param itemNum   每行item的个数
         */
        public RecyclerItemDecoration(int itemSpace, int itemNum) {
            this.itemSpace = itemSpace;
            this.itemNum = itemNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = itemSpace;
            if (parent.getChildLayoutPosition(view) % itemNum == 0) {  //parent.getChildLayoutPosition(view) 获取view的下标
                outRect.left = 0;
            } else {
                // outRect.left = itemSpace;
            }
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }


    private AlertDialog builder;

    private void exitDialog() {
        builder = new AlertDialog.Builder(MainActivity.this).create();
        builder.show();
        if (builder.getWindow() == null) {
            return;
        }
        //设置弹出框加载的布局
        builder.getWindow().setContentView(R.layout.exit_dialog);
        TextView cancellationTV = builder.findViewById(R.id.cancellation_btn);
        TextView exitTV = builder.findViewById(R.id.exit_btn);
        TextView quitTV = builder.findViewById(R.id.quit_btn);
        cancellationTV.setOnClickListener(this::onClick);
        exitTV.setOnClickListener(this::onClick);
        quitTV.setOnClickListener(this::onClick);
    }


    public void onClick(View v) {
        builder.dismiss();
        switch (v.getId()) {
            case R.id.cancellation_btn:
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.exit_btn:
                finish();
                break;
            case R.id.quit_btn:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName cn = new ComponentName("com.android.launcher3", "com.android.launcher3.Launcher");
                intent.setComponent(cn);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "未找到主界面.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
