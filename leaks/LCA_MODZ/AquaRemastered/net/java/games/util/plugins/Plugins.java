package net.java.games.util.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Plugins {
   static final boolean DEBUG = true;
   List pluginList = new ArrayList();

   public Plugins(File pluginRoot) throws IOException {
      this.scanPlugins(pluginRoot);
   }

   private void scanPlugins(File dir) throws IOException {
      File[] files = dir.listFiles();
      if (files == null) {
         throw new FileNotFoundException("Plugin directory " + dir.getName() + " not found.");
      } else {
         for(int i = 0; i < files.length; ++i) {
            File f = files[i];
            if (f.getName().endsWith(".jar")) {
               this.processJar(f);
            } else if (f.isDirectory()) {
               this.scanPlugins(f);
            }
         }
      }
   }

   private void processJar(File f) {
      try {
         System.out.println("Scanning jar: " + f.getName());
         PluginLoader loader = new PluginLoader(f);
         JarFile jf = new JarFile(f);
         Enumeration en = jf.entries();

         while(en.hasMoreElements()) {
            JarEntry je = (JarEntry)en.nextElement();
            System.out.println("Examining file : " + je.getName());
            if (je.getName().endsWith("Plugin.class")) {
               System.out.println("Found candidate class: " + je.getName());
               String cname = je.getName();
               cname = cname.substring(0, cname.length() - 6);
               cname = cname.replace('/', '.');
               Class pc = loader.loadClass(cname);
               if (loader.attemptPluginDefine(pc)) {
                  System.out.println("Adding class to plugins:" + pc.getName());
                  this.pluginList.add(pc);
               }
            }
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public Class[] get() {
      Class[] pluginArray = new Class[this.pluginList.size()];
      return this.pluginList.toArray(pluginArray);
   }

   public Class[] getImplementsAny(Class[] interfaces) {
      List matchList = new ArrayList(this.pluginList.size());
      Set interfaceSet = new HashSet();

      for(int i = 0; i < interfaces.length; ++i) {
         interfaceSet.add(interfaces[i]);
      }

      for(Class pluginClass : this.pluginList) {
         if (this.classImplementsAny(pluginClass, interfaceSet)) {
            matchList.add(pluginClass);
         }
      }

      Class[] pluginArray = new Class[matchList.size()];
      return matchList.toArray(pluginArray);
   }

   private boolean classImplementsAny(Class testClass, Set interfaces) {
      if (testClass == null) {
         return false;
      } else {
         Class[] implementedInterfaces = testClass.getInterfaces();

         for(int i = 0; i < implementedInterfaces.length; ++i) {
            if (interfaces.contains(implementedInterfaces[i])) {
               return true;
            }
         }

         for(int i = 0; i < implementedInterfaces.length; ++i) {
            if (this.classImplementsAny(implementedInterfaces[i], interfaces)) {
               return true;
            }
         }

         return this.classImplementsAny(testClass.getSuperclass(), interfaces);
      }
   }

   public Class[] getImplementsAll(Class[] interfaces) {
      List matchList = new ArrayList(this.pluginList.size());
      Set interfaceSet = new HashSet();

      for(int i = 0; i < interfaces.length; ++i) {
         interfaceSet.add(interfaces[i]);
      }

      for(Class pluginClass : this.pluginList) {
         if (this.classImplementsAll(pluginClass, interfaceSet)) {
            matchList.add(pluginClass);
         }
      }

      Class[] pluginArray = new Class[matchList.size()];
      return matchList.toArray(pluginArray);
   }

   private boolean classImplementsAll(Class testClass, Set interfaces) {
      if (testClass == null) {
         return false;
      } else {
         Class[] implementedInterfaces = testClass.getInterfaces();

         for(int i = 0; i < implementedInterfaces.length; ++i) {
            if (interfaces.contains(implementedInterfaces[i])) {
               interfaces.remove(implementedInterfaces[i]);
               if (interfaces.size() == 0) {
                  return true;
               }
            }
         }

         for(int i = 0; i < implementedInterfaces.length; ++i) {
            if (this.classImplementsAll(implementedInterfaces[i], interfaces)) {
               return true;
            }
         }

         return this.classImplementsAll(testClass.getSuperclass(), interfaces);
      }
   }

   public Class[] getExtends(Class superclass) {
      List matchList = new ArrayList(this.pluginList.size());

      for(Class pluginClass : this.pluginList) {
         if (this.classExtends(pluginClass, superclass)) {
            matchList.add(pluginClass);
         }
      }

      Class[] pluginArray = new Class[matchList.size()];
      return matchList.toArray(pluginArray);
   }

   private boolean classExtends(Class testClass, Class superclass) {
      if (testClass == null) {
         return false;
      } else {
         return testClass == superclass ? true : this.classExtends(testClass.getSuperclass(), superclass);
      }
   }
}
