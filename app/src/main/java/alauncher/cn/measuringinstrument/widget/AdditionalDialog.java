package alauncher.cn.measuringinstrument.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdditionalDialog extends AlertDialog {

    private Context mContext;

    @BindView(R.id.workpieceid_edt)
    public EditText workpieceidEdt;

    @BindView(R.id.eventid_edt)
    public EditText eventidEdt;

    @BindView(R.id.workpieceid_sp)
    public Spinner workpieceidSP;

    @BindView(R.id.evnet_sp)
    public Spinner eventSP;

    @BindView(R.id.additional_show_ch)
    public CheckBox isShowCB;


    AdditionDialogInterface mAdditionDialogInterface;

    public AdditionalDialog(Context context) {
        super(context);
        mContext = context;
    }

    public AdditionalDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    public void setDialogInterface(AdditionDialogInterface pAdditionDialogInterface) {
        mAdditionDialogInterface = pAdditionDialogInterface;
    }

    @OnClick({R.id.no, R.id.yes})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no:
                break;
            case R.id.yes:
                if (mAdditionDialogInterface != null) {
                    AddInfoBean _bean = new AddInfoBean();
                    _bean.setAutoShow(isShowCB.isChecked());
                    _bean.setWorkid(workpieceidEdt.getText().toString());
                    _bean.setWork(mContext.getResources().getStringArray(R.array.workids)[(int) workpieceidSP.getSelectedItemId()]);
                    _bean.setEvent(mContext.getResources().getStringArray(R.array.eventid_items)[(int) eventSP.getSelectedItemId()]);
                    _bean.setEventid(eventidEdt.getText().toString());
                    mAdditionDialogInterface.onAdditionSet(_bean);
                }
                break;
            default:
                break;
        }
        dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_dialog_layout);
        ButterKnife.bind(this);
    }

    public interface AdditionDialogInterface {
        void onAdditionSet(AddInfoBean pBean);
    }

}
