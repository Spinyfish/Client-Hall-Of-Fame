package net.optifine.expr;

public class ExpressionFloatArrayCached implements IExpressionFloatArray, IExpressionCached {
   private IExpressionFloatArray expression;
   private boolean cached;
   private float[] value;

   public ExpressionFloatArrayCached(IExpressionFloatArray expression) {
      this.expression = expression;
   }

   @Override
   public float[] eval() {
      if (!this.cached) {
         this.value = this.expression.eval();
         this.cached = true;
      }

      return this.value;
   }

   @Override
   public void reset() {
      this.cached = false;
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public String toString() {
      return "cached(" + this.expression + ")";
   }
}
