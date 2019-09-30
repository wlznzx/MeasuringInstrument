package alauncher.cn.measuringinstrument.utils;

public class StepUtils {

    public static boolean getChannelByStep(int channel, int value) {
        if (((1 << channel) & value) > 0) {
            return true;
        }
        return false;
    }

    public static int setChannel(int channel, int baseValue, boolean isEnable) {
        if (isEnable) {
            int _value = 1 << channel;
            baseValue = baseValue | _value;
        } else {
            int _value = ~(1 << channel);
            baseValue = baseValue & _value;
        }
        return baseValue;
    }

}
