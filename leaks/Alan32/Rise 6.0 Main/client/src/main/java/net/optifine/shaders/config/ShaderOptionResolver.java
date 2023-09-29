package net.optifine.shaders.config;

import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;

import java.util.HashMap;
import java.util.Map;

public class ShaderOptionResolver implements IExpressionResolver {
    private final Map<String, ExpressionShaderOptionSwitch> mapOptions = new HashMap();

    public ShaderOptionResolver(final ShaderOption[] options) {
        for (int i = 0; i < options.length; ++i) {
            final ShaderOption shaderoption = options[i];

            if (shaderoption instanceof ShaderOptionSwitch) {
                final ShaderOptionSwitch shaderoptionswitch = (ShaderOptionSwitch) shaderoption;
                final ExpressionShaderOptionSwitch expressionshaderoptionswitch = new ExpressionShaderOptionSwitch(shaderoptionswitch);
                this.mapOptions.put(shaderoption.getName(), expressionshaderoptionswitch);
            }
        }
    }

    public IExpression getExpression(final String name) {
        final ExpressionShaderOptionSwitch expressionshaderoptionswitch = this.mapOptions.get(name);
        return expressionshaderoptionswitch;
    }
}
