package net.java.games.input;

import java.security.PrivilegedAction;

final class OSXEnvironmentPlugin$3 implements PrivilegedAction {
   OSXEnvironmentPlugin$3(String var1, String var2) {
      this.val$property = var1;
      this.val$default_value = var2;
   }

   public Object run() {
      return System.getProperty(this.val$property, this.val$default_value);
   }
}
