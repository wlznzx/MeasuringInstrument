package alauncher.cn.measuringinstrument.utils;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;
import org.nfunk.jep.type.Complex;

import java.util.Stack;

public class Avg extends PostfixMathCommand {
    public Avg() {
        this.numberOfParameters = -1;
    }

    @Override
    public void run(Stack var1) throws ParseException {
        this.checkStack(var1);

        System.out.println("size = " + var1.size());
        double total = 0;
        for (int var4 = 0; var4 < this.curNumberOfParameters; var4++) {
            total += ((Number) var1.get(var4)).doubleValue();
        }
        var1.clear();
        var1.push(total/this.curNumberOfParameters);
    }
}
