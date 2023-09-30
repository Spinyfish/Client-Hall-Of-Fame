package net.optifine.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

public class ModelUtils {
    public static void dbgModel(final IBakedModel model) {
        if (model != null) {
            Config.dbg("Model: " + model + ", ao: " + model.isAmbientOcclusion() + ", gui3d: " + model.isGui3d() + ", builtIn: " + model.isBuiltInRenderer() + ", particle: " + model.getTexture());
            final EnumFacing[] aenumfacing = EnumFacing.VALUES;

            for (int i = 0; i < aenumfacing.length; ++i) {
                final EnumFacing enumfacing = aenumfacing[i];
                final List list = model.getFaceQuads(enumfacing);
                dbgQuads(enumfacing.getName(), list, "  ");
            }

            final List list1 = model.getGeneralQuads();
            dbgQuads("General", list1, "  ");
        }
    }

    private static void dbgQuads(final String name, final List quads, final String prefix) {
        for (final Object e : quads) {
            final BakedQuad bakedquad = (BakedQuad) e;
            dbgQuad(name, bakedquad, prefix);
        }
    }

    public static void dbgQuad(final String name, final BakedQuad quad, final String prefix) {
        Config.dbg(prefix + "Quad: " + quad.getClass().getName() + ", type: " + name + ", face: " + quad.getFace() + ", tint: " + quad.getTintIndex() + ", sprite: " + quad.getSprite());
        dbgVertexData(quad.getVertexData(), "  " + prefix);
    }

    public static void dbgVertexData(final int[] vd, final String prefix) {
        final int i = vd.length / 4;
        Config.dbg(prefix + "Length: " + vd.length + ", step: " + i);

        for (int j = 0; j < 4; ++j) {
            final int k = j * i;
            final float f = Float.intBitsToFloat(vd[k + 0]);
            final float f1 = Float.intBitsToFloat(vd[k + 1]);
            final float f2 = Float.intBitsToFloat(vd[k + 2]);
            final int l = vd[k + 3];
            final float f3 = Float.intBitsToFloat(vd[k + 4]);
            final float f4 = Float.intBitsToFloat(vd[k + 5]);
            Config.dbg(prefix + j + " xyz: " + f + "," + f1 + "," + f2 + " col: " + l + " u,v: " + f3 + "," + f4);
        }
    }

    public static IBakedModel duplicateModel(final IBakedModel model) {
        final List list = duplicateQuadList(model.getGeneralQuads());
        final EnumFacing[] aenumfacing = EnumFacing.VALUES;
        final List list1 = new ArrayList();

        for (int i = 0; i < aenumfacing.length; ++i) {
            final EnumFacing enumfacing = aenumfacing[i];
            final List list2 = model.getFaceQuads(enumfacing);
            final List list3 = duplicateQuadList(list2);
            list1.add(list3);
        }

        final SimpleBakedModel simplebakedmodel = new SimpleBakedModel(list, list1, model.isAmbientOcclusion(), model.isGui3d(), model.getTexture(), model.getItemCameraTransforms());
        return simplebakedmodel;
    }

    public static List duplicateQuadList(final List lists) {
        final List list = new ArrayList();

        for (final Object e : lists) {
            final BakedQuad bakedquad = (BakedQuad) e;
            final BakedQuad bakedquad1 = duplicateQuad(bakedquad);
            list.add(bakedquad1);
        }

        return list;
    }

    public static BakedQuad duplicateQuad(final BakedQuad quad) {
        final BakedQuad bakedquad = new BakedQuad(quad.getVertexData().clone(), quad.getTintIndex(), quad.getFace(), quad.getSprite());
        return bakedquad;
    }
}
