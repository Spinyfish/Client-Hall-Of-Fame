package igbt.astolfy.utils;

import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;

public class PotionUtils {
	public static String getAmplifierString(int amplifier) {
		if(amplifier <= 10 && amplifier > 0 ) {
			return I18n.format("enchantment.level." + amplifier, new Object[0]);
		}
		return "";
	}
}
