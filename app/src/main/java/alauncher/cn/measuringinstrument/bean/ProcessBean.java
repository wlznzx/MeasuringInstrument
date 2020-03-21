package alauncher.cn.measuringinstrument.bean;

import java.util.List;

public class ProcessBean {

    public String getReplaceName() {
        return replaceName;
    }


    public String getExpressionType() {
        return expressionType;
    }

    // 过程值替代符号;
    String replaceName;
    // 过程运算表达式;
    List<String> expression;
    // 过程运算表达式类型，LMax、LMin、LDif、LAvg；
    String expressionType;

    String fullCode;

    public void setReplaceName(String replaceName) {
        this.replaceName = replaceName;
    }


    public void setExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    public void setVar1(double var1) {
        this.var1 = var1;
    }

    public void setVar2(double var2) {
        this.var2 = var2;
    }

    // 过程中间值;
    double var1;
    // 过程中间值，第二个;
    double var2;

    public ProcessBean(String replaceName, List<String> expression, String expressionType, String fullCode) {
        this.replaceName = replaceName;
        this.expression = expression;
        this.expressionType = expressionType;
        this.fullCode = fullCode;
    }

    public String getFullCode() {
        return fullCode;
    }

    public void setFullCode(String fullCode) {
        this.fullCode = fullCode;
    }

    public List<String> getExpression() {
        return expression;
    }

    public void setExpression(List<String> expression) {
        this.expression = expression;
    }

    public double getVar1() {
        return var1;
    }

    public double getVar2() {
        return var2;
    }

    @Override
    public String toString() {
        return "ProcessBean{" +
                "replaceName='" + replaceName + '\'' +
                ", expression=" + expression +
                ", expressionType='" + expressionType + '\'' +
                ", fullCode='" + fullCode + '\'' +
                '}';
    }
}
