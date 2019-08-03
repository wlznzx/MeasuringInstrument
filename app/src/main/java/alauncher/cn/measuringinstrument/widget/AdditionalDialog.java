package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdditionalDialog extends Dialog {

    private Context mContext;

    @BindView(R.id.workpieceid_edt)
    public EditText workpieceidEdt;

    @BindView(R.id.eventid_edt)
    public EditText eventidEdt;

    @BindView(R.id.workpieceid_sp)
    public Spinner workpieceidSP;

    private boolean isFrist = true;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.additional_dialog_layout);
        ButterKnife.bind(this);
        isShowCB.setChecked(App.getSetupBean().getIsAutoPopUp());
        eventSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                android.util.Log.d("wlDebug", "pos = " + eventSP.getItemAtPosition(position));
                if (!isFrist) {
                    eventidEdt.setText((String) eventSP.getItemAtPosition(position));
                } else {
                    isFrist = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public interface AdditionDialogInterface {
        void onAdditionSet(AddInfoBean pBean);
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
                    _bean.setEvent(eventidEdt.getText().toString().trim());
                    _bean.setEventid(eventidEdt.getText().toString().trim());
                    mAdditionDialogInterface.onAdditionSet(_bean);
                }
                break;
            default:
                break;
        }
        dismiss();
    }

}
