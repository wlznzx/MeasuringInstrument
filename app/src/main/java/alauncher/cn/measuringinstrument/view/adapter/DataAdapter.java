package alauncher.cn.measuringinstrument.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.utils.DateUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by guohao on 2017/9/6.
 */

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final int MYLIVE_MODE_CHECK = 0;
    public static final int MYLIVE_MODE_EDIT = 1;
    private static final String TAG = DataAdapter.class.getSimpleName();
    int mEditMode = MYLIVE_MODE_CHECK;

    private int secret = 0;
    private String title = "";
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    List<ResultBean> datas;

    private ParameterBean mParameterBean;

    public DataAdapter(Context context, List<ResultBean> pDatas, ParameterBean parameterBean) {
        this.context = context;
        datas = pDatas;
        mParameterBean = parameterBean;
    }


    public void notifyAdapter(List<ResultBean> myLiveList, boolean isAdd) {

        if (!isAdd) {
            this.datas = myLiveList;
        } else {
            this.datas.addAll(myLiveList);
        }
        notifyDataSetChanged();
    }

    public List<ResultBean> getMyLiveList() {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        return datas;
    }

    @NonNull
    @Override
    public alauncher.cn.measuringinstrument.base.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return alauncher.cn.measuringinstrument.base.ViewHolder.createViewHolder(context, parent, R.layout.data_list_item);
    }

    @Override
    public void onBindViewHolder(@NonNull alauncher.cn.measuringinstrument.base.ViewHolder holder, int position) {
        if (mEditMode == MYLIVE_MODE_CHECK) {
            holder.setVisible(R.id.check_box, false);
        } else {
            holder.setVisible(R.id.check_box, true);

            if (datas.get(position).isSelect()) {
                holder.setImageResource(R.id.check_box, R.mipmap.ic_checked);
            } else {
                holder.setImageResource(R.id.check_box, R.mipmap.ic_uncheck);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), datas);
            }
        });


        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // setEditMode(MYLIVE_MODE_EDIT);
                mOnItemClickListener.onItemLongClickListener(holder.getAdapterPosition(), datas);
                return false;
            }
        });

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
        holder.setText(R.id.data_num, "" + (position + 1));

        if (mParameterBean != null) {
            holder.setVisible(R.id.data_m1, mParameterBean.getM1_enable() ? true : false);
            holder.setVisible(R.id.data_m1_group, mParameterBean.getM1_enable() ? true : false);
            holder.setVisible(R.id.data_m2, mParameterBean.getM2_enable() ? true : false);
            holder.setVisible(R.id.data_m2_group, mParameterBean.getM2_enable() ? true : false);
            holder.setVisible(R.id.data_m3, mParameterBean.getM3_enable() ? true : false);
            holder.setVisible(R.id.data_m3_group, mParameterBean.getM3_enable() ? true : false);
            holder.setVisible(R.id.data_m4, mParameterBean.getM4_enable() ? true : false);
            holder.setVisible(R.id.data_m4_group, mParameterBean.getM4_enable() ? true : false);
        }

        if (position % 2 == 0) {
            // holder.setBackgroundColor(R.id.data_layout, Color.argb(250, 255, 255, 255));
            holder.setBackgroundColor(R.id.data_layout, Color.argb(100, 69, 90, 100));
        } else {
            holder.setBackgroundColor(R.id.data_layout, Color.argb(50, 69, 90, 100));
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int pos, List<ResultBean> myLiveList);

        void onItemLongClickListener(int pos, List<ResultBean> myLiveList);
    }

    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }

}
