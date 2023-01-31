package net.java.games.util.plugins;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginLoader extends URLClassLoader {
   static final boolean DEBUG = false;
   File parentDir;
   boolean localDLLs = true;

   public PluginLoader(File jf) throws MalformedURLException {
      super(new URL[]{jf.toURL()}, Thread.currentThread().getContextClassLoader());
      this.parentDir = jf.getParentFile();
      if (System.getProperty("net.java.games.util.plugins.nolocalnative") != null) {
         this.localDLLs = false;
      }
   }

   protected String findLibrary(String libname) {
      return this.localDLLs ? this.parentDir.getPath() + File.separator + System.mapLibraryName(libname) : super.findLibrary(libname);
   }

   public boolean attemptPluginDefine(Class pc) {
      return !pc.isInterface() && this.classImplementsPlugin(pc);
   }

   private boolean classImplementsPlugin(Class testClass) {
      if (testClass == null) {
         return false;
      } else {
         Class[] implementedInterfaces = testClass.getInterfaces();

         for(int i = 0; i < implementedInterfaces.length; ++i) {
            if (implementedInterfaces[i] == Plugin.class) {
               return true;
            }
         }

         for(int i = 0; i < implementedInterfaces.length; ++i) {
            if (this.classImplementsPlugin(implementedInterfaces[i])) {
               return true;
            }
         }

         return this.classImplementsPlugin(testClass.getSuperclass());
      }
   }
}
