package alauncher.cn.measuringinstrument.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alauncher.cn.measuringinstrument.R;
import alauncher.cn.measuringinstrument.bean.ProcessBean;
import alauncher.cn.measuringinstrument.utils.Avg;
import alauncher.cn.measuringinstrument.utils.Dif;
import alauncher.cn.measuringinstrument.utils.Max;
import alauncher.cn.measuringinstrument.utils.Min;
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
        codeTV.setText(code);
    }

    public void setCode(String pCode) {
        code = pCode;
    }

    @OnClick({R.id.ch1_btn, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_clear,
            R.id.ch2_btn, R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_plus,
            R.id.ch3_btn, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_minus,
            R.id.ch4_btn, R.id.btn_0, R.id.btn_multiply, R.id.btn_divide, R.id.btn_ok,
            R.id.max_btn, R.id.min_btn, R.id.avg_btn, R.id.dif_btn, R.id.comma_btn,
            R.id.left_brackets_btn, R.id.right_brackets_btn, R.id.back_space_btn, R.id.radix_point_btn,
            R.id.lmax_btn, R.id.lmin_btn, R.id.lavg_btn, R.id.ldif_btn
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
            case R.id.max_btn:
                onFunDown("Max");
                break;
            case R.id.min_btn:
                onFunDown("Min");
                break;
            case R.id.avg_btn:
                onFunDown("Avg");
                break;
            case R.id.dif_btn:
                onFunDown("Dif");
                break;
            case R.id.lmax_btn:
                onFunDown("LMax");
                break;
            case R.id.lmin_btn:
                onFunDown("LMin");
                break;
            case R.id.lavg_btn:
                onFunDown("LAvg");
                break;
            case R.id.ldif_btn:
                onFunDown("LDif");
                break;
            case R.id.comma_btn:
                onOperateDown(",");
                break;
            case R.id.left_brackets_btn:
                onOperateDown("(");
                break;
            case R.id.right_brackets_btn:
                onOperateDown(")");
                break;
            case R.id.back_space_btn:
                try {
                    code = code.substring(0, code.length() - 1);
                    codeTV.setText(code);
                } catch (Exception e) {

                }
                break;
            case R.id.radix_point_btn:
                onOperateDown(".");
                break;
            case R.id.btn_ok:
                // 先把过程值提取出来;
                List<ProcessBean> process = new ArrayList<>();
                String reCode = code;
                // G开头，G结尾;
                String _regx = "L.*?\\)";
                Pattern p = Pattern.compile(_regx);
                Matcher matcher = p.matcher(code);
                while (matcher.find()) {
                    ProcessBean _process = new ProcessBean("x" + process.size(), code.substring(matcher.start() + 5, matcher.end() - 1), code.substring(matcher.start(), matcher.start() + 4));
                    process.add(_process);
                    reCode = reCode.replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }
                JEP jep = new JEP();
                jep.addFunction("Max", new Max());
                jep.addFunction("Min", new Min());
                jep.addFunction("Avg", new Avg());
                jep.addFunction("Dif", new Dif());
                jep.addVariable("ch1", 1);
                jep.addVariable("ch2", 2);
                jep.addVariable("ch3", 3);
                jep.addVariable("ch4", 4);
                try {
                    for (ProcessBean _process : process) {
                        // 添加过程值变量;
                        jep.addVariable(_process.getReplaceName(), 1);
                        Node node = jep.parse(_process.getExpression());
                        Object result = jep.evaluate(node);
                        System.out.println(_process.toString());
                    }
                    Node node = jep.parse(reCode);
                    Object result = jep.evaluate(node);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "公式有误，请检查.", Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                mCodeInterface.onCodeSet(m, code);
                break;
            default:
                break;
        }
    }

    private void onFunDown(String fun) {
//        code = fun + "(" + code + ")";
        code = code + fun + "(";
        codeTV.setText(code);
    }

    private void onOperateDown(String operate) {
//        if (isOperateDown) return;
        isOperateDown = true;
        isNumDown = false;
        code = code + operate;
        codeTV.setText(code);
    }

    private void onNumDown(String num) {
//        if (isNumDown) return;
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
