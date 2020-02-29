package alauncher.cn.measuringinstrument.mvp.presenter.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alauncher.cn.measuringinstrument.App;
import alauncher.cn.measuringinstrument.bean.AddInfoBean;
import alauncher.cn.measuringinstrument.bean.CalibrationBean;
import alauncher.cn.measuringinstrument.bean.DeviceInfoBean;
import alauncher.cn.measuringinstrument.bean.GroupBean2;
import alauncher.cn.measuringinstrument.bean.ParameterBean;
import alauncher.cn.measuringinstrument.bean.ParameterBean2;
import alauncher.cn.measuringinstrument.bean.ProcessBean;
import alauncher.cn.measuringinstrument.bean.ResultBean2;
import alauncher.cn.measuringinstrument.bean.StepBean2;
import alauncher.cn.measuringinstrument.bean.StoreBean2;
import alauncher.cn.measuringinstrument.bean.TriggerConditionBean;
import alauncher.cn.measuringinstrument.database.greenDao.db.GroupBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.ParameterBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StepBean2Dao;
import alauncher.cn.measuringinstrument.database.greenDao.db.StoreBean2Dao;
import alauncher.cn.measuringinstrument.mvp.presenter.MeasuringPresenter;
import alauncher.cn.measuringinstrument.utils.Arith;
import alauncher.cn.measuringinstrument.utils.Avg;
import alauncher.cn.measuringinstrument.utils.Dif;
import alauncher.cn.measuringinstrument.utils.JdbcUtil;
import alauncher.cn.measuringinstrument.utils.Max;
import alauncher.cn.measuringinstrument.utils.Min;
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

public class MeasuringPresenterImpl2 implements MeasuringPresenter {

    final String TAG = "MeasuringPresenterImpl2";

    private String sPort = "/dev/ttyMT1";
    private int iBaudRate = 115200;
    private SerialHelper serialHelper;

    private MeasuringActivityView mView;

    private byte[] _chValue = new byte[2];

    // 公式解析;
    private JEP jep = new JEP();
    private JEP calculationJEP = new JEP();

    public CalibrationBean mCalibrationBean;

    private int[] currentCHADValue = {4230, 8241, 12342, 14537};

    // 参数
    private List<ParameterBean2> mParameterBean2Lists;

    // 上公差值;
    private double[] upperValue = new double[4];
    // 下公差值;
    private double[] lowerValue = new double[4];

    // 附加信息;
    private List<StepBean2> stepBeans;

    // 总共有的测量步骤
    public int maxStep;

    // 缓存测量值;
    private double[] tempMs;

    // 重新解析公式;
    private List<String> reCodeList = new ArrayList<>();

    // 重新解析公式，为了个过程值之间的时候，也可以运算;
    private List<String> reCodesForCaluationList = new ArrayList<>();

    // 存储过程中的测量值;
    private List<List<Double>> tempValues = new ArrayList<List<Double>>();

    // 测量状态;
    private int measure_state = 3;


    private int currentStep = -1;

    private List<List<ProcessBean>> processBeanLists = new ArrayList<>();

    private DeviceInfoBean mDeviceInfoBean;

    public boolean[] mGeted;
    //
    public boolean[] inLimited;

    public double[] midValue;

    public StoreBean2 mStoreBean;

    public long lastMeetConditionTime = 0;

    private boolean haveProcessCalculate = false;

    private boolean isGetProcessValue = false;
    // 是单步还是分步取值;
    private boolean isSingleStep = true;

    // ch字的原始值;
    private double[] chValues = new double[4];
    // 用于返回的M值;
    private double[] mValues;
    // 计算Node;
    private Node[] nodes;
    // 分组信息;
    private List<List<GroupBean2>> groupBean2Lists = new ArrayList<>();
    // M值与测量值的键值对;
    private Map<String, Integer> mKeyMap = new HashMap<String, Integer>();

    private final int MSG_AUTO_STORE = 1;

    private boolean isPaused = false;

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

    public MeasuringPresenterImpl2(MeasuringActivityView view) {
        mView = view;
        jep.addFunction("Max", new Max());
        jep.addFunction("Min", new Min());
        jep.addFunction("Avg", new Avg());
        jep.addFunction("Dif", new Dif());
        calculationJEP.addFunction("Max", new Max());
        calculationJEP.addFunction("Min", new Min());
        calculationJEP.addFunction("Avg", new Avg());
        calculationJEP.addFunction("Dif", new Dif());
        for (int i = 0; i < 4; i++) {
            tempValues.add(new ArrayList<>());
        }
        mDeviceInfoBean = App.getDeviceInfo();
        initParameter();
    }

    public void initParameter() {
        stopAutoStore();
        // 初始化过程值的List;
        reCodeList.clear();
        reCodesForCaluationList.clear();
        processBeanLists.clear();
        groupBean2Lists.clear();
        mKeyMap.clear();
        // 获取参数列表;
        mParameterBean2Lists = App.getDaoSession().getParameterBean2Dao().queryBuilder()
                .where(ParameterBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID()), ParameterBean2Dao.Properties.Enable.eq(true))
                .orderAsc(ParameterBean2Dao.Properties.SequenceNumber).list();

        for (int i = 0; i < mParameterBean2Lists.size(); i++) {
            groupBean2Lists.add(App.getDaoSession().getGroupBean2Dao().queryBuilder().where(GroupBean2Dao.Properties.PID.eq(mParameterBean2Lists.get(i).getId())).list());
            mKeyMap.put(String.valueOf(mParameterBean2Lists.get(i).getSequenceNumber()), i);
        }

        mValues = new double[mParameterBean2Lists.size()];
        nodes = new Node[mParameterBean2Lists.size()];
        mGeted = new boolean[mParameterBean2Lists.size()];
        inLimited = new boolean[mParameterBean2Lists.size()];
        tempMs = new double[mParameterBean2Lists.size()];
        midValue = new double[mParameterBean2Lists.size()];

        for (int i = 0; i < mParameterBean2Lists.size(); i++) {
            processBeanLists.add(new ArrayList<>());
            midValue[i] = (mParameterBean2Lists.get(i).getLowerToleranceValue() + mParameterBean2Lists.get(i).getNominalValue()
                    + mParameterBean2Lists.get(i).getUpperToleranceValue() + mParameterBean2Lists.get(i).getNominalValue()) / 2;
        }

        mStoreBean = App.getDaoSession().getStoreBean2Dao()
                .queryBuilder().where(StoreBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).unique();

        if (mParameterBean2Lists.size() > 0) {
            // 计算上下公差值;

            // 解析公式，提取过程值;
            String _regx = "L.*?\\)";
            Pattern p = Pattern.compile(_regx);

            String _reCgx = "L.*?\\(";
            Pattern _reCPattern = Pattern.compile(_reCgx);

            for (int i = 0; i < mParameterBean2Lists.size(); i++) {
                String _reCode = mParameterBean2Lists.get(i).getCode();
                Matcher matcher = p.matcher(_reCode);
                while (matcher.find()) {
                    ProcessBean _process = new ProcessBean("x" + processBeanLists.get(0).size(),
                            _reCode.substring(matcher.start() + 5, matcher.end() - 1), _reCode.substring(matcher.start(), matcher.start() + 4));
                    processBeanLists.get(i).add(_process);
                    _reCode = _reCode.replace(_process.getExpressionType() + "(" + _process.getExpression() + ")", _process.getReplaceName());
                }
                reCodeList.add(_reCode);

                // 用于计算过程值，实时显示的公式;
                String _reCodesForCalculate = mParameterBean2Lists.get(i).getCode();
                matcher = _reCPattern.matcher(_reCodesForCalculate);
                while (matcher.find()) {
                    _reCodesForCalculate = _reCodesForCalculate.replace(_reCodesForCalculate.substring(matcher.start(), matcher.end() - 1), "");
                }
                reCodesForCaluationList.add(_reCodesForCalculate);
            }
        }

        mCalibrationBean = App.getDaoSession().getCalibrationBeanDao().load((long) App.getSetupBean().getCodeID());
        if (mCalibrationBean != null) {
            Log.d(TAG, mCalibrationBean.toString());
        }

        stepBeans = App.getDaoSession().
                getStepBean2Dao().queryBuilder().where(StepBean2Dao.Properties.CodeID.eq(App.getSetupBean().getCodeID())).orderAsc(StepBean2Dao.Properties.SequenceNumber).list();

        android.util.Log.d("wlDebug", "" + stepBeans.toString());

        maxStep = stepBeans.size();
        if (maxStep > 0)
            currentStep = 0;
        else
            currentStep = -1;

        // 判断是否设置分步测量;
        isSingleStep = true;
        if (maxStep > 0 && mStoreBean.getStoreMode() == 1) {
            isSingleStep = false;
        }

        // 判断是否有过程值存在;
        haveProcessCalculate = false;
        for (List<ProcessBean> list : processBeanLists) {
            if (list.size() > 0) {
                haveProcessCalculate = true;
            }
        }

        android.util.Log.d("wlDebug", "processBeanLists = " + processBeanLists.toString());
        android.util.Log.d("wlDebug", "mKeyMap = " + mKeyMap.toString());
        if (isSingleStep) {
            // 没有分步,判断是否有过程值与单点值同时存在的情况；
            if (haveProcessCalculate) {
                for (List<ProcessBean> _processBeanList : processBeanLists) {
                    if (_processBeanList.size() == 0) {
                        mView.showUnSupportDialog(-1);
                        return;
                    }
                }
            }
            mView.setValueBtnVisible(false);
        } else {
            // 有分步，要判断出具体哪一步有过程值和单点值同时存在;
            for (int i = 0; i < stepBeans.size(); i++) {
                StepBean2 _bean = stepBeans.get(i);
                boolean isFirst = true, temp = true;
                for (int j = 0; j < _bean.getMeasureItems().size(); j++) {
                    String index = _bean.getMeasureItems().get(j);
                    if (isFirst) {
                        isFirst = false;
                        temp = processBeanLists.get(mKeyMap.get(index)).size() > 0;
                    } else {
                        if (temp ^ (processBeanLists.get(mKeyMap.get(index)).size() > 0)) {
                            // 异或，存在单点与过程同在的情况;
                            mView.showUnSupportDialog(i);
                            return;
                        }
                    }
                }
            }
        }
        measure_state = MeasuringPresenter.NORMAL_NODE;
        mView.updateSaveBtnMsg();
        startAutoStore();
    }

    private long lastValueTime = 0;
    // 记录上一次的value值，不刷新界面;
    private String lastValue = "";
    //
    private String[] values = new String[4];

    @Override
    public void startMeasuring() {
        Log.d("wlDebug", "startMeasuring.");
        if (serialHelper == null) {
            serialHelper = new SerialHelper(sPort, iBaudRate) {
                @Override
                protected void onDataReceived(ComBean paramComBean) {
                    if (isPaused) return;
                    // 重新解析Byte;
                    if (paramComBean.bRec[0] == 0x53 && paramComBean.bRec[11] == 0x54) {

                        long currentTime = System.currentTimeMillis();
                        if (lastValueTime != 0) {
                            long stepTime = (currentTime - lastValueTime);
                            Log.d("wlDebug", "_value = " + lastValue + " last time:" + stepTime + "ms");
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
        String[] _values = {"500", "950", "980", "1000"};
        String[] _values2 = {"3036", "3036", "13036", "3036"};
        String[] _values3 = {"13036", "2036", "13036", "13036"};
        String[] _values4 = {"2036", "2036", "2036", "2036"};

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mView.onMeasuringDataUpdate(doCH2P(_values));
                    Thread.sleep(2000);
                    mView.onMeasuringDataUpdate(doCH2P(_values));
                    Thread.sleep(2000);
                    mView.onMeasuringDataUpdate(doCH2P(_values));
                    Thread.sleep(2000);
                    mView.onMeasuringDataUpdate(doCH2P(_values));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */
        forValueTest();
    }

    // 5301 1086 2031 3036 38C9 4E54
    @Override
    public void stopMeasuring() {
        // Log.d("wlDebug", "stopMeasuring.");
        stopAutoStore();
        if (serialHelper != null && serialHelper.isOpen()) {
            serialHelper.close();
        }
    }

    @Override
    public ParameterBean getParameterBean() {
        return null;
    }

    @Override
    public String saveResult(double[] ms, AddInfoBean bean, boolean isManual) {

        // android.util.Log.d("wlDebug", Arrays.toString(ms));
        if (ms == null) {
            return "- -";
        }

        // 如果是手动点击的保存，需要计算，具体第几个测量参数是过程值，就取过程值;
        if (isManual) {
            for (int i = 0; i < processBeanLists.size(); i++) {
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
            StepBean2 _bean = stepBeans.get(getStep());
            TriggerConditionBean _TriggerConditionBean = App.getDaoSession().getTriggerConditionBeanDao().load(_bean.getConditionID());
            if (_TriggerConditionBean != null) {
                int mIndex = mKeyMap.get(String.valueOf(_TriggerConditionBean.getMIndex()));
                if (_TriggerConditionBean.getIsScale()) {
                    // 增加的部分，公差带 * scale / 2
                    double m = (Math.abs(mParameterBean2Lists.get(mIndex)
                            .getUpperToleranceValue() - mParameterBean2Lists.get(mIndex).getLowerToleranceValue()) * _TriggerConditionBean.getScale()) / 2;

                    // 计算公差中值;
                    double scaleUpperLimit = midValue[mIndex] + m;
                    double scaleLowerLimit = midValue[mIndex] - m;

                    Log.d("wlDebug", "scaleUpperLimit = " + scaleUpperLimit + " scaleLowerLimit = " + scaleLowerLimit + " M = " + ms[mIndex]);
                    if (ms[mIndex] < scaleLowerLimit || ms[mIndex] > scaleUpperLimit) {
                        lastMeetConditionTime = -1;
                        inLimited[mIndex] = false;
                        Log.d("wlDebug", "不在范围内.");
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
                Log.d("wlDebug", "currentTime = " + currentTime + " lastMeetConditionTime = " + lastMeetConditionTime + " Stable = " + _TriggerConditionBean.getStableTime() * 1000);

                if (currentTime - lastMeetConditionTime > _TriggerConditionBean.getStableTime() * 1000) {

                } else {
                    Log.d("wlDebug", "持续时间未到.");
                    return "NoSave;";
                }
                inLimited[mIndex] = true;

            } else if (isManual == false) { // 如果不是手动点击保存，不保存；
                return "NoSave;";
            } else {

            }

            // 当前步序测量所得值;
            /*
            for (int i = 0; i < ms.length; i++) {
                if (StepUtils.getChannelByStep(i, _bean.getMeasured())) {
                    Log.d("wlDebug", "获取第" + i + "值:" + ms[i]);
                    lastMeetConditionTime = -1;
                    tempMs[i] = ms[i];
                    mGeted[i] = true;
                }
            }
            */
            for (int i = 0; i < _bean.getMeasureItems().size(); i++) {
                tempMs[mKeyMap.get(_bean.getMeasureItems().get(i))] = ms[mKeyMap.get(_bean.getMeasureItems().get(i))];
                mGeted[mKeyMap.get(_bean.getMeasureItems().get(i))] = true;
            }
            android.util.Log.d("wlDebug", "tempMs = " + Arrays.toString(tempMs));
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
        ResultBean2 _bean = new ResultBean2();
        _bean.setHandlerAccount(App.handlerAccout);
        _bean.setCodeID(App.getSetupBean().getCodeID());
        _bean.setMeasurementValues(new ArrayList<>());
        _bean.setMItems(new ArrayList<>());
        _bean.setMDescribe(new ArrayList<>());

        for (int i = 0; i < ms.length; i++) {
            _bean.getMeasurementValues().add(String.valueOf(ms[i]));
            _bean.getMItems().add("" + (mParameterBean2Lists.get(i).getSequenceNumber() + 1));
            _bean.getMDescribe().add(mParameterBean2Lists.get(i).getDescribe());
        }
        _bean.setMeasurementGroup(Arrays.asList(getMGroupValues(ms)));

        if (mParameterBean2Lists.size() > 0) {
            _bean.setResult(getMResults(ms));
        } else {
            _bean.setResult("- -");
        }

        _bean.setTimeStamp(System.currentTimeMillis());
        if (bean != null) {
            _bean.setEvent(bean.getEvent());
            _bean.setWorkID(bean.getWorkid());
        } else {
            _bean.setEvent("- -");
            _bean.setWorkID("- -");
        }
        Log.d("wlDebug", "save bean = " + _bean.toString());
        App.getDaoSession().getResultBean2Dao().insert(_bean);
        toSQLServer(_bean);
    }

    private void toSQLServer(final ResultBean2 _bean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int r = JdbcUtil.addResult2(mDeviceInfoBean.getFactoryCode(), mDeviceInfoBean.getDeviceCode(), App.getSetupBean().getCodeID(), "", _bean);
                    // 上传成功;
                    if (r > 0) {
                        _bean.setIsUploaded(true);
                        App.getDaoSession().getResultBean2Dao().insertOrReplace(_bean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public String[] getMGroupValues(double[] ms) {
        String[] result = new String[ms.length];
        try {
            for (int i = 0; i < ms.length; i++) {
                List<GroupBean2> _groupList = groupBean2Lists.get(i);
                if (_groupList == null || _groupList.size() == 0) {
                    result[i] = "- -";
                } else {
                    result[i] = "- -";
                    for (GroupBean2 _bean : _groupList) {
                        if (ms[i] > _bean.getLowerLimit() && ms[i] < _bean.getUpperLimit()) {
                            result[i] = _bean.getName();
                            break;
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return result;
    }

    private void nextStep() {
        if (currentStep < maxStep - 1) {
            currentStep++;
        } else {
            currentStep = 0;
        }
        measure_state = MeasuringPresenter.NORMAL_NODE;
        // Log.d("wlDebug", "Measured = " + Integer.toBinaryString(stepBeans.get(getStep()).getMeasured()));
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
        measure_state = MeasuringPresenter.IN_PROCESS_VALUE_MODE;
        mView.updateSaveBtnMsg();
    }

    @Override
    public void stopGetProcessValue() {
        isGetProcessValue = false;
        measure_state = MeasuringPresenter.IN_PROCESS_VALUE_BEEN_TAKEN_MODE;
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

    public StepBean2 getStepBean() {
        if (stepBeans.size() > 0) {
            StepBean2 _bean = stepBeans.get(currentStep);
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
        if (mParameterBean2Lists.size() == 0) {
            return "- -";
        }
        // 所有测量值正常才为OK;
        for (int i = 0; i < ms.length; i++) {
            ParameterBean2 _bean = mParameterBean2Lists.get(i);
            if ((ms[i] > (_bean.getNominalValue() + _bean.getUpperToleranceValue()) || ms[i] < (_bean.getNominalValue() + _bean.getLowerToleranceValue()))) {
                return "NG";
            }
        }
        return "OK";
    }

    /*
     *
     *
     *
     * */
    @Override
    public String[] getResults(double[] ms) {
        String[] result = new String[ms.length];
        for (int i = 0; i < ms.length; i++) {
            ParameterBean2 _bean = mParameterBean2Lists.get(i);
            if ((ms[i] > (_bean.getNominalValue() + _bean.getUpperToleranceValue()) || ms[i] < (_bean.getNominalValue() + _bean.getLowerToleranceValue()))) {
                result[i] = "NG";
            } else {
                result[i] = "OK";
            }
        }
        return result;
    }

    @Override
    public boolean[] getGeted() {
        return mGeted;
    }

    @Override
    public void setPause(boolean isPause) {
        isPaused = isPause;
    }

    /*
     *
     *   将读出来的AD字，通过校准，转化为校准后的测量值;
     *
     * */

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

        if (mParameterBean2Lists.size() > 0) {
            try {
                jep.addVariable("ch1", chValues[0]);
                jep.addVariable("ch2", chValues[1]);
                jep.addVariable("ch3", chValues[2]);
                jep.addVariable("ch4", chValues[3]);

                for (int i = 0; i < mParameterBean2Lists.size(); i++) {
                    if (reCodeList.get(i) != null && !reCodeList.get(i).equals("")) {
                        if (processBeanLists.get(i).size() > 0) {
                            Node node = jep.parse(reCodesForCaluationList.get(i));
                            mValues[i] = Arith.round((double) jep.evaluate(node) + mParameterBean2Lists.get(i).getDeviation(), 4);
                        } else {
                            if (nodes[i] == null) nodes[i] = jep.parse(reCodeList.get(i));
                            mValues[i] = Arith.round((double) jep.evaluate(nodes[i]) + mParameterBean2Lists.get(i).getDeviation(), 4);
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

        if (isGetProcessValue && haveProcessCalculate && tempValues.get(0).size() <= 5000) {
            for (int i = 0; i < 4; i++) {
                tempValues.get(i).add(chValues[i]);
            }
        }

        if (mParameterBean2Lists.size() > 0) {
            try {
                jep.addVariable("ch1", chValues[0]);
                jep.addVariable("ch2", chValues[1]);
                jep.addVariable("ch3", chValues[2]);
                jep.addVariable("ch4", chValues[3]);
                for (int i = 0; i < mParameterBean2Lists.size(); i++) {
                    if (reCodeList.get(i) != null && !reCodeList.get(i).equals("")) {
                        if (processBeanLists.get(i).size() > 0) {
                            Node node = jep.parse(reCodesForCaluationList.get(i));
                            mValues[i] = Arith.round((double) jep.evaluate(node) + mParameterBean2Lists.get(i).getDeviation(), 4);
                        } else {
                            if (nodes[i] == null) nodes[i] = jep.parse(reCodeList.get(i));
                            mValues[i] = Arith.round((double) jep.evaluate(nodes[i]) + mParameterBean2Lists.get(i).getDeviation(), 4);
                        }
                    }
                }
                // android.util.Log.d("wlDebug", "mValues = " + Arrays.toString(mValues));
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
                node = calculationJEP.parse(_process.getExpression());
                calculationValuesList.add((Double) calculationJEP.evaluate(node));
            }
            Collections.sort(calculationValuesList);
            Log.d("wlDebug", "reList = " + calculationValuesList.toString());
            jep.addVariable(_process.getReplaceName(), calculationProcess(_process, calculationValuesList));
        }
        if (nodes[index] == null) nodes[index] = jep.parse(reCodeList.get(index));
        return (double) jep.evaluate(nodes[index]) + mParameterBean2Lists.get(index).getDeviation();
    }

    private double calculationProcess(ProcessBean bean, List<Double> values) {
        if (values.size() == 0) return 0;
        double sum = 0D;
        int num = 0;
        int percent_10, percent_20, percent_80, percent_90;
        double result = 0;
        switch (bean.getExpressionType()) {
            case "LMax":
                percent_80 = (int) Math.round(values.size() * 0.8) - 1;
                percent_90 = (int) Math.round(values.size() * 0.9) - 1;

                if (percent_80 < 0) percent_80 = 0;
                for (int i = percent_80; i <= percent_90; i++, num++) {
                    sum += values.get(i);
                }
                result = BigDecimal.valueOf(sum / num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
            case "LMin":
                percent_10 = (int) Math.round(values.size() * 0.1) - 1;
                percent_20 = (int) Math.round(values.size() * 0.2) - 1;

                if (percent_10 < 0) percent_10 = 0;
                if (percent_20 < 0) percent_20 = 0;
                for (int i = percent_10; i <= percent_20; i++, num++) {
                    sum += values.get(i);
                }
                result = BigDecimal.valueOf(sum / num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
            case "LAvg":
                percent_10 = (int) Math.round(values.size() * 0.1) - 1;
                percent_90 = (int) Math.round(values.size() * 0.9) - 1;
                if (percent_10 < 0) percent_10 = 0;
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
                if (percent_10 < 0) percent_10 = 0;
                if (percent_20 < 0) percent_20 = 0;
                if (percent_80 < 0) percent_80 = 0;
                if (percent_90 < 0) percent_90 = 0;
                Log.d("wlDebug", "percent_10 = " + percent_10);
                Log.d("wlDebug", "percent_20 = " + percent_20);
                Log.d("wlDebug", "percent_80 = " + percent_80);
                Log.d("wlDebug", "percent_90 = " + percent_90);
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
        Log.d("wlDebug", bean.getExpressionType() + " = " + result);
        return result;
    }

    // 判断当前步骤是否有过程值运算;
    @Override
    public boolean isCurStepHaveProcess() {
        if (isSingleStep) {
            return haveProcessCalculate ? true : false;
        } else {
            StepBean2 _bean = stepBeans.get(getStep());
            for (int j = 0; j < mParameterBean2Lists.size(); j++) {
                if (_bean.getMeasureItems().contains(String.valueOf(mParameterBean2Lists.get(j).getSequenceNumber()))) {
                    if (processBeanLists.get(j).size() > 0) return true;
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
            handler.sendEmptyMessageDelayed(MSG_AUTO_STORE, 1 * 1000);
        }
    }

    private void doAutoStore() {
        mView.toDoSave(false);
        handler.sendEmptyMessageDelayed(MSG_AUTO_STORE, 1 * 1000);
    }

    private void stopAutoStore() {
        handler.removeMessages(MSG_AUTO_STORE);
    }


    private void forValueTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{30, 30, 30, 30}));
                    Thread.sleep(2000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.806335611, 0, 0, 0}));


                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.880121264, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.201054649, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.825012437, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.604557736, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.064053735, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.416952491, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.616241929, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.832199558, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.40698447, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.765952038, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.346109684, 0, 0, 0}));
                    Thread.sleep(1000);
                    mView.onMeasuringDataUpdate(doCH2PTest(new double[]{0.603089989, 0, 0, 0}));
                    Thread.sleep(1000);
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
