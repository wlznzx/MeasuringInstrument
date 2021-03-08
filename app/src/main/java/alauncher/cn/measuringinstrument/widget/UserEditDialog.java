package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.AuthorityGroupBean;
import alauncher.cn.measuringinstrument.bean.User;
import alauncher.cn.measuringinstrument.database.greenDao.db.AuthorityGroupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.UserDao;
import alauncher.cn.measuringinstrument.utils.UserReg;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserEditDialog extends Dialog {

    private Context mContext;

    @BindView(R.id.accout_edt)
    public EditText accoutEdt;

    @BindView(R.id.fullname_edt)
    public EditText fullNameEdt;

    @BindView(R.id.password_edt)
    public EditText passwordEdt;

    @BindView(R.id.repassword_edt)
    public EditText rePasswordEdt;

    @BindView(R.id.status_sp)
    public Spinner statusSP;

    @BindView(R.id.limit_sp)
    public Spinner limitSP;

    @BindView(R.id.authority_group_sp)
    public Spinner authorityGroupSP;

    @BindView(R.id.workpiece_edt)
    public EditText workpieceEdt;

    @BindView(R.id.email_edt)
    public EditText emailEdt;

    @BindView(R.id.user_dialog_title_tv)
    public TextView dialogTitleTV;

    @BindView(R.id.authority_layout)
    public View authorityLayout;

    @BindView(R.id.status_layout)
    public View statusLayout;


    AdditionDialogInterface mAdditionDialogInterface;

    UserDao mUserDao;

    UIInterface mUIInterface;

    private User mUser, currentUser;

    public Map<Integer, Integer> spMap = new HashMap<>();
    public Map<Integer, Integer> toMap = new HashMap<>();

    //
    List<AuthorityGroupBean> mAuthorityGroupBeans;

    public UserEditDialog(Context context) {
        super(context, R.style.Dialog);
        mContext = context;
        mUserDao = App.getDaoSession().getUserDao();
        spMap.put(0, 0);
        spMap.put(1, 4);

        toMap.put(0, 0);
        toMap.put(4, 1);
    }

    public UserEditDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        mUserDao = App.getDaoSession().getUserDao();
    }

    public void setmUIInterface(UIInterface uiInterface) {
        mUIInterface = uiInterface;
    }

    public void setDialogInterface(AdditionDialogInterface pAdditionDialogInterface) {
        mAdditionDialogInterface = pAdditionDialogInterface;
    }


    public void goEditMode(User user) {
        dialogTitleTV.setText(R.string.edit_user_title);
        mUser = user;
        accoutEdt.setText(mUser.getAccout());
        accoutEdt.setEnabled(false);
        fullNameEdt.setText(mUser.getName());
        passwordEdt.setText(mUser.getPassword());
        rePasswordEdt.setText(mUser.getPassword());
        statusSP.setSelection(mUser.getStatus());
        for (int i = 0; i < mAuthorityGroupBeans.size(); i++) {
            if (mUser.getUseAuthorityGroupID() == mAuthorityGroupBeans.get(i).getId()) {
                authorityGroupSP.setSelection((i + 1));
                break;
            }
        }
        if (mUser.getAccout().equals(App.handlerAccout)) {
            authorityLayout.setVisibility(View.GONE);
            statusLayout.setVisibility(View.GONE);
        }
        // limitSP.setSelection(mUser.getLimit() - currentUser.getLimit());
        // limitSP.setSelection(mUser.getLimit());
        emailEdt.setText(mUser.getEmail());
        workpieceEdt.setText(mUser.getId());
    }

    @OnClick({R.id.no, R.id.yes})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no:
                dismiss();
                break;
            case R.id.yes:
                doAddUser();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adduser_dialog_layout);
        ButterKnife.bind(this);
        currentUser = App.getDaoSession().getUserDao().load(App.handlerAccout);
        List<String> aGroupList = new ArrayList<>();

        mAuthorityGroupBeans = App.getDaoSession().getAuthorityGroupBeanDao().queryBuilder()
                .where(AuthorityGroupBeanDao.Properties.Limit.gt(App.getCurrentAuthorityGroupBean().getLimit()),
                        AuthorityGroupBeanDao.Properties.Limit.lt(11)).list();
        aGroupList.add("默认组");
        for (AuthorityGroupBean _bean : mAuthorityGroupBeans) {
            aGroupList.add(_bean.getName());
        }
        ArrayAdapter<String> aGroupAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                aGroupList);
        authorityGroupSP.setAdapter(aGroupAdapter);
        //
        List<String> limitList = new ArrayList<>();
        for (int i = currentUser.getLimit(); i < 11; i++) {
            limitList.add(String.valueOf(i));
        }
        ArrayAdapter<String> limitAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item,
                limitList);
        limitSP.setAdapter(limitAdapter);
    }

    public interface AdditionDialogInterface {
        void onAdditionSet(AddInfoBean pBean);
    }


    public void doAddUser() {
        String accoutStr = accoutEdt.getText().toString().trim();
        if (accoutStr == null || accoutStr.equals("")) {
            Toast.makeText(mContext, "账号不能为空.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!UserReg.validateUserName(accoutStr)) {
            Toast.makeText(mContext, "账号格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        String fullName = fullNameEdt.getText().toString().trim();
        if (fullName == null || fullName.equals("")) {
            Toast.makeText(mContext, "名称不能为空.", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordStr = passwordEdt.getText().toString().trim();
        if (passwordStr == null || passwordStr.equals("")) {
            Toast.makeText(mContext, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String repasswordStr = rePasswordEdt.getText().toString().trim();
        if (!repasswordStr.equals(passwordStr)) {
            Toast.makeText(mContext, "两次输入密码必须一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUser == null) {
            if (mUserDao.load(accoutStr) != null) {
                Toast.makeText(mContext, "账号已经注册了.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (mUser == null) {
            mUser = new User();
        }
        mUser.setAccout(accoutStr);
        mUser.setName(fullName);
        mUser.setPassword(passwordStr);
        mUser.setStatus((int) statusSP.getSelectedItemId());
        mUser.setEmail(emailEdt.getText().toString().trim());
//        _user.setId(workpieceEdt.getText().toString().trim());
        int _id = (int) authorityGroupSP.getSelectedItemId();
        android.util.Log.d("wlDebug", "_id = " + _id);
        if (authorityLayout.isShown()) {
            if (_id == 0) {
                mUser.setUseAuthorityGroupID(4);
            } else {
                mUser.setUseAuthorityGroupID(mAuthorityGroupBeans.get((_id - 1)).getId());
            }
        }
        mUser.setLimit(Integer.parseInt(limitSP.getSelectedItem().toString()));
        android.util.Log.d("wlDebug", mUser.toString());
        dismiss();
//        if (mUser != null) {
//            mUserDao.update(_user);
//        } else {
//            mUserDao.insert(_user);
//        }
        mUserDao.insertOrReplace(mUser);
        if (mUIInterface != null) {
            mUIInterface.upDateUserUI();
        }
    }

    public interface UIInterface {
        void upDateUserUI();
    }

    @Override
    public void dismiss() {
        //避免闪屏 提高用户体验
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UserEditDialog.super.dismiss();
            }
        }, 500);

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(accoutEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(fullNameEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(rePasswordEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(statusSP.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(authorityGroupSP.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(workpieceEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(emailEdt.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(dialogTitleTV.getWindowToken(), 0);
    }
}
