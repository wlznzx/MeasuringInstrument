package alauncher.cn.measuringinstrument.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.ViewHolder;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.ResultBean2;
import alauncher.cn.measuringinstrument.utils.DateUtils;

/**
 * Created by guohao on 2017/9/6.
 */

public class Data2Adapter extends RecyclerView.Adapter<ViewHolder> {

    public static final int MYLIVE_MODE_CHECK = 0;
    public static final int MYLIVE_MODE_EDIT = 1;
    private static final String TAG = Data2Adapter.class.getSimpleName();
    int mEditMode = MYLIVE_MODE_CHECK;

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private List<ResultBean2> datas;
    private List<ParameterBean2> mParameterBean2List;

    private List<Integer> mValueID = new ArrayList<>();
    private List<Integer> mGroupID = new ArrayList<>();

    public Data2Adapter(Context context, List<ResultBean2> pDatas, List<ParameterBean2> pParameterBean2List) {
        this.context = context;
        datas = pDatas;
        for (int i = 0; i < pParameterBean2List.size(); i++) {
            mValueID.add(View.generateViewId());
            mGroupID.add(View.generateViewId());
        }
    }

    public void notifyAdapter(List<ResultBean2> myLiveList, boolean isAdd) {
        if (!isAdd) {
            this.datas = myLiveList;
        } else {
            this.datas.addAll(myLiveList);
        }
        notifyDataSetChanged();
    }

    public List<ResultBean2> getMyLiveList() {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        return datas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(context).inflate(R.layout.data_list_item2, parent, false);
        ViewGroup _Layout = itemView.findViewById(R.id.data_layout);
        int id = itemView.generateViewId();
        itemView.setId(id);
        for (int i = 0; i < mValueID.size(); i++) {
            View valueView = LayoutInflater.from(context).inflate(R.layout.measure_data_item, _Layout, false);
            valueView.setId(mValueID.get(i));
            _Layout.addView(valueView);
            TextView groupView = (TextView) LayoutInflater.from(context).inflate(R.layout.measure_data_item, _Layout, false);
            groupView.setId(mGroupID.get(i));
            _Layout.addView(groupView);
        }
        ViewHolder holder = new ViewHolder(context, itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mEditMode == MYLIVE_MODE_CHECK) {
            holder.setVisible(R.id.check_box, false);
        } else {
            holder.setVisible(R.id.check_box, true);
            if (datas.get(position).getIsSelect()) {
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
                mOnItemClickListener.onItemLongClickListener(holder.getAdapterPosition(), datas);
                return false;
            }
        });

        holder.setText(R.id.data_num, "" + (position + 1));
        holder.setText(R.id.data_handler, "" + datas.get(position).getHandlerAccount());
        holder.setText(R.id.data_workpiece_id, datas.get(position).getWorkID() == null || datas.get(position).getEvent().equals("") ? "- -" : datas.get(position).getWorkID());
        holder.setText(R.id.data_event, datas.get(position).getEvent() == null || datas.get(position).getEvent().equals("") ? "- -" : datas.get(position).getEvent());
        holder.setText(R.id.data_time, DateUtils.getDate(datas.get(position).getTimeStamp()));
        holder.setText(R.id.data_result, "" + datas.get(position).getResult());

        /*
        String _data = "";
        for (int i = 0; i < datas.get(position).getMeasurementValues().size(); i++) {
            _data = _data + datas.get(position).getMItems().get(i)
                    + "(" + datas.get(position).getMDescribe().get(i) + ")"
                    + "(" + datas.get(position).getMeasurementGroup().get(i) + ")"
                    + datas.get(position).getMeasurementValues().get(i)
                    + "\n";
        }
        holder.setText(R.id.data_m, _data);
        */
        for (int i = 0; i < datas.get(position).getMeasurementValues().size(); i++) {
            try {
                holder.setText(mValueID.get(i), datas.get(position).getMeasurementValues().get(i));
                holder.setText(mGroupID.get(i), datas.get(position).getMeasurementGroup().get(i));
            } catch (IndexOutOfBoundsException e) {

            }
        }

        /**/
        if (position % 2 == 0) {
            holder.getConvertView().setBackgroundColor(Color.argb(100, 69, 90, 100));
            // holder.setBackgroundColor(R.id.data_layout, Color.argb(100, 69, 90, 100));
        } else {
            holder.getConvertView().setBackgroundColor(Color.argb(50, 69, 90, 100));
            // holder.setBackgroundColor(R.id.data_layout, Color.argb(50, 69, 90, 100));
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
        void onItemClickListener(int pos, List<ResultBean2> myLiveList);

        void onItemLongClickListener(int pos, List<ResultBean2> myLiveList);
    }

    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }

}
