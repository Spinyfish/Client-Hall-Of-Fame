package intentions.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class Vec3d {
   public final double zCoord;
   public final double yCoord;
   public final double xCoord;
   public static final Vec3d ZERO = new Vec3d(0.0D, 0.0D, 0.0D);

   public Vec3d(Vec3i var1) {
      this((double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
   }

   public Vec3d addVector(double var1, double var3, double var5) {
      return new Vec3d(this.xCoord + var1, this.yCoord + var3, this.zCoord + var5);
   }

   public String toString() {
      return String.valueOf((new StringBuilder("(")).append(this.xCoord).append(", ").append(this.yCoord).append(", ").append(this.zCoord).append(")"));
   }
   

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Vec3d)) {
         return false;
      } else {
         Vec3d var2 = (Vec3d)var1;
         return Double.compare(var2.xCoord, this.xCoord) == 0 && Double.compare(var2.yCoord, this.yCoord) == 0 && Double.compare(var2.zCoord, this.zCoord) == 0;
      }
   }

   public Vec3d add(Vec3d var1) {
      return this.addVector(var1.xCoord, var1.yCoord, var1.zCoord);
   }

   public Vec3d(double var1, double var3, double var5) {
      if (var1 == -0.0D) {
         var1 = 0.0D;
      }

      if (var3 == -0.0D) {
         var3 = 0.0D;
      }

      if (var5 == -0.0D) {
         var5 = 0.0D;
      }

      this.xCoord = var1;
      this.yCoord = var3;
      this.zCoord = var5;
   }

   public Vec3d subtract(Vec3d var1) {
      return this.subtract(var1.xCoord, var1.yCoord, var1.zCoord);
   }

   public double squareDistanceTo(Vec3d var1) {
      double var2 = var1.xCoord - this.xCoord;
      double var4 = var1.yCoord - this.yCoord;
      double var6 = var1.zCoord - this.zCoord;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public Vec3d subtract(double var1, double var3, double var5) {
      return this.addVector(-var1, -var3, -var5);
   }

   public Vec3d normalize() {
      double var1 = Math.sqrt(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
      return var1 < 1.0E-4D ? ZERO : new Vec3d(this.xCoord / var1, this.yCoord / var1, this.zCoord / var1);
   }

   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.xCoord);
      int var3 = (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.yCoord);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.zCoord);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      return var3;
   }

   public double squareDistanceTo(double var1, double var3, double var5) {
      double var7 = var1 - this.xCoord;
      double var9 = var3 - this.yCoord;
      double var11 = var5 - this.zCoord;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public Vec3d scale(double var1) {
      return new Vec3d(this.xCoord * var1, this.yCoord * var1, this.zCoord * var1);
   }

   public Vec3 getAsVec3() {
		return new Vec3(this.xCoord, this.yCoord, this.zCoord);
	}

   public Vec3d add(double range, double i, double range2) {
		return this.add(new Vec3d(range, i, range2));
	}
}