package optifine;

import net.minecraft.client.renderer.texture.*;
import net.minecraft.util.*;
import java.util.*;
import org.lwjgl.util.vector.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.client.renderer.block.model.*;

public class BlockModelUtils
{
    public static IBakedModel makeModelCube(final String p_makeModelCube_0_, final int p_makeModelCube_1_) {
        final TextureAtlasSprite textureatlassprite = Config.getMinecraft().getTextureMapBlocks().getAtlasSprite(p_makeModelCube_0_);
        return makeModelCube(textureatlassprite, p_makeModelCube_1_);
    }
    
    public static IBakedModel makeModelCube(final TextureAtlasSprite p_makeModelCube_0_, final int p_makeModelCube_1_) {
        final List list = new ArrayList();
        final EnumFacing[] aenumfacing = EnumFacing.VALUES;
        final List list2 = new ArrayList(aenumfacing.length);
        for (int i = 0; i < aenumfacing.length; ++i) {
            final EnumFacing enumfacing = aenumfacing[i];
            final List list3 = new ArrayList();
            list3.add(makeBakedQuad(enumfacing, p_makeModelCube_0_, p_makeModelCube_1_));
            list2.add(list3);
        }
        final IBakedModel ibakedmodel = new SimpleBakedModel(list, list2, true, true, p_makeModelCube_0_, ItemCameraTransforms.DEFAULT);
        return ibakedmodel;
    }
    
    private static BakedQuad makeBakedQuad(final EnumFacing p_makeBakedQuad_0_, final TextureAtlasSprite p_makeBakedQuad_1_, final int p_makeBakedQuad_2_) {
        final Vector3f vector3f = new Vector3f(0.0f, 0.0f, 0.0f);
        final Vector3f vector3f2 = new Vector3f(16.0f, 16.0f, 16.0f);
        final BlockFaceUV blockfaceuv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
        final BlockPartFace blockpartface = new BlockPartFace(p_makeBakedQuad_0_, p_makeBakedQuad_2_, "#" + p_makeBakedQuad_0_.getName(), blockfaceuv);
        final ModelRotation modelrotation = ModelRotation.X0_Y0;
        final BlockPartRotation blockpartrotation = null;
        final boolean flag = false;
        final boolean flag2 = true;
        final FaceBakery facebakery = new FaceBakery();
        final BakedQuad bakedquad = facebakery.makeBakedQuad(vector3f, vector3f2, blockpartface, p_makeBakedQuad_1_, p_makeBakedQuad_0_, modelrotation, blockpartrotation, flag, flag2);
        return bakedquad;
    }
}
