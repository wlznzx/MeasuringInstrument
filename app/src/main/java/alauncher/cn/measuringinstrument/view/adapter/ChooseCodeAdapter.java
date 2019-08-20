package alauncher.cn.measuringinstrument.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import alauncher.cn.measuringinstrument.R;

/**
 * Created by guohao on 2017/9/6.
 */

public class ChooseCodeAdapter extends BaseAdapter {

    private List<Integer> stuList;
    private LayoutInflater inflater;

    public ChooseCodeAdapter(List<Integer> stuList, Context context) {
        this.stuList = stuList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 11;
    }

    @Override
    public Integer getItem(int position) {
        return stuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        View view = inflater.inflate(R.layout.code_item, null);
        // Integer student = getItem(position);
        //在view视图中查找id为image_photo的控件
        TextView tv_name = (TextView) view.findViewById(R.id.code_tv);
        tv_name.setText("程序" + position);
        return view;
    }

}
