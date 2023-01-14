package net.optifine.shaders.config;

import java.util.HashMap;
import java.util.Map;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;

public class ShaderOptionResolver implements IExpressionResolver {
   private final Map<String, ExpressionShaderOptionSwitch> mapOptions = new HashMap<>();

   public ShaderOptionResolver(ShaderOption[] options) {
      for(ShaderOption shaderoption : options) {
         if (shaderoption instanceof ShaderOptionSwitch) {
            ShaderOptionSwitch shaderoptionswitch = (ShaderOptionSwitch)shaderoption;
            ExpressionShaderOptionSwitch expressionshaderoptionswitch = new ExpressionShaderOptionSwitch(shaderoptionswitch);
            this.mapOptions.put(shaderoption.getName(), expressionshaderoptionswitch);
         }
      }
   }

   @Override
   public IExpression getExpression(String name) {
      return this.mapOptions.get(name);
   }
}
