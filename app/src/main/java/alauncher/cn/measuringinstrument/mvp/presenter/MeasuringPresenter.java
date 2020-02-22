package alauncher.cn.measuringinstrument.mvp.presenter;

import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;

public interface MeasuringPresenter {

    public static int NORMAL_NODE = 1;

    public static int IN_PROCESS_VALUE_MODE = 2;

    public static int IN_PROCESS_VALUE_BEEN_TAKEN_MODE = 3;

    /**
     * 開啓數據獲取計算.
     */
    void startMeasuring();

    void stopMeasuring();

    ParameterBean getParameterBean();

    String saveResult(double[] ms, AddInfoBean bean, boolean isManual);

    String[] getMGroupValues(double[] ms);

    int getStep();

    void startGetProcessValue();

    void stopGetProcessValue();

    boolean getIsStartProcessValue();

    int getMeasureState();

    boolean isCurStepHaveProcess();

    boolean isSingleStep();

    String getMResults(double[] ms);

    String[] getResults(double[] ms);

    boolean[] getGeted();
}
