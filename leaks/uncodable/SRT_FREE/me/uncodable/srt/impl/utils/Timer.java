package me.uncodable.srt.impl.utils;

public class Timer {
   private long elapsedTime;

   public Timer() {
      this.reset();
   }

   public long getTime() {
      return System.nanoTime() / 1000000L;
   }

   public long difference() {
      return this.getTime() - this.elapsedTime;
   }

   public void reset() {
      this.elapsedTime = this.getTime();
   }

   public boolean elapsed(long milliseconds) {
      return this.difference() >= milliseconds;
   }

   public long getElapsedTime() {
      return this.elapsedTime;
   }

   public void setElapsedTime(long elapsedTime) {
      this.elapsedTime = elapsedTime;
   }
}
