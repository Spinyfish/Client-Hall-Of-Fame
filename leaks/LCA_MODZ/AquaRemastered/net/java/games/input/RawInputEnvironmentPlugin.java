package net.java.games.input;

import java.io.IOException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import net.java.games.util.plugins.Plugin;

public final class RawInputEnvironmentPlugin extends ControllerEnvironment implements Plugin {
   private static boolean supported = false;
   private final Controller[] controllers;

   static void loadLibrary(String lib_name) {
      AccessController.doPrivileged(new RawInputEnvironmentPlugin$1(lib_name));
   }

   static String getPrivilegedProperty(String property) {
      return AccessController.doPrivileged(new RawInputEnvironmentPlugin$2(property));
   }

   static String getPrivilegedProperty(String property, String default_value) {
      return AccessController.doPrivileged(new RawInputEnvironmentPlugin$3(property, default_value));
   }

   public RawInputEnvironmentPlugin() {
      Controller[] controllers = new Controller[0];
      if (this.isSupported()) {
         try {
            RawInputEventQueue queue = new RawInputEventQueue();
            controllers = this.enumControllers(queue);
         } catch (IOException var4) {
            logln("Failed to enumerate devices: " + var4.getMessage());
         }
      }

      this.controllers = controllers;
   }

   public final Controller[] getControllers() {
      return this.controllers;
   }

   private static final SetupAPIDevice lookupSetupAPIDevice(String device_name, List setupapi_devices) {
      device_name = device_name.replaceAll("#", "\\\\").toUpperCase();

      for(int i = 0; i < setupapi_devices.size(); ++i) {
         SetupAPIDevice device = (SetupAPIDevice)setupapi_devices.get(i);
         if (device_name.indexOf(device.getInstanceId().toUpperCase()) != -1) {
            return device;
         }
      }

      return null;
   }

   private static final void createControllersFromDevices(RawInputEventQueue queue, List controllers, List devices, List setupapi_devices) throws IOException {
      List active_devices = new ArrayList();

      for(int i = 0; i < devices.size(); ++i) {
         RawDevice device = (RawDevice)devices.get(i);
         SetupAPIDevice setupapi_device = lookupSetupAPIDevice(device.getName(), setupapi_devices);
         if (setupapi_device != null) {
            RawDeviceInfo info = device.getInfo();
            Controller controller = info.createControllerFromDevice(device, setupapi_device);
            if (controller != null) {
               controllers.add(controller);
               active_devices.add(device);
            }
         }
      }

      queue.start(active_devices);
   }

   private static final native void enumerateDevices(RawInputEventQueue var0, List var1) throws IOException;

   private final Controller[] enumControllers(RawInputEventQueue queue) throws IOException {
      List controllers = new ArrayList();
      List devices = new ArrayList();
      enumerateDevices(queue, devices);
      List setupapi_devices = enumSetupAPIDevices();
      createControllersFromDevices(queue, controllers, devices, setupapi_devices);
      Controller[] controllers_array = new Controller[controllers.size()];
      controllers.toArray(controllers_array);
      return controllers_array;
   }

   public boolean isSupported() {
      return supported;
   }

   private static final List enumSetupAPIDevices() throws IOException {
      List devices = new ArrayList();
      nEnumSetupAPIDevices(getKeyboardClassGUID(), devices);
      nEnumSetupAPIDevices(getMouseClassGUID(), devices);
      return devices;
   }

   private static final native void nEnumSetupAPIDevices(byte[] var0, List var1) throws IOException;

   private static final native byte[] getKeyboardClassGUID();

   private static final native byte[] getMouseClassGUID();

   static {
      String osName = getPrivilegedProperty("os.name", "").trim();
      if (osName.startsWith("Windows")) {
         supported = true;
         if ("x86".equals(getPrivilegedProperty("os.arch"))) {
            loadLibrary("jinput-raw");
         } else {
            loadLibrary("jinput-raw_64");
         }
      }
   }
}
