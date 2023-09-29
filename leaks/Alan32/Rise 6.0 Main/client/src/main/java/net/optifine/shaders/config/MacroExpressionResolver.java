package net.optifine.shaders.config;

import net.minecraft.src.Config;
import net.optifine.expr.*;

import java.util.Map;

public class MacroExpressionResolver implements IExpressionResolver {
    private Map<String, String> mapMacroValues = null;

    public MacroExpressionResolver(final Map<String, String> mapMacroValues) {
        this.mapMacroValues = mapMacroValues;
    }

    public IExpression getExpression(String name) {
        final String s = "defined_";

        if (name.startsWith(s)) {
            final String s2 = name.substring(s.length());
            return this.mapMacroValues.containsKey(s2) ? new FunctionBool(FunctionType.TRUE, null) : new FunctionBool(FunctionType.FALSE, null);
        } else {
            while (this.mapMacroValues.containsKey(name)) {
                final String s1 = this.mapMacroValues.get(name);

                if (s1 == null || s1.equals(name)) {
                    break;
                }

                name = s1;
            }

            final int i = Config.parseInt(name, Integer.MIN_VALUE);

            if (i == Integer.MIN_VALUE) {
                Config.warn("Unknown macro value: " + name);
                return new ConstantFloat(0.0F);
            } else {
                return new ConstantFloat((float) i);
            }
        }
    }
}
