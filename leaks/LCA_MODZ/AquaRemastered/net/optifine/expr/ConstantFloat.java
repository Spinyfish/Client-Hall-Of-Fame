package net.optifine.expr;

public class ConstantFloat implements IExpressionFloat {
   private float value;

   public ConstantFloat(float value) {
      this.value = value;
   }

   @Override
   public float eval() {
      return this.value;
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public String toString() {
      return "" + this.value;
   }
}
