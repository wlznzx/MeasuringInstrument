package alauncher.cn.measuringinstrument.view.activity_view;


public interface MeasuringActivityView {
    void onMeasuringDataUpdate(double[] values);

    void showUnSupportDialog();

    void setValueBtnVisible(boolean isVisible);
}
