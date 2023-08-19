/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 17:07
 */

package cc.swift.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class MathUtil {

    public static double round(double val, double inc) {
        double v = Math.round(val / inc) * inc;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(10, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
