package alauncher.cn.measuringinstrument.mvp.presenter.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.greendao.DaoException;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.bean.GroupBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ProcessBean;
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

    private byte[] _chValue = new byte[2];
    public ParameterBean mParameterBean;
    private JEP jep = new JEP();
    private JEP calculationJEP = new JEP();

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

    // 重新解析公式;
    private String[] reCodes = new String[4];

    // 重新解析公式，为了个过程值之间的时候，也可以运算;
    private String[] reCodesForCaluations = new String[4];

    //
    private int measure_state = 3;

//    private List<ProcessBean> m1processBeanLists = new ArrayList<>();
//    private List<ProcessBean> m2processBeanLists = new ArrayList<>();
//    private List<ProcessBean> m3processBeanLists = new ArrayList<>();
//    private List<ProcessBean> m4processBeanLists = new ArrayList<>();

    private List<List<ProcessBean>> processBeanLists = new ArrayList<>();

    private int currentStep = -1;

    private DeviceInfoBean _dBean;

    public boolean[] mGeted = {false, false, false, false};
    //
    public boolean[] inLimited = {true, true, true, true};

    public StoreBean mStoreBean;

    public long lastMeetConditionTime = 0;

    private boolean haveProcessCalculate = false;

    private boolean isGetProcessValue = false;

    // 是单步还是分步取值;
    private boolean isSingleStep = true;

    private final int MSG_AUTO_STORE = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUTO_STORE:
                    handler.removeMessages(MSG_AUTO_STORE);
                    doAutoStore();
                    break;
            }
        }
    };

    // 存储过程中的测量值;
    private List<List<Double>> tempValues = new ArrayList<List<Double>>();

    public MeasuringPresenterImpl(MeasuringActivityView view) {
        mView = view;
        jep.addFunction("Max", new Max());
        jep.addFunction("Min", new Min());
        jep.addFunction("Avg", new Avg());
        jep.addFunction("Dif", new Dif());
        calculationJEP.addFunction("Max", new Max());
        calculationJEP.addFunction("Min", new Min());
        calculationJEP.addFunction("Avg", new Avg());
        calculationJEP.addFunction("Dif", new Dif());
        initParameter();

        _dBean = App.getDeviceInfo();
    }

    public void initParameter() {
        stopAutoStore();
        // 初始化过程值的List;
        tempValues.clear();
        processBeanLists.clear();
        for (int i = 0; i < 4; i++) {
            tempValues.add(new ArrayList<>());
            processBeanLists.add(new ArrayList<>());
        }
        mStoreBean = App.getDaoSession().getStoreBeanDao().load(App.SETTING_ID);

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

            // 解析公式，提取过程值;
            String _regx = "L.*?\\)";
            Pattern p = Pattern.compile(_regx);

            String _reCgx = "L.*?\\(";
            Pattern _reCPattern = Pattern.compile(_reCgx);
            if (mParameterBean.getM1_code() != null) {
                reCodes[0] = mParameterBean.getM1_code();
                Matcher matcher = p.matcher(mParameterBean.getM1_code());
                while (matcher.find()) {
//                    ProcessBean _process = new ProcessBean("x" + processBeanLists.get(0).size(), mParameterBean.getM1_code().substring(matcher.start() + 5, matcher.end() - 1), mParameterBean.getM1_code().substring(matcher.start(), matcher.start() + 4));
//                    processBeanLists.get(0).add(_process);
//                    reCodes[0] = reCodes[0].replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }

                reCodesForCaluations[0] = mParameterBean.getM1_code();
                matcher = _reCPattern.matcher(mParameterBean.getM1_code());
                while (matcher.find()) {
                    reCodesForCaluations[0] = reCodesForCaluations[0].replace(mParameterBean.getM1_code().substring(matcher.start(), matcher.end() - 1), "");
                }
            }

            if (mParameterBean.getM2_code() != null) {
                reCodes[1] = mParameterBean.getM2_code();
                Matcher matcher = p.matcher(mParameterBean.getM2_code());
                while (matcher.find()) {
//                    ProcessBean _process = new ProcessBean("x" + processBeanLists.get(1).size(), mParameterBean.getM2_code().substring(matcher.start() + 5, matcher.end() - 1), mParameterBean.getM2_code().substring(matcher.start(), matcher.start() + 4));
//                    processBeanLists.get(1).add(_process);
//                    reCodes[1] = reCodes[1].replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }

                reCodesForCaluations[1] = mParameterBean.getM2_code();
                matcher = _reCPattern.matcher(mParameterBean.getM2_code());
                while (matcher.find()) {
                    reCodesForCaluations[1] = reCodesForCaluations[1].replace(mParameterBean.getM2_code().substring(matcher.start(), matcher.end() - 1), "");
                }
            }

            if (mParameterBean.getM3_code() != null) {
                reCodes[2] = mParameterBean.getM3_code();
                Matcher matcher = p.matcher(mParameterBean.getM3_code());
                while (matcher.find()) {
//                    ProcessBean _process = new ProcessBean("x" + processBeanLists.get(2).size(), mParameterBean.getM3_code().substring(matcher.start() + 5, matcher.end() - 1), mParameterBean.getM3_code().substring(matcher.start(), matcher.start() + 4));
//                    processBeanLists.get(2).add(_process);
//                    reCodes[2] = reCodes[2].replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }

                reCodesForCaluations[2] = mParameterBean.getM3_code();
                matcher = _reCPattern.matcher(mParameterBean.getM3_code());
                while (matcher.find()) {
                    reCodesForCaluations[2] = reCodesForCaluations[2].replace(mParameterBean.getM3_code().substring(matcher.start(), matcher.end() - 1), "");
                }
            }

            if (mParameterBean.getM4_code() != null) {
                reCodes[3] = mParameterBean.getM4_code();
                Matcher matcher = p.matcher(mParameterBean.getM4_code());
                while (matcher.find()) {
//                    ProcessBean _process = new ProcessBean("x" + processBeanLists.get(3).size(), mParameterBean.getM4_code().substring(matcher.start() + 5, matcher.end() - 1), mParameterBean.getM4_code().substring(matcher.start(), matcher.start() + 4));
//                    processBeanLists.get(3).add(_process);
//                    reCodes[3] = reCodes[3].replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }

                reCodesForCaluations[3] = mParameterBean.getM4_code();
                matcher = _reCPattern.matcher(mParameterBean.getM4_code());
                while (matcher.find()) {
                    reCodesForCaluations[3] = reCodesForCaluations[3].replace(mParameterBean.getM4_code().substring(matcher.start(), matcher.end() - 1), "");
                }
            }
        }
        mCalibrationBean = App.getDaoSession().getCalibrationBeanDao().load((long) App.getSetupBean().getCodeID());
        if (mCalibrationBean != null) {
            Log.d(TAG, mCalibrationBean.toString());
        }

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
        if (maxStep > 0) {
            currentStep = 0;
        } else {
            currentStep = -1;
        }
        isSingleStep = true;
        if (maxStep > 0 && mStoreBean.getStoreMode() == 1) {
            isSingleStep = false;
        }

        haveProcessCalculate = false;
        for (List<ProcessBean> list : processBeanLists) {
            if (list.size() > 0) {
                haveProcessCalculate = true;
            }
        }

        if (currentStep == -1) {
            // 没有分步,判断是否有过程值与单点值同时存在的情况；
            if (haveProcessCalculate) {
                boolean _isEnable = true;
                for (int i = 0; i < 4; i++) {
                    switch (i) {
                        case 0:
                            _isEnable = mParameterBean.getM1_enable();
                            break;
                        case 1:
                            _isEnable = mParameterBean.getM2_enable();
                            break;
                        case 2:
                            _isEnable = mParameterBean.getM3_enable();
                            break;
                        case 3:
                            _isEnable = mParameterBean.getM4_enable();
                            break;
                    }
                    if (processBeanLists.get(i).size() == 0 && _isEnable) {
                        mView.showUnSupportDialog(-1);
                        return;
                    }
                }
                measure_state = 1;
                mView.setValueBtnVisible(false);
            } else {
                mView.setValueBtnVisible(false);
            }
        } else {
            // 有分步，要判断出具体哪一步有过程值和单点值同时存在;
            for (int i = 0; i < stepBeans.size(); i++) {
                StepBean _bean = stepBeans.get(i);
                boolean flag = false, temp = true;
                for (int j = 0; j < 4; j++) {
                    if (StepUtils.getChannelByStep(j, _bean.getMeasured())) {
                        if (!flag) {
                            flag = true;
                            temp = processBeanLists.get(j).size() > 0;
                        } else {
                            if (temp ^ (processBeanLists.get(j).size() > 0)) {
                                // 异或，存在单点与过程同在的情况;
                                android.util.Log.d("wlDebug", "i = " + i);
                                mView.showUnSupportDialog(i);
                                return;
                            }
                        }
                    }
                }
            }
            measure_state = 1;
        }
        // mView.updateSaveBtnMsg();
        startAutoStore();
    }

    private long lastValueTime = 0;
    // 记录上一次的value值，不刷新界面;
    private String lastValue = "";

    //
    private String[] values = new String[4];

    @Override
    public void startMeasuring() {
        android.util.Log.d("wlDebug", "startMeasuing.");
        if (serialHelper == null) {
            serialHelper = new SerialHelper(sPort, iBaudRate) {
                @Override
                protected void onDataReceived(ComBean paramComBean) {
                    // 重新解析Byte;
                    if (paramComBean.bRec[0] == 0x53 && paramComBean.bRec[11] == 0x54) {

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
                }
            };
        }
        try {
            serialHelper.open();
        } catch (Exception e) {
            Toast.makeText((Context) mView, "串口打开失败.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        lastMeetConditionTime = System.currentTimeMillis();

        // 测试用;
        String[] _values = {"1086", "1086", "1086", "1086"};
        String[] _values2 = {"3036", "3036", "3036", "3036"};
        String[] _values3 = {"13036", "13036", "13036", "13036"};
        String[] _values4 = {"2036", "2036", "2036", "2036"};

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2P(_values));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */

        // forValueTest();
    }

    // 5301 1086 2031 3036 38C9 4E54
    @Override
    public void stopMeasuring() {
        android.util.Log.d("wlDebug", "stopMeasuing.");
        stopAutoStore();
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
        // 如果是手动点击的保存，需要计算;
        if (isManual) {
            for (int i = 0; i < 4; i++) {
                if (processBeanLists.get(i).size() > 0) {
                    try {
                        ms[i] = handlerProcessValues2(processBeanLists.get(i), i);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        for (List<Double> list : tempValues) {
            list.clear();
        }
        if (!isSingleStep) {
            StepBean _bean = stepBeans.get(getStep());
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
                // 要满足两个条件；1、从范围外面进来一次。2、持续一定的时间；
                if (lastMeetConditionTime == -1) {
                    lastMeetConditionTime = System.currentTimeMillis();
                }
                // 如果在限制内，退出;
                if (inLimited[mIndex]) {
                    // android.util.Log.d("wlDebug", "一直在区域内.");
                    return "NoSave";
                }

                long currentTime = System.currentTimeMillis();
                android.util.Log.d("wlDebug", "currentTime = " + currentTime + " lastMeetConditionTime = " + lastMeetConditionTime + " Stable = " + _TriggerConditionBean.getStableTime() * 1000);

                if (currentTime - lastMeetConditionTime > _TriggerConditionBean.getStableTime() * 1000) {

                } else {
                    android.util.Log.d("wlDebug", "持续时间未到.");
                    return "NoSave;";
                }
                inLimited[mIndex] = true;

            } else if (isManual == false) { // 如果不是手动点击保存，不保存；
                return "NoSave;";
            } else {

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
        measure_state = 1;
        mView.updateSaveBtnMsg();
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

        android.util.Log.d("wlDebug", "save bean = " + _bean.toString());

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
                    // JdbcUtil.addResult2(_dBean.getFactoryCode(), _dBean.getDeviceCode(), App.getSetupBean().getCodeID(), "", _bean);
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
        measure_state = 1;
        android.util.Log.d("wlDebug", "Measured = " + Integer.toBinaryString(stepBeans.get(getStep()).getMeasured()));
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

    @Override
    public void startGetProcessValue() {
        isGetProcessValue = true;
        // 清空当前取值;
        for (List<Double> list : tempValues) {
            list.clear();
        }
        measure_state = 2;
        mView.updateSaveBtnMsg();
    }

    @Override
    public void stopGetProcessValue() {
        isGetProcessValue = false;
        measure_state = 3;
        mView.updateSaveBtnMsg();
    }

    @Override
    public boolean getIsStartProcessValue() {
        return isGetProcessValue;
    }

    @Override
    public int getMeasureState() {
        return measure_state;
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
    @Override
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

    @Override
    public String[] getResults(double[] ms) {
        return new String[0];
    }


    @Override
    public boolean[] getGeted() {
        return mGeted;
    }

    @Override
    public void setPause(boolean isPause) {

    }

    @Override
    public boolean isInMeasuring(String item) {
        return false;
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
        // 计算测量值，ch1~ch4;
        if (mCalibrationBean != null) {
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
        if (haveProcessCalculate && tempValues.get(0).size() <= 5000 && isGetProcessValue) {
            for (int i = 0; i < 4; i++) {
                tempValues.get(i).add(chValues[i]);
            }
        }

        if (mParameterBean != null) {
            try {
                jep.addVariable("ch1", chValues[0]);
                jep.addVariable("ch2", chValues[1]);
                jep.addVariable("ch3", chValues[2]);
                jep.addVariable("ch4", chValues[3]);

                for (int i = 0; i < 4; i++) {
                    if (reCodes[i] != null && !reCodes[i].equals("")) {
                        if (processBeanLists.get(i).size() > 0) {
                            /**/
                            // for (ProcessBean _process : processBeanLists.get(i)) {
                            // 添加过程值变量;
                            Node node = jep.parse(reCodesForCaluations[i]);
                            // jep.addVariable(_process.getReplaceName(), handlerProcessValues(_process, (Double) jep.evaluate(node)));
                            // }
                            switch (i) {
                                case 0:
                                    mValues[i] = (double) jep.evaluate(node) + mParameterBean.getM1_offect();
                                    break;
                                case 1:
                                    mValues[i] = (double) jep.evaluate(node) + mParameterBean.getM2_offect();
                                    break;
                                case 2:
                                    mValues[i] = (double) jep.evaluate(node) + mParameterBean.getM3_offect();
                                    break;
                                case 3:
                                    mValues[i] = (double) jep.evaluate(node) + mParameterBean.getM4_offect();
                                    break;
                            }

                        } else {
                            if (nodes[i] == null) {
                                nodes[i] = jep.parse(reCodes[i]);
                            }
                            switch (i) {
                                case 0:
                                    mValues[i] = (double) jep.evaluate(nodes[i]) + mParameterBean.getM1_offect();
                                    break;
                                case 1:
                                    mValues[i] = (double) jep.evaluate(nodes[i]) + mParameterBean.getM2_offect();
                                    break;
                                case 2:
                                    mValues[i] = (double) jep.evaluate(nodes[i]) + mParameterBean.getM3_offect();
                                    break;
                                case 3:
                                    mValues[i] = (double) jep.evaluate(nodes[i]) + mParameterBean.getM4_offect();
                                    break;
                            }
                        }
                    }
                }
                if (mView != null) {
                    mView.onMeasuringDataUpdate(mValues);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis(); // 获取结束时间
        long stepTime = (endTime - startTime);
        Log.d("wlDebug", "公式计算耗时： " + stepTime + "ms");
        return null;
    }

    private double[] doCH2PTest(double[] inputValue) {
        long startTime = System.currentTimeMillis(); // 获取开始时间

        chValues[0] = inputValue[0];
        chValues[1] = inputValue[1];
        chValues[2] = inputValue[2];
        chValues[3] = inputValue[3];

        if (haveProcessCalculate && tempValues.get(0).size() <= 5000) {
            for (int i = 0; i < 4; i++) {
                tempValues.get(i).add(chValues[i]);
            }
        }

        if (mParameterBean != null) {
            try {
                jep.addVariable("ch1", chValues[0]);
                jep.addVariable("ch2", chValues[1]);
                jep.addVariable("ch3", chValues[2]);
                jep.addVariable("ch4", chValues[3]);

                for (int i = 0; i < 4; i++) {
                    if (reCodes[i] != null && !reCodes[i].equals("")) {
                        if (processBeanLists.get(i).size() > 0) {
                            /**/
                            // for (ProcessBean _process : processBeanLists.get(i)) {
                            // 添加过程值变量;
                            Node node = jep.parse(reCodesForCaluations[i]);
                            // jep.addVariable(_process.getReplaceName(), handlerProcessValues(_process, (Double) jep.evaluate(node)));
                            // }
                            switch (i) {
                                case 0:
                                    mValues[i] = (double) jep.evaluate(node) + mParameterBean.getM1_offect();
                                    break;
                                case 1:
                                    mValues[i] = (double) jep.evaluate(node) + mParameterBean.getM2_offect();
                                    break;
                                case 2:
                                    mValues[i] = (double) jep.evaluate(node) + mParameterBean.getM3_offect();
                                    break;
                                case 3:
                                    mValues[i] = (double) jep.evaluate(node) + mParameterBean.getM4_offect();
                                    break;
                            }

                        } else {
                            if (nodes[i] == null) {
                                nodes[i] = jep.parse(reCodes[i]);
                            }
                            switch (i) {
                                case 0:
                                    mValues[i] = (double) jep.evaluate(nodes[i]) + mParameterBean.getM1_offect();
                                    break;
                                case 1:
                                    mValues[i] = (double) jep.evaluate(nodes[i]) + mParameterBean.getM2_offect();
                                    break;
                                case 2:
                                    mValues[i] = (double) jep.evaluate(nodes[i]) + mParameterBean.getM3_offect();
                                    break;
                                case 3:
                                    mValues[i] = (double) jep.evaluate(nodes[i]) + mParameterBean.getM4_offect();
                                    break;
                            }
                        }
                    }
                }
                if (mView != null) {
                    mView.onMeasuringDataUpdate(mValues);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis(); // 获取结束时间
        long stepTime = (endTime - startTime);
        Log.d("wlDebug", "公式计算耗时： " + stepTime + "ms");
        return null;
    }


    private double handlerProcessValues(ProcessBean bean, double value) {
        switch (bean.getExpressionType()) {
            case "LMax":
                if (value > bean.getVar1()) {
                    bean.setVar1(value);
                }
                return bean.getVar1();
            case "LMin":
                if (bean.getVar2() == 0) {
                    bean.setVar2(-1);
                    bean.setVar1(value);
                    return bean.getVar1();
                }
                if (value < bean.getVar1()) {
                    bean.setVar1(value);
                }
                return bean.getVar1();
            case "LAvg":
                if (bean.getVar2() == 0) {
                    bean.setVar2(-1);
                    bean.setVar1(value);
                } else {
                    bean.setVar1((bean.getVar1() + value) / 2);
                }
                return bean.getVar1();
            case "LDif":
                if (bean.getVar1() == 0 && bean.getVar2() == 0) {
                    bean.setVar1(value);
                    bean.setVar2(value);
                }
                if (value > bean.getVar1()) {
                    bean.setVar1(value);
                }
                if (value < bean.getVar2()) {
                    bean.setVar2(value);
                }
                return Math.abs(bean.getVar1() - bean.getVar2());
        }
        return 0;
    }

    // 计算出过程值，写入List;
    private List<Double> calculationValuesList = new ArrayList<>();

    private double handlerProcessValues2(List<ProcessBean> list, int index) throws ParseException {
        for (ProcessBean _process : list) {
            calculationValuesList.clear();
            // 添加过程值变量;
            Node node = null;
            for (int i = 0; i < tempValues.get(0).size(); i++) {
                calculationJEP.addVariable("ch1", tempValues.get(0).get(i));
                calculationJEP.addVariable("ch2", tempValues.get(1).get(i));
                calculationJEP.addVariable("ch3", tempValues.get(2).get(i));
                calculationJEP.addVariable("ch4", tempValues.get(3).get(i));
//                node = calculationJEP.parse(_process.getExpression());
                calculationValuesList.add((Double) calculationJEP.evaluate(node));
            }
            Collections.sort(calculationValuesList);
            android.util.Log.d("wlDebug", "reList.size() = " + calculationValuesList.size());
            android.util.Log.d("wlDebug", "reList = " + calculationValuesList.toString());
            jep.addVariable(_process.getReplaceName(), calculationProcess(_process, calculationValuesList));
        }
        if (nodes[index] == null) {
            nodes[index] = jep.parse(reCodes[index]);
        }
        return (double) jep.evaluate(nodes[index]) + mParameterBean.getM1_offect();
    }

    private double calculationProcess(ProcessBean bean, List<Double> values) {
        if (values.size() == 0) {
            return 0;
        }
        double sum = 0D;
        int num = 0;
        int percent_10, percent_20, percent_80, percent_90;
        double result = 0;
        switch (bean.getExpressionType()) {
            case "LMax":
                percent_80 = (int) Math.round(values.size() * 0.8) - 1;
                percent_90 = (int) Math.round(values.size() * 0.9) - 1;

                if (percent_80 < 0) {
                    percent_80 = 0;
                }
                for (int i = percent_80; i <= percent_90; i++, num++) {
                    sum += values.get(i);
                }
                result = BigDecimal.valueOf(sum / num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
            case "LMin":
                percent_10 = (int) Math.round(values.size() * 0.1) - 1;
                percent_20 = (int) Math.round(values.size() * 0.2) - 1;

                if (percent_10 < 0) {
                    percent_10 = 0;
                }
                if (percent_20 < 0) {
                    percent_20 = 0;
                }
                for (int i = percent_10; i <= percent_20; i++, num++) {
                    sum += values.get(i);
                }
                result = BigDecimal.valueOf(sum / num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
            case "LAvg":
                percent_10 = (int) Math.round(values.size() * 0.1) - 1;
                percent_90 = (int) Math.round(values.size() * 0.9) - 1;
                if (percent_10 < 0) {
                    percent_10 = 0;
                }
                for (int i = percent_10; i <= percent_90; i++, num++) {
                    sum += values.get(i);
                }
                result = BigDecimal.valueOf(sum / num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
            case "LDif":
                percent_10 = (int) Math.round(values.size() * 0.1) - 1;
                percent_20 = (int) Math.round(values.size() * 0.2) - 1;
                percent_80 = (int) Math.round(values.size() * 0.8) - 1;
                percent_90 = (int) Math.round(values.size() * 0.9) - 1;
                if (percent_10 < 0) {
                    percent_10 = 0;
                }
                if (percent_20 < 0) {
                    percent_20 = 0;
                }
                if (percent_80 < 0) {
                    percent_80 = 0;
                }
                if (percent_90 < 0) {
                    percent_90 = 0;
                }
                android.util.Log.d("wlDebug", "percent_10 = " + percent_10);
                android.util.Log.d("wlDebug", "percent_20 = " + percent_20);
                android.util.Log.d("wlDebug", "percent_80 = " + percent_80);
                android.util.Log.d("wlDebug", "percent_90 = " + percent_90);
                for (int i = percent_80; i <= percent_90; i++, num++) {
                    sum += values.get(i);
                }
                double sum2 = 0;
                int num2 = 0;
                for (int i = percent_10; i <= percent_20; i++, num2++) {
                    sum2 += values.get(i);
                }
                result = BigDecimal.valueOf(sum / num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() - BigDecimal.valueOf(sum2 / num2).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
        }
        android.util.Log.d("wlDebug", bean.getExpressionType() + " = " + result);
        return result;
    }

    // 判断当前步骤是否有过程值运算;
    @Override
    public boolean isCurStepHaveProcess() {
        if (isSingleStep) {
            return haveProcessCalculate ? true : false;
        } else {
            StepBean _bean = stepBeans.get(getStep());
            for (int j = 0; j < 4; j++) {
                if (StepUtils.getChannelByStep(j, _bean.getMeasured())) {
                    if (processBeanLists.get(j).size() > 0) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public boolean isSingleStep() {
        return isSingleStep;
    }

    private void startAutoStore() {
        if (!isSingleStep) {
            handler.sendEmptyMessageDelayed(MSG_AUTO_STORE, mStoreBean.getDelayTime() * 1000);
        }
    }

    private void doAutoStore() {
        mView.toDoSave(false);
        handler.sendEmptyMessageDelayed(MSG_AUTO_STORE, mStoreBean.getDelayTime() * 1000);
    }

    private void stopAutoStore() {
        handler.removeMessages(MSG_AUTO_STORE);
    }


    private void forValueTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.734849534, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.806335611, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.880121264, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.201054649, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.825012437, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.604557736, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.064053735, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.416952491, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.616241929, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.832199558, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.40698447, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.765952038, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.346109684, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.603089989, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.500988845, 0, 0, 0}));

                    /*
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.656299797, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.953417387, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.165816817, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.605159724, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.050628206, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.006994375, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.180025257, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.645229498, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.425027884, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.714960729, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.984675362, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.654539063, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.955927876, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.597868463, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.991571408, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.415365111, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.374348733, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.244692062, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.906543499, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.178921159, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.618551724, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.985959156, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.215174786, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.991140984, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.36320991, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.689347815, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.225909375, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.287353287, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.877595188, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.834286711, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.945547674, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.975807496, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.682299605, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.39210939, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.256959184, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.478149349, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.381442711, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.372155953, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.919182417, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.058968223, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.962137847, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.029177731, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.663634554, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.240549306, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.5055132, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.62698706, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.640639968, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.090672382, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.999842973, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.335588265, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.539818693, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.720223602, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.204356308, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.049248495, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.694983999, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.193313175, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.448805881, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.424030721, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.051402117, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.912083829, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.604937179, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.392475696, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.597292437, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.380083226, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.482228458, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.721135073, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.374207954, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.9674419, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.239817203, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.292485183, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.557573488, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.747028289, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.292472482, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.138040343, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.382994008, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.846572733, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.809970508, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.154207636, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.655632294, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.854803317, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.706066737, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.282731565, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.694444735, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.989753622, 0, 0, 0}));
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.883354002, 0, 0, 0}));
                    Thread.sleep(500);
                     */

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
