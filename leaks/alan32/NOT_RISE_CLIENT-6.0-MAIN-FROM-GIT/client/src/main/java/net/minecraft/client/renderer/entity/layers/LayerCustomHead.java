package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;

public class LayerCustomHead implements LayerRenderer<EntityLivingBase> {
    private final ModelRenderer field_177209_a;

    public LayerCustomHead(final ModelRenderer p_i46120_1_) {
        this.field_177209_a = p_i46120_1_;
    }

    public void doRenderLayer(final EntityLivingBase entitylivingbaseIn, final float p_177141_2_, final float p_177141_3_, final float partialTicks, final float p_177141_5_, final float p_177141_6_, final float p_177141_7_, final float scale) {
        final ItemStack itemstack = entitylivingbaseIn.getCurrentArmor(3);

        if (itemstack != null && itemstack.getItem() != null) {
            final Item item = itemstack.getItem();
            final Minecraft minecraft = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();

            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            final boolean flag = entitylivingbaseIn instanceof EntityVillager || entitylivingbaseIn instanceof EntityZombie && ((EntityZombie) entitylivingbaseIn).isVillager();

            if (!flag && entitylivingbaseIn.isChild()) {
                final float f = 2.0F;
                final float f1 = 1.4F;
                GlStateManager.scale(f1 / f, f1 / f, f1 / f);
                GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            }

            this.field_177209_a.postRender(0.0625F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (item instanceof ItemBlock) {
                final float f2 = 0.625F;
                GlStateManager.translate(0.0F, -0.25F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(f2, -f2, -f2);

                if (flag) {
                    GlStateManager.translate(0.0F, 0.1875F, 0.0F);
                }

                minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.HEAD);
            } else if (item == Items.skull) {
                final float f3 = 1.1875F;
                GlStateManager.scale(f3, -f3, -f3);

                if (flag) {
                    GlStateManager.translate(0.0F, 0.0625F, 0.0F);
                }

                GameProfile gameprofile = null;

                if (itemstack.hasTagCompound()) {
                    final NBTTagCompound nbttagcompound = itemstack.getTagCompound();

                    if (nbttagcompound.hasKey("SkullOwner", 10)) {
                        gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                    } else if (nbttagcompound.hasKey("SkullOwner", 8)) {
                        final String s = nbttagcompound.getString("SkullOwner");

                        if (!StringUtils.isNullOrEmpty(s)) {
                            gameprofile = TileEntitySkull.updateGameprofile(new GameProfile(null, s));
                            nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                        }
                    }
                }

                TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, itemstack.getMetadata(), gameprofile, -1);
            }

            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}
