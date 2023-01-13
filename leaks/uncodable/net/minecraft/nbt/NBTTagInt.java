package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagInt extends NBTBase.NBTPrimitive {
   private int data;

   NBTTagInt() {
   }

   public NBTTagInt(int data) {
      this.data = data;
   }

   @Override
   void write(DataOutput output) throws IOException {
      output.writeInt(this.data);
   }

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(96L);
      this.data = input.readInt();
   }

   @Override
   public byte getId() {
      return 3;
   }

   @Override
   public String toString() {
      return "" + this.data;
   }

   @Override
   public NBTBase copy() {
      return new NBTTagInt(this.data);
   }

   @Override
   public boolean equals(Object p_equals_1_) {
      if (super.equals(p_equals_1_)) {
         NBTTagInt nbttagint = (NBTTagInt)p_equals_1_;
         return this.data == nbttagint.data;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.data;
   }

   @Override
   public long getLong() {
      return (long)this.data;
   }

   @Override
   public int getInt() {
      return this.data;
   }

   @Override
   public short getShort() {
      return (short)(this.data & 65535);
   }

   @Override
   public byte getByte() {
      return (byte)(this.data & 0xFF);
   }

   @Override
   public double getDouble() {
      return (double)this.data;
   }

   @Override
   public float getFloat() {
      return (float)this.data;
   }
}
