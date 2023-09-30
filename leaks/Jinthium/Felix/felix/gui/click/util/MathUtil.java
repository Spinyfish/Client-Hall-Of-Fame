package felix.gui.click.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at 11.11.2020. 
 *         Use is only authorized if given credit!
 * 
 */
public class MathUtil {

	/**
	 * @param toRound
	 * @param scale
	 * @return
	 */
	public static double round(double value, int places) {
	    if (places < 0)
	      throw new IllegalArgumentException(); 
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	  }
}
