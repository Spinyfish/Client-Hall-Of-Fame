package net.java.games.input;

import java.io.File;
import java.io.FilenameFilter;

final class LinuxEnvironmentPlugin$5 implements FilenameFilter {
   public final boolean accept(File dir, String name) {
      return name.startsWith("js");
   }
}
