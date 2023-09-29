package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;

public class Tezzelator {
    public static Tessellator t = Tessellator.getInstance();
    public static final Tezzelator instance = new Tezzelator();

    public void end() {
        t.draw();
    }

    public Tezzelator vertex(final double p_vertex_1_, final double p_vertex_3_, final double p_vertex_5_) {
        t.getWorldRenderer().pos(p_vertex_1_, p_vertex_3_, p_vertex_5_);
        return this;
    }

    public void color(final float p_color_1_, final float p_color_2_, final float p_color_3_, final float p_color_4_) {
        t.getWorldRenderer().func_181666_a(p_color_1_, p_color_2_, p_color_3_, p_color_4_);
    }

    public void tex2(final short p_tex2_1_, final short p_tex2_2_) {
        t.getWorldRenderer().func_181671_a(p_tex2_1_, p_tex2_2_);
    }

    public void normal(final float p_normal_1_, final float p_normal_2_, final float p_normal_3_) {
        t.getWorldRenderer().func_181663_c(p_normal_1_, p_normal_2_, p_normal_3_);
    }

    public void begin(final int p_begin_1_, final RealmsVertexFormat p_begin_2_) {
        t.getWorldRenderer().begin(p_begin_1_, p_begin_2_.getVertexFormat());
    }

    public void endVertex() {
        t.getWorldRenderer().endVertex();
    }

    public void offset(final double p_offset_1_, final double p_offset_3_, final double p_offset_5_) {
        t.getWorldRenderer().setTranslation(p_offset_1_, p_offset_3_, p_offset_5_);
    }

    public RealmsBufferBuilder color(final int p_color_1_, final int p_color_2_, final int p_color_3_, final int p_color_4_) {
        return new RealmsBufferBuilder(t.getWorldRenderer().func_181669_b(p_color_1_, p_color_2_, p_color_3_, p_color_4_));
    }

    public Tezzelator tex(final double p_tex_1_, final double p_tex_3_) {
        t.getWorldRenderer().tex(p_tex_1_, p_tex_3_);
        return this;
    }
}
