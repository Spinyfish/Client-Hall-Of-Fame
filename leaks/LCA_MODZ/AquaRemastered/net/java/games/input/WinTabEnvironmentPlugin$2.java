package net.java.games.input;

import java.security.PrivilegedAction;

final class WinTabEnvironmentPlugin$2 implements PrivilegedAction {
   WinTabEnvironmentPlugin$2(String var1) {
      this.val$property = var1;
   }

   public Object run() {
      return System.getProperty(this.val$property);
   }
}
