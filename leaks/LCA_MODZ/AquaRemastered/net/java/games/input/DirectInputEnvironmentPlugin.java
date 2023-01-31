package net.java.games.input;

import java.io.IOException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import net.java.games.util.plugins.Plugin;

public final class DirectInputEnvironmentPlugin extends ControllerEnvironment implements Plugin {
   private static boolean supported = false;
   private final Controller[] controllers;
   private final List active_devices = new ArrayList();
   private final DummyWindow window;

   static void loadLibrary(String lib_name) {
      AccessController.doPrivileged(new DirectInputEnvironmentPlugin$1(lib_name));
   }

   static String getPrivilegedProperty(String property) {
      return AccessController.doPrivileged(new DirectInputEnvironmentPlugin$2(property));
   }

   static String getPrivilegedProperty(String property, String default_value) {
      return AccessController.doPrivileged(new DirectInputEnvironmentPlugin$3(property, default_value));
   }

   public DirectInputEnvironmentPlugin() {
      DummyWindow window = null;
      Controller[] controllers = new Controller[0];
      if (this.isSupported()) {
         try {
            window = new DummyWindow();

            try {
               controllers = this.enumControllers(window);
            } catch (IOException var4) {
               window.destroy();
               throw var4;
            }
         } catch (IOException var5) {
            logln("Failed to enumerate devices: " + var5.getMessage());
         }

         this.window = window;
         this.controllers = controllers;
         AccessController.doPrivileged(new DirectInputEnvironmentPlugin$4(this));
      } else {
         this.window = null;
         this.controllers = controllers;
      }
   }

   public final Controller[] getControllers() {
      return this.controllers;
   }

   private final Component[] createComponents(IDirectInputDevice device, boolean map_mouse_buttons) {
      List device_objects = device.getObjects();
      List controller_components = new ArrayList();

      for(int i = 0; i < device_objects.size(); ++i) {
         DIDeviceObject device_object = (DIDeviceObject)device_objects.get(i);
         Component.Identifier identifier = device_object.getIdentifier();
         if (identifier != null) {
            if (map_mouse_buttons && identifier instanceof Component.Identifier.Button) {
               identifier = DIIdentifierMap.mapMouseButtonIdentifier((Component.Identifier.Button)identifier);
            }

            DIComponent component = new DIComponent(identifier, device_object);
            controller_components.add(component);
            device.registerComponent(device_object, component);
         }
      }

      Component[] components = new Component[controller_components.size()];
      controller_components.toArray(components);
      return components;
   }

   private final Mouse createMouseFromDevice(IDirectInputDevice device) {
      Component[] components = this.createComponents(device, true);
      Mouse mouse = new DIMouse(device, components, new Controller[0], device.getRumblers());
      return mouse.getX() != null && mouse.getY() != null && mouse.getPrimaryButton() != null ? mouse : null;
   }

   private final AbstractController createControllerFromDevice(IDirectInputDevice device, Controller.Type type) {
      Component[] components = this.createComponents(device, false);
      AbstractController controller = new DIAbstractController(device, components, new Controller[0], device.getRumblers(), type);
      return controller;
   }

   private final Keyboard createKeyboardFromDevice(IDirectInputDevice device) {
      Component[] components = this.createComponents(device, false);
      return new DIKeyboard(device, components, new Controller[0], device.getRumblers());
   }

   private final Controller createControllerFromDevice(IDirectInputDevice device) {
      switch(device.getType()) {
         case 18:
            return this.createMouseFromDevice(device);
         case 19:
            return this.createKeyboardFromDevice(device);
         case 20:
         case 23:
         case 24:
            return this.createControllerFromDevice(device, Controller.Type.STICK);
         case 21:
            return this.createControllerFromDevice(device, Controller.Type.GAMEPAD);
         case 22:
            return this.createControllerFromDevice(device, Controller.Type.WHEEL);
         default:
            return this.createControllerFromDevice(device, Controller.Type.UNKNOWN);
      }
   }

   private final Controller[] enumControllers(DummyWindow window) throws IOException {
      List controllers = new ArrayList();
      IDirectInput dinput = new IDirectInput(window);

      try {
         List devices = dinput.getDevices();

         for(int i = 0; i < devices.size(); ++i) {
            IDirectInputDevice device = (IDirectInputDevice)devices.get(i);
            Controller controller = this.createControllerFromDevice(device);
            if (controller != null) {
               controllers.add(controller);
               this.active_devices.add(device);
            } else {
               device.release();
            }
         }
      } finally {
         dinput.release();
      }

      Controller[] var11 = new Controller[controllers.size()];
      controllers.toArray(var11);
      return var11;
   }

   public boolean isSupported() {
      return supported;
   }

   static {
      String osName = getPrivilegedProperty("os.name", "").trim();
      if (osName.startsWith("Windows")) {
         supported = true;
         if ("x86".equals(getPrivilegedProperty("os.arch"))) {
            loadLibrary("jinput-dx8");
         } else {
            loadLibrary("jinput-dx8_64");
         }
      }
   }

   private final class ShutdownHook extends Thread {
      private ShutdownHook() {
      }

      public final void run() {
         for(int i = 0; i < DirectInputEnvironmentPlugin.this.active_devices.size(); ++i) {
            IDirectInputDevice device = (IDirectInputDevice)DirectInputEnvironmentPlugin.this.active_devices.get(i);
            device.release();
         }
      }
   }
}
