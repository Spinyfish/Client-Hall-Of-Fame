package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;

public class BlockPartFace
{
    public static final EnumFacing FACING_DEFAULT = null;
    public final EnumFacing cullFace;
    public final int tintIndex;
    public final String texture;
    public final BlockFaceUV blockFaceUV;

    public BlockPartFace(EnumFacing cullFaceIn, int tintIndexIn, String textureIn, BlockFaceUV blockFaceUVIn)
    {
        this.cullFace = cullFaceIn;
        this.tintIndex = tintIndexIn;
        this.texture = textureIn;
        this.blockFaceUV = blockFaceUVIn;
    }

    static class Deserializer implements JsonDeserializer
    {

        public BlockPartFace parseBlockPartFace(JsonElement p_178338_1_, Type p_178338_2_, JsonDeserializationContext p_178338_3_)
        {
            JsonObject var4 = p_178338_1_.getAsJsonObject();
            EnumFacing var5 = this.parseCullFace(var4);
            int var6 = this.parseTintIndex(var4);
            String var7 = this.parseTexture(var4);
            BlockFaceUV var8 = (BlockFaceUV)p_178338_3_.deserialize(var4, BlockFaceUV.class);
            return new BlockPartFace(var5, var6, var7, var8);
        }

        protected int parseTintIndex(JsonObject p_178337_1_)
        {
            return JsonUtils.getInt(p_178337_1_, "tintindex", -1);
        }

        private String parseTexture(JsonObject p_178340_1_)
        {
            return JsonUtils.getString(p_178340_1_, "texture");
        }

        private EnumFacing parseCullFace(JsonObject p_178339_1_)
        {
            String var2 = JsonUtils.getString(p_178339_1_, "cullface", "");
            return EnumFacing.byName(var2);
        }

        public Object deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_)
        {
            return this.parseBlockPartFace(p_deserialize_1_, p_deserialize_2_, p_deserialize_3_);
        }
    }
}
