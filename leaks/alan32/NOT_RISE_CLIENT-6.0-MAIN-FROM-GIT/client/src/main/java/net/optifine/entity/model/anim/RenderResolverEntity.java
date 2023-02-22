package net.optifine.entity.model.anim;

import net.optifine.expr.IExpression;

public class RenderResolverEntity implements IRenderResolver {
    public IExpression getParameter(final String name) {
        final RenderEntityParameterBool renderentityparameterbool = RenderEntityParameterBool.parse(name);

        if (renderentityparameterbool != null) {
            return renderentityparameterbool;
        } else {
            final RenderEntityParameterFloat renderentityparameterfloat = RenderEntityParameterFloat.parse(name);
            return renderentityparameterfloat;
        }
    }
}
