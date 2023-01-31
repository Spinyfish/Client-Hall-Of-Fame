package net.java.games.input;

final class LinuxJoystickEvent {
   private long nanos;
   private int value;
   private int type;
   private int number;

   public final void set(long millis, int value, int type, int number) {
      this.nanos = millis * 1000000L;
      this.value = value;
      this.type = type;
      this.number = number;
   }

   public final int getValue() {
      return this.value;
   }

   public final int getType() {
      return this.type;
   }

   public final int getNumber() {
      return this.number;
   }

   public final long getNanos() {
      return this.nanos;
   }
}
