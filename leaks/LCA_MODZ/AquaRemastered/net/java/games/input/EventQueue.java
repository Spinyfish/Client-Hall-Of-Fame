package net.java.games.input;

public final class EventQueue {
   private final Event[] queue;
   private int head;
   private int tail;

   public EventQueue(int size) {
      this.queue = new Event[size + 1];

      for(int i = 0; i < this.queue.length; ++i) {
         this.queue[i] = new Event();
      }
   }

   final synchronized void add(Event event) {
      this.queue[this.tail].set(event);
      this.tail = this.increase(this.tail);
   }

   final synchronized boolean isFull() {
      return this.increase(this.tail) == this.head;
   }

   private final int increase(int x) {
      return (x + 1) % this.queue.length;
   }

   public final synchronized boolean getNextEvent(Event event) {
      if (this.head == this.tail) {
         return false;
      } else {
         event.set(this.queue[this.head]);
         this.head = this.increase(this.head);
         return true;
      }
   }
}
