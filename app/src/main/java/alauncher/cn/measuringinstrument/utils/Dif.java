package alauncher.cn.measuringinstrument.utils;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import java.math.BigDecimal;
import java.util.Stack;

public class Dif extends PostfixMathCommand {
    public Dif() {
        this.numberOfParameters = -1;
    }

    @Override
    public void run(Stack var1) throws ParseException {
        this.checkStack(var1);
        double in[] = new double[var1.size()];
        for (int var4 = 0; var4 < this.curNumberOfParameters; var4++) {
            in[var4] = ((Number) var1.get(var4)).doubleValue();
        }
        var1.clear();
        var1.push(range(in));
    }

    public double range(double[] in) {
        if (in == null) {
            throw new java.lang.NumberFormatException();
        }
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < in.length; i++) {
            max = Math.max(max, in[i]);
            min = Math.min(min, in[i]);
        }
        return subtract(max, min);
    }

    public double subtract(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

}
