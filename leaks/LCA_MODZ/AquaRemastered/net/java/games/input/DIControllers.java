package net.java.games.input;

import java.io.IOException;

final class DIControllers {
   private static final DIDeviceObjectData di_event = new DIDeviceObjectData();

   public static final synchronized boolean getNextDeviceEvent(Event event, IDirectInputDevice device) throws IOException {
      if (!device.getNextEvent(di_event)) {
         return false;
      } else {
         DIDeviceObject object = device.mapEvent(di_event);
         DIComponent component = device.mapObject(object);
         if (component == null) {
            return false;
         } else {
            int event_value;
            if (object.isRelative()) {
               event_value = object.getRelativeEventValue(di_event.getData());
            } else {
               event_value = di_event.getData();
            }

            event.set(component, component.getDeviceObject().convertValue((float)event_value), di_event.getNanos());
            return true;
         }
      }
   }

   public static final float poll(Component component, DIDeviceObject object) throws IOException {
      int poll_data = object.getDevice().getPollData(object);
      float result;
      if (object.isRelative()) {
         result = (float)object.getRelativePollValue(poll_data);
      } else {
         result = (float)poll_data;
      }

      return object.convertValue(result);
   }
}
