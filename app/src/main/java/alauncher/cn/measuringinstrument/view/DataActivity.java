package alauncher.cn.measuringinstrument.view;

import android.os.Bundle;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.ResultData;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBeanDao;
import alauncher.cn.measuringinstrument.utils.DateUtils;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class DataActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView rv;

    public ResultBeanDao mResultBeanDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_data);
    }

    @Override
    protected void initView() {
        List<ResultData> _datas = new ArrayList();
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));
        _datas.add(new ResultData(1, "操作员", System.currentTimeMillis(), 123456, "换刀", 1, 0.7023, 0.7023, 0.7023, 0.7023));


        mResultBeanDao = App.getDaoSession().getResultBeanDao();


        DataActivity.DataAdapter _adapter = new DataActivity.DataAdapter(mResultBeanDao.queryBuilder().orderDesc(ResultBeanDao.Properties.Id).list());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DataActivity.this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(_adapter);
    }

    class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

        List<ResultBean> datas;

        public DataAdapter(List<ResultBean> pDatas) {
            datas = pDatas;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return ViewHolder.createViewHolder(DataActivity.this, parent, R.layout.data_list_item);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setText(R.id.data_handler, "" + datas.get(position).getHandlerAccout());
            holder.setText(R.id.data_workpiece_id, "" + datas.get(position).getWorkid());
            holder.setText(R.id.data_event, datas.get(position).getEvent());
            holder.setText(R.id.data_m1, "" + datas.get(position).m1);
            holder.setText(R.id.data_m1_group, "" + datas.get(position).getM1_group());
            holder.setText(R.id.data_m2, "" + datas.get(position).m2);
            holder.setText(R.id.data_m2_group, "" + datas.get(position).getM2_group());
            holder.setText(R.id.data_m3, "" + datas.get(position).m3);
            holder.setText(R.id.data_m3_group, "" + datas.get(position).getM3_group());
            holder.setText(R.id.data_m4, "" + datas.get(position).m4);
            holder.setText(R.id.data_m4_group, "" + datas.get(position).getM4_group());
            holder.setText(R.id.data_time, DateUtils.getDate(datas.get(position).getTimeStamp()));
            holder.setText(R.id.data_result, "" + datas.get(position).getResult());
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }
}
