package net.java.games.input;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import net.java.games.util.plugins.Plugin;

public class WinTabEnvironmentPlugin extends ControllerEnvironment implements Plugin {
   private static boolean supported = false;
   private final Controller[] controllers;
   private final List active_devices = new ArrayList();
   private final WinTabContext winTabContext;

   static void loadLibrary(String lib_name) {
      AccessController.doPrivileged(new WinTabEnvironmentPlugin$1(lib_name));
   }

   static String getPrivilegedProperty(String property) {
      return AccessController.doPrivileged(new WinTabEnvironmentPlugin$2(property));
   }

   static String getPrivilegedProperty(String property, String default_value) {
      return AccessController.doPrivileged(new WinTabEnvironmentPlugin$3(property, default_value));
   }

   public WinTabEnvironmentPlugin() {
      if (this.isSupported()) {
         DummyWindow window = null;
         WinTabContext winTabContext = null;
         Controller[] controllers = new Controller[0];

         try {
            window = new DummyWindow();
            winTabContext = new WinTabContext(window);

            try {
               winTabContext.open();
               controllers = winTabContext.getControllers();
            } catch (Exception var5) {
               window.destroy();
               throw var5;
            }
         } catch (Exception var6) {
            logln("Failed to enumerate devices: " + var6.getMessage());
            var6.printStackTrace();
         }

         this.controllers = controllers;
         this.winTabContext = winTabContext;
         AccessController.doPrivileged(new WinTabEnvironmentPlugin$4(this));
      } else {
         this.winTabContext = null;
         this.controllers = new Controller[0];
      }
   }

   public boolean isSupported() {
      return supported;
   }

   public Controller[] getControllers() {
      return this.controllers;
   }

   static {
      String osName = getPrivilegedProperty("os.name", "").trim();
      if (osName.startsWith("Windows")) {
         supported = true;
         loadLibrary("jinput-wintab");
      }
   }

   private final class ShutdownHook extends Thread {
      private ShutdownHook() {
      }

      public final void run() {
         int i = 0;

         while(i < WinTabEnvironmentPlugin.this.active_devices.size()) {
            ++i;
         }

         WinTabEnvironmentPlugin.this.winTabContext.close();
      }
   }
}
