package intent.AquaDev.aqua.utils;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
   public static RandomUtil instance = new RandomUtil();

   public int nextInt(int origin, int bound) {
      return origin == bound ? origin : ThreadLocalRandom.current().nextInt(origin, bound);
   }

   public int nextInt(double origin, double bound) {
      return (int)origin == (int)bound ? (int)origin : ThreadLocalRandom.current().nextInt((int)origin, (int)bound);
   }

   public long nextLong(long origin, long bound) {
      return origin == bound ? origin : ThreadLocalRandom.current().nextLong(origin, bound);
   }

   public long nextLong(double origin, double bound) {
      return (long)origin == (long)bound ? (long)origin : ThreadLocalRandom.current().nextLong((long)origin, (long)bound);
   }

   public float nextFloat(double origin, double bound) {
      return origin == bound ? (float)origin : (float)ThreadLocalRandom.current().nextDouble((double)((float)origin), (double)((float)bound));
   }

   public float nextFloat(float origin, float bound) {
      return origin == bound ? origin : (float)ThreadLocalRandom.current().nextDouble((double)origin, (double)bound);
   }

   public double nextDouble(double origin, double bound) {
      return origin == bound ? origin : ThreadLocalRandom.current().nextDouble(origin, bound);
   }

   public double nextSecureInt(int origin, int bound) {
      if (origin == bound) {
         return (double)origin;
      } else {
         SecureRandom secureRandom = new SecureRandom();
         int difference = bound - origin;
         return (double)(origin + secureRandom.nextInt(difference));
      }
   }

   public double nextSecureInt(double origin, double bound) {
      if ((int)origin == (int)bound) {
         return origin;
      } else {
         SecureRandom secureRandom = new SecureRandom();
         int difference = (int)bound - (int)origin;
         return origin + (double)secureRandom.nextInt(difference);
      }
   }

   public double nextSecureDouble(double origin, double bound) {
      if (origin == bound) {
         return origin;
      } else {
         SecureRandom secureRandom = new SecureRandom();
         double difference = bound - origin;
         return origin + secureRandom.nextDouble() * difference;
      }
   }

   public float nextSecureFloat(double origin, double bound) {
      if (origin == bound) {
         return (float)origin;
      } else {
         SecureRandom secureRandom = new SecureRandom();
         float difference = (float)(bound - origin);
         return (float)(origin + (double)(secureRandom.nextFloat() * difference));
      }
   }

   public double randomSin() {
      return Math.sin(instance.nextDouble(0.0, Math.PI * 2));
   }
}
