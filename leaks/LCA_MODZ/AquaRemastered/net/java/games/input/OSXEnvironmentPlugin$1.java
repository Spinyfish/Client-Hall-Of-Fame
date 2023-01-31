package net.java.games.input;

import java.io.File;
import java.security.PrivilegedAction;

final class OSXEnvironmentPlugin$1 implements PrivilegedAction {
   OSXEnvironmentPlugin$1(String var1) {
      this.val$lib_name = var1;
   }

   public final Object run() {
      try {
         String lib_path = System.getProperty("net.java.games.input.librarypath");
         if (lib_path != null) {
            System.load(lib_path + File.separator + System.mapLibraryName(this.val$lib_name));
         } else {
            System.loadLibrary(this.val$lib_name);
         }
      } catch (UnsatisfiedLinkError var2) {
         var2.printStackTrace();
         OSXEnvironmentPlugin.access$002(false);
      }

      return null;
   }
}
