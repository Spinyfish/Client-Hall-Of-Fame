package net.optifine.shaders;

import java.util.ArrayDeque;
import java.util.Deque;

public class ProgramStack {
   private final Deque<Program> stack = new ArrayDeque<>();

   public void push(Program p) {
      this.stack.addLast(p);
   }

   public Program pop() {
      return this.stack.isEmpty() ? Shaders.ProgramNone : this.stack.pollLast();
   }
}
