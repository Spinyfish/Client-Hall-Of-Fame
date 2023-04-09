package optifine;

import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class BlockModelUtils {
    public static IBakedModel makeModelCube(String spriteName, int tintIndex) {
        TextureAtlasSprite sprite = Config.getMinecraft().getTextureMapBlocks().getAtlasSprite(spriteName);
        return makeModelCube(sprite, tintIndex);
    }

    public static IBakedModel makeModelCube(TextureAtlasSprite sprite, int tintIndex) {
        ArrayList generalQuads = new ArrayList();
        EnumFacing[] facings = EnumFacing.VALUES;
        ArrayList faceQuads = new ArrayList(facings.length);

        for (int bakedModel = 0; bakedModel < facings.length; ++bakedModel) {
            EnumFacing facing = facings[bakedModel];
            ArrayList quads = new ArrayList();
            quads.add(makeBakedQuad(facing, sprite, tintIndex));
            faceQuads.add(quads);
        }

        SimpleBakedModel var8 = new SimpleBakedModel(generalQuads, faceQuads, true, true, sprite, ItemCameraTransforms.field_178357_a);
        return var8;
    }

    private static BakedQuad makeBakedQuad(EnumFacing facing, TextureAtlasSprite sprite, int tintIndex) {
        Vector3f posFrom = new Vector3f(0.0F, 0.0F, 0.0F);
        Vector3f posTo = new Vector3f(16.0F, 16.0F, 16.0F);
        BlockFaceUV uv = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
        BlockPartFace face = new BlockPartFace(facing, tintIndex, "#" + facing.getName(), uv);
        ModelRotation modelRotation = ModelRotation.X0_Y0;
        Object partRotation = null;
        boolean uvLocked = false;
        boolean shade = true;
        FaceBakery faceBakery = new FaceBakery();
        BakedQuad quad = faceBakery.func_178414_a(posFrom, posTo, face, sprite, facing, modelRotation, (BlockPartRotation) partRotation, uvLocked, shade);
        return quad;
    }
}
