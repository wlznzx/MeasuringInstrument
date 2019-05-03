package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.User;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;


public class AccoutManagementActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_accout);
    }

    @Override
    protected void initView() {
        List<User> _datas = new ArrayList();
        _datas.add(new User());
        _datas.add(new User("acccout","name","123456",1,"email",1234567891));
        _datas.add(new User("acccout","name","123456",1,"email",1234567891));
        _datas.add(new User("acccout","name","123456",1,"email",1234567891));
        _datas.add(new User("acccout","name","123456",1,"email",1234567891));
        _datas.add(new User("acccout","name","123456",1,"email",1234567891));
        _datas.add(new User("acccout","name","123456",1,"email",1234567891));
        AccoutAdapter _adapter = new AccoutAdapter(_datas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AccoutManagementActivity.this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(_adapter);
    }


    class AccoutAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<User> datas;

        public AccoutAdapter(List<User> pDatas) {
            datas = pDatas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(AccoutManagementActivity.this, parent, R.layout.accout_list_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setImageResource(R.id.accout_item_radio, R.drawable.radio_checked);
            holder.setText(R.id.accout_tv, "" + datas.get(position).accout);
            holder.setText(R.id.accout_id_tv, "" + datas.get(position).id);
            holder.setText(R.id.accout_name_tv, datas.get(position).name);
            holder.setText(R.id.accout_status_tv, "" + R.string.on_job);
            holder.setText(R.id.accout_email_tv, "" + datas.get(position).email);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

}
