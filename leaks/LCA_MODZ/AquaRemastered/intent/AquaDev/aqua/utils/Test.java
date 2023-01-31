package intent.AquaDev.aqua.utils;

public class Test {
   private long lastMS = 0L;

   public long getCurrentMS() {
      return System.currentTimeMillis();
   }

   public boolean hasReached(long milliseconds) {
      return this.getCurrentMS() - this.lastMS >= milliseconds;
   }

   public void reset() {
      this.lastMS = this.getCurrentMS();
   }

   public int convertToMS(int perSecond) {
      return 1000 / perSecond;
   }

   public void setCurrentDifference(int difference) {
      this.lastMS = System.currentTimeMillis() - (long)difference;
   }

   public boolean hasTimePassed(long delay) {
      return System.currentTimeMillis() >= this.lastMS + delay;
   }

   public void setLastMS() {
      this.lastMS = System.currentTimeMillis();
   }

   public long getElapsedTime() {
      return System.currentTimeMillis() - this.lastMS;
   }

   public boolean hasTimeElapsed(long time, boolean reset) {
      if (this.lastMS > System.currentTimeMillis()) {
         this.lastMS = System.currentTimeMillis();
      }

      if (System.currentTimeMillis() - this.lastMS > time) {
         if (reset) {
            this.reset();
         }

         return true;
      } else {
         return false;
      }
   }
}
