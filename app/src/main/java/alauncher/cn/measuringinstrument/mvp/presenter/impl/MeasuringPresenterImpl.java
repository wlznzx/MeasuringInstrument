package alauncher.cn.measuringinstrument.mvp.presenter.impl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.greendao.DaoException;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;

import java.util.List;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ResultBean;
import alauncher.cn.measuringinstrument.bean.StepBean;
import alauncher.cn.measuringinstrument.bean.StoreBean;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBeanDao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBeanDao;
import alauncher.cn.measuringinstrument.mvp.presenter.MeasuringPresenter;
import alauncher.cn.measuringinstrument.utils.Arith;
import alauncher.cn.measuringinstrument.utils.Avg;
import alauncher.cn.measuringinstrument.utils.Dif;
import alauncher.cn.measuringinstrument.utils.JdbcUtil;
import alauncher.cn.measuringinstrument.utils.Max;
import alauncher.cn.measuringinstrument.utils.Min;
import alauncher.cn.measuringinstrument.utils.StepUtils;
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
    // private byte[] command = new byte[12];
    private byte[] _chValue = new byte[2];
    public ParameterBean mParameterBean;
    private JEP jep = new JEP();

    public CalibrationBean mCalibrationBean;

    private int[] currentCHADValue = {4230, 8241, 12342, 14537};

    private GroupBean[] mGroupBeans = new GroupBean[4];

    // 上公差值;
    private double[] upperValue = new double[4];
    // 下公差值;
    private double[] lowerValue = new double[4];

    //
    private double[] upperToleranceValue = new double[4];
    private double[] lowerToleranceValue = new double[4];
    //
    private double[] nominalValue = new double[4];

    // 附加信息;
    private List<StepBean> stepBeans;

    // 总共有的测量步骤
    public int maxStep;

    // 缓存测量值;
    private double[] tempMs = new double[4];

    private int currentStep = -1;

    private DeviceInfoBean _dBean;

    public boolean[] mGeted = {false, false, false, false};
    //
    public boolean[] inLimited = {true, true, true, true};

    public StoreBean mStoreBean;

    public long lastMeetConditionTime = 0;

    public MeasuringPresenterImpl(MeasuringActivityView view) {
        mView = view;
        jep.addFunction("Max", new Max());
        jep.addFunction("Min", new Min());
        jep.addFunction("Avg", new Avg());
        jep.addFunction("Dif", new Dif());
        mParameterBean = App.getDaoSession().getParameterBeanDao().load((long) App.getSetupBean().getCodeID());
        if (mParameterBean != null) {
            android.util.Log.d(TAG, mParameterBean.toString());
            // 计算上下公差值;
            upperValue[0] = mParameterBean.getM1_nominal_value() + (mParameterBean.getM1_upper_tolerance_value());
            upperValue[1] = mParameterBean.getM2_nominal_value() + (mParameterBean.getM2_upper_tolerance_value());
            upperValue[2] = mParameterBean.getM3_nominal_value() + (mParameterBean.getM3_upper_tolerance_value());
            upperValue[3] = mParameterBean.getM4_nominal_value() + (mParameterBean.getM4_upper_tolerance_value());

            lowerValue[0] = mParameterBean.getM1_nominal_value() + (mParameterBean.getM1_lower_tolerance_value());
            lowerValue[1] = mParameterBean.getM2_nominal_value() + (mParameterBean.getM2_lower_tolerance_value());
            lowerValue[2] = mParameterBean.getM3_nominal_value() + (mParameterBean.getM3_lower_tolerance_value());
            lowerValue[3] = mParameterBean.getM4_nominal_value() + (mParameterBean.getM4_lower_tolerance_value());

            nominalValue[0] = mParameterBean.getM1_nominal_value();
            nominalValue[1] = mParameterBean.getM2_nominal_value();
            nominalValue[2] = mParameterBean.getM3_nominal_value();
            nominalValue[3] = mParameterBean.getM4_nominal_value();

            upperToleranceValue[0] = mParameterBean.getM1_upper_tolerance_value();
            upperToleranceValue[1] = mParameterBean.getM2_upper_tolerance_value();
            upperToleranceValue[2] = mParameterBean.getM3_upper_tolerance_value();
            upperToleranceValue[3] = mParameterBean.getM4_upper_tolerance_value();

            lowerToleranceValue[0] = mParameterBean.getM1_lower_tolerance_value();
            lowerToleranceValue[1] = mParameterBean.getM2_lower_tolerance_value();
            lowerToleranceValue[2] = mParameterBean.getM3_lower_tolerance_value();
            lowerToleranceValue[3] = mParameterBean.getM4_lower_tolerance_value();
        }
        mCalibrationBean = App.getDaoSession().getCalibrationBeanDao().load((long) App.getSetupBean().getCodeID());
        if (mCalibrationBean != null) Log.d(TAG, mCalibrationBean.toString());

        GroupBeanDao _dao = App.getDaoSession().getGroupBeanDao();
        if (_dao != null) {
            for (int i = 0; i < 4; i++) {
                try {
                    mGroupBeans[i] = _dao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(i + 1)).unique();
                } catch (DaoException e) {
                    mGroupBeans[i] = _dao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(i + 1)).list().get(0);
                }
            }
        }

        stepBeans = App.getDaoSession().
                getStepBeanDao().queryBuilder().where(StepBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderAsc(StepBeanDao.Properties.StepID).list();
        maxStep = stepBeans.size();
        if (maxStep > 0)
            currentStep = 0;
        else
            currentStep = -1;

        _dBean = App.getDeviceInfo();

        mStoreBean = App.getDaoSession().getStoreBeanDao().load(App.SETTING_ID);
    }

    public void initParameter() {
        mParameterBean = App.getDaoSession().getParameterBeanDao().load((long) App.getSetupBean().getCodeID());
        if (mParameterBean != null) {
            android.util.Log.d(TAG, mParameterBean.toString());
            // 计算上下公差值;
            upperValue[0] = mParameterBean.getM1_nominal_value() + (mParameterBean.getM1_upper_tolerance_value());
            upperValue[1] = mParameterBean.getM2_nominal_value() + (mParameterBean.getM2_upper_tolerance_value());
            upperValue[2] = mParameterBean.getM3_nominal_value() + (mParameterBean.getM3_upper_tolerance_value());
            upperValue[3] = mParameterBean.getM4_nominal_value() + (mParameterBean.getM4_upper_tolerance_value());

            lowerValue[0] = mParameterBean.getM1_nominal_value() + (mParameterBean.getM1_lower_tolerance_value());
            lowerValue[1] = mParameterBean.getM2_nominal_value() + (mParameterBean.getM2_lower_tolerance_value());
            lowerValue[2] = mParameterBean.getM3_nominal_value() + (mParameterBean.getM3_lower_tolerance_value());
            lowerValue[3] = mParameterBean.getM4_nominal_value() + (mParameterBean.getM4_lower_tolerance_value());

            nominalValue[0] = mParameterBean.getM1_nominal_value();
            nominalValue[1] = mParameterBean.getM2_nominal_value();
            nominalValue[2] = mParameterBean.getM3_nominal_value();
            nominalValue[3] = mParameterBean.getM4_nominal_value();

            upperToleranceValue[0] = mParameterBean.getM1_upper_tolerance_value();
            upperToleranceValue[1] = mParameterBean.getM2_upper_tolerance_value();
            upperToleranceValue[2] = mParameterBean.getM3_upper_tolerance_value();
            upperToleranceValue[3] = mParameterBean.getM4_upper_tolerance_value();

            lowerToleranceValue[0] = mParameterBean.getM1_lower_tolerance_value();
            lowerToleranceValue[1] = mParameterBean.getM2_lower_tolerance_value();
            lowerToleranceValue[2] = mParameterBean.getM3_lower_tolerance_value();
            lowerToleranceValue[3] = mParameterBean.getM4_lower_tolerance_value();
        }

        mCalibrationBean = App.getDaoSession().getCalibrationBeanDao().load((long) App.getSetupBean().getCodeID());
        if (mCalibrationBean != null) Log.d(TAG, mCalibrationBean.toString());

        GroupBeanDao _dao = App.getDaoSession().getGroupBeanDao();
        if (_dao != null) {
            for (int i = 0; i < 4; i++) {
                mGroupBeans[i] = _dao.queryBuilder().where(GroupBeanDao.Properties.Code_id.eq(App.getSetupBean().getCodeID()), GroupBeanDao.Properties.M_index.eq(i + 1)).unique();
            }
        }

        stepBeans = App.getDaoSession().
                getStepBeanDao().queryBuilder().where(StepBeanDao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderAsc(StepBeanDao.Properties.StepID).list();
        maxStep = stepBeans.size();
        if (maxStep > 0)
            currentStep = 0;
        else
            currentStep = -1;
    }

    private long lastValueTime = 0;
    // 记录上一次的value值，不刷新界面;
    private String lastValue = "";

    //
    private String[] values = new String[4];
    @Override
    public void startMeasuing() {
        android.util.Log.d("wlDebug", "startMeasuing.");
        if (serialHelper == null) {
            serialHelper = new SerialHelper(sPort, iBaudRate) {
                @Override
                protected void onDataReceived(ComBean paramComBean) {

                    // android.util.Log.d("wlDebug", "onDataReceived = " + ByteUtil.ByteArrToHex(paramComBean.bRec));
//                    long _currentTime = System.currentTimeMillis();
//                    if (lastValueTime != 0) {
//                        long stepTime = (_currentTime - lastValueTime);
//                        android.util.Log.d("wlDebug", " last time:" + stepTime + "ms");
//                    }
//                    lastValueTime = _currentTime;
//                    if(true)return;


                    // 重新解析Byte;
                    if (paramComBean.bRec[0] == 0x53 && paramComBean.bRec[11] == 0x54) {

                        if (lastValue.equals(ByteUtil.ByteArrToHex(paramComBean.bRec))) {
                            return;
                        }
                        lastValue = ByteUtil.ByteArrToHex(paramComBean.bRec);

                        long currentTime = System.currentTimeMillis();
                        if (lastValueTime != 0) {
                            long stepTime = (currentTime - lastValueTime);
                            android.util.Log.d("wlDebug", "_value = " + lastValue + " last time:" + stepTime + "ms");
                        }
                        lastValueTime = currentTime;

                        _chValue[0] = paramComBean.bRec[2];
                        _chValue[1] = paramComBean.bRec[3];
                        values[0] = ByteUtil.ByteArrToHex(_chValue);

                        _chValue[0] = paramComBean.bRec[4];
                        _chValue[1] = paramComBean.bRec[5];
                        values[1] = ByteUtil.ByteArrToHex(_chValue);

                        _chValue[0] = paramComBean.bRec[6];
                        _chValue[1] = paramComBean.bRec[7];
                        values[2] = ByteUtil.ByteArrToHex(_chValue);

                        _chValue[0] = paramComBean.bRec[8];
                        _chValue[1] = paramComBean.bRec[9];
                        values[3] = ByteUtil.ByteArrToHex(_chValue);
                        if (mView != null) {
                            doCH2P(values);
                        }
                    }

                    /*
                    for (byte _byte : paramComBean.bRec) {
                        if (_byte == 0x53 && !isCommandStart) {
                            isCommandStart = true;
                            command_index = 0;
                        }
                        if (isCommandStart) {
                            command[command_index] = _byte;
                            command_index++;
                        }
                        if (_byte == 0x54 && command_index == 12) {
                            isCommandStart = false;
                            String _value = ByteUtil.ByteArrToHex(command);
                            if (_value.equals(lastValue)) {
                                // 记录上一次的value值，不刷新界面;
                                return;
                            }
                            lastValue = _value;
                            long currentTime = System.currentTimeMillis();
                            if (lastValueTime != 0) {
                                long stepTime = (currentTime - lastValueTime);
                                android.util.Log.d("wlDebug", "_value = " + _value + " last time:" + stepTime + "ms");
                            }

                            lastValueTime = currentTime;
                            // android.util.Log.d("wlDebug", "_value = " + _value);
                            _chValue[0] = command[2];
                            _chValue[1] = command[3];
                            // int x1 = Integer.parseInt(ByteUtil.ByteArrToHex(_chValue), 16);
                            // Double ch1 = Double.valueOf(x1);
                            String _value1 = ByteUtil.ByteArrToHex(_chValue);
//                            android.util.Log.d("wlDebug", "ch1 = " + ch1);
                            _chValue[0] = command[4];
                            _chValue[1] = command[5];
                            String _value2 = ByteUtil.ByteArrToHex(_chValue);
                            // int x2 = Integer.parseInt(ByteUtil.ByteArrToHex(_chValue), 16);
                            // Double ch2 = Double.valueOf(x2);
//                            android.util.Log.d("wlDebug", "ch2 = " + ch2);
                            _chValue[0] = command[6];
                            _chValue[1] = command[7];
                            String _value3 = ByteUtil.ByteArrToHex(_chValue);
                            // int x3 = Integer.parseInt(ByteUtil.ByteArrToHex(_chValue), 16);
                            // Double ch3 = Double.valueOf(x3);
//                            android.util.Log.d("wlDebug", "ch3 = " + ch3);
                            _chValue[0] = command[8];
                            _chValue[1] = command[9];
                            String _value4 = ByteUtil.ByteArrToHex(_chValue);
                            // int x4 = Integer.parseInt(ByteUtil.ByteArrToHex(_chValue), 16);
                            // Double ch4 = Double.valueOf(x4);
//                            android.util.Log.d("wlDebug", "ch4 = " + ch4);
                            if (mView != null) {
                                doCH2P(new String[]{_value1, _value2, _value3, _value4});
                            }
                        }
                    }
                    */
                }
            };
        }
        try {
            serialHelper.open();
        } catch (Exception e) {
            Toast.makeText((Context) mView, "串口打开失败.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // 测试用;
        String[] _values = {"1086", "2031", "3036", "38C9"};
        String[] _values2 = {"3036", "38C9", "3036", "38C9"};

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(5);
                        mView.onMeasuringDataUpdate(doCH2P(_values));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */
    }

    // 5301 1086 2031 3036 38C9 4E54
    @Override
    public void stopMeasuing() {
        android.util.Log.d("wlDebug", "stopMeasuing.");
        if (serialHelper != null && serialHelper.isOpen()) {
            serialHelper.close();
        }
    }

    @Override
    public ParameterBean getParameterBean() {
        return mParameterBean;
    }

    @Override
    public String saveResult(double[] ms, AddInfoBean bean, boolean isManual) {
        if (stepBeans.size() > 0) {
            StepBean _bean = stepBeans.get(getStep());

            if (mStoreBean.getStoreMode() == 1) {
                TriggerConditionBean _TriggerConditionBean = App.getDaoSession().getTriggerConditionBeanDao().load(_bean.getConditionID());
                if (_TriggerConditionBean != null) {
                    int mIndex = _TriggerConditionBean.getMIndex() - 1;
                    if (_TriggerConditionBean.getIsScale()) {
                        // 增加的部分，公差带 * scale / 2
                        double m = (Math.abs(upperToleranceValue[mIndex] - lowerToleranceValue[mIndex]) * _TriggerConditionBean.getScale()) / 2;

                        // 计算公差中值;
                        double midValue = (upperValue[mIndex] + lowerValue[mIndex]) / 2;
                        double scaleUpperLimit = midValue + m;
                        double scaleLowerLimit = midValue - m;

                        android.util.Log.d("wlDebug", "scaleUpperLimit = " + scaleUpperLimit + " scaleLowerLimit = " + scaleLowerLimit + " M = " + ms[mIndex]);
                        if (ms[mIndex] < scaleLowerLimit || ms[mIndex] > scaleUpperLimit) {
                            lastMeetConditionTime = -1;
                            inLimited[mIndex] = false;
                            android.util.Log.d("wlDebug", "不在范围内.");
                            return "NoSave";
                        }
                    } else {
                        if (ms[mIndex] < _TriggerConditionBean.getLowerLimit() || ms[mIndex] > _TriggerConditionBean.getUpperLimit()) {
                            lastMeetConditionTime = -1;
                            inLimited[mIndex] = false;
                            return "NoSave";
                        }
                    }

                    // 如果在限制内，退出;
                    if (inLimited[mIndex]) {
                        // android.util.Log.d("wlDebug", "一直在区域内.");
                        return "NoSave";
                    }

                    long currentTime = System.currentTimeMillis();
                    // android.util.Log.d("wlDebug", "currentTime = " + currentTime + " lastMeetConditionTime = " + lastMeetConditionTime + " Stable = " + _TriggerConditionBean.getStableTime() * 1000);

                    if (currentTime - lastMeetConditionTime > _TriggerConditionBean.getStableTime() * 1000) {

                    } else {
                        // android.util.Log.d("wlDebug", "持续时间未到.");
                        return "NoSave;";
                    }
                    inLimited[mIndex] = true;
                    if (lastMeetConditionTime == -1) {
                        lastMeetConditionTime = System.currentTimeMillis();
                    }
                } else if (isManual == false) {
                    return "NoSave;";
                } else {

                }
            }

            for (int i = 0; i < ms.length; i++) {
                if (StepUtils.getChannelByStep(i, _bean.getMeasured())) {
                    android.util.Log.d("wlDebug", "获取第" + i + "值:" + ms[i]);
                    lastMeetConditionTime = -1;
                    tempMs[i] = ms[i];
                    mGeted[i] = true;
                }
            }

            if (getStep() == maxStep - 1) {
                doSave(tempMs, bean);
            }
            nextStep();
        } else {
            doSave(ms, bean);
        }
        return "OK";
    }

    private void doSave(double[] ms, AddInfoBean bean) {
        for (int i = 0; i < mGeted.length; i++) {
            mGeted[i] = false;
        }
        ResultBean _bean = new ResultBean();
        _bean.setHandlerAccout(App.handlerAccout);
        _bean.setCodeID(App.getSetupBean().getCodeID());
        _bean.setM1(ms[0]);
        _bean.setM2(ms[1]);
        _bean.setM3(ms[2]);
        _bean.setM4(ms[3]);

        if (mParameterBean != null) {
            _bean.setResult(getMResults(ms));
        } else {
            _bean.setResult("- -");
        }

        String[] _group = getMGroupValues(ms);
        _bean.setM1_group(_group[0]);
        _bean.setM2_group(_group[1]);
        _bean.setM3_group(_group[2]);
        _bean.setM4_group(_group[3]);
        _bean.setTimeStamp(System.currentTimeMillis());

        if (bean != null) {
            _bean.setEvent(bean.getEvent());
            _bean.setWorkid(bean.getWorkid());
        } else {
            _bean.setEvent("");
            _bean.setWorkid("");
        }
        App.getDaoSession().getResultBeanDao().insert(_bean);
        toSQLServer(_bean);
    }

    private void toSQLServer(final ResultBean _bean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JdbcUtil.addResult2(_dBean.getFactoryCode(), _dBean.getDeviceCode(), App.getSetupBean().getCodeID(), "", _bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public String[] getMGroupValues(double[] ms) {
        String[] result = new String[4];
        for (int i = 0; i < 4; i++) {
            GroupBean _bean = mGroupBeans[i];
            if (_bean != null) {
                if (ms[i] < _bean.getA_upper_limit() && ms[i] >= _bean.getA_lower_limit()) {
                    result[i] = "A";
                } else if (ms[i] < _bean.getB_upper_limit() && ms[i] >= _bean.getB_lower_limit()) {
                    result[i] = "B";
                } else if (ms[i] < _bean.getC_upper_limit() && ms[i] >= _bean.getC_lower_limit()) {
                    result[i] = "C";
                } else if (ms[i] < _bean.getD_upper_limit() && ms[i] >= _bean.getD_lower_limit()) {
                    result[i] = "D";
                } else {
                    result[i] = "- -";
                }
            } else {
                result[i] = "- -";
            }
        }
        return result;
    }

    private void nextStep() {
        if (currentStep < maxStep - 1) {
            currentStep++;
        } else {
            currentStep = 0;
        }
    }

    /*
     *
     * 获取当前测量步骤;
     *
     * */
    @Override
    public int getStep() {
        return currentStep;
    }

    public StepBean getStepBean() {
        if (stepBeans.size() > 0) {
            StepBean _bean = stepBeans.get(currentStep);
            return _bean;
        } else {
            return null;
        }
    }

    /*
     *
     * 根据测量值和上下公差，返回测试结果;
     *
     */
    public String getMResults(double[] ms) {

        if (mParameterBean == null) {
            return "- -";
        }

        for (int i = 0; i < 4; i++) {
            boolean isEnable = true;
            if (0 == i) {
                isEnable = mParameterBean.getM1_enable();
            } else if (1 == i) {
                isEnable = mParameterBean.getM2_enable();
            } else if (2 == i) {
                isEnable = mParameterBean.getM3_enable();
            } else if (3 == i) {
                isEnable = mParameterBean.getM4_enable();
            }
            if ((ms[i] > upperValue[i] || ms[i] < lowerValue[i]) && isEnable) {
                return "NG";
            }
        }
        return "OK";
    }

    /*
     *
     *   将读出来的AD字，通过校准，转化为校准后的测量值;
     *
     * */
    private double[] chValues = new double[4];
    private double[] mValues = new double[4];
    private Node[] nodes = new Node[4];

    private double[] doCH2P(String[] inputValue) {
        long startTime = System.currentTimeMillis(); // 获取开始时间
        // double[] _values = new double[4];
        // 计算测量值，ch1~ch4;
        if (mCalibrationBean != null) {
            /*
            int x1 = Integer.parseInt(inputValue[0], 16);
            double y1 = Arith.add(Arith.mul(mCalibrationBean.getCh1KValue(), x1), mCalibrationBean.getCh1CompensationValue());

            int x2 = Integer.parseInt(inputValue[1], 16);
            double y2 = Arith.add(Arith.mul(mCalibrationBean.getCh2KValue(), x2), mCalibrationBean.getCh2CompensationValue());

            int x3 = Integer.parseInt(inputValue[2], 16);
            double y3 = Arith.add(Arith.mul(mCalibrationBean.getCh3KValue(), x3), mCalibrationBean.getCh3CompensationValue());

            int x4 = Integer.parseInt(inputValue[3], 16);
            double y4 = Arith.add(Arith.mul(mCalibrationBean.getCh4KValue(), x4), mCalibrationBean.getCh4CompensationValue());
            ch1 = y1;
            ch2 = y2;
            ch3 = y3;
            ch4 = y4;
            */
            chValues[0] = Arith.add(Arith.mul(mCalibrationBean.getCh1KValue(), Integer.parseInt(inputValue[0], 16)), mCalibrationBean.getCh1CompensationValue());
            chValues[1] = Arith.add(Arith.mul(mCalibrationBean.getCh2KValue(), Integer.parseInt(inputValue[1], 16)), mCalibrationBean.getCh2CompensationValue());
            chValues[2] = Arith.add(Arith.mul(mCalibrationBean.getCh3KValue(), Integer.parseInt(inputValue[2], 16)), mCalibrationBean.getCh3CompensationValue());
            chValues[3] = Arith.add(Arith.mul(mCalibrationBean.getCh4KValue(), Integer.parseInt(inputValue[3], 16)), mCalibrationBean.getCh4CompensationValue());
        } else {
            chValues[0] = 1;
            chValues[1] = 2;
            chValues[2] = 3;
            chValues[3] = 4;
        }
        // 如果参数管理不为空的话，那么需要进行公式的校验;
//        double m1 = ch1;
//        double m2 = ch2;
//        double m3 = ch3;
//        double m4 = ch4;
        if (mParameterBean != null) {
            try {
                jep.addVariable("ch1", chValues[0]);
                jep.addVariable("ch2", chValues[1]);
                jep.addVariable("ch3", chValues[2]);
                jep.addVariable("ch4", chValues[3]);
                if (mParameterBean.getM1_code() != null && !mParameterBean.getM1_code().equals("")) {
                    // Node node = jep.parse(mParameterBean.getM1_code());
                    if (nodes[0] == null) nodes[0] = jep.parse(mParameterBean.getM1_code());
                    mValues[0] = (double) jep.evaluate(nodes[0]) + mParameterBean.getM1_offect();
                }
                if (mParameterBean.getM2_code() != null && mParameterBean.getM2_code() != null) {
//                    Node node = jep.parse(mParameterBean.getM2_code());
                    if (nodes[1] == null) nodes[1] = jep.parse(mParameterBean.getM2_code());
                    mValues[1] = (double) jep.evaluate(nodes[1]) + mParameterBean.getM2_offect();
                }
                if (mParameterBean.getM3_code() != null && mParameterBean.getM3_code() != null) {
//                    Node node = jep.parse(mParameterBean.getM3_code());
                    if (nodes[2] == null) nodes[2] = jep.parse(mParameterBean.getM3_code());
                    mValues[2] = (double) jep.evaluate(nodes[2]) + mParameterBean.getM3_offect();
                }
                if (mParameterBean.getM4_code() != null && mParameterBean.getM4_code() != null) {
                    // Node node = jep.parse(mParameterBean.getM4_code());
                    if (nodes[3] == null) nodes[3] = jep.parse(mParameterBean.getM4_code());
                    mValues[3] = (double) jep.evaluate(nodes[3]) + mParameterBean.getM4_offect();
                }
                if (mView != null)
                    // mView.onMeasuringDataUpdate(new double[]{m1, m2, m3, m4});
                    mView.onMeasuringDataUpdate(mValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        _values[0] = Arith.round(m1, 4);
//        _values[1] = Arith.round(m2, 4);
//        _values[2] = Arith.round(m3, 4);
//        _values[3] = Arith.round(m4, 4);
        long endTime = System.currentTimeMillis(); // 获取结束时间
        long stepTime = (endTime - startTime);
        Log.d("wlDebug", "公式计算耗时： " + (endTime - startTime) + "ms");
        return null;
    }


}
