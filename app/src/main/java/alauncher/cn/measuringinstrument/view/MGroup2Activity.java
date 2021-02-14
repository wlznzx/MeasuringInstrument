package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.GroupBean2;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBean2Dao;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import alauncher.cn.measuringinstrument.widget.GroupEditDialog;
import butterknife.BindView;
import butterknife.OnClick;


public class MGroup2Activity extends BaseOActivity implements DataUpdateInterface {

    @BindView(R.id.rv)
    public RecyclerView rv;

    @BindView(R.id.add_group_btn)
    public TextView addBtn;

    private List<GroupBean2> mGroupBean2List;

    private long pID;

    private MGroupAdapter mMGroupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_m_group2);
    }

    @Override
    protected void initView() {
        pID = getIntent().getLongExtra("pID", -1);
        mGroupBean2List = App.getDaoSession().getGroupBean2Dao().queryBuilder().where(GroupBean2Dao.Properties.PID.eq(pID)).list();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MGroup2Activity.this);
        rv.setLayoutManager(layoutManager);
        mMGroupAdapter = new MGroupAdapter();
        rv.setAdapter(mMGroupAdapter);
        actionTitleTV.setText(R.string.grouping);
    }

/*
    隐藏键盘
    public void hideInput() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }*/


    @OnClick(R.id.add_group_btn)
    public void addGroup(View v) {
        // Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        GroupEditDialog _dialog = new GroupEditDialog(MGroup2Activity.this, null);
        _dialog.setDataUpdateInterface(MGroup2Activity.this);
        _dialog.setPID(pID);
        _dialog.show();
    }

    @Override
    public void dataUpdate() {
        mGroupBean2List = App.getDaoSession().getGroupBean2Dao().queryBuilder().where(GroupBean2Dao.Properties.PID.eq(pID)).list();
        mMGroupAdapter.notifyDataSetChanged();
    }

    class MGroupAdapter extends RecyclerView.Adapter<ViewHolder> {
        public MGroupAdapter() {

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(MGroup2Activity.this, parent, R.layout.group_list_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GroupBean2 _bean = mGroupBean2List.get(position);
            holder.setText(R.id.group_name_tv, _bean.getName());
            holder.setText(R.id.upper_limit_tv, String.valueOf(_bean.getUpperLimit()));
            holder.setText(R.id.lower_limit_tv, String.valueOf(_bean.getLowerLimit()));
            holder.setText(R.id.describe_tv, _bean.getDescribe());

            holder.setOnClickListener(R.id.data_layout, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupEditDialog _dialog = new GroupEditDialog(MGroup2Activity.this, _bean);
                    _dialog.setDataUpdateInterface(MGroup2Activity.this);
                    _dialog.show();
                }
            });

            holder.setOnLongClickListener(R.id.data_layout, new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog builder = new AlertDialog.Builder(MGroup2Activity.this)
                            .create();
                    builder.show();
                    if (builder.getWindow() == null) {
                        return false;
                    }
                    builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
                    TextView msg = builder.findViewById(R.id.tv_msg);
                    Button cancel = builder.findViewById(R.id.btn_cancle);
                    Button sure = builder.findViewById(R.id.btn_sure);
                    msg.setText("是否删除 " + _bean.getDescribe() + " ?");
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            App.getDaoSession().getGroupBean2Dao().delete(_bean);
                            builder.dismiss();
                            dataUpdate();
                        }
                    });
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mGroupBean2List.size();
        }
    }

}
