package alauncher.cn.measuringinstrument.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.CodeBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBean2Dao;
import alauncher.cn.measuringinstrument.utils.DialogUtils;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;

/**
 * Created by guohao on 2017/9/6.
 */

public class CodeListAdapter extends BaseAdapter {
    private List<CodeBean> listText;
    private Context context;
    // 用于记录每个RadioButton的状态，并保证只可选一个
    public HashMap<String, Boolean> states = new HashMap<String, Boolean>();

    public int currentCodeID;

    private DataUpdateInterface mDataUpdateInterface;

    private String filterTest;

    public CodeListAdapter(List<CodeBean> listText, HashMap<String, Boolean> pstates, DataUpdateInterface pDataUpdateInterface, Context context) {
        this.listText = listText;
        this.states = pstates;
        this.context = context;
        mDataUpdateInterface = pDataUpdateInterface;
        currentCodeID = App.getSetupBean().getCodeID();
    }

    public void setFilterText(String text) {
        filterTest = text;
    }

    public void setList(List<CodeBean> pList) {
        listText = pList;
    }

    @Override
    public int getCount() {
        //return返回的是int类型，也就是页面要显示的数量。
        return listText.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null || true) {
            //通过一个打气筒 inflate 可以把一个布局转换成一个view对象
            view = View.inflate(context, R.layout.list_code_item, null);
        } else {
            view = convertView;//复用历史缓存对象
        }

        View list_item_layout = view.findViewById(R.id.list_item_layout);


        //单选按钮的文字
        TextView radioText = view.findViewById(R.id.tv_radio_text);
        //单选按钮
        RadioButton radioButton = view.findViewById(R.id.rb_radio_button);


        /**/
        if (TextUtils.isEmpty(filterTest)) {

        } else {
            if (listText.get(position).getName().contains(filterTest) || filterTest.equals("")) {
//                list_item_layout.setVisibility(View.VISIBLE);
//                radioButton.setVisibility(View.VISIBLE);
            } else {
//                list_item_layout.setVisibility(View.GONE);
//                radioButton.setVisibility(View.GONE);
                view = View.inflate(context, R.layout.list_code_item_null, null);
                return view;
            }
        }


        radioButton.setText(String.valueOf(position + 1));
        radioText.setText(listText.get(position).getName());
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 重置，确保最多只有一项被选中
                for (String key : states.keySet()) {
                    states.put(key, false);
                }
                states.put(String.valueOf(position), true);
                currentCodeID = listText.get(position).getId().intValue();
                CodeListAdapter.this.notifyDataSetChanged();
            }
        });
        boolean res;
        /*
        if (states.get(String.valueOf(position)) == null || states.get(String.valueOf(position)) == false) {
            res = false;
            states.put(String.valueOf(position), false);
        } else {
            res = true;
        }
        */

        if (currentCodeID == listText.get(position).getId().intValue()) {
            res = true;
        } else {
            res = false;
        }


        radioButton.setChecked(res);

        radioButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (App.getSetupBean().getCodeID() == listText.get(position).getId().intValue()) {
                    DialogUtils.showDialog(context, context.getResources().getString(R.string.cannot_delete),
                            context.getResources().getString(R.string.cannot_delete_msg));
                    return false;
                }

                final AlertDialog builder = new AlertDialog.Builder(context)
                        .create();
                builder.show();
                if (builder.getWindow() == null) return false;
                builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
                TextView msg = builder.findViewById(R.id.tv_msg);
                Button cancel = builder.findViewById(R.id.btn_cancle);
                Button sure = builder.findViewById(R.id.btn_sure);
                msg.setText("是否删除 " + listText.get(position).getName() + " ?");
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        App.getDaoSession().getCodeBeanDao().queryBuilder().where(CodeBeanDao.Properties.Id.eq(listText.get(position).getId())).buildDelete().executeDeleteWithoutDetachingEntities();
                        /*
                         * 还要依次删除其他程序相关;
                         * */
                        App.getDaoSession().getCalibrationBeanDao().deleteByKey(listText.get(position).getId());
                        App.getDaoSession().getForceCalibrationBeanDao().deleteByKey(listText.get(position).getId());
                        App.getDaoSession().getMeasureConfigurationBeanDao().deleteByKey(listText.get(position).getId());
                        App.getDaoSession().getParameterBeanDao().deleteByKey(listText.get(position).getId());
                        App.getDaoSession().getCodeBeanDao().deleteByKey(listText.get(position).getId());
                        App.getDaoSession().getStoreBean2Dao().queryBuilder()
                                .where(StoreBean2Dao.Properties.CodeID.eq(listText.get(position).getId())).buildDelete().executeDeleteWithoutDetachingEntities();
                        App.getDaoSession().getParameterBean2Dao().queryBuilder()
                                .where(ParameterBean2Dao.Properties.CodeID.eq(listText.get(position).getId())).buildDelete().executeDeleteWithoutDetachingEntities();
                        builder.dismiss();
                        if (mDataUpdateInterface != null) {
                            mDataUpdateInterface.dataUpdate();
                        }
                    }
                });
                return false;
            }
        });
        return view;
    }

}
