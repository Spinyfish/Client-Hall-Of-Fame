package net.java.games.input;

final class RawMouseEvent {
   private static final int WHEEL_SCALE = 120;
   private long millis;
   private int flags;
   private int button_flags;
   private int button_data;
   private long raw_buttons;
   private long last_x;
   private long last_y;
   private long extra_information;

   public final void set(long millis, int flags, int button_flags, int button_data, long raw_buttons, long last_x, long last_y, long extra_information) {
      this.millis = millis;
      this.flags = flags;
      this.button_flags = button_flags;
      this.button_data = button_data;
      this.raw_buttons = raw_buttons;
      this.last_x = last_x;
      this.last_y = last_y;
      this.extra_information = extra_information;
   }

   public final void set(RawMouseEvent event) {
      this.set(event.millis, event.flags, event.button_flags, event.button_data, event.raw_buttons, event.last_x, event.last_y, event.extra_information);
   }

   public final int getWheelDelta() {
      return this.button_data / 120;
   }

   private final int getButtonData() {
      return this.button_data;
   }

   public final int getFlags() {
      return this.flags;
   }

   public final int getButtonFlags() {
      return this.button_flags;
   }

   public final int getLastX() {
      return (int)this.last_x;
   }

   public final int getLastY() {
      return (int)this.last_y;
   }

   public final long getRawButtons() {
      return this.raw_buttons;
   }

   public final long getNanos() {
      return this.millis * 1000000L;
   }
}
