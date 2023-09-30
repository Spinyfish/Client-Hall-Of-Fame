package frapppyz.cutefurry.pics.util;

import java.math.BigDecimal;

public class MathUtil {

    public static double round(double value, double dec){
        return new BigDecimal(Math.ceil(value/dec)*dec).doubleValue();

    }

    public static double square(double value){
        return value*=value;
    }
}
