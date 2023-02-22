package net.optifine.shaders.config;

import net.minecraft.src.Config;
import net.optifine.expr.*;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.*;
import net.optifine.shaders.uniform.CustomUniform;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderExpressionResolver;
import net.optifine.shaders.uniform.UniformType;
import net.optifine.util.StrUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderPackParser {
    private static final Pattern PATTERN_VERSION = Pattern.compile("^\\s*#version\\s+.*$");
    private static final Pattern PATTERN_INCLUDE = Pattern.compile("^\\s*#include\\s+\"([A-Za-z0-9_/\\.]+)\".*$");
    private static final Set<String> setConstNames = makeSetConstNames();
    private static final Map<String, Integer> mapAlphaFuncs = makeMapAlphaFuncs();
    private static final Map<String, Integer> mapBlendFactors = makeMapBlendFactors();

    public static ShaderOption[] parseShaderPackOptions(final IShaderPack shaderPack, final String[] programNames, final List<Integer> listDimensions) {
        if (shaderPack == null) {
            return new ShaderOption[0];
        } else {
            final Map<String, ShaderOption> map = new HashMap();
            collectShaderOptions(shaderPack, "/shaders", programNames, map);
            final Iterator<Integer> iterator = listDimensions.iterator();

            while (iterator.hasNext()) {
                final int i = iterator.next().intValue();
                final String s = "/shaders/world" + i;
                collectShaderOptions(shaderPack, s, programNames, map);
            }

            final Collection<ShaderOption> collection = map.values();
            final ShaderOption[] ashaderoption = collection.toArray(new ShaderOption[collection.size()]);
            final Comparator<ShaderOption> comparator = new Comparator<ShaderOption>() {
                public int compare(final ShaderOption o1, final ShaderOption o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            };
            Arrays.sort(ashaderoption, comparator);
            return ashaderoption;
        }
    }

    private static void collectShaderOptions(final IShaderPack shaderPack, final String dir, final String[] programNames, final Map<String, ShaderOption> mapOptions) {
        for (int i = 0; i < programNames.length; ++i) {
            final String s = programNames[i];

            if (!s.equals("")) {
                final String s1 = dir + "/" + s + ".vsh";
                final String s2 = dir + "/" + s + ".fsh";
                collectShaderOptions(shaderPack, s1, mapOptions);
                collectShaderOptions(shaderPack, s2, mapOptions);
            }
        }
    }

    private static void collectShaderOptions(final IShaderPack sp, final String path, final Map<String, ShaderOption> mapOptions) {
        final String[] astring = getLines(sp, path);

        for (int i = 0; i < astring.length; ++i) {
            final String s = astring[i];
            final ShaderOption shaderoption = getShaderOption(s, path);

            if (shaderoption != null && !shaderoption.getName().startsWith(ShaderMacros.getPrefixMacro()) && (!shaderoption.checkUsed() || isOptionUsed(shaderoption, astring))) {
                final String s1 = shaderoption.getName();
                final ShaderOption shaderoption1 = mapOptions.get(s1);

                if (shaderoption1 != null) {
                    if (!Config.equals(shaderoption1.getValueDefault(), shaderoption.getValueDefault())) {
                        Config.warn("Ambiguous shader option: " + shaderoption.getName());
                        Config.warn(" - in " + Config.arrayToString(shaderoption1.getPaths()) + ": " + shaderoption1.getValueDefault());
                        Config.warn(" - in " + Config.arrayToString(shaderoption.getPaths()) + ": " + shaderoption.getValueDefault());
                        shaderoption1.setEnabled(false);
                    }

                    if (shaderoption1.getDescription() == null || shaderoption1.getDescription().length() <= 0) {
                        shaderoption1.setDescription(shaderoption.getDescription());
                    }

                    shaderoption1.addPaths(shaderoption.getPaths());
                } else {
                    mapOptions.put(s1, shaderoption);
                }
            }
        }
    }

    private static boolean isOptionUsed(final ShaderOption so, final String[] lines) {
        for (int i = 0; i < lines.length; ++i) {
            final String s = lines[i];

            if (so.isUsedInLine(s)) {
                return true;
            }
        }

        return false;
    }

    private static String[] getLines(final IShaderPack sp, final String path) {
        try {
            final List<String> list = new ArrayList();
            final String s = loadFile(path, sp, 0, list, 0);

            if (s == null) {
                return new String[0];
            } else {
                final ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(s.getBytes());
                final String[] astring = Config.readLines(bytearrayinputstream);
                return astring;
            }
        } catch (final IOException ioexception) {
            Config.dbg(ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return new String[0];
        }
    }

    private static ShaderOption getShaderOption(final String line, final String path) {
        ShaderOption shaderoption = null;

        if (shaderoption == null) {
            shaderoption = ShaderOptionSwitch.parseOption(line, path);
        }

        if (shaderoption == null) {
            shaderoption = ShaderOptionVariable.parseOption(line, path);
        }

        if (shaderoption != null) {
            return shaderoption;
        } else {
            if (shaderoption == null) {
                shaderoption = ShaderOptionSwitchConst.parseOption(line, path);
            }

            if (shaderoption == null) {
                shaderoption = ShaderOptionVariableConst.parseOption(line, path);
            }

            return shaderoption != null && setConstNames.contains(shaderoption.getName()) ? shaderoption : null;
        }
    }

    private static Set<String> makeSetConstNames() {
        final Set<String> set = new HashSet();
        set.add("shadowMapResolution");
        set.add("shadowMapFov");
        set.add("shadowDistance");
        set.add("shadowDistanceRenderMul");
        set.add("shadowIntervalSize");
        set.add("generateShadowMipmap");
        set.add("generateShadowColorMipmap");
        set.add("shadowHardwareFiltering");
        set.add("shadowHardwareFiltering0");
        set.add("shadowHardwareFiltering1");
        set.add("shadowtex0Mipmap");
        set.add("shadowtexMipmap");
        set.add("shadowtex1Mipmap");
        set.add("shadowcolor0Mipmap");
        set.add("shadowColor0Mipmap");
        set.add("shadowcolor1Mipmap");
        set.add("shadowColor1Mipmap");
        set.add("shadowtex0Nearest");
        set.add("shadowtexNearest");
        set.add("shadow0MinMagNearest");
        set.add("shadowtex1Nearest");
        set.add("shadow1MinMagNearest");
        set.add("shadowcolor0Nearest");
        set.add("shadowColor0Nearest");
        set.add("shadowColor0MinMagNearest");
        set.add("shadowcolor1Nearest");
        set.add("shadowColor1Nearest");
        set.add("shadowColor1MinMagNearest");
        set.add("wetnessHalflife");
        set.add("drynessHalflife");
        set.add("eyeBrightnessHalflife");
        set.add("centerDepthHalflife");
        set.add("sunPathRotation");
        set.add("ambientOcclusionLevel");
        set.add("superSamplingLevel");
        set.add("noiseTextureResolution");
        return set;
    }

    public static ShaderProfile[] parseProfiles(final Properties props, final ShaderOption[] shaderOptions) {
        final String s = "profile.";
        final List<ShaderProfile> list = new ArrayList();

        for (final Object e : props.keySet()) {
            final String s1 = (String) e;
            if (s1.startsWith(s)) {
                final String s2 = s1.substring(s.length());
                props.getProperty(s1);
                final Set<String> set = new HashSet();
                final ShaderProfile shaderprofile = parseProfile(s2, props, set, shaderOptions);

                if (shaderprofile != null) {
                    list.add(shaderprofile);
                }
            }
        }

        if (list.size() <= 0) {
            return null;
        } else {
            final ShaderProfile[] ashaderprofile = list.toArray(new ShaderProfile[list.size()]);
            return ashaderprofile;
        }
    }

    public static Map<String, IExpressionBool> parseProgramConditions(final Properties props, final ShaderOption[] shaderOptions) {
        final String s = "program.";
        final Pattern pattern = Pattern.compile("program\\.([^.]+)\\.enabled");
        final Map<String, IExpressionBool> map = new HashMap();

        for (final Object e : props.keySet()) {
            final String s1 = (String) e;
            final Matcher matcher = pattern.matcher(s1);

            if (matcher.matches()) {
                final String s2 = matcher.group(1);
                final String s3 = props.getProperty(s1).trim();
                final IExpressionBool iexpressionbool = parseOptionExpression(s3, shaderOptions);

                if (iexpressionbool == null) {
                    SMCLog.severe("Error parsing program condition: " + s1);
                } else {
                    map.put(s2, iexpressionbool);
                }
            }
        }

        return map;
    }

    private static IExpressionBool parseOptionExpression(final String val, final ShaderOption[] shaderOptions) {
        try {
            final ShaderOptionResolver shaderoptionresolver = new ShaderOptionResolver(shaderOptions);
            final ExpressionParser expressionparser = new ExpressionParser(shaderoptionresolver);
            final IExpressionBool iexpressionbool = expressionparser.parseBool(val);
            return iexpressionbool;
        } catch (final ParseException parseexception) {
            SMCLog.warning(parseexception.getClass().getName() + ": " + parseexception.getMessage());
            return null;
        }
    }

    public static Set<String> parseOptionSliders(final Properties props, final ShaderOption[] shaderOptions) {
        final Set<String> set = new HashSet();
        final String s = props.getProperty("sliders");

        if (s == null) {
            return set;
        } else {
            final String[] astring = Config.tokenize(s, " ");

            for (int i = 0; i < astring.length; ++i) {
                final String s1 = astring[i];
                final ShaderOption shaderoption = ShaderUtils.getShaderOption(s1, shaderOptions);

                if (shaderoption == null) {
                    Config.warn("Invalid shader option: " + s1);
                } else {
                    set.add(s1);
                }
            }

            return set;
        }
    }

    private static ShaderProfile parseProfile(final String name, final Properties props, final Set<String> parsedProfiles, final ShaderOption[] shaderOptions) {
        final String s = "profile.";
        final String s1 = s + name;

        if (parsedProfiles.contains(s1)) {
            Config.warn("[Shaders] Profile already parsed: " + name);
            return null;
        } else {
            parsedProfiles.add(name);
            final ShaderProfile shaderprofile = new ShaderProfile(name);
            final String s2 = props.getProperty(s1);
            final String[] astring = Config.tokenize(s2, " ");

            for (int i = 0; i < astring.length; ++i) {
                final String s3 = astring[i];

                if (s3.startsWith(s)) {
                    final String s4 = s3.substring(s.length());
                    final ShaderProfile shaderprofile1 = parseProfile(s4, props, parsedProfiles, shaderOptions);

                    if (shaderprofile != null) {
                        shaderprofile.addOptionValues(shaderprofile1);
                        shaderprofile.addDisabledPrograms(shaderprofile1.getDisabledPrograms());
                    }
                } else {
                    final String[] astring1 = Config.tokenize(s3, ":=");

                    if (astring1.length == 1) {
                        String s7 = astring1[0];
                        boolean flag = true;

                        if (s7.startsWith("!")) {
                            flag = false;
                            s7 = s7.substring(1);
                        }

                        final String s5 = "program.";

                        if (s7.startsWith(s5)) {
                            final String s6 = s7.substring(s5.length());

                            if (!Shaders.isProgramPath(s6)) {
                                Config.warn("Invalid program: " + s6 + " in profile: " + shaderprofile.getName());
                            } else if (flag) {
                                shaderprofile.removeDisabledProgram(s6);
                            } else {
                                shaderprofile.addDisabledProgram(s6);
                            }
                        } else {
                            final ShaderOption shaderoption1 = ShaderUtils.getShaderOption(s7, shaderOptions);

                            if (!(shaderoption1 instanceof ShaderOptionSwitch)) {
                                Config.warn("[Shaders] Invalid option: " + s7);
                            } else {
                                shaderprofile.addOptionValue(s7, String.valueOf(flag));
                                shaderoption1.setVisible(true);
                            }
                        }
                    } else if (astring1.length != 2) {
                        Config.warn("[Shaders] Invalid option value: " + s3);
                    } else {
                        final String s8 = astring1[0];
                        final String s9 = astring1[1];
                        final ShaderOption shaderoption = ShaderUtils.getShaderOption(s8, shaderOptions);

                        if (shaderoption == null) {
                            Config.warn("[Shaders] Invalid option: " + s3);
                        } else if (!shaderoption.isValidValue(s9)) {
                            Config.warn("[Shaders] Invalid value: " + s3);
                        } else {
                            shaderoption.setVisible(true);
                            shaderprofile.addOptionValue(s8, s9);
                        }
                    }
                }
            }

            return shaderprofile;
        }
    }

    public static Map<String, ScreenShaderOptions> parseGuiScreens(final Properties props, final ShaderProfile[] shaderProfiles, final ShaderOption[] shaderOptions) {
        final Map<String, ScreenShaderOptions> map = new HashMap();
        parseGuiScreen("screen", props, map, shaderProfiles, shaderOptions);
        return map.isEmpty() ? null : map;
    }

    private static boolean parseGuiScreen(final String key, final Properties props, final Map<String, ScreenShaderOptions> map, final ShaderProfile[] shaderProfiles, final ShaderOption[] shaderOptions) {
        final String s = props.getProperty(key);

        if (s == null) {
            return false;
        } else {
            final List<ShaderOption> list = new ArrayList();
            final Set<String> set = new HashSet();
            final String[] astring = Config.tokenize(s, " ");

            for (int i = 0; i < astring.length; ++i) {
                final String s1 = astring[i];

                if (s1.equals("<empty>")) {
                    list.add(null);
                } else if (set.contains(s1)) {
                    Config.warn("[Shaders] Duplicate option: " + s1 + ", key: " + key);
                } else {
                    set.add(s1);

                    if (s1.equals("<profile>")) {
                        if (shaderProfiles == null) {
                            Config.warn("[Shaders] Option profile can not be used, no profiles defined: " + s1 + ", key: " + key);
                        } else {
                            final ShaderOptionProfile shaderoptionprofile = new ShaderOptionProfile(shaderProfiles, shaderOptions);
                            list.add(shaderoptionprofile);
                        }
                    } else if (s1.equals("*")) {
                        final ShaderOption shaderoption1 = new ShaderOptionRest("<rest>");
                        list.add(shaderoption1);
                    } else if (s1.startsWith("[") && s1.endsWith("]")) {
                        final String s3 = StrUtils.removePrefixSuffix(s1, "[", "]");

                        if (!s3.matches("^[a-zA-Z0-9_]+$")) {
                            Config.warn("[Shaders] Invalid screen: " + s1 + ", key: " + key);
                        } else if (!parseGuiScreen("screen." + s3, props, map, shaderProfiles, shaderOptions)) {
                            Config.warn("[Shaders] Invalid screen: " + s1 + ", key: " + key);
                        } else {
                            final ShaderOptionScreen shaderoptionscreen = new ShaderOptionScreen(s3);
                            list.add(shaderoptionscreen);
                        }
                    } else {
                        final ShaderOption shaderoption = ShaderUtils.getShaderOption(s1, shaderOptions);

                        if (shaderoption == null) {
                            Config.warn("[Shaders] Invalid option: " + s1 + ", key: " + key);
                            list.add(null);
                        } else {
                            shaderoption.setVisible(true);
                            list.add(shaderoption);
                        }
                    }
                }
            }

            final ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
            final String s2 = props.getProperty(key + ".columns");
            final int j = Config.parseInt(s2, 2);
            final ScreenShaderOptions screenshaderoptions = new ScreenShaderOptions(key, ashaderoption, j);
            map.put(key, screenshaderoptions);
            return true;
        }
    }

    public static BufferedReader resolveIncludes(final BufferedReader reader, final String filePath, final IShaderPack shaderPack, final int fileIndex, final List<String> listFiles, final int includeLevel) throws IOException {
        String s = "/";
        final int i = filePath.lastIndexOf("/");

        if (i >= 0) {
            s = filePath.substring(0, i);
        }

        final CharArrayWriter chararraywriter = new CharArrayWriter();
        int j = -1;
        final Set<ShaderMacro> set = new LinkedHashSet();
        int k = 1;

        while (true) {
            String s1 = reader.readLine();

            if (s1 == null) {
                char[] achar = chararraywriter.toCharArray();

                if (j >= 0 && set.size() > 0) {
                    final StringBuilder stringbuilder = new StringBuilder();

                    for (final ShaderMacro shadermacro : set) {
                        stringbuilder.append("#define ");
                        stringbuilder.append(shadermacro.getName());
                        stringbuilder.append(" ");
                        stringbuilder.append(shadermacro.getValue());
                        stringbuilder.append("\n");
                    }

                    final String s7 = stringbuilder.toString();
                    final StringBuilder stringbuilder1 = new StringBuilder(new String(achar));
                    stringbuilder1.insert(j, s7);
                    final String s9 = stringbuilder1.toString();
                    achar = s9.toCharArray();
                }

                final CharArrayReader chararrayreader = new CharArrayReader(achar);
                return new BufferedReader(chararrayreader);
            }

            if (j < 0) {
                final Matcher matcher = PATTERN_VERSION.matcher(s1);

                if (matcher.matches()) {
                    final String s2 = ShaderMacros.getFixedMacroLines() + ShaderMacros.getOptionMacroLines();
                    final String s3 = s1 + "\n" + s2;
                    final String s4 = "#line " + (k + 1) + " " + fileIndex;
                    s1 = s3 + s4;
                    j = chararraywriter.size() + s3.length();
                }
            }

            final Matcher matcher1 = PATTERN_INCLUDE.matcher(s1);

            if (matcher1.matches()) {
                final String s6 = matcher1.group(1);
                final boolean flag = s6.startsWith("/");
                final String s8 = flag ? "/shaders" + s6 : s + "/" + s6;

                if (!listFiles.contains(s8)) {
                    listFiles.add(s8);
                }

                final int l = listFiles.indexOf(s8) + 1;
                s1 = loadFile(s8, shaderPack, l, listFiles, includeLevel);

                if (s1 == null) {
                    throw new IOException("Included file not found: " + filePath);
                }

                if (s1.endsWith("\n")) {
                    s1 = s1.substring(0, s1.length() - 1);
                }

                String s5 = "#line 1 " + l + "\n";

                if (s1.startsWith("#version ")) {
                    s5 = "";
                }

                s1 = s5 + s1 + "\n" + "#line " + (k + 1) + " " + fileIndex;
            }

            if (j >= 0 && s1.contains(ShaderMacros.getPrefixMacro())) {
                final ShaderMacro[] ashadermacro = findMacros(s1, ShaderMacros.getExtensions());

                for (int i1 = 0; i1 < ashadermacro.length; ++i1) {
                    final ShaderMacro shadermacro1 = ashadermacro[i1];
                    set.add(shadermacro1);
                }
            }

            chararraywriter.write(s1);
            chararraywriter.write("\n");
            ++k;
        }
    }

    private static ShaderMacro[] findMacros(final String line, final ShaderMacro[] macros) {
        final List<ShaderMacro> list = new ArrayList();

        for (int i = 0; i < macros.length; ++i) {
            final ShaderMacro shadermacro = macros[i];

            if (line.contains(shadermacro.getName())) {
                list.add(shadermacro);
            }
        }

        final ShaderMacro[] ashadermacro = list.toArray(new ShaderMacro[list.size()]);
        return ashadermacro;
    }

    private static String loadFile(final String filePath, final IShaderPack shaderPack, final int fileIndex, final List<String> listFiles, int includeLevel) throws IOException {
        if (includeLevel >= 10) {
            throw new IOException("#include depth exceeded: " + includeLevel + ", file: " + filePath);
        } else {
            ++includeLevel;
            final InputStream inputstream = shaderPack.getResourceAsStream(filePath);

            if (inputstream == null) {
                return null;
            } else {
                final InputStreamReader inputstreamreader = new InputStreamReader(inputstream, StandardCharsets.US_ASCII);
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                bufferedreader = resolveIncludes(bufferedreader, filePath, shaderPack, fileIndex, listFiles, includeLevel);
                final CharArrayWriter chararraywriter = new CharArrayWriter();

                while (true) {
                    final String s = bufferedreader.readLine();

                    if (s == null) {
                        return chararraywriter.toString();
                    }

                    chararraywriter.write(s);
                    chararraywriter.write("\n");
                }
            }
        }
    }

    public static CustomUniforms parseCustomUniforms(final Properties props) {
        final String s = "uniform";
        final String s1 = "variable";
        final String s2 = s + ".";
        final String s3 = s1 + ".";
        final Map<String, IExpression> map = new HashMap();
        final List<CustomUniform> list = new ArrayList();

        for (final Object e : props.keySet()) {
            final String s4 = (String) e;
            final String[] astring = Config.tokenize(s4, ".");

            if (astring.length == 3) {
                final String s5 = astring[0];
                final String s6 = astring[1];
                final String s7 = astring[2];
                final String s8 = props.getProperty(s4).trim();

                if (map.containsKey(s7)) {
                    SMCLog.warning("Expression already defined: " + s7);
                } else if (s5.equals(s) || s5.equals(s1)) {
                    SMCLog.info("Custom " + s5 + ": " + s7);
                    final CustomUniform customuniform = parseCustomUniform(s5, s7, s6, s8, map);

                    if (customuniform != null) {
                        map.put(s7, customuniform.getExpression());

                        if (!s5.equals(s1)) {
                            list.add(customuniform);
                        }
                    }
                }
            }
        }

        if (list.size() <= 0) {
            return null;
        } else {
            final CustomUniform[] acustomuniform = list.toArray(new CustomUniform[list.size()]);
            final CustomUniforms customuniforms = new CustomUniforms(acustomuniform, map);
            return customuniforms;
        }
    }

    private static CustomUniform parseCustomUniform(final String kind, final String name, final String type, final String src, final Map<String, IExpression> mapExpressions) {
        try {
            final UniformType uniformtype = UniformType.parse(type);

            if (uniformtype == null) {
                SMCLog.warning("Unknown " + kind + " type: " + uniformtype);
                return null;
            } else {
                final ShaderExpressionResolver shaderexpressionresolver = new ShaderExpressionResolver(mapExpressions);
                final ExpressionParser expressionparser = new ExpressionParser(shaderexpressionresolver);
                IExpression iexpression = expressionparser.parse(src);
                final ExpressionType expressiontype = iexpression.getExpressionType();

                if (!uniformtype.matchesExpressionType(expressiontype)) {
                    SMCLog.warning("Expression type does not match " + kind + " type, expression: " + expressiontype + ", " + kind + ": " + uniformtype + " " + name);
                    return null;
                } else {
                    iexpression = makeExpressionCached(iexpression);
                    final CustomUniform customuniform = new CustomUniform(name, uniformtype, iexpression);
                    return customuniform;
                }
            }
        } catch (final ParseException parseexception) {
            SMCLog.warning(parseexception.getClass().getName() + ": " + parseexception.getMessage());
            return null;
        }
    }

    private static IExpression makeExpressionCached(final IExpression expr) {
        return expr instanceof IExpressionFloat ? new ExpressionFloatCached((IExpressionFloat) expr) : (expr instanceof IExpressionFloatArray ? new ExpressionFloatArrayCached((IExpressionFloatArray) expr) : expr);
    }

    public static void parseAlphaStates(final Properties props) {
        for (final Object e : props.keySet()) {
            final String s = (String) e;
            final String[] astring = Config.tokenize(s, ".");

            if (astring.length == 2) {
                final String s1 = astring[0];
                final String s2 = astring[1];

                if (s1.equals("alphaTest")) {
                    final Program program = Shaders.getProgram(s2);

                    if (program == null) {
                        SMCLog.severe("Invalid program name: " + s2);
                    } else {
                        final String s3 = props.getProperty(s).trim();
                        final GlAlphaState glalphastate = parseAlphaState(s3);

                        if (glalphastate != null) {
                            program.setAlphaState(glalphastate);
                        }
                    }
                }
            }
        }
    }

    private static GlAlphaState parseAlphaState(final String str) {
        final String[] astring = Config.tokenize(str, " ");

        if (astring.length == 1) {
            final String s = astring[0];

            if (s.equals("off") || s.equals("false")) {
                return new GlAlphaState(false);
            }
        } else if (astring.length == 2) {
            final String s2 = astring[0];
            final String s1 = astring[1];
            final Integer integer = mapAlphaFuncs.get(s2);
            final float f = Config.parseFloat(s1, -1.0F);

            if (integer != null && f >= 0.0F) {
                return new GlAlphaState(true, integer.intValue(), f);
            }
        }

        SMCLog.severe("Invalid alpha test: " + str);
        return null;
    }

    public static void parseBlendStates(final Properties props) {
        for (final Object e : props.keySet()) {
            final String s = (String) e;
            final String[] astring = Config.tokenize(s, ".");

            if (astring.length == 2) {
                final String s1 = astring[0];
                final String s2 = astring[1];

                if (s1.equals("blend")) {
                    final Program program = Shaders.getProgram(s2);

                    if (program == null) {
                        SMCLog.severe("Invalid program name: " + s2);
                    } else {
                        final String s3 = props.getProperty(s).trim();
                        final GlBlendState glblendstate = parseBlendState(s3);

                        if (glblendstate != null) {
                            program.setBlendState(glblendstate);
                        }
                    }
                }
            }
        }
    }

    private static GlBlendState parseBlendState(final String str) {
        final String[] astring = Config.tokenize(str, " ");

        if (astring.length == 1) {
            final String s = astring[0];

            if (s.equals("off") || s.equals("false")) {
                return new GlBlendState(false);
            }
        } else if (astring.length == 2 || astring.length == 4) {
            final String s4 = astring[0];
            final String s1 = astring[1];
            String s2 = s4;
            String s3 = s1;

            if (astring.length == 4) {
                s2 = astring[2];
                s3 = astring[3];
            }

            final Integer integer = mapBlendFactors.get(s4);
            final Integer integer1 = mapBlendFactors.get(s1);
            final Integer integer2 = mapBlendFactors.get(s2);
            final Integer integer3 = mapBlendFactors.get(s3);

            if (integer != null && integer1 != null && integer2 != null && integer3 != null) {
                return new GlBlendState(true, integer.intValue(), integer1.intValue(), integer2.intValue(), integer3.intValue());
            }
        }

        SMCLog.severe("Invalid blend mode: " + str);
        return null;
    }

    public static void parseRenderScales(final Properties props) {
        for (final Object e : props.keySet()) {
            final String s = (String) e;
            final String[] astring = Config.tokenize(s, ".");

            if (astring.length == 2) {
                final String s1 = astring[0];
                final String s2 = astring[1];

                if (s1.equals("scale")) {
                    final Program program = Shaders.getProgram(s2);

                    if (program == null) {
                        SMCLog.severe("Invalid program name: " + s2);
                    } else {
                        final String s3 = props.getProperty(s).trim();
                        final RenderScale renderscale = parseRenderScale(s3);

                        if (renderscale != null) {
                            program.setRenderScale(renderscale);
                        }
                    }
                }
            }
        }
    }

    private static RenderScale parseRenderScale(final String str) {
        final String[] astring = Config.tokenize(str, " ");
        final float f = Config.parseFloat(astring[0], -1.0F);
        float f1 = 0.0F;
        float f2 = 0.0F;

        if (astring.length > 1) {
            if (astring.length != 3) {
                SMCLog.severe("Invalid render scale: " + str);
                return null;
            }

            f1 = Config.parseFloat(astring[1], -1.0F);
            f2 = Config.parseFloat(astring[2], -1.0F);
        }

        if (Config.between(f, 0.0F, 1.0F) && Config.between(f1, 0.0F, 1.0F) && Config.between(f2, 0.0F, 1.0F)) {
            return new RenderScale(f, f1, f2);
        } else {
            SMCLog.severe("Invalid render scale: " + str);
            return null;
        }
    }

    public static void parseBuffersFlip(final Properties props) {
        for (final Object e : props.keySet()) {
            final String s = (String) e;
            final String[] astring = Config.tokenize(s, ".");

            if (astring.length == 3) {
                final String s1 = astring[0];
                final String s2 = astring[1];
                final String s3 = astring[2];

                if (s1.equals("flip")) {
                    final Program program = Shaders.getProgram(s2);

                    if (program == null) {
                        SMCLog.severe("Invalid program name: " + s2);
                    } else {
                        final Boolean[] aboolean = program.getBuffersFlip();
                        final int i = Shaders.getBufferIndexFromString(s3);

                        if (i >= 0 && i < aboolean.length) {
                            final String s4 = props.getProperty(s).trim();
                            final Boolean obool = Config.parseBoolean(s4, null);

                            if (obool == null) {
                                SMCLog.severe("Invalid boolean value: " + s4);
                            } else {
                                aboolean[i] = obool;
                            }
                        } else {
                            SMCLog.severe("Invalid buffer name: " + s3);
                        }
                    }
                }
            }
        }
    }

    private static Map<String, Integer> makeMapAlphaFuncs() {
        final Map<String, Integer> map = new HashMap();
        map.put("NEVER", new Integer(512));
        map.put("LESS", new Integer(513));
        map.put("EQUAL", new Integer(514));
        map.put("LEQUAL", new Integer(515));
        map.put("GREATER", new Integer(516));
        map.put("NOTEQUAL", new Integer(517));
        map.put("GEQUAL", new Integer(518));
        map.put("ALWAYS", new Integer(519));
        return Collections.unmodifiableMap(map);
    }

    private static Map<String, Integer> makeMapBlendFactors() {
        final Map<String, Integer> map = new HashMap();
        map.put("ZERO", new Integer(0));
        map.put("ONE", new Integer(1));
        map.put("SRC_COLOR", new Integer(768));
        map.put("ONE_MINUS_SRC_COLOR", new Integer(769));
        map.put("DST_COLOR", new Integer(774));
        map.put("ONE_MINUS_DST_COLOR", new Integer(775));
        map.put("SRC_ALPHA", new Integer(770));
        map.put("ONE_MINUS_SRC_ALPHA", new Integer(771));
        map.put("DST_ALPHA", new Integer(772));
        map.put("ONE_MINUS_DST_ALPHA", new Integer(773));
        map.put("CONSTANT_COLOR", new Integer(32769));
        map.put("ONE_MINUS_CONSTANT_COLOR", new Integer(32770));
        map.put("CONSTANT_ALPHA", new Integer(32771));
        map.put("ONE_MINUS_CONSTANT_ALPHA", new Integer(32772));
        map.put("SRC_ALPHA_SATURATE", new Integer(776));
        return Collections.unmodifiableMap(map);
    }
}
