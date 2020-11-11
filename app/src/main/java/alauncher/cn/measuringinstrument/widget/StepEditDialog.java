package alauncher.cn.measuringinstrument.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.StepBean2;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.TriggerConditionBeanDao;
import alauncher.cn.measuringinstrument.view.activity_view.DataUpdateInterface;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StepEditDialog extends Dialog {

    private Context mContext;

    @BindView(R.id.show_item_sp)
    public Spinner showItemSP;

    @BindView(R.id.condition_sp)
    public Spinner conditionSP;

    public List<String> condications = new ArrayList();

    public List<String> steps = new ArrayList();

    private List<TriggerConditionBean> mTriggerConditionBeans;

    DataUpdateInterface dataUpdateInterface;

    private List<ParameterBean2> mParameterBean2s;

    private List<StepBean2> mStepBean2s;

    private boolean isAdd = true;

    private StepBean2 _bean;

    private String[] items;

    private boolean[] checked;
    // 已被测量参数列表;
    private List<String> measuredItems = new ArrayList<>();

    // 未被测量参数列表;
    private List<String> unMeasuredItems = new ArrayList<>();

    @BindView(R.id.measure_item_btn)
    public Button measureItemBtn;

    @BindView(R.id.dialog_tips)
    public TextView dialogTipsTV;


    public StepEditDialog(Context context, StepBean2 pStepBean2) {
        super(context, R.style.FDialog);
        mContext = context;
        if (pStepBean2 != null) {
            isAdd = false;
            _bean = pStepBean2;
        }
        if (_bean == null) {
            _bean = new StepBean2();
            _bean.setMeasureItems(new ArrayList<>());
        }
        mParameterBean2s = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID()), ParameterBean2Dao.Properties.Enable.eq(true))
                .list();

        for (int i = 0; i < mParameterBean2s.size(); i++) {
            steps.add(String.valueOf(i + 1));
        }

        mStepBean2s = App.getDaoSession().getStepBean2Dao().queryBuilder()
                .where(StepBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderAsc(StepBean2Dao.Properties.SequenceNumber).list();

        mTriggerConditionBeans = App.getDaoSession().getTriggerConditionBeanDao().queryBuilder()
                .where(TriggerConditionBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).list();

        initUnCheck();
    }

    public void setDataUpdateInterface(DataUpdateInterface pDataUpdateInterface) {
        dataUpdateInterface = pDataUpdateInterface;
    }

    @OnClick({R.id.no, R.id.yes, R.id.measure_item_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no:
                dismiss();
                break;
            case R.id.yes:
                if (doConditionAdd()) {
                    if (dataUpdateInterface != null) dataUpdateInterface.dataUpdate();
                    dismiss();
                }
                break;
            case R.id.measure_item_btn:

                AlertDialog dlg = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.choose_measure_item)
                        .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                checked[which] = isChecked;
                            }
                        })
                        .setPositiveButton(R.string.ok, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int index) {
                                _bean.setMeasureItems(new ArrayList<>());
                                String _str = "";
                                for (int i = 0; i < checked.length; i++) {
                                    if (checked[i]) {
                                        _str += items[i] + ".";
                                        _bean.getMeasureItems().add(unMeasuredItems.get(i));
                                    }
                                }
                                measureItemBtn.setText(_str);
                            }
                        })
                        .create();
                dlg.show();
                break;
            default:
                break;
        }
    }

    private void initUnCheck() {
        for (StepBean2 _bean2 : mStepBean2s) {
            if (!isAdd && _bean2.getId().equals(_bean.getId())) continue;
            if (_bean2.getMeasureItems() != null) measuredItems.addAll(_bean2.getMeasureItems());
        }

        for (int i = 0; i < mParameterBean2s.size(); i++) {
            if (!measuredItems.contains(String.valueOf(mParameterBean2s.get(i).getSequenceNumber()))) {
                unMeasuredItems.add(String.valueOf(mParameterBean2s.get(i).getSequenceNumber()));
            }
        }
        items = new String[unMeasuredItems.size()];

        for (int i = 0; i < unMeasuredItems.size(); i++) {
            items[i] = "M" + (Integer.parseInt(unMeasuredItems.get(i)) + 1);
        }
        checked = new boolean[items.length];
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_dialog_layout);
        ButterKnife.bind(this);
        // if (!isAdd) parameterDialogTitleTV.setText(R.string.edit_step);

        // 初始化
        ArrayAdapter<String> stepAdapter = new ArrayAdapter<>(getContext(), R.layout.step_spinner_item, steps);
        showItemSP.setAdapter(stepAdapter);
        showItemSP.setSelection(isAdd ? mStepBean2s.size() : _bean.getSequenceNumber());

        // 初始化条件;
        int conditionIndex = 0;
        condications.add(getContext().getResources().getString(R.string.press_save));
        for (int i = 0; i < mTriggerConditionBeans.size(); i++) {
            condications.add(mTriggerConditionBeans.get(i).getConditionName());
            if (!isAdd && _bean.getConditionID() == mTriggerConditionBeans.get(i).getId())
                conditionIndex = i + 1;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.step_spinner_item, condications);
        conditionSP.setAdapter(adapter);
        conditionSP.setSelection(conditionIndex);

        if (!isAdd) {
            String _str = "";
            for (String item : _bean.getMeasureItems()) {
                _str += "M" + (Integer.parseInt(item) + 1) + ".";
            }
            measureItemBtn.setText(_str);
        }
    }

    public boolean doConditionAdd() {
        if (_bean.getMeasureItems() != null && _bean.getMeasureItems().size() == 0) {
            showTips(getContext().getResources().getString(R.string.without_measured_str));
            return false;
        }
        try {
            int sequenceNumber = (int) showItemSP.getSelectedItemId();
            // 如果是新加数据;
            if (isAdd) {
                _bean.setCodeID(App.getSetupBean().getCodeID());
                if (sequenceNumber >= mStepBean2s.size()) {
                    // 将序号设置为最后的序号;
                    _bean.setSequenceNumber(mStepBean2s.size());
                } else {
                    // 如果插入新添加的数据;
                    for (StepBean2 _bean2 : mStepBean2s) {
                        if (_bean2.getSequenceNumber() >= sequenceNumber) {
                            _bean2.setSequenceNumber(_bean2.getSequenceNumber() + 1);
                            App.getDaoSession().getStepBean2Dao().insertOrReplace(_bean2);
                        }
                    }
                    _bean.setSequenceNumber(sequenceNumber);
                }
            } else {
                for (StepBean2 _bean2 : mStepBean2s) {
                    if (_bean2.getSequenceNumber() > _bean.getSequenceNumber()) {
                        _bean2.setSequenceNumber(_bean2.getSequenceNumber() - 1);
                        App.getDaoSession().getStepBean2Dao().insertOrReplace(_bean2);
                    }
                }
                if (sequenceNumber >= mStepBean2s.size()) {
                    // 将序号设置为最后的序号;
                    _bean.setSequenceNumber(mStepBean2s.size() - 1);
                } else {
                    // 如果插入新添加的数据;
                    for (StepBean2 _bean2 : mStepBean2s) {
                        if (_bean2.getSequenceNumber() >= sequenceNumber) {
                            _bean2.setSequenceNumber(_bean2.getSequenceNumber() + 1);
                            App.getDaoSession().getStepBean2Dao().insertOrReplace(_bean2);
                        }
                    }
                    _bean.setSequenceNumber(sequenceNumber);
                }
            }
            int index = conditionSP.getSelectedItemPosition();
            if (index > 0) {
                _bean.setConditionID(mTriggerConditionBeans.get(index - 1).getId());
            } else {
                _bean.setConditionID(-1);
            }
            Long pID = App.getDaoSession().getStepBean2Dao().insertOrReplace(_bean);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(mContext, "输入条件有误，请检查。", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return false;
    }

    private void showTips(String msg) {
        dialogTipsTV.setVisibility(View.VISIBLE);
        dialogTipsTV.setText(msg);
    }

    public void dismiss() {
        //避免闪屏 提高用户体验
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StepEditDialog.super.dismiss();
            }
        }, 500);

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(showItemSP.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(conditionSP.getWindowToken(), 0);

    }
}
