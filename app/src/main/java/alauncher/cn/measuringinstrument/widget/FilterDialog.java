package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.FilterBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
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


    public UserDao mUserDao;
    private User mUser;

    private boolean isHandlerSpinnerFirst = true;

    private boolean isResultSpinnerFirst = true;

    private FilterInterface mFilterInterface;

    public FilterDialog(Context context, FilterInterface pFilterInterface) {
        super(context);
        mContext = context;
        mUserDao = App.getDaoSession().getUserDao();
        mFilterInterface = pFilterInterface;
    }

    public FilterDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        mUserDao = App.getDaoSession().getUserDao();
    }


    @OnClick({R.id.no, R.id.yes})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no:
                dismiss();
                break;
            case R.id.yes:
                mFilterInterface.dataFilterUpdate(view2Bean());
                dismiss();
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
        return bean;
    }

}
