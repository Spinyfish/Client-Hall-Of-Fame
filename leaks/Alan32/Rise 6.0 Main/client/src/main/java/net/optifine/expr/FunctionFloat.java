package net.optifine.expr;

import net.optifine.shaders.uniform.Smoother;

public class FunctionFloat implements IExpressionFloat {
    private final FunctionType type;
    private final IExpression[] arguments;
    private int smoothId = -1;

    public FunctionFloat(final FunctionType type, final IExpression[] arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    public float eval() {
        final IExpression[] aiexpression = this.arguments;

        switch (this.type) {
            case SMOOTH:
                final IExpression iexpression = aiexpression[0];

                if (!(iexpression instanceof ConstantFloat)) {
                    final float f = evalFloat(aiexpression, 0);
                    final float f1 = aiexpression.length > 1 ? evalFloat(aiexpression, 1) : 1.0F;
                    final float f2 = aiexpression.length > 2 ? evalFloat(aiexpression, 2) : f1;

                    if (this.smoothId < 0) {
                        this.smoothId = Smoother.getNextId();
                    }

                    final float f3 = Smoother.getSmoothValue(this.smoothId, f, f1, f2);
                    return f3;
                }

            default:
                return this.type.evalFloat(this.arguments);
        }
    }

    private static float evalFloat(final IExpression[] exprs, final int index) {
        final IExpressionFloat iexpressionfloat = (IExpressionFloat) exprs[index];
        final float f = iexpressionfloat.eval();
        return f;
    }

    public ExpressionType getExpressionType() {
        return ExpressionType.FLOAT;
    }

    public String toString() {
        return "" + this.type + "()";
    }
}
