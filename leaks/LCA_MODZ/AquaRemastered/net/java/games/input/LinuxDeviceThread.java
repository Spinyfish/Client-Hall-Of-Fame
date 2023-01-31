package net.java.games.input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

final class LinuxDeviceThread extends Thread {
   private final List tasks = new ArrayList();

   public LinuxDeviceThread() {
      this.setDaemon(true);
      this.start();
   }

   public final synchronized void run() {
      while(true) {
         if (!this.tasks.isEmpty()) {
            LinuxDeviceTask task = (LinuxDeviceTask)this.tasks.remove(0);
            task.doExecute();
            synchronized(task) {
               task.notify();
            }
         } else {
            try {
               this.wait();
            } catch (InterruptedException var5) {
            }
         }
      }
   }

   public final Object execute(LinuxDeviceTask task) throws IOException {
      synchronized(this) {
         this.tasks.add(task);
         this.notify();
      }

      synchronized(task) {
         while(task.getState() == 1) {
            try {
               task.wait();
            } catch (InterruptedException var5) {
            }
         }
      }

      switch(task.getState()) {
         case 2:
            return task.getResult();
         case 3:
            throw task.getException();
         default:
            throw new RuntimeException("Invalid task state: " + task.getState());
      }
   }
}
