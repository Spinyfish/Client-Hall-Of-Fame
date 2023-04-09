package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.renderer.vertex.VertexFormat;

public interface IVertexConsumer {
    VertexFormat getVertexFormat();

    void put(int var1, float... var2);
}
