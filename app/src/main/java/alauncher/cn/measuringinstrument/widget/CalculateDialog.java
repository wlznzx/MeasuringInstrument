package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import alauncher.cn.measuringinstrument.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalculateDialog extends Dialog {

    private Context mContext;

    @BindView(R.id.code_tv)
    public TextView codeTV;


    private String code = "";


    private boolean isOperateDown = true;
    private boolean isNumDown = false;
    private int m;
    private CodeInterface mCodeInterface;


    public CalculateDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_dialog_layout);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.ch1_btn, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_clear,
            R.id.ch2_btn, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_plus,
            R.id.ch3_btn, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_minus,
            R.id.ch4_btn, R.id.btn_0, R.id.btn_multiply, R.id.btn_divide, R.id.btn_ok,
    })
    public void onBtnClick(View v) {
        switch (v.getId()) {
            case R.id.ch1_btn:
                onNumDown("ch1");
                break;
            case R.id.ch2_btn:
                onNumDown("ch2");
                break;
            case R.id.ch3_btn:
                onNumDown("ch3");
                break;
            case R.id.ch4_btn:
                onNumDown("ch4");
                break;
            case R.id.btn_0:
                onNumDown("0");
                break;
            case R.id.btn_1:
                onNumDown("1");
                break;
            case R.id.btn_2:
                onNumDown("2");
                break;
            case R.id.btn_3:
                onNumDown("3");
                break;
            case R.id.btn_4:
                onNumDown("4");
                break;
            case R.id.btn_5:
                onNumDown("5");
                break;
            case R.id.btn_6:
                onNumDown("6");
                break;
            case R.id.btn_7:
                onNumDown("7");
                break;
            case R.id.btn_8:
                onNumDown("8");
                break;
            case R.id.btn_9:
                onNumDown("9");
                break;
            case R.id.btn_clear:
                isOperateDown = true;
                isNumDown = false;
                code = "";
                codeTV.setText(code);
                break;
            case R.id.btn_plus:
                onOperateDown("+");
                break;
            case R.id.btn_minus:
                onOperateDown("-");
                break;
            case R.id.btn_multiply:
                onOperateDown("*");
                break;
            case R.id.btn_divide:
                onOperateDown("/");
                break;
            case R.id.btn_ok:
                JEP jep = new JEP();
                try {
                    jep.addVariable("ch1", 1);
                    jep.addVariable("ch2", 2);
                    jep.addVariable("ch3", 3);
                    jep.addVariable("ch4", 4);

                    Node node = jep.parse(code);
                    Object result = jep.evaluate(node);
                    // Toast.makeText(mContext, "result = " + result, Toast.LENGTH_SHORT).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dismiss();
                mCodeInterface.onCodeSet(m, code);
                break;
            default:
                break;
        }
    }

    private void onOperateDown(String operate) {
        if (isOperateDown) return;
        isOperateDown = true;
        isNumDown = false;
        code = code + operate;
        codeTV.setText(code);
    }

    private void onNumDown(String num) {
        if (isNumDown) return;
        isNumDown = true;
        isOperateDown = false;
        code = code + num;
        codeTV.setText(code);
    }

    public void setM(int _m) {
        m = _m;
    }

    public void setCodeInterface(CodeInterface pCodeInterface) {
        mCodeInterface = pCodeInterface;
    }


    public interface CodeInterface {
        void onCodeSet(int m, String code);
    }

}
