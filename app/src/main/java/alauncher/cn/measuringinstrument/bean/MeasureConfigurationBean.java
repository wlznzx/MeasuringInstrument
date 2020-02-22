package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MeasureConfigurationBean {

    @Id(autoincrement = true)
    public Long id;

    public long code_id;

    public int measureMode;

    public boolean isShowChart;

    @Generated(hash = 1576941849)
    public MeasureConfigurationBean(Long id, long code_id, int measureMode,
            boolean isShowChart) {
        this.id = id;
        this.code_id = code_id;
        this.measureMode = measureMode;
        this.isShowChart = isShowChart;
    }

    @Generated(hash = 688825423)
    public MeasureConfigurationBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMeasureMode() {
        return this.measureMode;
    }

    public void setMeasureMode(int measureMode) {
        this.measureMode = measureMode;
    }

    public long getCode_id() {
        return this.code_id;
    }

    public void setCode_id(long code_id) {
        this.code_id = code_id;
    }

    public boolean getIsShowChart() {
        return this.isShowChart;
    }

    public void setIsShowChart(boolean isShowChart) {
        this.isShowChart = isShowChart;
    }

}
