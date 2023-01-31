package net.java.games.input;

final class LinuxAxisDescriptor {
   private int type;
   private int code;

   public final void set(int type, int code) {
      this.type = type;
      this.code = code;
   }

   public final int getType() {
      return this.type;
   }

   public final int getCode() {
      return this.code;
   }

   public final int hashCode() {
      return this.type ^ this.code;
   }

   public final boolean equals(Object other) {
      if (!(other instanceof LinuxAxisDescriptor)) {
         return false;
      } else {
         LinuxAxisDescriptor descriptor = (LinuxAxisDescriptor)other;
         return descriptor.type == this.type && descriptor.code == this.code;
      }
   }

   public final String toString() {
      return "LinuxAxis: type = 0x" + Integer.toHexString(this.type) + ", code = 0x" + Integer.toHexString(this.code);
   }
}
