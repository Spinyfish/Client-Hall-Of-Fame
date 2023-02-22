package net.optifine.shaders.config;

import net.minecraft.src.Config;
import net.optifine.expr.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroState {
    private boolean active = true;
    private final Deque<Boolean> dequeState = new ArrayDeque();
    private final Deque<Boolean> dequeResolved = new ArrayDeque();
    private final Map<String, String> mapMacroValues = new HashMap();
    private static final Pattern PATTERN_DIRECTIVE = Pattern.compile("\\s*#\\s*(\\w+)\\s*(.*)");
    private static final Pattern PATTERN_DEFINED = Pattern.compile("defined\\s+(\\w+)");
    private static final Pattern PATTERN_DEFINED_FUNC = Pattern.compile("defined\\s*\\(\\s*(\\w+)\\s*\\)");
    private static final Pattern PATTERN_MACRO = Pattern.compile("(\\w+)");
    private static final String DEFINE = "define";
    private static final String UNDEF = "undef";
    private static final String IFDEF = "ifdef";
    private static final String IFNDEF = "ifndef";
    private static final String IF = "if";
    private static final String ELSE = "else";
    private static final String ELIF = "elif";
    private static final String ENDIF = "endif";
    private static final List<String> MACRO_NAMES = Arrays.asList("define", "undef", "ifdef", "ifndef", "if", "else", "elif", "endif");

    public boolean processLine(final String line) {
        final Matcher matcher = PATTERN_DIRECTIVE.matcher(line);

        if (!matcher.matches()) {
            return this.active;
        } else {
            final String s = matcher.group(1);
            String s1 = matcher.group(2);
            final int i = s1.indexOf("//");

            if (i >= 0) {
                s1 = s1.substring(0, i);
            }

            final boolean flag = this.active;
            this.processMacro(s, s1);
            this.active = !this.dequeState.contains(Boolean.FALSE);
            return this.active || flag;
        }
    }

    public static boolean isMacroLine(final String line) {
        final Matcher matcher = PATTERN_DIRECTIVE.matcher(line);

        if (!matcher.matches()) {
            return false;
        } else {
            final String s = matcher.group(1);
            return MACRO_NAMES.contains(s);
        }
    }

    private void processMacro(final String name, final String param) {
        final StringTokenizer stringtokenizer = new StringTokenizer(param, " \t");
        final String s = stringtokenizer.hasMoreTokens() ? stringtokenizer.nextToken() : "";
        final String s1 = stringtokenizer.hasMoreTokens() ? stringtokenizer.nextToken("").trim() : "";

        if (name.equals("define")) {
            this.mapMacroValues.put(s, s1);
        } else if (name.equals("undef")) {
            this.mapMacroValues.remove(s);
        } else if (name.equals("ifdef")) {
            final boolean flag6 = this.mapMacroValues.containsKey(s);
            this.dequeState.add(Boolean.valueOf(flag6));
            this.dequeResolved.add(Boolean.valueOf(flag6));
        } else if (name.equals("ifndef")) {
            final boolean flag5 = !this.mapMacroValues.containsKey(s);
            this.dequeState.add(Boolean.valueOf(flag5));
            this.dequeResolved.add(Boolean.valueOf(flag5));
        } else if (name.equals("if")) {
            final boolean flag4 = this.eval(param);
            this.dequeState.add(Boolean.valueOf(flag4));
            this.dequeResolved.add(Boolean.valueOf(flag4));
        } else if (!this.dequeState.isEmpty()) {
            if (name.equals("elif")) {
                final boolean flag3 = this.dequeState.removeLast().booleanValue();
                final boolean flag7 = this.dequeResolved.removeLast().booleanValue();

                if (flag7) {
                    this.dequeState.add(Boolean.valueOf(false));
                    this.dequeResolved.add(Boolean.valueOf(flag7));
                } else {
                    final boolean flag8 = this.eval(param);
                    this.dequeState.add(Boolean.valueOf(flag8));
                    this.dequeResolved.add(Boolean.valueOf(flag8));
                }
            } else if (name.equals("else")) {
                final boolean flag = this.dequeState.removeLast().booleanValue();
                final boolean flag1 = this.dequeResolved.removeLast().booleanValue();
                final boolean flag2 = !flag1;
                this.dequeState.add(Boolean.valueOf(flag2));
                this.dequeResolved.add(Boolean.valueOf(true));
            } else if (name.equals("endif")) {
                this.dequeState.removeLast();
                this.dequeResolved.removeLast();
            }
        }
    }

    private boolean eval(String str) {
        final Matcher matcher = PATTERN_DEFINED.matcher(str);
        str = matcher.replaceAll("defined_$1");
        final Matcher matcher1 = PATTERN_DEFINED_FUNC.matcher(str);
        str = matcher1.replaceAll("defined_$1");
        boolean flag = false;
        int i = 0;

        while (true) {
            flag = false;
            final Matcher matcher2 = PATTERN_MACRO.matcher(str);

            while (matcher2.find()) {
                final String s = matcher2.group();

                if (s.length() > 0) {
                    final char c0 = s.charAt(0);

                    if ((Character.isLetter(c0) || c0 == 95) && this.mapMacroValues.containsKey(s)) {
                        String s1 = this.mapMacroValues.get(s);

                        if (s1 == null) {
                            s1 = "1";
                        }

                        final int j = matcher2.start();
                        final int k = matcher2.end();
                        str = str.substring(0, j) + " " + s1 + " " + str.substring(k);
                        flag = true;
                        ++i;
                        break;
                    }
                }
            }

            if (!flag || i >= 100) {
                break;
            }
        }

        if (i >= 100) {
            Config.warn("Too many iterations: " + i + ", when resolving: " + str);
            return true;
        } else {
            try {
                final IExpressionResolver iexpressionresolver = new MacroExpressionResolver(this.mapMacroValues);
                final ExpressionParser expressionparser = new ExpressionParser(iexpressionresolver);
                final IExpression iexpression = expressionparser.parse(str);

                if (iexpression.getExpressionType() == ExpressionType.BOOL) {
                    final IExpressionBool iexpressionbool = (IExpressionBool) iexpression;
                    final boolean flag1 = iexpressionbool.eval();
                    return flag1;
                } else if (iexpression.getExpressionType() == ExpressionType.FLOAT) {
                    final IExpressionFloat iexpressionfloat = (IExpressionFloat) iexpression;
                    final float f = iexpressionfloat.eval();
                    final boolean flag2 = f != 0.0F;
                    return flag2;
                } else {
                    throw new ParseException("Not a boolean or float expression: " + iexpression.getExpressionType());
                }
            } catch (final ParseException parseexception) {
                Config.warn("Invalid macro expression: " + str);
                Config.warn("Error: " + parseexception.getMessage());
                return false;
            }
        }
    }
}
