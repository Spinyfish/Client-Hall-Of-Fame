package net.optifine.shaders;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexAttrib {
   public final int index;
   public final int count;
   public final VertexFormatElement.EnumType type;
   public int offset;

   public SVertexAttrib(int index, int count, VertexFormatElement.EnumType type) {
      this.index = index;
      this.count = count;
      this.type = type;
   }
}
