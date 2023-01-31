package net.java.games.input;

import java.io.File;
import java.io.FilenameFilter;

class LinuxEnvironmentPlugin$8 implements FilenameFilter {
   LinuxEnvironmentPlugin$8(LinuxEnvironmentPlugin var1) {
      this.this$0 = var1;
   }

   public final boolean accept(File dir, String name) {
      return name.startsWith("event");
   }
}
