package org.crystalpvp.anticheat.api.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class NumberUtil {

	private static final NavigableMap<Long, String> SUFFIXES = new TreeMap<>();

	static {
		SUFFIXES.put(1_000L, "k");
		SUFFIXES.put(1_000_000L, "M");
		SUFFIXES.put(1_000_000_000L, "B");
		SUFFIXES.put(1_000_000_000_000L, "T");
		SUFFIXES.put(1_000_000_000_000L, "Q");
		SUFFIXES.put(1_000_000_000_000_000L, "QT");
	}

	public static int getRandomRange(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	public static float getRandomRange() {
		return ThreadLocalRandom.current().nextFloat();
	}

	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isShort(String input) {
		try {
			Short.parseShort(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isEven(int number) {
		return number % 2 == 0;
	}

	public static String convertAbbreviated(long value) {
		if (value == Long.MIN_VALUE) {
			return convertAbbreviated(Long.MIN_VALUE + 1);
		}

		if (value < 0) {
			return "-" + convertAbbreviated(-value);
		}

		if (value < 1000) {
			return Long.toString(value);
		}

		Map.Entry<Long, String> e = SUFFIXES.floorEntry(value);
		Long divideBy = e.getKey();
		String suffix = e.getValue();

		long truncated = value / (divideBy / 10);
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);

		return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
	}

	public static String formatNumber(long value) {
		return NumberFormat.getInstance(Locale.US).format(value);
	}

}
