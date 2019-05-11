package alauncher.cn.measuringinstrument.mvp.presenter.impl;

import alauncher.cn.measuringinstrument.mvp.presenter.MeasuringPresenter;
import alauncher.cn.measuringinstrument.view.activity_view.MeasuringActivityView;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;

/**
 * 日期：2019/5/9 0009 22:09
 * 包名：alauncher.cn.measuringinstrument.mvp.presenter.impl
 * 作者： wlznzx
 * 描述：
 */
public class MeasuringPresenterImpl implements MeasuringPresenter {

    private String sPort = "/dev/ttyMT1";
    private int iBaudRate = 9600;

    MeasuringActivityView mView;

    private SerialHelper serialHelper;

    public MeasuringPresenterImpl(MeasuringActivityView view) {
        mView = view;
    }

    @Override
    public void startMeasuing() {
        serialHelper = new SerialHelper(sPort, iBaudRate) {
            @Override
            protected void onDataReceived(ComBean paramComBean) {

            }
        };
    }

    @Override
    public void stopMeasuing() {
        if (serialHelper != null && serialHelper.isOpen()) {
            serialHelper.close();
        }
    }
}
