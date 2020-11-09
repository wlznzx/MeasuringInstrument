package alauncher.cn.measuringinstrument.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.CodeBean;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CodeBaseInfoFragment extends Fragment {

    private Unbinder unbinder;

    @BindView(R.id.machine_tool_edt)
    public EditText machineToolEdt;

    @BindView(R.id.parts_edt)
    public EditText partEdt;

    @BindView(R.id.code_name_edt)
    public EditText codeNameEdt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.code_baseinfo_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @OnClick(R.id.save_btn)
    public void onSave(View v) {
        CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) App.getSetupBean().getCodeID());
        if (_bean == null) {
            _bean = new CodeBean(App.getSetupBean().getId(), "",
                    machineToolEdt.getText().toString().trim(), partEdt.getText().toString().trim(),false,null);
        } else {
            _bean.setMachineTool(machineToolEdt.getText().toString().trim());
            _bean.setParts(partEdt.getText().toString().trim());
            _bean.setName(codeNameEdt.getText().toString().trim());
        }
        App.getDaoSession().getCodeBeanDao().insertOrReplace(_bean);
        Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initView() {
        CodeBean _bean = App.getDaoSession().getCodeBeanDao().load((long) App.getSetupBean().getCodeID());
        if (_bean != null) {
            machineToolEdt.setText(_bean.getMachineTool());
            partEdt.setText(_bean.getParts());
            codeNameEdt.setText(_bean.getName());
        } else {

        }
    }

}
