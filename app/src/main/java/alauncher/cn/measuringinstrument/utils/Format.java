package alauncher.cn.measuringinstrument.utils;

import java.math.BigDecimal;

/**
 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精
 * 确的浮点数运算，包括加减乘除和四舍五入。
 */
public class Format {

    //默认除法运算精度
    public static double m1(double f, int scale) {
        BigDecimal bg = new BigDecimal(f);
        double f1 = bg.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

};
