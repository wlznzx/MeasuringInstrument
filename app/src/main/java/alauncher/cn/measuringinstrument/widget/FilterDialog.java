package alauncher.cn.measuringinstrument.widget;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.FilterBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import alauncher.cn.measuringinstrument.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FilterDialog extends Dialog {

    private Context mContext;

    @BindView(R.id.filter_handler_sp)
    public Spinner filterHandlerSP;

    @BindView(R.id.workpiece_id_filter_edt)
    public EditText workpieceIDEdt;

    @BindView(R.id.event_filter_edt)
    public EditText eventFilterEdt;

    @BindView(R.id.result_filter_sp)
    public Spinner resultFilterSP;


    @BindView(R.id.start_time_btn)
    public Button startTimeBtn;

    @BindView(R.id.stop_time_btn)
    public Button stopTimeBtn;

    public UserDao mUserDao;
    private User mUser;

    private long startTime;

    private long stopTimeStamp;

    private boolean isHandlerSpinnerFirst = true;

    private boolean isResultSpinnerFirst = true;

    private FilterInterface mFilterInterface;

    public FilterDialog(Context context, FilterInterface pFilterInterface) {
        super(context, R.style.Dialog);
        mContext = context;
        mUserDao = App.getDaoSession().getUserDao();
        mFilterInterface = pFilterInterface;
    }

    public FilterDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        mUserDao = App.getDaoSession().getUserDao();
    }


    @OnClick({R.id.no, R.id.yes, R.id.start_time_btn, R.id.stop_time_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no:
                dismiss();
                break;
            case R.id.yes:
                mFilterInterface.dataFilterUpdate(view2Bean());
                dismiss();
                break;
            case R.id.start_time_btn:
                Calendar now = Calendar.getInstance();
                new android.app.DatePickerDialog(
                        mContext,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, month);
                                cal.set(Calendar.HOUR_OF_DAY, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.MILLISECOND, 0);
                                startTime = cal.getTimeInMillis();
                                startTimeBtn.setText(DateUtils.getDate(cal.getTimeInMillis()));
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;
            case R.id.stop_time_btn:
                Calendar _now = Calendar.getInstance();
                new android.app.DatePickerDialog(
                        mContext,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, month);
                                cal.set(Calendar.HOUR_OF_DAY, 23);
                                cal.set(Calendar.SECOND, 59);
                                cal.set(Calendar.MINUTE, 59);
                                stopTimeStamp = cal.getTimeInMillis();
                                stopTimeBtn.setText(DateUtils.getDate(stopTimeStamp));
                            }
                        },
                        _now.get(Calendar.YEAR),
                        _now.get(Calendar.MONTH),
                        _now.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_dialog_layout);
        ButterKnife.bind(this);

        List<User> users = mUserDao.loadAll();
        String[] items = new String[users.size() + 1];
        items[0] = "";
        for (int i = 1; i < users.size() + 1; i++) {
            items[i] = users.get(i - 1).accout;
        }
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, items);
        filterHandlerSP.setAdapter(_Adapter);
    }

    public interface FilterInterface {
        void dataFilterUpdate(FilterBean bean);
    }

    private FilterBean view2Bean() {
        FilterBean bean = new FilterBean();
        bean.setHandler((String) filterHandlerSP.getSelectedItem());
        bean.setResult((String) resultFilterSP.getSelectedItem());
        bean.setWorkid((String) workpieceIDEdt.getText().toString().trim());
        bean.setEvent((String) eventFilterEdt.getText().toString().trim());
        bean.setStartTime(startTime);
        bean.setEndTime(stopTimeStamp);
        return bean;
    }

    public void dismiss() {
        //避免闪屏 提高用户体验
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FilterDialog.super.dismiss();
            }
        }, 500);

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(filterHandlerSP.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(workpieceIDEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(eventFilterEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(resultFilterSP.getWindowToken(), 0);
    }
}
