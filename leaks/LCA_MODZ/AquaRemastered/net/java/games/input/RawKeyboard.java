package net.java.games.input;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

final class RawKeyboard extends Keyboard {
   private final RawKeyboardEvent raw_event = new RawKeyboardEvent();
   private final RawDevice device;

   protected RawKeyboard(String name, RawDevice device, Controller[] children, Rumbler[] rumblers) throws IOException {
      super(name, createKeyboardComponents(device), children, rumblers);
      this.device = device;
   }

   private static final Component[] createKeyboardComponents(RawDevice device) {
      List components = new ArrayList();
      Field[] vkey_fields = RawIdentifierMap.class.getFields();

      for(int i = 0; i < vkey_fields.length; ++i) {
         Field vkey_field = vkey_fields[i];

         try {
            if (Modifier.isStatic(vkey_field.getModifiers()) && vkey_field.getType() == Integer.TYPE) {
               int vkey_code = vkey_field.getInt(null);
               Component.Identifier.Key key_id = RawIdentifierMap.mapVKey(vkey_code);
               if (key_id != Component.Identifier.Key.UNKNOWN) {
                  components.add(new RawKeyboard.Key(device, vkey_code, key_id));
               }
            }
         } catch (IllegalAccessException var7) {
            throw new RuntimeException(var7);
         }
      }

      return components.toArray(new Component[0]);
   }

   protected final synchronized boolean getNextDeviceEvent(Event event) throws IOException {
      while(this.device.getNextKeyboardEvent(this.raw_event)) {
         int vkey = this.raw_event.getVKey();
         Component.Identifier.Key key_id = RawIdentifierMap.mapVKey(vkey);
         Component key = this.getComponent(key_id);
         if (key != null) {
            int message = this.raw_event.getMessage();
            if (message != 256 && message != 260) {
               if (message != 257 && message != 261) {
                  continue;
               }

               event.set(key, 0.0F, this.raw_event.getNanos());
               return true;
            }

            event.set(key, 1.0F, this.raw_event.getNanos());
            return true;
         }
      }

      return false;
   }

   public final void pollDevice() throws IOException {
      this.device.pollKeyboard();
   }

   protected final void setDeviceEventQueueSize(int size) throws IOException {
      this.device.setBufferSize(size);
   }

   static final class Key extends AbstractComponent {
      private final RawDevice device;
      private final int vkey_code;

      public Key(RawDevice device, int vkey_code, Component.Identifier.Key key_id) {
         super(key_id.getName(), key_id);
         this.device = device;
         this.vkey_code = vkey_code;
      }

      protected final float poll() throws IOException {
         return this.device.isKeyDown(this.vkey_code) ? 1.0F : 0.0F;
      }

      public final boolean isAnalog() {
         return false;
      }

      public final boolean isRelative() {
         return false;
      }
   }
}
