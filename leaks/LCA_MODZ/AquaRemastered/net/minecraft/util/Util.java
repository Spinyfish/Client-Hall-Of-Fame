package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util {
   public static Util.EnumOS getOSType() {
      String s = System.getProperty("os.name").toLowerCase();
      return s.contains("win")
         ? Util.EnumOS.WINDOWS
         : (
            s.contains("mac")
               ? Util.EnumOS.OSX
               : (
                  s.contains("solaris")
                     ? Util.EnumOS.SOLARIS
                     : (
                        s.contains("sunos")
                           ? Util.EnumOS.SOLARIS
                           : (s.contains("linux") ? Util.EnumOS.LINUX : (s.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN))
                     )
               )
         );
   }

   public static <V> V runTask(FutureTask<V> task, Logger logger) {
      try {
         task.run();
         return task.get();
      } catch (ExecutionException var4) {
         logger.fatal("Error executing task", (Throwable)var4);
         if (var4.getCause() instanceof OutOfMemoryError) {
            OutOfMemoryError outofmemoryerror = (OutOfMemoryError)var4.getCause();
            throw outofmemoryerror;
         }
      } catch (InterruptedException var5) {
         logger.fatal("Error executing task", (Throwable)var5);
      }

      return null;
   }

   public static enum EnumOS {
      LINUX,
      SOLARIS,
      WINDOWS,
      OSX,
      UNKNOWN;
   }
}
