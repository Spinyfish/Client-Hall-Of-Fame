package net.optifine.entity.model.anim;

import net.optifine.expr.IExpression;

public class RenderResolverEntity implements IRenderResolver {
   @Override
   public IExpression getParameter(String name) {
      RenderEntityParameterBool renderentityparameterbool = RenderEntityParameterBool.parse(name);
      return (IExpression)(renderentityparameterbool != null ? renderentityparameterbool : RenderEntityParameterFloat.parse(name));
   }
}
