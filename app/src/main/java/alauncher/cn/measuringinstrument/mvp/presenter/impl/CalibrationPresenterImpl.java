package alauncher.cn.measuringinstrument.mvp.presenter.impl;

import android.content.Context;
import android.widget.Toast;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.mvp.presenter.CalibrationPresenter;
import alauncher.cn.measuringinstrument.utils.Arith;
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

    private CalibrationBean mCalibrationBean;

    public CalibrationPresenterImpl(CalibrationActivityView view) {
        mView = view;
        mCalibrationBean = App.getDaoSession().getCalibrationBeanDao().load((long) App.getSetupBean().getCodeID());
        if (mCalibrationBean != null) android.util.Log.d("wlDebug", mCalibrationBean.toString());
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
                            /*
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
                            */

                            android.util.Log.d("wlDebug", "_value = " + _value);
                            _chValue[0] = command[2];
                            _chValue[1] = command[3];
                            int x1 = Integer.parseInt(ByteUtil.ByteArrToHex(_chValue), 16);
                            Double ch1 = Double.valueOf(x1);
                            android.util.Log.d("wlDebug", "ch1 = " + ch1);
                            _chValue[0] = command[4];
                            _chValue[1] = command[5];
                            int x2 = Integer.parseInt(ByteUtil.ByteArrToHex(_chValue), 16);
                            Double ch2 = Double.valueOf(x2);
                            android.util.Log.d("wlDebug", "ch2 = " + ch2);
                            _chValue[0] = command[6];
                            _chValue[1] = command[7];
                            int x3 = Integer.parseInt(ByteUtil.ByteArrToHex(_chValue), 16);
                            Double ch3 = Double.valueOf(x3);
                            android.util.Log.d("wlDebug", "ch3 = " + ch3);
                            _chValue[0] = command[8];
                            _chValue[1] = command[9];
                            int x4 = Integer.parseInt(ByteUtil.ByteArrToHex(_chValue), 16);
                            Double ch4 = Double.valueOf(x4);
                            android.util.Log.d("wlDebug", "ch4 = " + ch4);

                            if (mView != null) {
                                if (mView != null)
                                    mView.onDataUpdate(new int[]{x1, x2, x3, x4});
                            }
                            // mView.onDataUpdate(new int[]{ch1, ch2, ch3, ch4});
                        }
                    }
                }
            };
        }
        try {
            serialHelper.open();
        } catch (Exception e) {
            Toast.makeText((Context) mView, "串口打开失败.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
        // return y - k * x;
        return Arith.sub(y, Arith.mul(k, x));
    }

    @Override
    public double calculationValue(double k, double x, double c) {
        // return k * x + c;
        return Arith.add(Arith.mul(k, x), c);
    }

    @Override
    public void updateUI() {
        mCalibrationBean = App.getDaoSession().getCalibrationBeanDao().load((long) App.getSetupBean().getCodeID());
        if (mView != null) mView.onUIUpdate(mCalibrationBean);
    }

    @Override
    public void saveCalibration(CalibrationBean bean) {
        // 如果倍率超出了倍率上下限的范围，不保存，并提示;
        if (App.getDaoSession().getCalibrationBeanDao().load((long) App.getSetupBean().getCodeID()) == null) {
            App.getDaoSession().getCalibrationBeanDao().insert(bean);
        } else {
            App.getDaoSession().getCalibrationBeanDao().update(bean);
        }
    }
}
