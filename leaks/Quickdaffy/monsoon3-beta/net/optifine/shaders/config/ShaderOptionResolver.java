/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.shaders.config;

import java.util.HashMap;
import java.util.Map;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;
import net.optifine.shaders.config.ExpressionShaderOptionSwitch;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionSwitch;

public class ShaderOptionResolver
implements IExpressionResolver {
    private Map<String, ExpressionShaderOptionSwitch> mapOptions = new HashMap<String, ExpressionShaderOptionSwitch>();

    public ShaderOptionResolver(ShaderOption[] options) {
        for (int i = 0; i < options.length; ++i) {
            ShaderOption shaderoption = options[i];
            if (!(shaderoption instanceof ShaderOptionSwitch)) continue;
            ShaderOptionSwitch shaderoptionswitch = (ShaderOptionSwitch)shaderoption;
            ExpressionShaderOptionSwitch expressionshaderoptionswitch = new ExpressionShaderOptionSwitch(shaderoptionswitch);
            this.mapOptions.put(shaderoption.getName(), expressionshaderoptionswitch);
        }
    }

    @Override
    public IExpression getExpression(String name) {
        ExpressionShaderOptionSwitch expressionshaderoptionswitch = this.mapOptions.get(name);
        return expressionshaderoptionswitch;
    }
}

