package alauncher.cn.measuringinstrument.mvp.presenter.impl;

import alauncher.cn.measuringinstrument.mvp.presenter.CalibrationPresenter;
import alauncher.cn.measuringinstrument.view.activity_view.CalibrationActivityView;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.utils.ByteUtil;

/**
 * 日期：2019/6/3 0003 21:35
 * 包名：alauncher.cn.measuringinstrument.mvp.presenter.impl
 * 作者： wlznzx
 * 描述：
 */
public class CalibrationPresenterImpl implements CalibrationPresenter {

    private String sPort = "/dev/ttyMT1";
    private int iBaudRate = 115200;
    private SerialHelper serialHelper;
    private boolean isCommandStart = false;
    private int command_index = 0;
    private byte[] command = new byte[12];
    private byte[] _chValue = new byte[2];


    CalibrationActivityView mView;

    public CalibrationPresenterImpl(CalibrationActivityView view) {
        mView = view;
//        mParameterBean = App.getDaoSession().getParameterBeanDao().load((long) 1);
//        android.util.Log.d("wlDebug", mParameterBean.toString());
    }

    @Override
    public void startValueing() {
        if (serialHelper == null) {
            serialHelper = new SerialHelper(sPort, iBaudRate) {
                @Override
                protected void onDataReceived(ComBean paramComBean) {
                    for (byte _byte : paramComBean.bRec) {
                        if (_byte == 0x53) {
                            isCommandStart = true;
                            command_index = 0;
                        }
                        if (isCommandStart) {
                            command[command_index] = _byte;
                            command_index++;
                        }
                        if (_byte == 0x54) {
                            isCommandStart = false;
                            String _value = ByteUtil.ByteArrToHex(command);
                            android.util.Log.d("wlDebug", "_value = " + _value);
                            _chValue[0] = command[2];
                            _chValue[1] = command[3];
                            Double ch1 = Double.parseDouble(ByteUtil.ByteArrToHex(_chValue));
                            android.util.Log.d("wlDebug", "ch1 = " + ch1);
                            _chValue[0] = command[4];
                            _chValue[1] = command[5];
                            Double ch2 = Double.parseDouble(ByteUtil.ByteArrToHex(_chValue));
                            android.util.Log.d("wlDebug", "ch2 = " + ch2);
                            _chValue[0] = command[6];
                            _chValue[1] = command[7];
                            Double ch3 = Double.parseDouble(ByteUtil.ByteArrToHex(_chValue));
                            android.util.Log.d("wlDebug", "ch3 = " + ch3);
                            _chValue[0] = command[6];
                            _chValue[1] = command[7];
                            Double ch4 = Double.parseDouble(ByteUtil.ByteArrToHex(_chValue));
                            android.util.Log.d("wlDebug", "ch4 = " + ch4);

                            if (mView != null)
                                mView.onDataUpdate(new double[]{ch1, ch2, ch3, ch4});
                        }
                    }
                }
            };
        }
    }

    @Override
    public void stopValueing() {
        android.util.Log.d("wlDebug", "stopMeasuing.");
        if (serialHelper != null && serialHelper.isOpen()) {
            serialHelper.close();
        }
    }

    @Override
    public double onePieceCalibration(double y1, double k, double x, double y) {
        return 0;
    }

    @Override
    public double calculationK(double x1, double y1, double x2, double y2) {
        return (y1 - y2) / (x1 - x2);
    }

    @Override
    public double calculationC(double y, double k, double x) {
        return y - k * x;
    }

    @Override
    public double calculationValue(double k, double x, double c) {
        return k * x + c;
    }
}
