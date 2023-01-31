package net.java.games.input;

import java.io.File;
import java.util.Comparator;

class LinuxEnvironmentPlugin$7$1 implements Comparator {
   LinuxEnvironmentPlugin$7$1(LinuxEnvironmentPlugin$7 var1) {
      this.this$0 = var1;
   }

   public int compare(Object f1, Object f2) {
      return ((File)f1).getName().compareTo(((File)f2).getName());
   }
}
