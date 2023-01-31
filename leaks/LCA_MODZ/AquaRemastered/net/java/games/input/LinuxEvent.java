package net.java.games.input;

final class LinuxEvent {
   private long nanos;
   private final LinuxAxisDescriptor descriptor = new LinuxAxisDescriptor();
   private int value;

   public final void set(long seconds, long microseconds, int type, int code, int value) {
      this.nanos = (seconds * 1000000L + microseconds) * 1000L;
      this.descriptor.set(type, code);
      this.value = value;
   }

   public final int getValue() {
      return this.value;
   }

   public final LinuxAxisDescriptor getDescriptor() {
      return this.descriptor;
   }

   public final long getNanos() {
      return this.nanos;
   }
}
