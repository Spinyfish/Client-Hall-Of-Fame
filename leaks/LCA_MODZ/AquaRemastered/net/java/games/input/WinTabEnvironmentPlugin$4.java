package net.java.games.input;

import java.security.PrivilegedAction;

class WinTabEnvironmentPlugin$4 implements PrivilegedAction {
   WinTabEnvironmentPlugin$4(WinTabEnvironmentPlugin var1) {
      this.this$0 = var1;
   }

   public final Object run() {
      Runtime.getRuntime().addShutdownHook(this.this$0.new ShutdownHook(null));
      return null;
   }
}
