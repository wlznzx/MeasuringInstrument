package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.GroupBean2;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GroupEditDialog extends Dialog {

    private Context mContext;

    @BindView(R.id.group_name_edt)
    public EditText groupNameEdt;

    @BindView(R.id.upper_limit_edt)
    public EditText upperLimitEdt;

    @BindView(R.id.lower_limit_edt)
    public EditText lowerLimitEdt;

    @BindView(R.id.describe_edt)
    public EditText describeEdt;

    @BindView(R.id.group_title_tv)
    public TextView groupTitleTV;

    DataUpdateInterface dataUpdateInterface;

    private GroupBean2 mGroupBean2;

    private double pID = -1;

    public GroupEditDialog(Context context, GroupBean2 pGroupBean2) {
        super(context, R.style.Dialog);
        mGroupBean2 = pGroupBean2;
        mContext = context;
    }

    public void setPID(double pid) {
        pID = pid;
    }

    public void setDataUpdateInterface(DataUpdateInterface pDataUpdateInterface) {
        dataUpdateInterface = pDataUpdateInterface;
    }

    @OnClick({R.id.no, R.id.yes})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no:
                dismiss();
                break;
            case R.id.yes:
                if (doConditionAdd()) {
                    if (dataUpdateInterface != null) {
                        dataUpdateInterface.dataUpdate();
                    }
                    dismiss();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_edit_dialog_layout);
        ButterKnife.bind(this);

        if (mGroupBean2 != null) {
            groupNameEdt.setText(mGroupBean2.getName());
            lowerLimitEdt.setText(String.valueOf(mGroupBean2.getLowerLimit()));
            upperLimitEdt.setText(String.valueOf(mGroupBean2.getUpperLimit()));
            describeEdt.setText(mGroupBean2.getDescribe());
            groupTitleTV.setText(R.string.edit_group);
        }
    }


    public boolean doConditionAdd() {
        if (mGroupBean2 == null) {
            mGroupBean2 = new GroupBean2();
            mGroupBean2.setPID(pID);
        }
        try {
            mGroupBean2.setName(groupNameEdt.getText().toString().trim());
            mGroupBean2.setDescribe(describeEdt.getText().toString());

            double upperLimit = Double.valueOf(upperLimitEdt.getText().toString().trim());
            double lowerLimit = Double.valueOf(lowerLimitEdt.getText().toString().trim());
            if (lowerLimit >= upperLimit) {
                Toast.makeText(mContext, "上限必须大于下限，请检查。", Toast.LENGTH_SHORT).show();
                return false;
            }
            mGroupBean2.setUpperLimit(upperLimit);
            mGroupBean2.setLowerLimit(lowerLimit);
            App.getDaoSession().getGroupBean2Dao().insertOrReplace(mGroupBean2);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, "输入条件有误，请检查。", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

}
