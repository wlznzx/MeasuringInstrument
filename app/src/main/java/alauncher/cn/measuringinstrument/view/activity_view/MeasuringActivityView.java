package alauncher.cn.measuringinstrument.view.activity_view;


public interface MeasuringActivityView {
    void onMeasuringDataUpdate(double[] values);

    void showUnSupportDialog(int index);

    void setValueBtnVisible(boolean isVisible);

    void updateSaveBtnMsg();

    void toDoSave(boolean isManual);
}
