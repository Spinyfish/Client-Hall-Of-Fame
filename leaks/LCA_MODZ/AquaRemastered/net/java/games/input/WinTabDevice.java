package net.java.games.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WinTabDevice extends AbstractController {
   private WinTabContext context;
   private List eventList = new ArrayList();

   private WinTabDevice(WinTabContext context, int index, String name, Component[] components) {
      super(name, components, new Controller[0], new Rumbler[0]);
      this.context = context;
   }

   protected boolean getNextDeviceEvent(Event event) throws IOException {
      if (this.eventList.size() > 0) {
         Event ourEvent = (Event)this.eventList.remove(0);
         event.set(ourEvent);
         return true;
      } else {
         return false;
      }
   }

   protected void pollDevice() throws IOException {
      this.context.processEvents();
      super.pollDevice();
   }

   public Controller.Type getType() {
      return Controller.Type.TRACKPAD;
   }

   public void processPacket(WinTabPacket packet) {
      Component[] components = this.getComponents();

      for(int i = 0; i < components.length; ++i) {
         Event event = ((WinTabComponent)components[i]).processPacket(packet);
         if (event != null) {
            this.eventList.add(event);
         }
      }
   }

   public static WinTabDevice createDevice(WinTabContext context, int deviceIndex) {
      String name = nGetName(deviceIndex);
      WinTabEnvironmentPlugin.logln("Device " + deviceIndex + ", name: " + name);
      List componentsList = new ArrayList();
      int[] axisDetails = nGetAxisDetails(deviceIndex, 1);
      if (axisDetails.length == 0) {
         WinTabEnvironmentPlugin.logln("ZAxis not supported");
      } else {
         WinTabEnvironmentPlugin.logln("Xmin: " + axisDetails[0] + ", Xmax: " + axisDetails[1]);
         componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 1, axisDetails));
      }

      axisDetails = nGetAxisDetails(deviceIndex, 2);
      if (axisDetails.length == 0) {
         WinTabEnvironmentPlugin.logln("YAxis not supported");
      } else {
         WinTabEnvironmentPlugin.logln("Ymin: " + axisDetails[0] + ", Ymax: " + axisDetails[1]);
         componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 2, axisDetails));
      }

      axisDetails = nGetAxisDetails(deviceIndex, 3);
      if (axisDetails.length == 0) {
         WinTabEnvironmentPlugin.logln("ZAxis not supported");
      } else {
         WinTabEnvironmentPlugin.logln("Zmin: " + axisDetails[0] + ", Zmax: " + axisDetails[1]);
         componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 3, axisDetails));
      }

      axisDetails = nGetAxisDetails(deviceIndex, 4);
      if (axisDetails.length == 0) {
         WinTabEnvironmentPlugin.logln("NPressureAxis not supported");
      } else {
         WinTabEnvironmentPlugin.logln("NPressMin: " + axisDetails[0] + ", NPressMax: " + axisDetails[1]);
         componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 4, axisDetails));
      }

      axisDetails = nGetAxisDetails(deviceIndex, 5);
      if (axisDetails.length == 0) {
         WinTabEnvironmentPlugin.logln("TPressureAxis not supported");
      } else {
         WinTabEnvironmentPlugin.logln("TPressureAxismin: " + axisDetails[0] + ", TPressureAxismax: " + axisDetails[1]);
         componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 5, axisDetails));
      }

      axisDetails = nGetAxisDetails(deviceIndex, 6);
      if (axisDetails.length == 0) {
         WinTabEnvironmentPlugin.logln("OrientationAxis not supported");
      } else {
         WinTabEnvironmentPlugin.logln(
            "OrientationAxis mins/maxs: "
               + axisDetails[0]
               + ","
               + axisDetails[1]
               + ", "
               + axisDetails[2]
               + ","
               + axisDetails[3]
               + ", "
               + axisDetails[4]
               + ","
               + axisDetails[5]
         );
         componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 6, axisDetails));
      }

      axisDetails = nGetAxisDetails(deviceIndex, 7);
      if (axisDetails.length == 0) {
         WinTabEnvironmentPlugin.logln("RotationAxis not supported");
      } else {
         WinTabEnvironmentPlugin.logln("RotationAxis is supported (by the device, not by this plugin)");
         componentsList.addAll(WinTabComponent.createComponents(context, deviceIndex, 7, axisDetails));
      }

      String[] cursorNames = nGetCursorNames(deviceIndex);
      componentsList.addAll(WinTabComponent.createCursors(context, deviceIndex, cursorNames));

      for(int i = 0; i < cursorNames.length; ++i) {
         WinTabEnvironmentPlugin.logln("Cursor " + i + "'s name: " + cursorNames[i]);
      }

      int numberOfButtons = nGetMaxButtonCount(deviceIndex);
      WinTabEnvironmentPlugin.logln("Device has " + numberOfButtons + " buttons");
      componentsList.addAll(WinTabComponent.createButtons(context, deviceIndex, numberOfButtons));
      Component[] components = componentsList.toArray(new Component[0]);
      return new WinTabDevice(context, deviceIndex, name, components);
   }

   private static final native String nGetName(int var0);

   private static final native int[] nGetAxisDetails(int var0, int var1);

   private static final native String[] nGetCursorNames(int var0);

   private static final native int nGetMaxButtonCount(int var0);
}
