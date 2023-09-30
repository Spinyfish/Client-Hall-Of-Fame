/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.src.Config;
import net.optifine.expr.ExpressionFloatArrayCached;
import net.optifine.expr.ExpressionFloatCached;
import net.optifine.expr.ExpressionParser;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.IExpressionFloatArray;
import net.optifine.expr.ParseException;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Program;
import net.optifine.shaders.SMCLog;
import net.optifine.shaders.ShaderUtils;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.RenderScale;
import net.optifine.shaders.config.ScreenShaderOptions;
import net.optifine.shaders.config.ShaderMacro;
import net.optifine.shaders.config.ShaderMacros;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionProfile;
import net.optifine.shaders.config.ShaderOptionResolver;
import net.optifine.shaders.config.ShaderOptionRest;
import net.optifine.shaders.config.ShaderOptionScreen;
import net.optifine.shaders.config.ShaderOptionSwitch;
import net.optifine.shaders.config.ShaderOptionSwitchConst;
import net.optifine.shaders.config.ShaderOptionVariable;
import net.optifine.shaders.config.ShaderOptionVariableConst;
import net.optifine.shaders.config.ShaderProfile;
import net.optifine.shaders.uniform.CustomUniform;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderExpressionResolver;
import net.optifine.shaders.uniform.UniformType;
import net.optifine.util.StrUtils;

public class ShaderPackParser {
    private static final Pattern PATTERN_VERSION = Pattern.compile("^\\s*#version\\s+.*$");
    private static final Pattern PATTERN_INCLUDE = Pattern.compile("^\\s*#include\\s+\"([A-Za-z0-9_/\\.]+)\".*$");
    private static final Set<String> setConstNames = ShaderPackParser.makeSetConstNames();
    private static final Map<String, Integer> mapAlphaFuncs = ShaderPackParser.makeMapAlphaFuncs();
    private static final Map<String, Integer> mapBlendFactors = ShaderPackParser.makeMapBlendFactors();

    public static ShaderOption[] parseShaderPackOptions(IShaderPack shaderPack, String[] programNames, List<Integer> listDimensions) {
        if (shaderPack == null) {
            return new ShaderOption[0];
        }
        HashMap<String, ShaderOption> map = new HashMap<String, ShaderOption>();
        ShaderPackParser.collectShaderOptions(shaderPack, "/shaders", programNames, map);
        for (int i : listDimensions) {
            String s = "/shaders/world" + i;
            ShaderPackParser.collectShaderOptions(shaderPack, s, programNames, map);
        }
        Collection collection = map.values();
        ShaderOption[] ashaderoption = collection.toArray(new ShaderOption[collection.size()]);
        Comparator<ShaderOption> comparator = new Comparator<ShaderOption>(){

            @Override
            public int compare(ShaderOption o1, ShaderOption o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        };
        Arrays.sort(ashaderoption, comparator);
        return ashaderoption;
    }

    private static void collectShaderOptions(IShaderPack shaderPack, String dir, String[] programNames, Map<String, ShaderOption> mapOptions) {
        for (int i = 0; i < programNames.length; ++i) {
            String s = programNames[i];
            if (s.equals("")) continue;
            String s1 = dir + "/" + s + ".vsh";
            String s2 = dir + "/" + s + ".fsh";
            ShaderPackParser.collectShaderOptions(shaderPack, s1, mapOptions);
            ShaderPackParser.collectShaderOptions(shaderPack, s2, mapOptions);
        }
    }

    private static void collectShaderOptions(IShaderPack sp, String path, Map<String, ShaderOption> mapOptions) {
        String[] astring = ShaderPackParser.getLines(sp, path);
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            ShaderOption shaderoption = ShaderPackParser.getShaderOption(s, path);
            if (shaderoption == null || shaderoption.getName().startsWith(ShaderMacros.getPrefixMacro()) || shaderoption.checkUsed() && !ShaderPackParser.isOptionUsed(shaderoption, astring)) continue;
            String s1 = shaderoption.getName();
            ShaderOption shaderoption1 = mapOptions.get(s1);
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
                continue;
            }
            mapOptions.put(s1, shaderoption);
        }
    }

    private static boolean isOptionUsed(ShaderOption so, String[] lines) {
        for (int i = 0; i < lines.length; ++i) {
            String s = lines[i];
            if (!so.isUsedInLine(s)) continue;
            return true;
        }
        return false;
    }

    private static String[] getLines(IShaderPack sp, String path) {
        try {
            ArrayList<String> list = new ArrayList<String>();
            String s = ShaderPackParser.loadFile(path, sp, 0, list, 0);
            if (s == null) {
                return new String[0];
            }
            ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(s.getBytes());
            String[] astring = Config.readLines(bytearrayinputstream);
            return astring;
        }
        catch (IOException ioexception) {
            Config.dbg(ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return new String[0];
        }
    }

    private static ShaderOption getShaderOption(String line, String path) {
        ShaderOption shaderoption = null;
        if (shaderoption == null) {
            shaderoption = ShaderOptionSwitch.parseOption(line, path);
        }
        if (shaderoption == null) {
            shaderoption = ShaderOptionVariable.parseOption(line, path);
        }
        if (shaderoption != null) {
            return shaderoption;
        }
        if (shaderoption == null) {
            shaderoption = ShaderOptionSwitchConst.parseOption(line, path);
        }
        if (shaderoption == null) {
            shaderoption = ShaderOptionVariableConst.parseOption(line, path);
        }
        return shaderoption != null && setConstNames.contains(shaderoption.getName()) ? shaderoption : null;
    }

    private static Set<String> makeSetConstNames() {
        HashSet<String> set = new HashSet<String>();
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

    public static ShaderProfile[] parseProfiles(Properties props, ShaderOption[] shaderOptions) {
        String s = "profile.";
        ArrayList<ShaderProfile> list = new ArrayList<ShaderProfile>();
        for (Object e : props.keySet()) {
            String s1 = (String)e;
            if (!s1.startsWith(s)) continue;
            String s2 = s1.substring(s.length());
            props.getProperty(s1);
            HashSet<String> set = new HashSet<String>();
            ShaderProfile shaderprofile = ShaderPackParser.parseProfile(s2, props, set, shaderOptions);
            if (shaderprofile == null) continue;
            list.add(shaderprofile);
        }
        if (list.size() <= 0) {
            return null;
        }
        ShaderProfile[] ashaderprofile = list.toArray(new ShaderProfile[list.size()]);
        return ashaderprofile;
    }

    public static Map<String, IExpressionBool> parseProgramConditions(Properties props, ShaderOption[] shaderOptions) {
        String s = "program.";
        Pattern pattern = Pattern.compile("program\\.([^.]+)\\.enabled");
        HashMap<String, IExpressionBool> map = new HashMap<String, IExpressionBool>();
        for (Object e : props.keySet()) {
            String s1 = (String)e;
            Matcher matcher = pattern.matcher(s1);
            if (!matcher.matches()) continue;
            String s2 = matcher.group(1);
            String s3 = props.getProperty(s1).trim();
            IExpressionBool iexpressionbool = ShaderPackParser.parseOptionExpression(s3, shaderOptions);
            if (iexpressionbool == null) {
                SMCLog.severe("Error parsing program condition: " + s1);
                continue;
            }
            map.put(s2, iexpressionbool);
        }
        return map;
    }

    private static IExpressionBool parseOptionExpression(String val, ShaderOption[] shaderOptions) {
        try {
            ShaderOptionResolver shaderoptionresolver = new ShaderOptionResolver(shaderOptions);
            ExpressionParser expressionparser = new ExpressionParser(shaderoptionresolver);
            IExpressionBool iexpressionbool = expressionparser.parseBool(val);
            return iexpressionbool;
        }
        catch (ParseException parseexception) {
            SMCLog.warning(parseexception.getClass().getName() + ": " + parseexception.getMessage());
            return null;
        }
    }

    public static Set<String> parseOptionSliders(Properties props, ShaderOption[] shaderOptions) {
        HashSet<String> set = new HashSet<String>();
        String s = props.getProperty("sliders");
        if (s == null) {
            return set;
        }
        String[] astring = Config.tokenize(s, " ");
        for (int i = 0; i < astring.length; ++i) {
            String s1 = astring[i];
            ShaderOption shaderoption = ShaderUtils.getShaderOption(s1, shaderOptions);
            if (shaderoption == null) {
                Config.warn("Invalid shader option: " + s1);
                continue;
            }
            set.add(s1);
        }
        return set;
    }

    private static ShaderProfile parseProfile(String name, Properties props, Set<String> parsedProfiles, ShaderOption[] shaderOptions) {
        String s = "profile.";
        String s1 = s + name;
        if (parsedProfiles.contains(s1)) {
            Config.warn("[Shaders] Profile already parsed: " + name);
            return null;
        }
        parsedProfiles.add(name);
        ShaderProfile shaderprofile = new ShaderProfile(name);
        String s2 = props.getProperty(s1);
        String[] astring = Config.tokenize(s2, " ");
        for (int i = 0; i < astring.length; ++i) {
            String s3 = astring[i];
            if (s3.startsWith(s)) {
                String s4 = s3.substring(s.length());
                ShaderProfile shaderprofile1 = ShaderPackParser.parseProfile(s4, props, parsedProfiles, shaderOptions);
                if (shaderprofile == null) continue;
                shaderprofile.addOptionValues(shaderprofile1);
                shaderprofile.addDisabledPrograms(shaderprofile1.getDisabledPrograms());
                continue;
            }
            String[] astring1 = Config.tokenize(s3, ":=");
            if (astring1.length == 1) {
                String s5;
                String s7 = astring1[0];
                boolean flag = true;
                if (s7.startsWith("!")) {
                    flag = false;
                    s7 = s7.substring(1);
                }
                if (s7.startsWith(s5 = "program.")) {
                    String s6 = s7.substring(s5.length());
                    if (!Shaders.isProgramPath(s6)) {
                        Config.warn("Invalid program: " + s6 + " in profile: " + shaderprofile.getName());
                        continue;
                    }
                    if (flag) {
                        shaderprofile.removeDisabledProgram(s6);
                        continue;
                    }
                    shaderprofile.addDisabledProgram(s6);
                    continue;
                }
                ShaderOption shaderoption1 = ShaderUtils.getShaderOption(s7, shaderOptions);
                if (!(shaderoption1 instanceof ShaderOptionSwitch)) {
                    Config.warn("[Shaders] Invalid option: " + s7);
                    continue;
                }
                shaderprofile.addOptionValue(s7, String.valueOf(flag));
                shaderoption1.setVisible(true);
                continue;
            }
            if (astring1.length != 2) {
                Config.warn("[Shaders] Invalid option value: " + s3);
                continue;
            }
            String s8 = astring1[0];
            String s9 = astring1[1];
            ShaderOption shaderoption = ShaderUtils.getShaderOption(s8, shaderOptions);
            if (shaderoption == null) {
                Config.warn("[Shaders] Invalid option: " + s3);
                continue;
            }
            if (!shaderoption.isValidValue(s9)) {
                Config.warn("[Shaders] Invalid value: " + s3);
                continue;
            }
            shaderoption.setVisible(true);
            shaderprofile.addOptionValue(s8, s9);
        }
        return shaderprofile;
    }

    public static Map<String, ScreenShaderOptions> parseGuiScreens(Properties props, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions) {
        HashMap<String, ScreenShaderOptions> map = new HashMap<String, ScreenShaderOptions>();
        ShaderPackParser.parseGuiScreen("screen", props, map, shaderProfiles, shaderOptions);
        return map.isEmpty() ? null : map;
    }

    private static boolean parseGuiScreen(String key, Properties props, Map<String, ScreenShaderOptions> map, ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions) {
        String s = props.getProperty(key);
        if (s == null) {
            return false;
        }
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        HashSet<String> set = new HashSet<String>();
        String[] astring = Config.tokenize(s, " ");
        for (int i = 0; i < astring.length; ++i) {
            String s1 = astring[i];
            if (s1.equals("<empty>")) {
                list.add(null);
                continue;
            }
            if (set.contains(s1)) {
                Config.warn("[Shaders] Duplicate option: " + s1 + ", key: " + key);
                continue;
            }
            set.add(s1);
            if (s1.equals("<profile>")) {
                if (shaderProfiles == null) {
                    Config.warn("[Shaders] Option profile can not be used, no profiles defined: " + s1 + ", key: " + key);
                    continue;
                }
                ShaderOptionProfile shaderoptionprofile = new ShaderOptionProfile(shaderProfiles, shaderOptions);
                list.add(shaderoptionprofile);
                continue;
            }
            if (s1.equals("*")) {
                ShaderOptionRest shaderoption1 = new ShaderOptionRest("<rest>");
                list.add(shaderoption1);
                continue;
            }
            if (s1.startsWith("[") && s1.endsWith("]")) {
                String s3 = StrUtils.removePrefixSuffix(s1, "[", "]");
                if (!s3.matches("^[a-zA-Z0-9_]+$")) {
                    Config.warn("[Shaders] Invalid screen: " + s1 + ", key: " + key);
                    continue;
                }
                if (!ShaderPackParser.parseGuiScreen("screen." + s3, props, map, shaderProfiles, shaderOptions)) {
                    Config.warn("[Shaders] Invalid screen: " + s1 + ", key: " + key);
                    continue;
                }
                ShaderOptionScreen shaderoptionscreen = new ShaderOptionScreen(s3);
                list.add(shaderoptionscreen);
                continue;
            }
            ShaderOption shaderoption = ShaderUtils.getShaderOption(s1, shaderOptions);
            if (shaderoption == null) {
                Config.warn("[Shaders] Invalid option: " + s1 + ", key: " + key);
                list.add(null);
                continue;
            }
            shaderoption.setVisible(true);
            list.add(shaderoption);
        }
        ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
        String s2 = props.getProperty(key + ".columns");
        int j = Config.parseInt(s2, 2);
        ScreenShaderOptions screenshaderoptions = new ScreenShaderOptions(key, ashaderoption, j);
        map.put(key, screenshaderoptions);
        return true;
    }

    public static BufferedReader resolveIncludes(BufferedReader reader, String filePath, IShaderPack shaderPack, int fileIndex, List<String> listFiles, int includeLevel) throws IOException {
        String s = "/";
        int i = filePath.lastIndexOf("/");
        if (i >= 0) {
            s = filePath.substring(0, i);
        }
        CharArrayWriter chararraywriter = new CharArrayWriter();
        int j = -1;
        LinkedHashSet<ShaderMacro> set = new LinkedHashSet<ShaderMacro>();
        int k = 1;
        while (true) {
            Matcher matcher1;
            Matcher matcher;
            String s1;
            if ((s1 = reader.readLine()) == null) {
                char[] achar = chararraywriter.toCharArray();
                if (j >= 0 && set.size() > 0) {
                    StringBuilder stringbuilder = new StringBuilder();
                    for (ShaderMacro shadermacro : set) {
                        stringbuilder.append("#define ");
                        stringbuilder.append(shadermacro.getName());
                        stringbuilder.append(" ");
                        stringbuilder.append(shadermacro.getValue());
                        stringbuilder.append("\n");
                    }
                    String s7 = stringbuilder.toString();
                    StringBuilder stringbuilder1 = new StringBuilder(new String(achar));
                    stringbuilder1.insert(j, s7);
                    String s9 = stringbuilder1.toString();
                    achar = s9.toCharArray();
                }
                CharArrayReader chararrayreader = new CharArrayReader(achar);
                return new BufferedReader(chararrayreader);
            }
            if (j < 0 && (matcher = PATTERN_VERSION.matcher(s1)).matches()) {
                String s2 = ShaderMacros.getFixedMacroLines() + ShaderMacros.getOptionMacroLines();
                String s3 = s1 + "\n" + s2;
                String s4 = "#line " + (k + 1) + " " + fileIndex;
                s1 = s3 + s4;
                j = chararraywriter.size() + s3.length();
            }
            if ((matcher1 = PATTERN_INCLUDE.matcher(s1)).matches()) {
                int l;
                String s8;
                String s6 = matcher1.group(1);
                boolean flag = s6.startsWith("/");
                String string = s8 = flag ? "/shaders" + s6 : s + "/" + s6;
                if (!listFiles.contains(s8)) {
                    listFiles.add(s8);
                }
                if ((s1 = ShaderPackParser.loadFile(s8, shaderPack, l = listFiles.indexOf(s8) + 1, listFiles, includeLevel)) == null) {
                    throw new IOException("Included file not found: " + filePath);
                }
                if (s1.endsWith("\n")) {
                    s1 = s1.substring(0, s1.length() - 1);
                }
                String s5 = "#line 1 " + l + "\n";
                if (s1.startsWith("#version ")) {
                    s5 = "";
                }
                s1 = s5 + s1 + "\n#line " + (k + 1) + " " + fileIndex;
            }
            if (j >= 0 && s1.contains(ShaderMacros.getPrefixMacro())) {
                ShaderMacro[] ashadermacro = ShaderPackParser.findMacros(s1, ShaderMacros.getExtensions());
                for (int i1 = 0; i1 < ashadermacro.length; ++i1) {
                    ShaderMacro shadermacro1 = ashadermacro[i1];
                    set.add(shadermacro1);
                }
            }
            chararraywriter.write(s1);
            chararraywriter.write("\n");
            ++k;
        }
    }

    private static ShaderMacro[] findMacros(String line, ShaderMacro[] macros) {
        ArrayList<ShaderMacro> list = new ArrayList<ShaderMacro>();
        for (int i = 0; i < macros.length; ++i) {
            ShaderMacro shadermacro = macros[i];
            if (!line.contains(shadermacro.getName())) continue;
            list.add(shadermacro);
        }
        ShaderMacro[] ashadermacro = list.toArray(new ShaderMacro[list.size()]);
        return ashadermacro;
    }

    private static String loadFile(String filePath, IShaderPack shaderPack, int fileIndex, List<String> listFiles, int includeLevel) throws IOException {
        if (includeLevel >= 10) {
            throw new IOException("#include depth exceeded: " + includeLevel + ", file: " + filePath);
        }
        ++includeLevel;
        InputStream inputstream = shaderPack.getResourceAsStream(filePath);
        if (inputstream == null) {
            return null;
        }
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream, "ASCII");
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filePath, shaderPack, fileIndex, listFiles, includeLevel);
        CharArrayWriter chararraywriter = new CharArrayWriter();
        String s;
        while ((s = bufferedreader.readLine()) != null) {
            chararraywriter.write(s);
            chararraywriter.write("\n");
        }
        return chararraywriter.toString();
    }

    public static CustomUniforms parseCustomUniforms(Properties props) {
        String s = "uniform";
        String s1 = "variable";
        String s2 = s + ".";
        String s3 = s1 + ".";
        HashMap<String, IExpression> map = new HashMap<String, IExpression>();
        ArrayList<CustomUniform> list = new ArrayList<CustomUniform>();
        for (Object e : props.keySet()) {
            String s4 = (String)e;
            String[] astring = Config.tokenize(s4, ".");
            if (astring.length != 3) continue;
            String s5 = astring[0];
            String s6 = astring[1];
            String s7 = astring[2];
            String s8 = props.getProperty(s4).trim();
            if (map.containsKey(s7)) {
                SMCLog.warning("Expression already defined: " + s7);
                continue;
            }
            if (!s5.equals(s) && !s5.equals(s1)) continue;
            SMCLog.info("Custom " + s5 + ": " + s7);
            CustomUniform customuniform = ShaderPackParser.parseCustomUniform(s5, s7, s6, s8, map);
            if (customuniform == null) continue;
            map.put(s7, customuniform.getExpression());
            if (s5.equals(s1)) continue;
            list.add(customuniform);
        }
        if (list.size() <= 0) {
            return null;
        }
        CustomUniform[] acustomuniform = list.toArray(new CustomUniform[list.size()]);
        CustomUniforms customuniforms = new CustomUniforms(acustomuniform, map);
        return customuniforms;
    }

    private static CustomUniform parseCustomUniform(String kind, String name, String type, String src, Map<String, IExpression> mapExpressions) {
        try {
            UniformType uniformtype = UniformType.parse(type);
            if (uniformtype == null) {
                SMCLog.warning("Unknown " + kind + " type: " + (Object)((Object)uniformtype));
                return null;
            }
            ShaderExpressionResolver shaderexpressionresolver = new ShaderExpressionResolver(mapExpressions);
            ExpressionParser expressionparser = new ExpressionParser(shaderexpressionresolver);
            IExpression iexpression = expressionparser.parse(src);
            ExpressionType expressiontype = iexpression.getExpressionType();
            if (!uniformtype.matchesExpressionType(expressiontype)) {
                SMCLog.warning("Expression type does not match " + kind + " type, expression: " + (Object)((Object)expressiontype) + ", " + kind + ": " + (Object)((Object)uniformtype) + " " + name);
                return null;
            }
            iexpression = ShaderPackParser.makeExpressionCached(iexpression);
            CustomUniform customuniform = new CustomUniform(name, uniformtype, iexpression);
            return customuniform;
        }
        catch (ParseException parseexception) {
            SMCLog.warning(parseexception.getClass().getName() + ": " + parseexception.getMessage());
            return null;
        }
    }

    private static IExpression makeExpressionCached(IExpression expr) {
        return expr instanceof IExpressionFloat ? new ExpressionFloatCached((IExpressionFloat)expr) : (expr instanceof IExpressionFloatArray ? new ExpressionFloatArrayCached((IExpressionFloatArray)expr) : expr);
    }

    public static void parseAlphaStates(Properties props) {
        for (Object e : props.keySet()) {
            String s = (String)e;
            String[] astring = Config.tokenize(s, ".");
            if (astring.length != 2) continue;
            String s1 = astring[0];
            String s2 = astring[1];
            if (!s1.equals("alphaTest")) continue;
            Program program = Shaders.getProgram(s2);
            if (program == null) {
                SMCLog.severe("Invalid program name: " + s2);
                continue;
            }
            String s3 = props.getProperty(s).trim();
            GlAlphaState glalphastate = ShaderPackParser.parseAlphaState(s3);
            if (glalphastate == null) continue;
            program.setAlphaState(glalphastate);
        }
    }

    private static GlAlphaState parseAlphaState(String str) {
        String[] astring = Config.tokenize(str, " ");
        if (astring.length == 1) {
            String s = astring[0];
            if (s.equals("off") || s.equals("false")) {
                return new GlAlphaState(false);
            }
        } else if (astring.length == 2) {
            String s2 = astring[0];
            String s1 = astring[1];
            Integer integer = mapAlphaFuncs.get(s2);
            float f = Config.parseFloat(s1, -1.0f);
            if (integer != null && f >= 0.0f) {
                return new GlAlphaState(true, integer, f);
            }
        }
        SMCLog.severe("Invalid alpha test: " + str);
        return null;
    }

    public static void parseBlendStates(Properties props) {
        for (Object e : props.keySet()) {
            String s = (String)e;
            String[] astring = Config.tokenize(s, ".");
            if (astring.length != 2) continue;
            String s1 = astring[0];
            String s2 = astring[1];
            if (!s1.equals("blend")) continue;
            Program program = Shaders.getProgram(s2);
            if (program == null) {
                SMCLog.severe("Invalid program name: " + s2);
                continue;
            }
            String s3 = props.getProperty(s).trim();
            GlBlendState glblendstate = ShaderPackParser.parseBlendState(s3);
            if (glblendstate == null) continue;
            program.setBlendState(glblendstate);
        }
    }

    private static GlBlendState parseBlendState(String str) {
        String[] astring = Config.tokenize(str, " ");
        if (astring.length == 1) {
            String s = astring[0];
            if (s.equals("off") || s.equals("false")) {
                return new GlBlendState(false);
            }
        } else if (astring.length == 2 || astring.length == 4) {
            String s4 = astring[0];
            String s1 = astring[1];
            String s2 = s4;
            String s3 = s1;
            if (astring.length == 4) {
                s2 = astring[2];
                s3 = astring[3];
            }
            Integer integer = mapBlendFactors.get(s4);
            Integer integer1 = mapBlendFactors.get(s1);
            Integer integer2 = mapBlendFactors.get(s2);
            Integer integer3 = mapBlendFactors.get(s3);
            if (integer != null && integer1 != null && integer2 != null && integer3 != null) {
                return new GlBlendState(true, integer, integer1, integer2, integer3);
            }
        }
        SMCLog.severe("Invalid blend mode: " + str);
        return null;
    }

    public static void parseRenderScales(Properties props) {
        for (Object e : props.keySet()) {
            String s = (String)e;
            String[] astring = Config.tokenize(s, ".");
            if (astring.length != 2) continue;
            String s1 = astring[0];
            String s2 = astring[1];
            if (!s1.equals("scale")) continue;
            Program program = Shaders.getProgram(s2);
            if (program == null) {
                SMCLog.severe("Invalid program name: " + s2);
                continue;
            }
            String s3 = props.getProperty(s).trim();
            RenderScale renderscale = ShaderPackParser.parseRenderScale(s3);
            if (renderscale == null) continue;
            program.setRenderScale(renderscale);
        }
    }

    private static RenderScale parseRenderScale(String str) {
        String[] astring = Config.tokenize(str, " ");
        float f = Config.parseFloat(astring[0], -1.0f);
        float f1 = 0.0f;
        float f2 = 0.0f;
        if (astring.length > 1) {
            if (astring.length != 3) {
                SMCLog.severe("Invalid render scale: " + str);
                return null;
            }
            f1 = Config.parseFloat(astring[1], -1.0f);
            f2 = Config.parseFloat(astring[2], -1.0f);
        }
        if (Config.between(f, 0.0f, 1.0f) && Config.between(f1, 0.0f, 1.0f) && Config.between(f2, 0.0f, 1.0f)) {
            return new RenderScale(f, f1, f2);
        }
        SMCLog.severe("Invalid render scale: " + str);
        return null;
    }

    public static void parseBuffersFlip(Properties props) {
        for (Object e : props.keySet()) {
            String s = (String)e;
            String[] astring = Config.tokenize(s, ".");
            if (astring.length != 3) continue;
            String s1 = astring[0];
            String s2 = astring[1];
            String s3 = astring[2];
            if (!s1.equals("flip")) continue;
            Program program = Shaders.getProgram(s2);
            if (program == null) {
                SMCLog.severe("Invalid program name: " + s2);
                continue;
            }
            Boolean[] aboolean = program.getBuffersFlip();
            int i = Shaders.getBufferIndexFromString(s3);
            if (i >= 0 && i < aboolean.length) {
                String s4 = props.getProperty(s).trim();
                Boolean obool = Config.parseBoolean(s4, null);
                if (obool == null) {
                    SMCLog.severe("Invalid boolean value: " + s4);
                    continue;
                }
                aboolean[i] = obool;
                continue;
            }
            SMCLog.severe("Invalid buffer name: " + s3);
        }
    }

    private static Map<String, Integer> makeMapAlphaFuncs() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
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
        HashMap<String, Integer> map = new HashMap<String, Integer>();
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

