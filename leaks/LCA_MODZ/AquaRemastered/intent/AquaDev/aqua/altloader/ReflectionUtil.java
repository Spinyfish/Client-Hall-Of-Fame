package intent.AquaDev.aqua.altloader;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

public class ReflectionUtil {
   public static Object getFieldByClass(Object o, Class<?> searchingClazz) {
      return getFieldByClass(o.getClass(), o, searchingClazz);
   }

   public static Object getFieldByClass(Class<?> parentClass, Object o, Class<?> searchingClazz) {
      AccessibleObject field = null;

      for(Field f : parentClass.getDeclaredFields()) {
         if (f.getType().equals(searchingClazz)) {
            field = f;
            break;
         }
      }

      if (field == null) {
         return null;
      } else {
         try {
            boolean isAccessible = field.isAccessible();
            ((Field)field).setAccessible(true);
            Object toReturn = ((Field)field).get(o);
            ((Field)field).setAccessible(isAccessible);
            return toReturn;
         } catch (IllegalAccessException var8) {
            var8.printStackTrace();
            return null;
         }
      }
   }

   public static void setFieldByClass(Object parentObject, Object newObject) {
      AccessibleObject field = null;

      for(Field f : parentObject.getClass().getDeclaredFields()) {
         if (f.getType().isInstance(newObject)) {
            field = f;
            break;
         }
      }

      if (field != null) {
         try {
            boolean accessible = field.isAccessible();
            ((Field)field).setAccessible(true);
            ((Field)field).set(parentObject, newObject);
            ((Field)field).setAccessible(accessible);
         } catch (IllegalAccessException var7) {
            var7.printStackTrace();
         }
      }
   }

   public static void setStaticField(Class clazz, String fieldName, Object value) {
      try {
         Field staticField = clazz.getDeclaredField(fieldName);
         staticField.setAccessible(true);
         Field modifiersField = Field.class.getDeclaredField("modifiers");
         modifiersField.setAccessible(true);
         modifiersField.setInt(staticField, staticField.getModifiers() & -17);
         staticField.set(null, value);
      } catch (NoSuchFieldException var5) {
         var5.printStackTrace();
      } catch (IllegalAccessException var6) {
         var6.printStackTrace();
      }
   }
}
