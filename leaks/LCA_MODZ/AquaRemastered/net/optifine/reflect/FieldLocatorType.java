package net.optifine.reflect;

import java.lang.reflect.Field;
import net.optifine.Log;

public class FieldLocatorType implements IFieldLocator {
   private ReflectorClass reflectorClass = null;
   private Class targetFieldType = null;
   private int targetFieldIndex;

   public FieldLocatorType(ReflectorClass reflectorClass, Class targetFieldType) {
      this(reflectorClass, targetFieldType, 0);
   }

   public FieldLocatorType(ReflectorClass reflectorClass, Class targetFieldType, int targetFieldIndex) {
      this.reflectorClass = reflectorClass;
      this.targetFieldType = targetFieldType;
      this.targetFieldIndex = targetFieldIndex;
   }

   @Override
   public Field getField() {
      Class oclass = this.reflectorClass.getTargetClass();
      if (oclass == null) {
         return null;
      } else {
         try {
            Field[] afield = oclass.getDeclaredFields();
            int i = 0;

            for(int j = 0; j < afield.length; ++j) {
               Field field = afield[j];
               if (field.getType() == this.targetFieldType) {
                  if (i == this.targetFieldIndex) {
                     field.setAccessible(true);
                     return field;
                  }

                  ++i;
               }
            }

            Log.log("(Reflector) Field not present: " + oclass.getName() + ".(type: " + this.targetFieldType + ", index: " + this.targetFieldIndex + ")");
            return null;
         } catch (SecurityException var6) {
            var6.printStackTrace();
            return null;
         } catch (Throwable var7) {
            var7.printStackTrace();
            return null;
         }
      }
   }
}
