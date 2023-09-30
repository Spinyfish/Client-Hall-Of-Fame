package net.optifine.expr;

public class Parameters implements IParameters {
    private final ExpressionType[] parameterTypes;

    public Parameters(final ExpressionType[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public ExpressionType[] getParameterTypes(final IExpression[] params) {
        return this.parameterTypes;
    }
}
