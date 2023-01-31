package net.java.games.input;

final class DataQueue {
   private final Object[] elements;
   private int position;
   private int limit;

   public DataQueue(int size, Class element_type) {
      this.elements = new Object[size];

      for(int i = 0; i < this.elements.length; ++i) {
         try {
            this.elements[i] = element_type.newInstance();
         } catch (InstantiationException var5) {
            throw new RuntimeException(var5);
         } catch (IllegalAccessException var6) {
            throw new RuntimeException(var6);
         }
      }

      this.clear();
   }

   public final void clear() {
      this.position = 0;
      this.limit = this.elements.length;
   }

   public final int position() {
      return this.position;
   }

   public final int limit() {
      return this.limit;
   }

   public final Object get(int index) {
      assert index < this.limit;

      return this.elements[index];
   }

   public final Object get() {
      return !this.hasRemaining() ? null : this.get(this.position++);
   }

   public final void compact() {
      int index;
      for(index = 0; this.hasRemaining(); ++index) {
         this.swap(this.position, index);
         ++this.position;
      }

      this.position = index;
      this.limit = this.elements.length;
   }

   private final void swap(int index1, int index2) {
      Object temp = this.elements[index1];
      this.elements[index1] = this.elements[index2];
      this.elements[index2] = temp;
   }

   public final void flip() {
      this.limit = this.position;
      this.position = 0;
   }

   public final boolean hasRemaining() {
      return this.remaining() > 0;
   }

   public final int remaining() {
      return this.limit - this.position;
   }

   public final void position(int position) {
      this.position = position;
   }

   public final Object[] getElements() {
      return this.elements;
   }
}
