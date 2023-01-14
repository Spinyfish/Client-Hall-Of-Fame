package net.optifine.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.src.Config;

public class ReflectorMethod {
   private final ReflectorClass reflectorClass;
   private final String targetMethodName;
   private final Class[] targetMethodParameterTypes;
   private boolean checked = false;
   private Method targetMethod = null;

   public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName) {
      this(reflectorClass, targetMethodName, null, false);
   }

   public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName, Class[] targetMethodParameterTypes) {
      this(reflectorClass, targetMethodName, targetMethodParameterTypes, false);
   }

   public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName, Class[] targetMethodParameterTypes, boolean lazyResolve) {
      this.reflectorClass = reflectorClass;
      this.targetMethodName = targetMethodName;
      this.targetMethodParameterTypes = targetMethodParameterTypes;
      if (!lazyResolve) {
         Method var5 = this.getTargetMethod();
      }
   }

   public Method getTargetMethod() {
      if (this.checked) {
         return this.targetMethod;
      } else {
         this.checked = true;
         Class oclass = this.reflectorClass.getTargetClass();
         if (oclass == null) {
            return null;
         } else {
            try {
               if (this.targetMethodParameterTypes == null) {
                  Method[] amethod = getMethods(oclass, this.targetMethodName);
                  if (amethod.length <= 0) {
                     Config.log("(Reflector) Method not present: " + oclass.getName() + "." + this.targetMethodName);
                     return null;
                  }

                  if (amethod.length > 1) {
                     Config.warn("(Reflector) More than one method found: " + oclass.getName() + "." + this.targetMethodName);

                     for(Method method : amethod) {
                        Config.warn("(Reflector)  - " + method);
                     }

                     return null;
                  }

                  this.targetMethod = amethod[0];
               } else {
                  this.targetMethod = getMethod(oclass, this.targetMethodName, this.targetMethodParameterTypes);
               }

               if (this.targetMethod == null) {
                  Config.log("(Reflector) Method not present: " + oclass.getName() + "." + this.targetMethodName);
                  return null;
               } else {
                  this.targetMethod.setAccessible(true);
                  return this.targetMethod;
               }
            } catch (Throwable var7) {
               var7.printStackTrace();
               return null;
            }
         }
      }
   }

   public boolean exists() {
      return this.checked ? this.targetMethod != null : this.getTargetMethod() != null;
   }

   public Class getReturnType() {
      Method method = this.getTargetMethod();
      return method == null ? null : method.getReturnType();
   }

   public void deactivate() {
      this.checked = true;
      this.targetMethod = null;
   }

   public static Method getMethod(Class cls, String methodName, Class[] paramTypes) {
      Method[] amethod = cls.getDeclaredMethods();

      for(Method method : amethod) {
         if (method.getName().equals(methodName)) {
            Class[] aclass = method.getParameterTypes();
            if (Reflector.matchesTypes(paramTypes, aclass)) {
               return method;
            }
         }
      }

      return null;
   }

   public static Method[] getMethods(Class cls, String methodName) {
      List list = new ArrayList();
      Method[] amethod = cls.getDeclaredMethods();

      for(Method method : amethod) {
         if (method.getName().equals(methodName)) {
            list.add(method);
         }
      }

      return list.toArray(new Method[0]);
   }
}
