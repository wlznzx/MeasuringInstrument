package alauncher.cn.measuringinstrument;

import android.widget.Toast;

import org.junit.Test;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alauncher.cn.measuringinstrument.bean.ProcessBean;
import alauncher.cn.measuringinstrument.utils.Avg;
import alauncher.cn.measuringinstrument.utils.Dif;
import alauncher.cn.measuringinstrument.utils.Max;
import alauncher.cn.measuringinstrument.utils.Min;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        JEP jep = new JEP();
        // jep.addStandardFunctions();
        jep.addFunction("Max", new Max());
        jep.addFunction("Min", new Min());
        jep.addFunction("Avg", new Avg());
        jep.addFunction("Dif", new Dif());
        String code = "Max(LMax(1,2),Min(3,4))";

        String str = "((GLDWH = '14000' ) '14000')";

        //       Pattern pattern = Pattern.compile("([\'\"])(.*?)\\1");
        Pattern pattern = Pattern.compile("Max*1");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            String collegeId = matcher.group(2);
            System.out.println("collegeld = " + collegeId);//14000
        }

        /*
        try {
            Node node = jep.parse(code);
            System.out.println("end: " + jep.evaluate(node));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        */
        String str1 = "123";
        // 字符串替换;
        String str2 = "2342803423jfasdf0132041a0a1231";
        Pattern p = Pattern.compile("[0-9]"); // 先编译
        Matcher matcher1 = p.matcher(str2);
        // 验证字符串是电话号码;
        // 1.11位; 2.必须是1开头; 3.第二个位置必须是3,5,7,8; 4.剩下的必须是0~9;
        String regx = "^[1][3|5|7|8][0-9]";
        p = Pattern.compile(regx);


    }


    @Test
    public void test05() throws ParseException {
        List<ProcessBean> process = new ArrayList<>();
        String code = "LMax(Max(ch1,ch2))";
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

        // 判断括号内的表达式合乎规则;
        JEP jep = new JEP();
        jep.addFunction("Max", new Max());
        jep.addFunction("Min", new Min());
        jep.addFunction("Avg", new Avg());
        jep.addFunction("Dif", new Dif());
        jep.addVariable("ch1", 1);
        jep.addVariable("ch2", 2);
        jep.addVariable("ch3", 3);
        jep.addVariable("ch4", 4);

        for (ProcessBean _process : process) {
            // 添加过程值变量;
            System.out.println(_process.toString());
            jep.addVariable(_process.getReplaceName(), 1);
            Node node = jep.parse(_process.getExpression());
            Object result = jep.evaluate(node);
        }

        Node node = jep.parse(reCode);
        Object result = jep.evaluate(node);

        System.out.println("reCode = " + result.toString());
    }

    @Test
    public void DateUtil(){
        //查询开始日期
        String beginTime="";
        //查询结束日期
        String endTime="";
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String timeStr = "07:45:00";
        if(DatesUtil.isBiggerNow(timeStr)){
            System.out.println("this 1 ?");
            beginTime = longSdf.format(DatesUtil.DateAddTimeStr(DatesUtil.getDayBegin(),timeStr,true));
            endTime = longSdf.format(DatesUtil.DateAddTimeStr(DatesUtil.getBeginDayOfTomorrow(),timeStr,true));
        }else{
            System.out.println("this 2 ?");
            beginTime = longSdf.format(DatesUtil.DateAddTimeStr(DatesUtil.getBeginDayOfYesterday(),timeStr,true));
            endTime = longSdf.format(DatesUtil.DateAddTimeStr(DatesUtil.getDayBegin(),timeStr,true));
        }

        System.out.println("beginTime = " + beginTime);
        System.out.println("endTime = " + endTime);
    }
}