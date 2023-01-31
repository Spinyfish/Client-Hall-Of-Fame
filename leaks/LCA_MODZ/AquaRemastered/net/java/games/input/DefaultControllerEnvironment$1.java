package net.java.games.input;

import java.io.File;
import java.security.PrivilegedAction;

final class DefaultControllerEnvironment$1 implements PrivilegedAction {
   DefaultControllerEnvironment$1(String var1) {
      this.val$lib_name = var1;
   }

   public final Object run() {
      String lib_path = System.getProperty("net.java.games.input.librarypath");
      if (lib_path != null) {
         System.load(lib_path + File.separator + System.mapLibraryName(this.val$lib_name));
      } else {
         System.loadLibrary(this.val$lib_name);
      }

      return null;
   }
}
