package net.java.games.input;

final class LinuxAbsInfo {
   private int value;
   private int minimum;
   private int maximum;
   private int fuzz;
   private int flat;

   public final void set(int value, int min, int max, int fuzz, int flat) {
      this.value = value;
      this.minimum = min;
      this.maximum = max;
      this.fuzz = fuzz;
      this.flat = flat;
   }

   public final String toString() {
      return "AbsInfo: value = " + this.value + " | min = " + this.minimum + " | max = " + this.maximum + " | fuzz = " + this.fuzz + " | flat = " + this.flat;
   }

   public final int getValue() {
      return this.value;
   }

   final int getMax() {
      return this.maximum;
   }

   final int getMin() {
      return this.minimum;
   }

   final int getFlat() {
      return this.flat;
   }

   final int getFuzz() {
      return this.fuzz;
   }
}
