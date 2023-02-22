package net.optifine.shaders.config;

import net.optifine.util.StrUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderOptionVariableConst extends ShaderOptionVariable {
    private String type = null;
    private static final Pattern PATTERN_CONST = Pattern.compile("^\\s*const\\s*(float|int)\\s*([A-Za-z0-9_]+)\\s*=\\s*(-?[0-9\\.]+f?F?)\\s*;\\s*(//.*)?$");

    public ShaderOptionVariableConst(final String name, final String type, final String description, final String value, final String[] values, final String path) {
        super(name, description, value, values, path);
        this.type = type;
    }

    public String getSourceLine() {
        return "const " + this.type + " " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
    }

    public boolean matchesLine(final String line) {
        final Matcher matcher = PATTERN_CONST.matcher(line);

        if (!matcher.matches()) {
            return false;
        } else {
            final String s = matcher.group(2);
            return s.matches(this.getName());
        }
    }

    public static ShaderOption parseOption(final String line, String path) {
        final Matcher matcher = PATTERN_CONST.matcher(line);

        if (!matcher.matches()) {
            return null;
        } else {
            final String s = matcher.group(1);
            final String s1 = matcher.group(2);
            final String s2 = matcher.group(3);
            String s3 = matcher.group(4);
            final String s4 = StrUtils.getSegment(s3, "[", "]");

            if (s4 != null && s4.length() > 0) {
                s3 = s3.replace(s4, "").trim();
            }

            final String[] astring = parseValues(s2, s4);

            if (s1 != null && s1.length() > 0) {
                path = StrUtils.removePrefix(path, "/shaders/");
                final ShaderOption shaderoption = new ShaderOptionVariableConst(s1, s, s3, s2, astring, path);
                return shaderoption;
            } else {
                return null;
            }
        }
    }
}
