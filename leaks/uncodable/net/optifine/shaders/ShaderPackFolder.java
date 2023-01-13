package net.optifine.shaders;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import net.optifine.util.StrUtils;

public class ShaderPackFolder implements IShaderPack {
   protected final File packFile;

   public ShaderPackFolder(File file) {
      this.packFile = file;
   }

   @Override
   public void close() {
   }

   @Override
   public InputStream getResourceAsStream(String resName) {
      try {
         String s = StrUtils.removePrefixSuffix(resName, "/", "/");
         File file1 = new File(this.packFile, s);
         return !file1.exists() ? null : new BufferedInputStream(new FileInputStream(file1));
      } catch (Exception var41) {
         return null;
      }
   }

   @Override
   public boolean hasDirectory(String name) {
      File file1 = new File(this.packFile, name.substring(1));
      return file1.exists() && file1.isDirectory();
   }

   @Override
   public String getName() {
      return this.packFile.getName();
   }
}
