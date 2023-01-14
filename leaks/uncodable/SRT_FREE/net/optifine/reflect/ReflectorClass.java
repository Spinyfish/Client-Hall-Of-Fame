package net.optifine.reflect;

import net.minecraft.src.Config;

public class ReflectorClass {
   private final String targetClassName;
   private boolean checked;
   private Class targetClass;

   public ReflectorClass(String targetClassName) {
      this.checked = false;
      this.targetClass = null;
      this.targetClassName = targetClassName;
   }

   public ReflectorClass(Class targetClass) {
      this.targetClass = targetClass;
      this.targetClassName = targetClass.getName();
      this.checked = true;
   }

   public Class getTargetClass() {
      if (!this.checked) {
         this.checked = true;

         try {
            this.targetClass = Class.forName(this.targetClassName);
         } catch (ClassNotFoundException var2) {
            Config.log("(Reflector) Class not present: " + this.targetClassName);
         } catch (Throwable var3) {
            var3.printStackTrace();
         }
      }

      return this.targetClass;
   }

   public boolean exists() {
      return this.getTargetClass() != null;
   }

   public boolean isInstance(Object obj) {
      return this.getTargetClass() != null && this.getTargetClass().isInstance(obj);
   }
}
