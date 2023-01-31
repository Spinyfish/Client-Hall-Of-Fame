package net.java.games.input;

import java.security.PrivilegedAction;

class LinuxEnvironmentPlugin$4 implements PrivilegedAction {
   LinuxEnvironmentPlugin$4(LinuxEnvironmentPlugin var1) {
      this.this$0 = var1;
   }

   public final Object run() {
      Runtime.getRuntime().addShutdownHook(this.this$0.new ShutdownHook(null));
      return null;
   }
}
