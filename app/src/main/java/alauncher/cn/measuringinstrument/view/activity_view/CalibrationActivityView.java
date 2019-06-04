package alauncher.cn.measuringinstrument.view.activity_view;


import alauncher.cn.measuringinstrument.bean.CalibrationBean;

public interface CalibrationActivityView {

    void onUIUpdate(CalibrationBean bean);

    void onDataUpdate(double[] values);
}
