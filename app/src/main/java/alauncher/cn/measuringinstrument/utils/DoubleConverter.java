package alauncher.cn.measuringinstrument.utils;

import org.greenrobot.greendao.converter.PropertyConverter;
import java.util.List;

public class DoubleConverter implements PropertyConverter<List<Double>, Double> {

    @Override
    public List<Double> convertToEntityProperty(Double databaseValue) {
        return null;
    }

    @Override
    public Double convertToDatabaseValue(List<Double> entityProperty) {
        return null;
    }

}
