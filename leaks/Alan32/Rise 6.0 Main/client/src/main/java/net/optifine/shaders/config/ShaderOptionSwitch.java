package net.optifine.shaders.config;

import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.util.StrUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderOptionSwitch extends ShaderOption {
    private static final Pattern PATTERN_DEFINE = Pattern.compile("^\\s*(//)?\\s*#define\\s+([A-Za-z0-9_]+)\\s*(//.*)?$");
    private static final Pattern PATTERN_IFDEF = Pattern.compile("^\\s*#if(n)?def\\s+([A-Za-z0-9_]+)(\\s*)?$");

    public ShaderOptionSwitch(final String name, final String description, final String value, final String path) {
        super(name, description, value, new String[]{"false", "true"}, value, path);
    }

    public String getSourceLine() {
        return isTrue(this.getValue()) ? "#define " + this.getName() + " // Shader option ON" : "//#define " + this.getName() + " // Shader option OFF";
    }

    public String getValueText(final String val) {
        final String s = super.getValueText(val);
        return s != val ? s : (isTrue(val) ? Lang.getOn() : Lang.getOff());
    }

    public String getValueColor(final String val) {
        return isTrue(val) ? "\u00a7a" : "\u00a7c";
    }

    public static ShaderOption parseOption(final String line, String path) {
        final Matcher matcher = PATTERN_DEFINE.matcher(line);

        if (!matcher.matches()) {
            return null;
        } else {
            final String s = matcher.group(1);
            final String s1 = matcher.group(2);
            final String s2 = matcher.group(3);

            if (s1 != null && s1.length() > 0) {
                final boolean flag = Config.equals(s, "//");
                final boolean flag1 = !flag;
                path = StrUtils.removePrefix(path, "/shaders/");
                final ShaderOption shaderoption = new ShaderOptionSwitch(s1, s2, String.valueOf(flag1), path);
                return shaderoption;
            } else {
                return null;
            }
        }
    }

    public boolean matchesLine(final String line) {
        final Matcher matcher = PATTERN_DEFINE.matcher(line);

        if (!matcher.matches()) {
            return false;
        } else {
            final String s = matcher.group(2);
            return s.matches(this.getName());
        }
    }

    public boolean checkUsed() {
        return true;
    }

    public boolean isUsedInLine(final String line) {
        final Matcher matcher = PATTERN_IFDEF.matcher(line);

        if (matcher.matches()) {
            final String s = matcher.group(2);

            return s.equals(this.getName());
        }

        return false;
    }

    public static boolean isTrue(final String val) {
        return Boolean.valueOf(val).booleanValue();
    }
}
