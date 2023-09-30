package net.optifine.shaders.uniform;

import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.expr.ConstantFloat;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;
import net.optifine.shaders.SMCLog;

import java.util.HashMap;
import java.util.Map;

public class ShaderExpressionResolver implements IExpressionResolver {
    private final Map<String, IExpression> mapExpressions = new HashMap();

    public ShaderExpressionResolver(final Map<String, IExpression> map) {
        this.registerExpressions();

        for (final String s : map.keySet()) {
            final IExpression iexpression = map.get(s);
            this.registerExpression(s, iexpression);
        }
    }

    private void registerExpressions() {
        final ShaderParameterFloat[] ashaderparameterfloat = ShaderParameterFloat.values();

        for (int i = 0; i < ashaderparameterfloat.length; ++i) {
            final ShaderParameterFloat shaderparameterfloat = ashaderparameterfloat[i];
            this.addParameterFloat(this.mapExpressions, shaderparameterfloat);
        }

        final ShaderParameterBool[] ashaderparameterbool = ShaderParameterBool.values();

        for (int k = 0; k < ashaderparameterbool.length; ++k) {
            final ShaderParameterBool shaderparameterbool = ashaderparameterbool[k];
            this.mapExpressions.put(shaderparameterbool.getName(), shaderparameterbool);
        }

        for (final BiomeGenBase biomegenbase : BiomeGenBase.BIOME_ID_MAP.values()) {
            String s = biomegenbase.biomeName.trim();
            s = "BIOME_" + s.toUpperCase().replace(' ', '_');
            final int j = biomegenbase.biomeID;
            final IExpression iexpression = new ConstantFloat((float) j);
            this.registerExpression(s, iexpression);
        }
    }

    private void addParameterFloat(final Map<String, IExpression> map, final ShaderParameterFloat spf) {
        final String[] astring = spf.getIndexNames1();

        if (astring == null) {
            map.put(spf.getName(), new ShaderParameterIndexed(spf));
        } else {
            for (int i = 0; i < astring.length; ++i) {
                final String s = astring[i];
                final String[] astring1 = spf.getIndexNames2();

                if (astring1 == null) {
                    map.put(spf.getName() + "." + s, new ShaderParameterIndexed(spf, i));
                } else {
                    for (int j = 0; j < astring1.length; ++j) {
                        final String s1 = astring1[j];
                        map.put(spf.getName() + "." + s + "." + s1, new ShaderParameterIndexed(spf, i, j));
                    }
                }
            }
        }
    }

    public boolean registerExpression(final String name, final IExpression expr) {
        if (this.mapExpressions.containsKey(name)) {
            SMCLog.warning("Expression already defined: " + name);
            return false;
        } else {
            this.mapExpressions.put(name, expr);
            return true;
        }
    }

    public IExpression getExpression(final String name) {
        return this.mapExpressions.get(name);
    }

    public boolean hasExpression(final String name) {
        return this.mapExpressions.containsKey(name);
    }
}
