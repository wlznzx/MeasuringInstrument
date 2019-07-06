package alauncher.cn.measuringinstrument.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.base.BaseActivity;
import alauncher.cn.measuringinstrument.bean.FilterBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.ResultData;
import alauncher.cn.measuringinstrument.database.greenDao.db.ResultBeanDao;
import alauncher.cn.measuringinstrument.utils.DateUtils;
import alauncher.cn.measuringinstrument.utils.ExcelUtil;
import alauncher.cn.measuringinstrument.view.adapter.DataAdapter;
import alauncher.cn.measuringinstrument.widget.CalculateDialog;
import alauncher.cn.measuringinstrument.widget.FilterDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import static alauncher.cn.measuringinstrument.view.adapter.DataAdapter.MYLIVE_MODE_CHECK;
import static alauncher.cn.measuringinstrument.view.adapter.DataAdapter.MYLIVE_MODE_EDIT;

public class DataActivity extends BaseActivity implements View.OnClickListener, DataAdapter.OnItemClickListener, FilterDialog.FilterInterface {

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
    @BindView(R.id.ll_mycollection_bottom_dialog)
    LinearLayout mLlMycollectionBottomDialog;

    public ResultBeanDao mResultBeanDao;

    DataAdapter mDataAdapter;

    private int mEditMode = MYLIVE_MODE_CHECK;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;

    private String[] title = {"操作员", "时间", "工件号", "事件", "结果", "M1", "M1分组", "M2", "M2分组", "M3", "M3分组", "M4", "M4分组"};

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

        mResultBeanDao = App.getDaoSession().getResultBeanDao();
        mDataAdapter = new DataAdapter(DataActivity.this, mResultBeanDao.queryBuilder().orderDesc(ResultBeanDao.Properties.Id).list());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DataActivity.this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(mDataAdapter);

        mDataAdapter.setOnItemClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);
        quitBtn.setOnClickListener(this);
        excelBtn.setOnClickListener(this);
        filterBtn.setOnClickListener(this);
    }

    /**
     * 全选和反选
     */
    private void selectAllMain() {
        if (mDataAdapter == null) return;
        if (!isSelectAll) {
            for (int i = 0, j = mDataAdapter.getMyLiveList().size(); i < j; i++) {
                mDataAdapter.getMyLiveList().get(i).setSelect(true);
            }
            index = mDataAdapter.getMyLiveList().size();
            mBtnDelete.setEnabled(true);
            mSelectAll.setText("取消全选");
            isSelectAll = true;
        } else {
            for (int i = 0, j = mDataAdapter.getMyLiveList().size(); i < j; i++) {
                mDataAdapter.getMyLiveList().get(i).setSelect(false);
            }
            index = 0;
            mBtnDelete.setEnabled(false);
            mSelectAll.setText("全选");
            isSelectAll = false;
        }
        mDataAdapter.notifyDataSetChanged();
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
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;

        if (index == 1) {
            msg.setText("删除后不可恢复，是否删除该条目？");
        } else {
            msg.setText("删除后不可恢复，是否删除这" + index + "个条目？");
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for (int i = mDataAdapter.getMyLiveList().size(), j = 0; i > j; i--) {
//                    ResultBean _baen = mDataAdapter.getMyLiveList().get(i - 1);
//                    if (_baen.isSelect()) {
//                        mDataAdapter.getMyLiveList().remove(_baen);
//                        index--;
//                    }
//                }

//                index = 0;
//                mTvSelectNum.setText(String.valueOf(0));
//                setBtnBackground(index);
//                if (mDataAdapter.getMyLiveList().size() == 0) {
//                    mLlMycollectionBottomDialog.setVisibility(View.GONE);
//                }
//                mDataAdapter.notifyDataSetChanged();
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
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;

        if (index == 1) {
            msg.setText("是否导出这个条目");
        } else {
            msg.setText("是否导出这" + index + "个条目？");
        }
        cancle.setOnClickListener(new View.OnClickListener() {
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

    private void updataEditMode() {
        mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
        if (mEditMode == MYLIVE_MODE_EDIT) {
            // mBtnEditor.setText("取消");
            mLlMycollectionBottomDialog.setVisibility(View.VISIBLE);
            filterBtn.setVisibility(View.GONE);
            editorStatus = true;
        } else {
            // mBtnEditor.setText("编辑");
            mLlMycollectionBottomDialog.setVisibility(View.GONE);
            filterBtn.setVisibility(View.VISIBLE);
            editorStatus = false;
            clearAll();
        }
        mDataAdapter.setEditMode(mEditMode);
    }


    private void clearAll() {
        // 清除选中状态
        for (int i = mDataAdapter.getMyLiveList().size(), j = 0; i > j; i--) {
            ResultBean _baen = mDataAdapter.getMyLiveList().get(i - 1);
            if (_baen.isSelect()) {
                _baen.setSelect(false);
            }
        }
        index = 0;
        mTvSelectNum.setText(String.valueOf(0));
        isSelectAll = false;
        mSelectAll.setText("全选");
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
    public void onItemClickListener(int pos, List<ResultBean> myLiveList) {
        android.util.Log.d("wlDebug", "pos = " + pos);
        if (editorStatus) {
            ResultBean _bean = myLiveList.get(pos);
            boolean isSelect = _bean.isSelect();
            if (!isSelect) {
                index++;
                _bean.setSelect(true);
                if (index == myLiveList.size()) {
                    isSelectAll = true;
                    mSelectAll.setText("取消全选");
                }
            } else {
                _bean.setSelect(false);
                index--;
                isSelectAll = false;
                mSelectAll.setText("全选");
            }
            setBtnBackground(index);
            mTvSelectNum.setText(String.valueOf(index));
            mDataAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemLongClickListener(int pos, List<ResultBean> myLiveList) {
        updataEditMode();
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
                updataEditMode();
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
            dialog = new ProgressDialog(DataActivity.this);
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
            for (int i = mDataAdapter.getMyLiveList().size(), j = 0; i > j; i--) {
                ResultBean _baen = mDataAdapter.getMyLiveList().get(i - 1);
                if (_baen.isSelect()) {
                    mDataAdapter.getMyLiveList().remove(_baen);
                    mResultBeanDao.delete(_baen);
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
            if (mDataAdapter.getMyLiveList().size() == 0) {
                mLlMycollectionBottomDialog.setVisibility(View.GONE);
            }
            mDataAdapter.notifyDataSetChanged();
            dialog.dismiss();
            updataEditMode();
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
        private String path = Environment.getExternalStorageDirectory() + "/ETGate/";

        //执行的第一个方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(DataActivity.this);
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

            List<ResultBean> selectedList = new ArrayList<>();
            for (int i = mDataAdapter.getMyLiveList().size(), j = 0; i > j; i--) {
                ResultBean _baen = mDataAdapter.getMyLiveList().get(i - 1);
                if (_baen.isSelect()) {
                    selectedList.add(_baen);
                    index--;
                }
            }

            path = path + "datas_" + DateUtils.getFileDate(System.currentTimeMillis()) + ".xls";
            ExcelUtil.initExcel(path, "data", title);
            ExcelUtil.writeObjListToExcel(selectedList, path, DataActivity.this);
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
            if (mDataAdapter.getMyLiveList().size() == 0) {
                mLlMycollectionBottomDialog.setVisibility(View.GONE);
            }
            mDataAdapter.notifyDataSetChanged();
            dialog.dismiss();
            updataEditMode();
            Toast.makeText(DataActivity.this, "导出至 : " + path, Toast.LENGTH_LONG).show();
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }

    @Override
    public void dataFilterUpdate(FilterBean bean) {
        Query query = mResultBeanDao.queryBuilder().where(
                new WhereCondition.StringCondition("_ID IN " +
                        "(SELECT * FROM RESULT_BEAN WHERE HANDLER_ACCOUT = '吴工')")).build();
        List<ResultBean> _datas = query.list();
        mDataAdapter.notifyAdapter(_datas,false);
    }

}
