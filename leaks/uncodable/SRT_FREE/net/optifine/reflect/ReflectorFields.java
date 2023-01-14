package net.optifine.reflect;

public class ReflectorFields {
   private final ReflectorClass reflectorClass;
   private final Class fieldType;
   private ReflectorField[] reflectorFields;

   public ReflectorFields(ReflectorClass reflectorClass, Class fieldType, int fieldCount) {
      this.reflectorClass = reflectorClass;
      this.fieldType = fieldType;
      if (reflectorClass.exists() && fieldType != null) {
         this.reflectorFields = new ReflectorField[fieldCount];

         for(int i = 0; i < this.reflectorFields.length; ++i) {
            this.reflectorFields[i] = new ReflectorField(reflectorClass, fieldType, i);
         }
      }
   }

   public ReflectorField getReflectorField(int index) {
      return index >= 0 && index < this.reflectorFields.length ? this.reflectorFields[index] : null;
   }
}
