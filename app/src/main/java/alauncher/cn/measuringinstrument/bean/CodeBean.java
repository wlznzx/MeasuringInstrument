package alauncher.cn.measuringinstrument.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 日期：2019/8/5 0025 9:27
 * 包名：alauncher.cn.measuringinstrument.bean
 * 作者： wlznzx
 * 描述：
 */
@Entity
public class CodeBean {

    @Id
    public long codeID;

    public String name;

    public String machineTool;

    public String parts;

    public boolean isEnableStep;

    public byte[] workpiecePic;

    @Generated(hash = 912941288)
    public CodeBean(long codeID, String name, String machineTool, String parts,
            boolean isEnableStep, byte[] workpiecePic) {
        this.codeID = codeID;
        this.name = name;
        this.machineTool = machineTool;
        this.parts = parts;
        this.isEnableStep = isEnableStep;
        this.workpiecePic = workpiecePic;
    }

    @Generated(hash = 544591002)
    public CodeBean() {
    }

    public long getCodeID() {
        return this.codeID;
    }

    public void setCodeID(long codeID) {
        this.codeID = codeID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMachineTool() {
        return this.machineTool;
    }

    public void setMachineTool(String machineTool) {
        this.machineTool = machineTool;
    }

    public String getParts() {
        return this.parts;
    }

    public void setParts(String parts) {
        this.parts = parts;
    }

    public boolean getIsEnableStep() {
        return this.isEnableStep;
    }

    public void setIsEnableStep(boolean isEnableStep) {
        this.isEnableStep = isEnableStep;
    }

    public byte[] getWorkpiecePic() {
        return this.workpiecePic;
    }

    public void setWorkpiecePic(byte[] workpiecePic) {
        this.workpiecePic = workpiecePic;
    }
}
