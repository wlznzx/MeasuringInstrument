package alauncher.cn.measuringinstrument.mvp.presenter.impl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.greendao.DaoException;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;

import java.util.ArrayList;
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

    // 重新解析公式;
    private String[] reCodes = new String[4];

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

    // 存储过程中的测量值;
    private List<List<Double>> tempValues = new ArrayList<List<Double>>();

    public MeasuringPresenterImpl(MeasuringActivityView view) {
        mView = view;
        jep.addFunction("Max", new Max());
        jep.addFunction("Min", new Min());
        jep.addFunction("Avg", new Avg());
        jep.addFunction("Dif", new Dif());
        initParameter();

        _dBean = App.getDeviceInfo();

        mStoreBean = App.getDaoSession().getStoreBeanDao().load(App.SETTING_ID);
    }

    public void initParameter() {
        // 初始化过程值的List;
        tempValues.clear();
        processBeanLists.clear();
        for (int i = 0; i < 4; i++) {
            tempValues.add(new ArrayList<>());
            processBeanLists.add(new ArrayList<>());
        }
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
            if (mParameterBean.getM1_code() != null) {
                reCodes[0] = mParameterBean.getM1_code();
                Matcher matcher = p.matcher(mParameterBean.getM1_code());
                while (matcher.find()) {
                    ProcessBean _process = new ProcessBean("x" + processBeanLists.get(0).size(), mParameterBean.getM1_code().substring(matcher.start() + 5, matcher.end() - 1), mParameterBean.getM1_code().substring(matcher.start(), matcher.start() + 4));
                    processBeanLists.get(0).add(_process);
                    reCodes[0] = reCodes[0].replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }
            }

            if (mParameterBean.getM2_code() != null) {
                reCodes[1] = mParameterBean.getM2_code();
                Matcher matcher = p.matcher(mParameterBean.getM2_code());
                while (matcher.find()) {
                    ProcessBean _process = new ProcessBean("x" + processBeanLists.get(1).size(), mParameterBean.getM2_code().substring(matcher.start() + 5, matcher.end() - 1), mParameterBean.getM2_code().substring(matcher.start(), matcher.start() + 4));
                    processBeanLists.get(1).add(_process);
                    reCodes[1] = reCodes[1].replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }
            }

            if (mParameterBean.getM3_code() != null) {
                reCodes[2] = mParameterBean.getM3_code();
                Matcher matcher = p.matcher(mParameterBean.getM3_code());
                while (matcher.find()) {
                    ProcessBean _process = new ProcessBean("x" + processBeanLists.get(2).size(), mParameterBean.getM3_code().substring(matcher.start() + 5, matcher.end() - 1), mParameterBean.getM3_code().substring(matcher.start(), matcher.start() + 4));
                    processBeanLists.get(2).add(_process);
                    reCodes[2] = reCodes[2].replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }
            }

            if (mParameterBean.getM4_code() != null) {
                reCodes[3] = mParameterBean.getM4_code();
                Matcher matcher = p.matcher(mParameterBean.getM4_code());
                while (matcher.find()) {
                    ProcessBean _process = new ProcessBean("x" + processBeanLists.get(3).size(), mParameterBean.getM4_code().substring(matcher.start() + 5, matcher.end() - 1), mParameterBean.getM4_code().substring(matcher.start(), matcher.start() + 4));
                    processBeanLists.get(3).add(_process);
                    reCodes[3] = reCodes[3].replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }
            }
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

        for (List<ProcessBean> list : processBeanLists) {
            if (list.size() > 0) {
                haveProcessCalculate = true;
                return;
            }
        }
        haveProcessCalculate = false;
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

        /**/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    while (true) {
                    Thread.sleep(500);
                    mView.onMeasuringDataUpdate(doCH2P(_values2));
                    Thread.sleep(1500);
                    mView.onMeasuringDataUpdate(doCH2P(_values));
                    Thread.sleep(1500);
                    mView.onMeasuringDataUpdate(doCH2P(_values3));
                    Thread.sleep(1500);
                    mView.onMeasuringDataUpdate(doCH2P(_values4));
//                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
        // 如果是手动点击的保存，需要计算;
        if (isManual) {

        }
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
        if (haveProcessCalculate) {
            for (int i = 0; i < 4; i++) {
                
            }
        }
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
        if (mParameterBean != null) {
            try {
                jep.addVariable("ch1", chValues[0]);
                jep.addVariable("ch2", chValues[1]);
                jep.addVariable("ch3", chValues[2]);
                jep.addVariable("ch4", chValues[3]);

                if (reCodes[0] != null && !reCodes[0].equals("")) {
                    android.util.Log.d("wlDebug", "reCodes[0] = " + reCodes[0]);
                    // 如果过程值不为空；
                    if (m1processBeanLists.size() > 0) {
                        /*
                        for (ProcessBean _process : m1processBeanLists) {
                            // 添加过程值变量;
                            Node node = jep.parse(_process.getExpression());
                            jep.addVariable(_process.getReplaceName(), handlerProcessValues(_process, (Double) jep.evaluate(node)));
                        }
                         */
                        tempValues.get(0).add(chValues[0]);
                    }
                    if (nodes[0] == null) nodes[0] = jep.parse(reCodes[0]);
                    mValues[0] = (double) jep.evaluate(nodes[0]) + mParameterBean.getM1_offect();
                }
                if (reCodes[1] != null && reCodes[1] != null) {
                    if (m2processBeanLists.size() > 0) {
                        /*
                        for (ProcessBean _process : m2processBeanLists) {
                            // 添加过程值变量;
                            Node node = jep.parse(_process.getExpression());
                            jep.addVariable(_process.getReplaceName(), handlerProcessValues(_process, (Double) jep.evaluate(node)));
                        }
                         */
                        tempValues.get(1).add(chValues[1]);
                    }
                    if (nodes[1] == null) nodes[1] = jep.parse(reCodes[1]);
                    mValues[1] = (double) jep.evaluate(nodes[1]) + mParameterBean.getM2_offect();
                }
                if (reCodes[2] != null && reCodes[2] != null) {
                    if (m3processBeanLists.size() > 0) {
                        /*
                        for (ProcessBean _process : m3processBeanLists) {
                            // 添加过程值变量;
                            Node node = jep.parse(_process.getExpression());
                            jep.addVariable(_process.getReplaceName(), handlerProcessValues(_process, (Double) jep.evaluate(node)));
                        }
                         */
                    }
                    tempValues.get(2).add(chValues[2]);
                    if (nodes[2] == null) nodes[2] = jep.parse(reCodes[2]);
                    mValues[2] = (double) jep.evaluate(nodes[2]) + mParameterBean.getM3_offect();
                }
                if (reCodes[3] != null && reCodes[3] != null) {
                    if (m4processBeanLists.size() > 0) {
                        /*
                        for (ProcessBean _process : m4processBeanLists) {
                            // 添加过程值变量;
                            Node node = jep.parse(_process.getExpression());
                            jep.addVariable(_process.getReplaceName(), handlerProcessValues(_process, (Double) jep.evaluate(node)));
                        }
                         */
                        tempValues.get(3).add(chValues[3]);
                    }
                    if (nodes[3] == null) nodes[3] = jep.parse(reCodes[3]);
                    mValues[3] = (double) jep.evaluate(nodes[3]) + mParameterBean.getM4_offect();
                }
                if (mView != null)
                    mView.onMeasuringDataUpdate(mValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis(); // 获取结束时间
        long stepTime = (endTime - startTime);
        Log.d("wlDebug", "公式计算耗时： " + (endTime - startTime) + "ms");
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
    private double handlerProcessValues2(ProcessBean bean, double value) {

        return 1;
    }
}
