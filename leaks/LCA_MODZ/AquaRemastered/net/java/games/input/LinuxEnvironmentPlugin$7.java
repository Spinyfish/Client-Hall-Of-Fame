package net.java.games.input;

import java.io.File;
import java.io.FilenameFilter;
import java.security.PrivilegedAction;
import java.util.Arrays;

final class LinuxEnvironmentPlugin$7 implements PrivilegedAction {
   LinuxEnvironmentPlugin$7(File var1, FilenameFilter var2) {
      this.val$dir = var1;
      this.val$filter = var2;
   }

   public Object run() {
      File[] files = this.val$dir.listFiles(this.val$filter);
      Arrays.sort(files, new LinuxEnvironmentPlugin$7$1(this));
      return files;
   }
}
