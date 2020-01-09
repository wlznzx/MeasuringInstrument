package alauncher.cn.measuringinstrument.bean;

public class ProcessBean {

    public ProcessBean(String replaceName, String expression, String expressionType) {
        this.replaceName = replaceName;
        this.expression = expression;
        this.expressionType = expressionType;
    }

    public String getReplaceName() {
        return replaceName;
    }

    public String getExpression() {
        return expression;
    }

    public String getExpressionType() {
        return expressionType;
    }

    // 过程值替代符号;
    String replaceName;
    // 过程运算表达式;
    String expression;
    // 过程运算表达式类型，LMax、LMin、LDif、LAvg；
    String expressionType;

    // 过程中间值;
    double var1;
    // 过程中间值，第二个;
    double var2;

    @Override
    public String toString() {
        return "Process{" +
                "replaceName='" + replaceName + '\'' +
                ", expression='" + expression + '\'' +
                ", expressionType='" + expressionType + '\'' +
                '}';
    }
}
