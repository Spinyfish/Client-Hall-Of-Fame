package net.optifine.shaders.config;

import net.optifine.util.StrUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderOptionSwitchConst extends ShaderOptionSwitch {
    private static final Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*bool\\s*([A-Za-z0-9_]+)\\s*=\\s*(true|false)\\s*;\\s*(//.*)?$");

    public ShaderOptionSwitchConst(final String name, final String description, final String value, final String path) {
        super(name, description, value, path);
    }

    public String getSourceLine() {
        return "const bool " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
    }

    public static ShaderOption parseOption(final String line, String path) {
        final Matcher matcher = PATTERN_CONST.matcher(line);

        if (!matcher.matches()) {
            return null;
        } else {
            final String s = matcher.group(1);
            final String s1 = matcher.group(2);
            final String s2 = matcher.group(3);

            if (s != null && s.length() > 0) {
                path = StrUtils.removePrefix(path, "/shaders/");
                final ShaderOption shaderoption = new ShaderOptionSwitchConst(s, s2, s1, path);
                shaderoption.setVisible(false);
                return shaderoption;
            } else {
                return null;
            }
        }
    }

    public boolean matchesLine(final String line) {
        final Matcher matcher = PATTERN_CONST.matcher(line);

        if (!matcher.matches()) {
            return false;
        } else {
            final String s = matcher.group(1);
            return s.matches(this.getName());
        }
    }

    public boolean checkUsed() {
        return false;
    }
}
