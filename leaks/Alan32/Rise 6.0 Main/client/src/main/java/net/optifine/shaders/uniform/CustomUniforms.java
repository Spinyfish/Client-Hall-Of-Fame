package net.optifine.shaders.uniform;

import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionCached;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomUniforms {
    private final CustomUniform[] uniforms;
    private final IExpressionCached[] expressionsCached;

    public CustomUniforms(final CustomUniform[] uniforms, final Map<String, IExpression> mapExpressions) {
        this.uniforms = uniforms;
        final List<IExpressionCached> list = new ArrayList();

        for (final String s : mapExpressions.keySet()) {
            final IExpression iexpression = mapExpressions.get(s);

            if (iexpression instanceof IExpressionCached) {
                final IExpressionCached iexpressioncached = (IExpressionCached) iexpression;
                list.add(iexpressioncached);
            }
        }

        this.expressionsCached = list.toArray(new IExpressionCached[list.size()]);
    }

    public void setProgram(final int program) {
        for (int i = 0; i < this.uniforms.length; ++i) {
            final CustomUniform customuniform = this.uniforms[i];
            customuniform.setProgram(program);
        }
    }

    public void update() {
        this.resetCache();

        for (int i = 0; i < this.uniforms.length; ++i) {
            final CustomUniform customuniform = this.uniforms[i];
            customuniform.update();
        }
    }

    private void resetCache() {
        for (int i = 0; i < this.expressionsCached.length; ++i) {
            final IExpressionCached iexpressioncached = this.expressionsCached[i];
            iexpressioncached.reset();
        }
    }

    public void reset() {
        for (int i = 0; i < this.uniforms.length; ++i) {
            final CustomUniform customuniform = this.uniforms[i];
            customuniform.reset();
        }
    }
}
