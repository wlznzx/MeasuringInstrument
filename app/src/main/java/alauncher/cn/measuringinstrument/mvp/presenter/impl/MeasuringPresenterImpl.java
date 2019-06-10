package alauncher.cn.measuringinstrument.mvp.presenter.impl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.DaoSession;
import alauncher.cn.measuringinstrument.mvp.presenter.MeasuringPresenter;
import alauncher.cn.measuringinstrument.utils.Arith;
import alauncher.cn.measuringinstrument.view.activity_view.MeasuringActivityView;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.utils.ByteUtil;

/**
 * 日期：2019/5/9 0009 22:09
 * 包名：alauncher.cn.measuringinstrument.mvp.presenter.impl
 * 作者： wlznzx
 * 描述：
 */
public class MeasuringPresenterImpl implements MeasuringPresenter {

    final String TAG = "MeasuringPresenterImpl";

    private String sPort = "/dev/ttyMT1";
    private int iBaudRate = 115200;
    private SerialHelper serialHelper;

    MeasuringActivityView mView;

    private boolean isCommandStart = false;

    private int command_index = 0;
    private byte[] command = new byte[12];
    private byte[] _chValue = new byte[2];
    public ParameterBean mParameterBean;
    private JEP jep = new JEP();

    public CalibrationBean mCalibrationBean;

    public MeasuringPresenterImpl(MeasuringActivityView view) {
        mView = view;
        mParameterBean = App.getDaoSession().getParameterBeanDao().load((long) 1);
        if (mParameterBean != null) android.util.Log.d(TAG, mParameterBean.toString());
        mCalibrationBean = App.getDaoSession().getCalibrationBeanDao().load((long) 1);
        if (mCalibrationBean != null) Log.d(TAG, mCalibrationBean.toString());
    }

    @Override
    public void startMeasuing() {
        android.util.Log.d("wlDebug", "startMeasuing.");
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


                            // 如果参数管理不为空的话，那么需要进行公式的校验;
                            if (mParameterBean != null) {
                                double m1 = ch1;
                                double m2 = ch2;
                                double m3 = ch3;
                                double m4 = ch4;
                                try {
                                    jep.addVariable("ch1", ch1);
                                    jep.addVariable("ch2", ch2);
                                    jep.addVariable("ch3", ch3);
                                    jep.addVariable("ch4", ch4);

                                    if (!mParameterBean.getM1_code().equals("")) {
                                        Node node = jep.parse(mParameterBean.getM1_code());
                                        m1 = (double) jep.evaluate(node);
                                    }
                                    if (mParameterBean.getM2_code() != null) {
                                        Node node = jep.parse(mParameterBean.getM2_code());
                                        m2 = (double) jep.evaluate(node);
                                    }
                                    if (mParameterBean.getM3_code() != null) {
                                        Node node = jep.parse(mParameterBean.getM3_code());
                                        m3 = (double) jep.evaluate(node);
                                    }
                                    if (mParameterBean.getM4_code() != null) {
                                        Node node = jep.parse(mParameterBean.getM4_code());
                                        m4 = (double) jep.evaluate(node);
                                    }/**/
                                    if (mView != null)
                                        mView.onMeasuringDataUpdate(new double[]{m1, m2, m3, m4});
                                    // Toast.makeText(mContext, "result = " + result, Toast.LENGTH_SHORT).show();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (mView != null)
                                    mView.onMeasuringDataUpdate(new double[]{ch1, ch2, ch3, ch4});
                            }
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

    // 5301 1086 2031 3036 38C9 4E54
    @Override
    public void stopMeasuing() {
        android.util.Log.d("wlDebug", "stopMeasuing.");
        if (serialHelper != null && serialHelper.isOpen()) {
            serialHelper.close();
        }
    }

    /*
     *
     *   将读出来的AD字，通过校准，转化为校准后的测量值;
     *
     * */
    private double[] doCH2P(String[] inputValue) {
        double[] _values = new double[4];
        if (mCalibrationBean != null) {
            int x1 = Integer.parseInt(inputValue[1], 16);
            double y1 = Arith.add(Arith.mul(mCalibrationBean.getCh1KValue(), x1), mCalibrationBean.getCh1CompensationValue());

            int x2 = Integer.parseInt(inputValue[2], 16);
            double y2 = Arith.add(Arith.mul(mCalibrationBean.getCh2KValue(), x2), mCalibrationBean.getCh2CompensationValue());

            int x3 = Integer.parseInt(inputValue[3], 16);
            double y3 = Arith.add(Arith.mul(mCalibrationBean.getCh3KValue(), x3), mCalibrationBean.getCh3CompensationValue());

            int x4 = Integer.parseInt(inputValue[4], 16);
            double y4 = Arith.add(Arith.mul(mCalibrationBean.getCh4KValue(), x4), mCalibrationBean.getCh4CompensationValue());

            _values[0] = x1;
            _values[1] = x2;
            _values[2] = x3;
            _values[3] = x4;
        } else {
            _values[0] = 1;
            _values[1] = 2;
            _values[2] = 3;
            _values[3] = 4;
        }
        return _values;
    }

}
