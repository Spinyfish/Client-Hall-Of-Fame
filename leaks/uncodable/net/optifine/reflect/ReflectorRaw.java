package net.optifine.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReflectorRaw {
   public static Field getField(Class cls, Class fieldType) {
      try {
         Field[] afield = cls.getDeclaredFields();

         for(Field field : afield) {
            if (field.getType() == fieldType) {
               field.setAccessible(true);
               return field;
            }
         }

         return null;
      } catch (Exception var7) {
         return null;
      }
   }

   public static Field[] getFields(Class cls, Class fieldType) {
      try {
         Field[] afield = cls.getDeclaredFields();
         return getFields(afield, fieldType);
      } catch (Exception var31) {
         return null;
      }
   }

   public static Field[] getFields(Field[] fields, Class fieldType) {
      try {
         List list = new ArrayList();

         for(Field field : fields) {
            if (field.getType() == fieldType) {
               field.setAccessible(true);
               list.add(field);
            }
         }

         return list.toArray(new Field[0]);
      } catch (Exception var7) {
         return null;
      }
   }

   public static Field[] getFieldsAfter(Class cls, Field field, Class fieldType) {
      try {
         Field[] afield = cls.getDeclaredFields();
         List<Field> list = Arrays.asList(afield);
         int i = list.indexOf(field);
         if (i < 0) {
            return new Field[0];
         } else {
            List<Field> list1 = list.subList(i + 1, list.size());
            Field[] afield1 = list1.toArray(new Field[0]);
            return getFields(afield1, fieldType);
         }
      } catch (Exception var81) {
         return null;
      }
   }

   public static Field getField(Class cls, Class fieldType, int index) {
      Field[] afield = getFields(cls, fieldType);
      return index >= 0 && index < ((Field[])Objects.requireNonNull(afield)).length ? afield[index] : null;
   }

   public static Object getFieldValue(Object obj, Class cls, Class fieldType, int index) {
      ReflectorField reflectorfield = getReflectorField(cls, fieldType, index);
      return reflectorfield == null ? null : (!reflectorfield.exists() ? null : Reflector.getFieldValue(obj, reflectorfield));
   }

   public static ReflectorField getReflectorField(Class cls, Class fieldType) {
      Field field = getField(cls, fieldType);
      if (field == null) {
         return null;
      } else {
         ReflectorClass reflectorclass = new ReflectorClass(cls);
         return new ReflectorField(reflectorclass, field.getName());
      }
   }

   public static ReflectorField getReflectorField(Class cls, Class fieldType, int index) {
      Field field = getField(cls, fieldType, index);
      if (field == null) {
         return null;
      } else {
         ReflectorClass reflectorclass = new ReflectorClass(cls);
         return new ReflectorField(reflectorclass, field.getName());
      }
   }
}
