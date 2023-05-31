package shadersmod.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderParser
{
    public static Pattern PATTERN_UNIFORM = Pattern.compile("\\s*uniform\\s+\\w+\\s+(\\w+).*");
    public static Pattern PATTERN_COMMENT = Pattern.compile("\\s*/\\*\\s+([A-Z]+):(\\S+)\\s+\\*/.*");
    public static Pattern PATTERN_CONST_INT = Pattern.compile("\\s*const\\s+int\\s+(\\w+)\\s*=\\s*([-+.\\w]+)\\s*;.*");
    public static Pattern PATTERN_CONST_FLOAT = Pattern.compile("\\s*const\\s+float\\s+(\\w+)\\s*=\\s*([-+.\\w]+)\\s*;.*");
    public static Pattern PATTERN_CONST_BOOL = Pattern.compile("\\s*const\\s+bool\\s+(\\w+)\\s*=\\s*(\\w+)\\s*;.*");
    public static Pattern PATTERN_COMPOSITE_FSH = Pattern.compile(".*composite[0-9]?\\.fsh");
    public static Pattern PATTERN_FINAL_FSH = Pattern.compile(".*final\\.fsh");
    public static Pattern PATTERN_DRAW_BUFFERS = Pattern.compile("[0-7N]*");

    public static ShaderLine parseLine(String line)
    {
        Matcher matcher = PATTERN_UNIFORM.matcher(line);

        if (matcher.matches())
        {
            return new ShaderLine(1, matcher.group(1), "", line);
        }
        else
        {
            Matcher matcher1 = PATTERN_COMMENT.matcher(line);

            if (matcher1.matches())
            {
                return new ShaderLine(2, matcher1.group(1), matcher1.group(2), line);
            }
            else
            {
                Matcher matcher2 = PATTERN_CONST_INT.matcher(line);

                if (matcher2.matches())
                {
                    return new ShaderLine(3, matcher2.group(1), matcher2.group(2), line);
                }
                else
                {
                    Matcher matcher3 = PATTERN_CONST_FLOAT.matcher(line);

                    if (matcher3.matches())
                    {
                        return new ShaderLine(4, matcher3.group(1), matcher3.group(2), line);
                    }
                    else
                    {
                        Matcher matcher4 = PATTERN_CONST_BOOL.matcher(line);
                        return matcher4.matches() ? new ShaderLine(5, matcher4.group(1), matcher4.group(2), line) : null;
                    }
                }
            }
        }
    }

    public static int getIndex(String uniform, String prefix, int minIndex, int maxIndex)
    {
        if (uniform.length() != prefix.length() + 1)
        {
            return -1;
        }
        else if (!uniform.startsWith(prefix))
        {
            return -1;
        }
        else
        {
            int i = uniform.charAt(prefix.length()) - 48;
            return i >= minIndex && i <= maxIndex ? i : -1;
        }
    }

    public static int getShadowDepthIndex(String uniform)
    {
        byte b0 = -1;

        switch (uniform.hashCode())
        {
            case -903579360:
                if (uniform.equals("shadow"))
                {
                    b0 = 0;
                }

                break;

            case 1235669239:
                if (uniform.equals("watershadow"))
                {
                    b0 = 1;
                }
        }

        switch (b0)
        {
            case 0:
                return 0;

            case 1:
                return 1;

            default:
                return getIndex(uniform, "shadowtex", 0, 1);
        }
    }

    public static int getShadowColorIndex(String uniform)
    {
        byte b0 = -1;

        switch (uniform.hashCode())
        {
            case -1560188349:
                if (uniform.equals("shadowcolor"))
                {
                    b0 = 0;
                }

            default:
                switch (b0)
                {
                    case 0:
                        return 0;

                    default:
                        return getIndex(uniform, "shadowcolor", 0, 1);
                }
        }
    }

    public static int getDepthIndex(String uniform)
    {
        return getIndex(uniform, "depthtex", 0, 2);
    }

    public static int getColorIndex(String uniform)
    {
        int i = getIndex(uniform, "gaux", 1, 4);
        return i > 0 ? i + 3 : getIndex(uniform, "colortex", 4, 7);
    }

    public static boolean isComposite(String filename)
    {
        return PATTERN_COMPOSITE_FSH.matcher(filename).matches();
    }

    public static boolean isFinal(String filename)
    {
        return PATTERN_FINAL_FSH.matcher(filename).matches();
    }

    public static boolean isValidDrawBuffers(String str)
    {
        return PATTERN_DRAW_BUFFERS.matcher(str).matches();
    }
}
