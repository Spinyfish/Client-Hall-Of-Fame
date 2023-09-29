package net.minecraft.util;

public class StatCollector {
    private static final StringTranslate localizedName = StringTranslate.getInstance();

    /**
     * A StringTranslate instance using the hardcoded default locale (en_US).  Used as a fallback in case the shared
     * StringTranslate singleton instance fails to translate a key.
     */
    private static final StringTranslate fallbackTranslator = new StringTranslate();

    /**
     * Translates a Stat name
     */
    public static String translateToLocal(final String key) {
        return localizedName.translateKey(key);
    }

    /**
     * Translates a Stat name with format args
     */
    public static String translateToLocalFormatted(final String key, final Object... format) {
        return localizedName.translateKeyFormat(key, format);
    }

    /**
     * Translates a Stat name using the fallback (hardcoded en_US) locale.  Looks like it's only intended to be used if
     * translateToLocal fails.
     */
    public static String translateToFallback(final String key) {
        return fallbackTranslator.translateKey(key);
    }

    /**
     * Determines whether or not translateToLocal will find a translation for the given key.
     */
    public static boolean canTranslate(final String key) {
        return localizedName.isKeyTranslated(key);
    }

    /**
     * Gets the time, in milliseconds since epoch, that the translation mapping was last updated
     */
    public static long getLastTranslationUpdateTimeInMilliseconds() {
        return localizedName.getLastUpdateTimeInMilliseconds();
    }
}
