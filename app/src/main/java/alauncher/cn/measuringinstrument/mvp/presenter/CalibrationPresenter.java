package alauncher.cn.measuringinstrument.mvp.presenter;

import alauncher.cn.measuringinstrument.bean.CalibrationBean;

/**
 * 日期：2019/6/3 0003 21:11
 * 包名：alauncher.cn.measuringinstrument.mvp.presenter.impl
 * 作者： wlznzx
 * 描述：
 */
public interface CalibrationPresenter {


    void startValueing();

    void stopValueing();

    double onePieceCalibration(double y1, double k, double x, double y);

    double calculationK(double x1, double y1, double x2, double y2);

    double calculationC(double y, double k, double x);

    double calculationValue(double k, double x, double c);

    void updateUI();

    void saveCalibration(CalibrationBean bean);
}
