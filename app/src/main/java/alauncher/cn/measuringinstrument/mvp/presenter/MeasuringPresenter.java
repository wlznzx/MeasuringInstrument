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

    void saveResult(double[] ms, AddInfoBean bean);

    String[] getMGroupValues(double[] ms);
}
