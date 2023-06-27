package net.minecraft.client.renderer.entity.layers;

import net.minecraft.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.monster.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.init.*;
import java.util.*;
import com.mojang.authlib.*;
import net.minecraft.tileentity.*;
import net.minecraft.nbt.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.item.*;

public class LayerCustomHead implements LayerRenderer<EntityLivingBase>
{
    private final ModelRenderer field_177209_a;
    
    public LayerCustomHead(final ModelRenderer p_i46120_1_) {
        this.field_177209_a = p_i46120_1_;
    }
    
    @Override
    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        final ItemStack itemstack = entitylivingbaseIn.getCurrentArmor(3);
        if (itemstack != null && itemstack.getItem() != null) {
            final Item item = itemstack.getItem();
            final Minecraft minecraft = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();
            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translate(0.0f, 0.2f, 0.0f);
            }
            final boolean flag = entitylivingbaseIn instanceof EntityVillager || (entitylivingbaseIn instanceof EntityZombie && ((EntityZombie)entitylivingbaseIn).isVillager());
            if (!flag && entitylivingbaseIn.isChild()) {
                final float f = 2.0f;
                final float f2 = 1.4f;
                GlStateManager.scale(f2 / f, f2 / f, f2 / f);
                GlStateManager.translate(0.0f, 16.0f * scale, 0.0f);
            }
            this.field_177209_a.postRender(0.0625f);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (item instanceof ItemBlock) {
                final float f3 = 0.625f;
                GlStateManager.translate(0.0f, -0.25f, 0.0f);
                GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                GlStateManager.scale(f3, -f3, -f3);
                if (flag) {
                    GlStateManager.translate(0.0f, 0.1875f, 0.0f);
                }
                minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD);
            }
            else if (item == Items.skull) {
                final float f4 = 1.1875f;
                GlStateManager.scale(f4, -f4, -f4);
                if (flag) {
                    GlStateManager.translate(0.0f, 0.0625f, 0.0f);
                }
                GameProfile gameprofile = null;
                if (itemstack.hasTagCompound()) {
                    final NBTTagCompound nbttagcompound = itemstack.getTagCompound();
                    if (nbttagcompound.hasKey("SkullOwner", 10)) {
                        gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                    }
                    else if (nbttagcompound.hasKey("SkullOwner", 8)) {
                        final String s = nbttagcompound.getString("SkullOwner");
                        if (!StringUtils.isNullOrEmpty(s)) {
                            gameprofile = TileEntitySkull.updateGameprofile(new GameProfile((UUID)null, s));
                            nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                        }
                    }
                }
                TileEntitySkullRenderer.instance.renderSkull(-0.5f, 0.0f, -0.5f, EnumFacing.UP, 180.0f, itemstack.getMetadata(), gameprofile, -1);
            }
            GlStateManager.popMatrix();
        }
    }
    
    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
