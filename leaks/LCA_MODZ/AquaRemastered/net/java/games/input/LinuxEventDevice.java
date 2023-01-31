package net.java.games.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class LinuxEventDevice implements LinuxDevice {
   private final Map component_map = new HashMap();
   private final Rumbler[] rumblers;
   private final long fd;
   private final String name;
   private final LinuxInputID input_id;
   private final List components;
   private final Controller.Type type;
   private boolean closed;
   private final byte[] key_states = new byte[64];

   public LinuxEventDevice(String filename) throws IOException {
      boolean detect_rumblers = true;

      long fd;
      try {
         fd = nOpen(filename, true);
      } catch (IOException var7) {
         fd = nOpen(filename, false);
         detect_rumblers = false;
      }

      this.fd = fd;

      try {
         this.name = this.getDeviceName();
         this.input_id = this.getDeviceInputID();
         this.components = this.getDeviceComponents();
         if (detect_rumblers) {
            this.rumblers = this.enumerateRumblers();
         } else {
            this.rumblers = new Rumbler[0];
         }

         this.type = this.guessType();
      } catch (IOException var6) {
         this.close();
         throw var6;
      }
   }

   private static final native long nOpen(String var0, boolean var1) throws IOException;

   public final Controller.Type getType() {
      return this.type;
   }

   private static final int countComponents(List components, Class id_type, boolean relative) {
      int count = 0;

      for(int i = 0; i < components.size(); ++i) {
         LinuxEventComponent component = (LinuxEventComponent)components.get(i);
         if (id_type.isInstance(component.getIdentifier()) && relative == component.isRelative()) {
            ++count;
         }
      }

      return count;
   }

   private final Controller.Type guessType() throws IOException {
      Controller.Type type_from_usages = this.guessTypeFromUsages();
      return type_from_usages == Controller.Type.UNKNOWN ? this.guessTypeFromComponents() : type_from_usages;
   }

   private final Controller.Type guessTypeFromUsages() throws IOException {
      byte[] usage_bits = this.getDeviceUsageBits();
      if (isBitSet(usage_bits, 0)) {
         return Controller.Type.MOUSE;
      } else if (isBitSet(usage_bits, 3)) {
         return Controller.Type.KEYBOARD;
      } else if (isBitSet(usage_bits, 2)) {
         return Controller.Type.GAMEPAD;
      } else {
         return isBitSet(usage_bits, 1) ? Controller.Type.STICK : Controller.Type.UNKNOWN;
      }
   }

   private final Controller.Type guessTypeFromComponents() throws IOException {
      List components = this.getComponents();
      if (components.size() == 0) {
         return Controller.Type.UNKNOWN;
      } else {
         int num_rel_axes = countComponents(components, Component.Identifier.Axis.class, true);
         int num_abs_axes = countComponents(components, Component.Identifier.Axis.class, false);
         int num_keys = countComponents(components, Component.Identifier.Key.class, false);
         int mouse_traits = 0;
         int keyboard_traits = 0;
         int joystick_traits = 0;
         int gamepad_traits = 0;
         if (this.name.toLowerCase().indexOf("mouse") != -1) {
            ++mouse_traits;
         }

         if (this.name.toLowerCase().indexOf("keyboard") != -1) {
            ++keyboard_traits;
         }

         if (this.name.toLowerCase().indexOf("joystick") != -1) {
            ++joystick_traits;
         }

         if (this.name.toLowerCase().indexOf("gamepad") != -1) {
            ++gamepad_traits;
         }

         int num_keyboard_button_traits = 0;
         int num_mouse_button_traits = 0;
         int num_joystick_button_traits = 0;
         int num_gamepad_button_traits = 0;

         for(int i = 0; i < components.size(); ++i) {
            LinuxEventComponent component = (LinuxEventComponent)components.get(i);
            if (component.getButtonTrait() == Controller.Type.MOUSE) {
               ++num_mouse_button_traits;
            } else if (component.getButtonTrait() == Controller.Type.KEYBOARD) {
               ++num_keyboard_button_traits;
            } else if (component.getButtonTrait() == Controller.Type.GAMEPAD) {
               ++num_gamepad_button_traits;
            } else if (component.getButtonTrait() == Controller.Type.STICK) {
               ++num_joystick_button_traits;
            }
         }

         if (num_mouse_button_traits >= num_keyboard_button_traits
            && num_mouse_button_traits >= num_joystick_button_traits
            && num_mouse_button_traits >= num_gamepad_button_traits) {
            ++mouse_traits;
         } else if (num_keyboard_button_traits >= num_mouse_button_traits
            && num_keyboard_button_traits >= num_joystick_button_traits
            && num_keyboard_button_traits >= num_gamepad_button_traits) {
            ++keyboard_traits;
         } else if (num_joystick_button_traits >= num_keyboard_button_traits
            && num_joystick_button_traits >= num_mouse_button_traits
            && num_joystick_button_traits >= num_gamepad_button_traits) {
            ++joystick_traits;
         } else if (num_gamepad_button_traits >= num_keyboard_button_traits
            && num_gamepad_button_traits >= num_mouse_button_traits
            && num_gamepad_button_traits >= num_joystick_button_traits) {
            ++gamepad_traits;
         }

         if (num_rel_axes >= 2) {
            ++mouse_traits;
         }

         if (num_abs_axes >= 2) {
            ++joystick_traits;
            ++gamepad_traits;
         }

         if (mouse_traits >= keyboard_traits && mouse_traits >= joystick_traits && mouse_traits >= gamepad_traits) {
            return Controller.Type.MOUSE;
         } else if (keyboard_traits >= mouse_traits && keyboard_traits >= joystick_traits && keyboard_traits >= gamepad_traits) {
            return Controller.Type.KEYBOARD;
         } else if (joystick_traits >= mouse_traits && joystick_traits >= keyboard_traits && joystick_traits >= gamepad_traits) {
            return Controller.Type.STICK;
         } else {
            return gamepad_traits >= mouse_traits && gamepad_traits >= keyboard_traits && gamepad_traits >= joystick_traits ? Controller.Type.GAMEPAD : null;
         }
      }
   }

   private final Rumbler[] enumerateRumblers() {
      List rumblers = new ArrayList();

      try {
         int num_effects = this.getNumEffects();
         if (num_effects <= 0) {
            return rumblers.toArray(new Rumbler[0]);
         }

         byte[] ff_bits = this.getForceFeedbackBits();
         if (isBitSet(ff_bits, 80) && num_effects > rumblers.size()) {
            rumblers.add(new LinuxRumbleFF(this));
         }
      } catch (IOException var4) {
         LinuxEnvironmentPlugin.logln("Failed to enumerate rumblers: " + var4.getMessage());
      }

      return rumblers.toArray(new Rumbler[0]);
   }

   public final Rumbler[] getRumblers() {
      return this.rumblers;
   }

   public final synchronized int uploadRumbleEffect(
      int id, int trigger_button, int direction, int trigger_interval, int replay_length, int replay_delay, int strong_magnitude, int weak_magnitude
   ) throws IOException {
      this.checkClosed();
      return nUploadRumbleEffect(this.fd, id, direction, trigger_button, trigger_interval, replay_length, replay_delay, strong_magnitude, weak_magnitude);
   }

   private static final native int nUploadRumbleEffect(long var0, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) throws IOException;

   public final synchronized int uploadConstantEffect(
      int id,
      int trigger_button,
      int direction,
      int trigger_interval,
      int replay_length,
      int replay_delay,
      int constant_level,
      int constant_env_attack_length,
      int constant_env_attack_level,
      int constant_env_fade_length,
      int constant_env_fade_level
   ) throws IOException {
      this.checkClosed();
      return nUploadConstantEffect(
         this.fd,
         id,
         direction,
         trigger_button,
         trigger_interval,
         replay_length,
         replay_delay,
         constant_level,
         constant_env_attack_length,
         constant_env_attack_level,
         constant_env_fade_length,
         constant_env_fade_level
      );
   }

   private static final native int nUploadConstantEffect(
      long var0, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12
   ) throws IOException;

   final void eraseEffect(int id) throws IOException {
      nEraseEffect(this.fd, id);
   }

   private static final native void nEraseEffect(long var0, int var2) throws IOException;

   public final synchronized void writeEvent(int type, int code, int value) throws IOException {
      this.checkClosed();
      nWriteEvent(this.fd, type, code, value);
   }

   private static final native void nWriteEvent(long var0, int var2, int var3, int var4) throws IOException;

   public final void registerComponent(LinuxAxisDescriptor desc, LinuxComponent component) {
      this.component_map.put(desc, component);
   }

   public final LinuxComponent mapDescriptor(LinuxAxisDescriptor desc) {
      return (LinuxComponent)this.component_map.get(desc);
   }

   public final Controller.PortType getPortType() throws IOException {
      return this.input_id.getPortType();
   }

   public final LinuxInputID getInputID() {
      return this.input_id;
   }

   private final LinuxInputID getDeviceInputID() throws IOException {
      return nGetInputID(this.fd);
   }

   private static final native LinuxInputID nGetInputID(long var0) throws IOException;

   public final int getNumEffects() throws IOException {
      return nGetNumEffects(this.fd);
   }

   private static final native int nGetNumEffects(long var0) throws IOException;

   private final int getVersion() throws IOException {
      return nGetVersion(this.fd);
   }

   private static final native int nGetVersion(long var0) throws IOException;

   public final synchronized boolean getNextEvent(LinuxEvent linux_event) throws IOException {
      this.checkClosed();
      return nGetNextEvent(this.fd, linux_event);
   }

   private static final native boolean nGetNextEvent(long var0, LinuxEvent var2) throws IOException;

   public final synchronized void getAbsInfo(int abs_axis, LinuxAbsInfo abs_info) throws IOException {
      this.checkClosed();
      nGetAbsInfo(this.fd, abs_axis, abs_info);
   }

   private static final native void nGetAbsInfo(long var0, int var2, LinuxAbsInfo var3) throws IOException;

   private final void addKeys(List components) throws IOException {
      byte[] bits = this.getKeysBits();

      for(int i = 0; i < bits.length * 8; ++i) {
         if (isBitSet(bits, i)) {
            Component.Identifier id = LinuxNativeTypesMap.getButtonID(i);
            components.add(new LinuxEventComponent(this, id, false, 1, i));
         }
      }
   }

   private final void addAbsoluteAxes(List components) throws IOException {
      byte[] bits = this.getAbsoluteAxesBits();

      for(int i = 0; i < bits.length * 8; ++i) {
         if (isBitSet(bits, i)) {
            Component.Identifier id = LinuxNativeTypesMap.getAbsAxisID(i);
            components.add(new LinuxEventComponent(this, id, false, 3, i));
         }
      }
   }

   private final void addRelativeAxes(List components) throws IOException {
      byte[] bits = this.getRelativeAxesBits();

      for(int i = 0; i < bits.length * 8; ++i) {
         if (isBitSet(bits, i)) {
            Component.Identifier id = LinuxNativeTypesMap.getRelAxisID(i);
            components.add(new LinuxEventComponent(this, id, true, 2, i));
         }
      }
   }

   public final List getComponents() {
      return this.components;
   }

   private final List getDeviceComponents() throws IOException {
      List components = new ArrayList();
      byte[] evtype_bits = this.getEventTypeBits();
      if (isBitSet(evtype_bits, 1)) {
         this.addKeys(components);
      }

      if (isBitSet(evtype_bits, 3)) {
         this.addAbsoluteAxes(components);
      }

      if (isBitSet(evtype_bits, 2)) {
         this.addRelativeAxes(components);
      }

      return components;
   }

   private final byte[] getForceFeedbackBits() throws IOException {
      byte[] bits = new byte[16];
      nGetBits(this.fd, 21, bits);
      return bits;
   }

   private final byte[] getKeysBits() throws IOException {
      byte[] bits = new byte[64];
      nGetBits(this.fd, 1, bits);
      return bits;
   }

   private final byte[] getAbsoluteAxesBits() throws IOException {
      byte[] bits = new byte[8];
      nGetBits(this.fd, 3, bits);
      return bits;
   }

   private final byte[] getRelativeAxesBits() throws IOException {
      byte[] bits = new byte[2];
      nGetBits(this.fd, 2, bits);
      return bits;
   }

   private final byte[] getEventTypeBits() throws IOException {
      byte[] bits = new byte[4];
      nGetBits(this.fd, 0, bits);
      return bits;
   }

   private static final native void nGetBits(long var0, int var2, byte[] var3) throws IOException;

   private final byte[] getDeviceUsageBits() throws IOException {
      byte[] bits = new byte[2];
      if (this.getVersion() >= 65537) {
         nGetDeviceUsageBits(this.fd, bits);
      }

      return bits;
   }

   private static final native void nGetDeviceUsageBits(long var0, byte[] var2) throws IOException;

   public final synchronized void pollKeyStates() throws IOException {
      nGetKeyStates(this.fd, this.key_states);
   }

   private static final native void nGetKeyStates(long var0, byte[] var2) throws IOException;

   public final boolean isKeySet(int bit) {
      return isBitSet(this.key_states, bit);
   }

   public static final boolean isBitSet(byte[] bits, int bit) {
      return (bits[bit / 8] & 1 << bit % 8) != 0;
   }

   public final String getName() {
      return this.name;
   }

   private final String getDeviceName() throws IOException {
      return nGetName(this.fd);
   }

   private static final native String nGetName(long var0) throws IOException;

   public final synchronized void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         LinuxEnvironmentPlugin.execute(new LinuxEventDevice$1(this));
      }
   }

   private static final native void nClose(long var0) throws IOException;

   private final void checkClosed() throws IOException {
      if (this.closed) {
         throw new IOException("Device is closed");
      }
   }

   protected void finalize() throws IOException {
      this.close();
   }
}
