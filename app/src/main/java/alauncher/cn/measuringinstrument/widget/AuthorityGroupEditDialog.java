package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.AuthorityGroupBean;
import alauncher.cn.measuringinstrument.view.AuthorityDetailActivity;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthorityGroupEditDialog extends Dialog implements CalculateDialog.CodeInterface {

    private Context mContext;

    @BindView(R.id.authority_group_tv)
    public TextView authorityGroupTV;

    @BindView(R.id.authority_group_edt)
    public EditText authorityGroupEDT;

    @BindView(R.id.describe_edt)
    public EditText describeEDT;

    @BindView(R.id.remarks_edt)
    public EditText remarksEDT;

    @BindView(R.id.authority_set_btn)
    public Button authoritySetBtn;

    @BindView(R.id.authority_set_layout)
    public View authoritySetLayout;

    @BindView(R.id.authority_name_tips_tv)
    public TextView authorityNameTipsTV;

    AuthorityGroupBean mAuthorityGroupBean;

    DataUpdateInterface dataUpdateInterface;

    private boolean isAdd = true;

    public AuthorityGroupEditDialog(Context context, AuthorityGroupBean pAuthorityGroupBean) {
        super(context, R.style.Dialog);
        mContext = context;
        mAuthorityGroupBean = pAuthorityGroupBean;
        if (pAuthorityGroupBean != null) {
            android.util.Log.d("alauncher",mAuthorityGroupBean.toString());
            isAdd = false;
        }
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
                if (doAdd()) {
                    if (dataUpdateInterface != null) dataUpdateInterface.dataUpdate();
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
        setContentView(R.layout.authority_group_dialog_layout);
        ButterKnife.bind(this);
        if (!isAdd) {
            authorityGroupTV.setText(R.string.edit_authority_group);
            authoritySetLayout.setVisibility(View.VISIBLE);
            authorityGroupEDT.setText(mAuthorityGroupBean.getName());
            describeEDT.setText(mAuthorityGroupBean.getDescribe());
            remarksEDT.setText(mAuthorityGroupBean.getRemarks());
        }
        authoritySetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AuthorityDetailActivity.class);
                intent.putExtra("pID", mAuthorityGroupBean != null ? mAuthorityGroupBean.getId() : -1);
                getContext().startActivity(intent);
            }
        });

    }

    @Override
    public void onCodeSet(int m, String code) {
//        formulaBtn.setText(code);
    }


    public boolean doAdd() {
        if(authorityGroupEDT.getText().toString().trim().equals("")){
            authorityNameTipsTV.setVisibility(View.VISIBLE);
            return false;
        }
        if (mAuthorityGroupBean == null) mAuthorityGroupBean = new AuthorityGroupBean();
        try {
            mAuthorityGroupBean.setDescribe(describeEDT.getText().toString().trim());
            mAuthorityGroupBean.setName(authorityGroupEDT.getText().toString().trim());
            mAuthorityGroupBean.setRemarks(remarksEDT.getText().toString().trim());
            // 如果是新加数据;
            if (isAdd) {

            } else {

            }
            Long pID = App.getDaoSession().getAuthorityGroupBeanDao().insertOrReplace(mAuthorityGroupBean);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, "输入条件有误，请检查。", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void dismiss() {
        //避免闪屏 提高用户体验
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AuthorityGroupEditDialog.super.dismiss();
            }
        }, 500);

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(authorityGroupEDT.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(describeEDT.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(remarksEDT.getWindowToken(), 0);
        super.dismiss();
    }
}
