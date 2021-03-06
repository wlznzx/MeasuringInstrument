package alauncher.cn.measuringinstrument.mvp.presenter;

import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;

public interface MeasuringPresenter {
    /**
     * 開啓數據獲取計算.
     */
    void startMeasuing();

    void stopMeasuing();

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
}
