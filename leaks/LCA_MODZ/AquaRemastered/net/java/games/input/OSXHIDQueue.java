package net.java.games.input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final class OSXHIDQueue {
   private final Map map = new HashMap();
   private final long queue_address;
   private boolean released;

   public OSXHIDQueue(long address, int queue_depth) throws IOException {
      this.queue_address = address;

      try {
         this.createQueue(queue_depth);
      } catch (IOException var5) {
         this.release();
         throw var5;
      }
   }

   public final synchronized void setQueueDepth(int queue_depth) throws IOException {
      this.checkReleased();
      this.stop();
      this.close();
      this.createQueue(queue_depth);
   }

   private final void createQueue(int queue_depth) throws IOException {
      this.open(queue_depth);

      try {
         this.start();
      } catch (IOException var3) {
         this.close();
         throw var3;
      }
   }

   public final OSXComponent mapEvent(OSXEvent event) {
      return (OSXComponent)this.map.get(new Long(event.getCookie()));
   }

   private final void open(int queue_depth) throws IOException {
      nOpen(this.queue_address, queue_depth);
   }

   private static final native void nOpen(long var0, int var2) throws IOException;

   private final void close() throws IOException {
      nClose(this.queue_address);
   }

   private static final native void nClose(long var0) throws IOException;

   private final void start() throws IOException {
      nStart(this.queue_address);
   }

   private static final native void nStart(long var0) throws IOException;

   private final void stop() throws IOException {
      nStop(this.queue_address);
   }

   private static final native void nStop(long var0) throws IOException;

   public final synchronized void release() throws IOException {
      if (!this.released) {
         this.released = true;

         try {
            this.stop();
            this.close();
         } finally {
            nReleaseQueue(this.queue_address);
         }
      }
   }

   private static final native void nReleaseQueue(long var0) throws IOException;

   public final void addElement(OSXHIDElement element, OSXComponent component) throws IOException {
      nAddElement(this.queue_address, element.getCookie());
      this.map.put(new Long(element.getCookie()), component);
   }

   private static final native void nAddElement(long var0, long var2) throws IOException;

   public final void removeElement(OSXHIDElement element) throws IOException {
      nRemoveElement(this.queue_address, element.getCookie());
      this.map.remove(new Long(element.getCookie()));
   }

   private static final native void nRemoveElement(long var0, long var2) throws IOException;

   public final synchronized boolean getNextEvent(OSXEvent event) throws IOException {
      this.checkReleased();
      return nGetNextEvent(this.queue_address, event);
   }

   private static final native boolean nGetNextEvent(long var0, OSXEvent var2) throws IOException;

   private final void checkReleased() throws IOException {
      if (this.released) {
         throw new IOException("Queue is released");
      }
   }
}
