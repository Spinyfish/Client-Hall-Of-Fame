package net.minecraft.util;

import net.minecraft.client.Minecraft;

public class Timer {
   float ticksPerSecond;
   private double lastHRTime;
   public int elapsedTicks;
   public float renderPartialTicks;
   public float timerSpeed = 1.0F;
   public float elapsedPartialTicks;
   private long lastSyncSysClock;
   private long lastSyncHRClock;
   private long field_74285_i;
   private double timeSyncAdjustment = 1.0;

   public Timer(float p_i1018_1_) {
      this.ticksPerSecond = p_i1018_1_;
      this.lastSyncSysClock = Minecraft.getSystemTime();
      this.lastSyncHRClock = System.nanoTime() / 1000000L;
   }

   public void updateTimer() {
      long i = Minecraft.getSystemTime();
      long j = i - this.lastSyncSysClock;
      long k = System.nanoTime() / 1000000L;
      double d0 = (double)k / 1000.0;
      if (j <= 1000L && j >= 0L) {
         this.field_74285_i += j;
         if (this.field_74285_i > 1000L) {
            long l = k - this.lastSyncHRClock;
            double d1 = (double)this.field_74285_i / (double)l;
            this.timeSyncAdjustment += (d1 - this.timeSyncAdjustment) * 0.2F;
            this.lastSyncHRClock = k;
            this.field_74285_i = 0L;
         }

         if (this.field_74285_i < 0L) {
            this.lastSyncHRClock = k;
         }
      } else {
         this.lastHRTime = d0;
      }

      this.lastSyncSysClock = i;
      double d2 = (d0 - this.lastHRTime) * this.timeSyncAdjustment;
      this.lastHRTime = d0;
      d2 = MathHelper.clamp_double(d2, 0.0, 1.0);
      this.elapsedPartialTicks = (float)((double)this.elapsedPartialTicks + d2 * (double)this.timerSpeed * (double)this.ticksPerSecond);
      this.elapsedTicks = (int)this.elapsedPartialTicks;
      this.elapsedPartialTicks -= (float)this.elapsedTicks;
      if (this.elapsedTicks > 10) {
         this.elapsedTicks = 10;
      }

      this.renderPartialTicks = this.elapsedPartialTicks;
   }

   public float getTicksPerSecond() {
      return this.ticksPerSecond;
   }

   public double getLastHRTime() {
      return this.lastHRTime;
   }

   public int getElapsedTicks() {
      return this.elapsedTicks;
   }

   public float getRenderPartialTicks() {
      return this.renderPartialTicks;
   }

   public float getTimerSpeed() {
      return this.timerSpeed;
   }

   public float getElapsedPartialTicks() {
      return this.elapsedPartialTicks;
   }

   public long getLastSyncSysClock() {
      return this.lastSyncSysClock;
   }

   public long getLastSyncHRClock() {
      return this.lastSyncHRClock;
   }

   public long getField_74285_i() {
      return this.field_74285_i;
   }

   public double getTimeSyncAdjustment() {
      return this.timeSyncAdjustment;
   }

   public void setTicksPerSecond(float ticksPerSecond) {
      this.ticksPerSecond = ticksPerSecond;
   }

   public void setLastHRTime(double lastHRTime) {
      this.lastHRTime = lastHRTime;
   }

   public void setElapsedTicks(int elapsedTicks) {
      this.elapsedTicks = elapsedTicks;
   }

   public void setRenderPartialTicks(float renderPartialTicks) {
      this.renderPartialTicks = renderPartialTicks;
   }

   public void setTimerSpeed(float timerSpeed) {
      this.timerSpeed = timerSpeed;
   }

   public void setElapsedPartialTicks(float elapsedPartialTicks) {
      this.elapsedPartialTicks = elapsedPartialTicks;
   }

   public void setLastSyncSysClock(long lastSyncSysClock) {
      this.lastSyncSysClock = lastSyncSysClock;
   }

   public void setLastSyncHRClock(long lastSyncHRClock) {
      this.lastSyncHRClock = lastSyncHRClock;
   }

   public void setField_74285_i(long field_74285_i) {
      this.field_74285_i = field_74285_i;
   }

   public void setTimeSyncAdjustment(double timeSyncAdjustment) {
      this.timeSyncAdjustment = timeSyncAdjustment;
   }
}
