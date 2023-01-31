package intent.AquaDev.aqua.utils;

import org.lwjgl.input.Mouse;

public class MouseClicker {
   private boolean next = true;

   public void stop() {
      this.next = false;
   }

   public boolean isNext() {
      return this.next;
   }

   public void release(int button) {
      if (!Mouse.isButtonDown(button)) {
         this.next = true;
      }
   }
}
