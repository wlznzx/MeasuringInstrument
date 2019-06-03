package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class CalibrationBean {
    @Id
    public long code_id;
    // 校验方式;
    int ch1CalibrationType;
    int ch2CalibrationType;
    int ch3CalibrationType;
    int ch4CalibrationType;
    // 小件标准;
    double ch1SmallPartStandard;
    double ch2SmallPartStandard;
    double ch3SmallPartStandard;
    double ch4SmallPartStandard;
    // 大件标准;
    double ch1BigPartStandard;
    double ch2BigPartStandard;
    double ch3BigPartStandard;
    double ch4BigPartStandard;
    // 倍率上限;
    double ch1UpperLimitRate;
    double ch2UpperLimitRate;
    double ch3UpperLimitRate;
    double ch4UpperLimitRate;
    // 倍率下限;
    double ch1LowerLimitRate;
    double ch2LowerLimitRate;
    double ch3LowerLimitRate;
    double ch4LowerLimitRate;
    // 补偿值
    double ch1CompensationValue;
    double ch2CompensationValue;
    double ch3CompensationValue;
    double ch4CompensationValue;

}
