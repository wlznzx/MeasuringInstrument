package alauncher.cn.measuringinstrument;

import org.junit.Test;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void test05() {
        String code = "Max(LMax(ch1),LMin(3,4))";
        // G开头，G结尾;
        String _regx = "LMax.*?\\)";
        Pattern p = Pattern.compile(_regx);
        Matcher matcher = p.matcher(code);
        while (matcher.find()) {
            System.out.println(matcher.start());//14000
            System.out.println(matcher.end());//14000
            System.out.println(matcher.group());//14000
        }
    }

    class Process {
        // 过程值替代符号;
        String replaceName;
        // 过程运算表达式;
        String expression;
        // 过程运算表达式类型，LMax、LMin、LDif、LAvg；
        String expressionType;
    }
}