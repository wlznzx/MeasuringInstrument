package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseOActivity;
import alauncher.cn.measuringinstrument.bean.FilterBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.ResultBean2;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBean2Dao;
import alauncher.cn.measuringinstrument.utils.CommonUtil;
import alauncher.cn.measuringinstrument.utils.DateUtils;
import alauncher.cn.measuringinstrument.utils.ExcelUtil;
import alauncher.cn.measuringinstrument.utils.StringConverter;
import alauncher.cn.measuringinstrument.view.adapter.Data2Adapter;
import alauncher.cn.measuringinstrument.widget.FilterDialog;
import butterknife.BindView;
import butterknife.OnClick;

import static alauncher.cn.measuringinstrument.view.adapter.DataAdapter.MYLIVE_MODE_CHECK;
import static alauncher.cn.measuringinstrument.view.adapter.DataAdapter.MYLIVE_MODE_EDIT;

public class Data2Activity extends BaseOActivity implements View.OnClickListener, Data2Adapter.OnItemClickListener, FilterDialog.FilterInterface {

    @BindView(R.id.rv)
    RecyclerView rv;

    @BindView(R.id.tv_select_num)
    TextView mTvSelectNum;
    @BindView(R.id.btn_delete)
    Button mBtnDelete;
    @BindView(R.id.select_all)
    TextView mSelectAll;
    @BindView(R.id.btn_quit)
    TextView quitBtn;
    @BindView(R.id.btn_excel)
    TextView excelBtn;
    @BindView(R.id.btn_filter)
    TextView filterBtn;
    @BindView(R.id.bottom_layout)
    ViewGroup bottomLayout;
    @BindView(R.id.ll_mycollection_bottom_dialog)
    LinearLayout mLlMyCollectionBottomDialog;

    @BindView(R.id.data_title_layout)
    LinearLayout dataTitleLayout;

    public ResultBean2Dao mResultBean2Dao;

    private Data2Adapter mData2Adapter;

    private List<ParameterBean2> mDates;

    private int mEditMode = MYLIVE_MODE_CHECK;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;

    private String[] title = {"操作员", "时间", "工件号", "事件", "结果"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_data2);
    }

    @Override
    protected void initView() {
        mResultBean2Dao = App.getDaoSession().getResultBean2Dao();
        mDates = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID()), ParameterBean2Dao.Properties.Enable.eq(true))
                .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();
        LayoutInflater inflater = LayoutInflater.from(this);
        mData2Adapter = new Data2Adapter(Data2Activity.this, mResultBean2Dao.queryBuilder()
                .where(ResultBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderDesc(ResultBean2Dao.Properties.Id).list(), mDates);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Data2Activity.this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(mData2Adapter);


        for (ParameterBean2 _bean2 : mDates) {
            TextView valueView = (TextView) inflater.inflate(R.layout.measure_data_item, dataTitleLayout, false);
            dataTitleLayout.addView(valueView);
            TextView groupView = (TextView) inflater.inflate(R.layout.measure_data_item, dataTitleLayout, false);
            dataTitleLayout.addView(groupView);
            valueView.setText("M" + (_bean2.getSequenceNumber() + 1));
            groupView.setText("M" + (_bean2.getSequenceNumber() + 1) + "分组");
        }

        mData2Adapter.setOnItemClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
        excelBtn.setOnClickListener(this);
        filterBtn.setOnClickListener(this);

        actionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEditMode == MYLIVE_MODE_EDIT) {
                    updateEditMode();
                    return;
                }
                finish();
            }
        });
    }

    /**
     * 全选和反选
     */
    private void selectAllMain() {
        if (mData2Adapter == null) return;
        if (!isSelectAll) {
            for (int i = 0, j = mData2Adapter.getMyLiveList().size(); i < j; i++) {
                mData2Adapter.getMyLiveList().get(i).setIsSelect(true);
            }
            index = mData2Adapter.getMyLiveList().size();
            mBtnDelete.setEnabled(true);
            mSelectAll.setText("取消全选");
            isSelectAll = true;
        } else {
            for (int i = 0, j = mData2Adapter.getMyLiveList().size(); i < j; i++) {
                mData2Adapter.getMyLiveList().get(i).setIsSelect(false);
            }
            index = 0;
            mBtnDelete.setEnabled(false);
            mSelectAll.setText("全选");
            isSelectAll = false;
        }
        mData2Adapter.notifyDataSetChanged();
        setBtnBackground(index);
        mTvSelectNum.setText(String.valueOf(index));
    }

    /**
     * 删除逻辑
     */
    private void deleteVideo() {
        if (index == 0) {
            mBtnDelete.setEnabled(false);
            return;
        }
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = builder.findViewById(R.id.tv_msg);
        Button cancel = builder.findViewById(R.id.btn_cancle);
        Button sure = builder.findViewById(R.id.btn_sure);
        if (msg == null || cancel == null || sure == null) return;
        
        if (index == 1) {
            msg.setText("删除后不可恢复，是否删除该条目？");
        } else {
            msg.setText("删除后不可恢复，是否删除这" + index + "个条目？");
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteTask().execute();
                builder.dismiss();
            }
        });
    }

    /**
     * 删除逻辑
     */
    private void excelDatas() {
        if (index == 0) {
            excelBtn.setEnabled(false);
            return;
        }
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = builder.findViewById(R.id.tv_msg);
        Button cancel = builder.findViewById(R.id.btn_cancle);
        Button sure = builder.findViewById(R.id.btn_sure);
        if (msg == null || cancel == null || sure == null) return;

        if (index == 1) {
            msg.setText("是否导出这个条目");
        } else {
            msg.setText("是否导出这" + index + "个条目？");
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ExcelTask().execute();
                builder.dismiss();
            }
        });
    }

    private void updateEditMode() {
        mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
        if (mEditMode == MYLIVE_MODE_EDIT) {
            mLlMyCollectionBottomDialog.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
            editorStatus = true;
        } else {
            mLlMyCollectionBottomDialog.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
            editorStatus = false;
            clearAll();
        }
        mData2Adapter.setEditMode(mEditMode);
    }


    private void clearAll() {
        // 清除选中状态
        for (int i = mData2Adapter.getMyLiveList().size(), j = 0; i > j; i--) {
            ResultBean2 _bean = mData2Adapter.getMyLiveList().get(i - 1);
            if (_bean.getIsSelect()) {
                _bean.setIsSelect(false);
            }
        }
        index = 0;
        mTvSelectNum.setText(String.valueOf(0));
        isSelectAll = false;
        mSelectAll.setText(R.string.select_all);
        setBtnBackground(0);
    }

    /**
     * 根据选择的数量是否为0来判断按钮的是否可点击.
     *
     * @param size
     */
    private void setBtnBackground(int size) {
        if (size != 0) {
            mBtnDelete.setBackgroundResource(R.drawable.button_shape);
            mBtnDelete.setEnabled(true);
            mBtnDelete.setTextColor(Color.WHITE);
        } else {
            mBtnDelete.setBackgroundResource(R.drawable.button_noclickable_shape);
            mBtnDelete.setEnabled(false);
            mBtnDelete.setTextColor(ContextCompat.getColor(this, R.color.color_b7b8bd));
        }
    }


    @Override
    public void onItemClickListener(int pos, List<ResultBean2> myLiveList) {
        if (pos == -1) return;
        if (editorStatus) {
            ResultBean2 _bean = myLiveList.get(pos);
            boolean isSelect = _bean.getIsSelect();
            if (!isSelect) {
                index++;
                _bean.setIsSelect(true);
                if (index == myLiveList.size()) {
                    isSelectAll = true;
                    mSelectAll.setText("取消全选");
                }
            } else {
                _bean.setIsSelect(false);
                index--;
                isSelectAll = false;
                mSelectAll.setText(R.string.select_all);
            }
            setBtnBackground(index);
            mTvSelectNum.setText(String.valueOf(index));
            mData2Adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemLongClickListener(int pos, List<ResultBean2> myLiveList) {
        updateEditMode();
    }

    @OnClick(R.id.export_excel_btn)
    public void inEditMode(View v) {
        updateEditMode();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                deleteVideo();
                break;
            case R.id.select_all:
                selectAllMain();
                break;
            case R.id.btn_quit:
                updateEditMode();
                break;
            case R.id.btn_excel:
                excelDatas();
                break;
            case R.id.btn_filter:
                FilterDialog mFilterDialog = new FilterDialog(this, this);
                mFilterDialog.show();
                break;
            default:
                break;
        }
    }


    public class DeleteTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Data2Activity.this);
            dialog.setTitle("删除");
            dialog.setMessage("正在删除数据 , 请稍等.");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        //第二个执行方法,在onPreExecute()后执行，用于后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            //处理耗时操作
            for (int i = mData2Adapter.getMyLiveList().size(), j = 0; i > j; i--) {
                ResultBean2 _bean = mData2Adapter.getMyLiveList().get(i - 1);
                if (_bean.getIsSelect()) {
                    mData2Adapter.getMyLiveList().remove(_bean);
                    App.getDaoSession().getResultBean2Dao().delete(_bean);
                    index--;
                }
            }
            return "后台任务执行完毕";
        }

        /*这个函数在doInBackground调用publishProgress(int i)时触发，虽然调用时只有一个参数
         但是这里取到的是一个数组,所以要用progesss[0]来取值
         第n个参数就用progress[n]来取值   */
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            //"loading..." + progresses[0] + "%"
        }

        /*doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
        这里的result就是上面doInBackground执行后的返回值，所以这里是"后台任务执行完毕"  */
        @Override
        protected void onPostExecute(String result) {
            index = 0;
            mTvSelectNum.setText(String.valueOf(0));
            setBtnBackground(index);
            if (mData2Adapter.getMyLiveList().size() == 0) {
                mLlMyCollectionBottomDialog.setVisibility(View.GONE);
            }
            mData2Adapter.notifyDataSetChanged();
            dialog.dismiss();
            updateEditMode();
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }


    /*
     *
     * 导出Excel Task.
     *
     * */
    public class ExcelTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;
        private String path = Environment.getExternalStorageDirectory() + "/NTGage/";

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Data2Activity.this);
            dialog.setTitle("导出Excel");
            dialog.setMessage("正在将数据导出Excel中 , 请稍等.");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        //第二个执行方法,在onPreExecute()后执行，用于后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            //处理耗时操作

            File destDir = new File(path);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            List<ResultBean2> selectedList = new ArrayList<>();
            for (int i = mData2Adapter.getMyLiveList().size(), j = 0; i > j; i--) {
                ResultBean2 _bean = mData2Adapter.getMyLiveList().get(i - 1);
                if (_bean.getIsSelect()) {
                    selectedList.add(_bean);
                    index--;
                }
            }

            Collections.reverse(selectedList);

            path = path + "datas_" + DateUtils.getFileDate(System.currentTimeMillis()) + ".xls";
            List<String> titles = new ArrayList<>();
            for (int i = 0; i < title.length; i++) {
                titles.add(title[i]);
            }

            if (selectedList.size() > 0) {
                ResultBean2 _bean = selectedList.get(0);
                for (int i = 0; i < _bean.getMItems().size(); i++) {
                    titles.add("M" + _bean.getMItems().get(i) + "(" + _bean.getMDescribe().get(i) + ")");
                }
            }

            ExcelUtil.initExcel(path, "data", titles);
            ExcelUtil.writeObjListToExcel(selectedList, path, Data2Activity.this);
            return "后台任务执行完毕";
        }

        /*这个函数在doInBackground调用publishProgress(int i)时触发，虽然调用时只有一个参数
         但是这里取到的是一个数组,所以要用progesss[0]来取值
         第n个参数就用progress[n]来取值   */
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            //"loading..." + progresses[0] + "%"
        }

        /*doInBackground返回时触发，换句话说，就是doInBackground执行完后触发
        这里的result就是上面doInBackground执行后的返回值，所以这里是"后台任务执行完毕"  */
        @Override
        protected void onPostExecute(String result) {
            index = 0;
            mTvSelectNum.setText(String.valueOf(0));
            setBtnBackground(index);
            if (mData2Adapter.getMyLiveList().size() == 0) {
                mLlMyCollectionBottomDialog.setVisibility(View.GONE);
            }
            mData2Adapter.notifyDataSetChanged();
            dialog.dismiss();
            updateEditMode();
            Toast.makeText(Data2Activity.this, "导出至 : " + path, Toast.LENGTH_LONG).show();
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }

    @Override
    public void dataFilterUpdate(FilterBean bean) {

        android.util.Log.d("wlDebug", bean.toString());

        //请求参数
        ArrayList<String> strParamLt = new ArrayList<String>();

        String queryString = "";
        if (bean.getStartTime() == 0 && bean.getEndTime() == 0) {
            queryString = "SELECT * FROM " + ResultBean2Dao.TABLENAME + " where 1==1 ";
        } else {
            queryString = "SELECT * FROM " + ResultBean2Dao.TABLENAME + " where " + ResultBean2Dao.Properties.TimeStamp.columnName + " between " + bean.getStartTime() + " and " + bean.getEndTime();
        }

        //用户
        if (!CommonUtil.isNull(bean.getHandler())) {
            queryString = queryString + " and "
                    + ResultBean2Dao.Properties.HandlerAccount.columnName + " =  ?";
            strParamLt.add(bean.getHandler());
        }

        // Event
        if (!CommonUtil.isNull(bean.getEvent())) {
            queryString = queryString + " and "
                    + ResultBean2Dao.Properties.Event.columnName + " =  ?";
            strParamLt.add(bean.getEvent());
        }

        // 工件号
        if (!CommonUtil.isNull(bean.getWorkid())) {
            queryString = queryString + " and "
                    + ResultBean2Dao.Properties.WorkID.columnName + " =  ?";
            strParamLt.add(bean.getWorkid());
        }

        // 结果
        if (!CommonUtil.isNull(bean.getResult())) {
            queryString = queryString + " and "
                    + ResultBean2Dao.Properties.Result.columnName + " =  ?";
            strParamLt.add(bean.getResult());
        }

        Object[] objs = strParamLt.toArray();
        String[] strs = new String[objs.length];

        for (int i = 0; i < objs.length; i++) {
            strs[i] = objs[i].toString();
        }

        Cursor cursor = mResultBean2Dao.getDatabase().rawQuery(queryString, strs);

        int HandlerAccount = cursor.getColumnIndex(ResultBean2Dao.Properties.HandlerAccount.columnName);
        int TimeStamp = cursor.getColumnIndex(ResultBean2Dao.Properties.TimeStamp.columnName);
        int WorkID = cursor.getColumnIndex(ResultBean2Dao.Properties.WorkID.columnName);
        int Event = cursor.getColumnIndex(ResultBean2Dao.Properties.Event.columnName);
        int Result = cursor.getColumnIndex(ResultBean2Dao.Properties.Result.columnName);
        int measurementValuesIndex = cursor.getColumnIndex(ResultBean2Dao.Properties.MeasurementValues.columnName);
        int measurementGroupIndex = cursor.getColumnIndex(ResultBean2Dao.Properties.MeasurementGroup.columnName);
        int mItemsIndex = cursor.getColumnIndex(ResultBean2Dao.Properties.MItems.columnName);
        int mDescribeIndex = cursor.getColumnIndex(ResultBean2Dao.Properties.MDescribe.columnName);

        List<ResultBean2> _datas = new ArrayList<>();

        while (cursor.moveToNext()) {
            ResultBean2 rBean = new ResultBean2();
            rBean.setHandlerAccount(cursor.getString(HandlerAccount));
            rBean.setWorkID(cursor.getString(WorkID));
            rBean.setTimeStamp(cursor.getLong(TimeStamp));
            rBean.setEvent(cursor.getString(Event));
            rBean.setResult(cursor.getString(Result));
            rBean.setMeasurementValues(StringConverter.convertToEntityPropertyG(cursor.getString(measurementValuesIndex)));
            rBean.setMeasurementGroup(StringConverter.convertToEntityPropertyG(cursor.getString(measurementGroupIndex)));
            rBean.setMItems(StringConverter.convertToEntityPropertyG(cursor.getString(mItemsIndex)));
            rBean.setMDescribe(StringConverter.convertToEntityPropertyG(cursor.getString(mDescribeIndex)));
            _datas.add(rBean);
        }
        mData2Adapter.notifyAdapter(_datas, false);
    }
}
