package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer {
    private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");
    public static TileEntitySkullRenderer instance;
    private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
    private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
    // private static final String __OBFID = "CL_00000971";

    public void func_180542_a(TileEntitySkull p_180542_1_, double p_180542_2_, double p_180542_4_, double p_180542_6_, float p_180542_8_, int p_180542_9_) {
        EnumFacing var10 = EnumFacing.getFront(p_180542_1_.getBlockMetadata() & 7);
        this.renderSkull((float) p_180542_2_, (float) p_180542_4_, (float) p_180542_6_, var10, (float) (p_180542_1_.getSkullRotation() * 360) / 16.0F, p_180542_1_.getSkullType(), p_180542_1_.getPlayerProfile(), p_180542_9_);
    }

    public void setRendererDispatcher(TileEntityRendererDispatcher p_147497_1_) {
        super.setRendererDispatcher(p_147497_1_);
        instance = this;
    }

    public void renderSkull(float p_180543_1_, float p_180543_2_, float p_180543_3_, EnumFacing enumFacing, float p_180543_5_, int itemMeta, GameProfile gameProfile, int destroyProgress) {
        ModelSkeletonHead skeletonHead = this.skeletonHead;

        if (destroyProgress >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyProgress]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 2.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            switch (itemMeta) {
                case 0:
                default:
                    this.bindTexture(SKELETON_TEXTURES);
                    break;

                case 1:
                    this.bindTexture(WITHER_SKELETON_TEXTURES);
                    break;

                case 2:
                    this.bindTexture(ZOMBIE_TEXTURES);
                    skeletonHead = this.humanoidHead;
                    break;

                case 3:
                    skeletonHead = this.humanoidHead;
                    ResourceLocation playerSkin = DefaultPlayerSkin.getDefaultSkinLegacy();

                    if (gameProfile != null) {
                        Minecraft mc = Minecraft.getMinecraft();
                        Map skinCache = mc.getSkinManager().loadSkinFromCache(gameProfile);

                        if (skinCache.containsKey(Type.SKIN)) {
                            playerSkin = mc.getSkinManager().loadSkin((MinecraftProfileTexture) skinCache.get(Type.SKIN), Type.SKIN);
                        } else {
                            UUID uuid = EntityPlayer.getUUID(gameProfile);
                            playerSkin = DefaultPlayerSkin.getDefaultSkin(uuid);
                        }
                    }

                    this.bindTexture(playerSkin);
                    break;

                case 4:
                    this.bindTexture(CREEPER_TEXTURES);
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        if (enumFacing != EnumFacing.UP) {
            switch (TileEntitySkullRenderer.SwitchEnumFacing.field_178458_a[enumFacing.ordinal()]) {
                case 1:
                    GlStateManager.translate(p_180543_1_ + 0.5F, p_180543_2_ + 0.25F, p_180543_3_ + 0.74F);
                    break;

                case 2:
                    GlStateManager.translate(p_180543_1_ + 0.5F, p_180543_2_ + 0.25F, p_180543_3_ + 0.26F);
                    p_180543_5_ = 180.0F;
                    break;

                case 3:
                    GlStateManager.translate(p_180543_1_ + 0.74F, p_180543_2_ + 0.25F, p_180543_3_ + 0.5F);
                    p_180543_5_ = 270.0F;
                    break;

                case 4:
                default:
                    GlStateManager.translate(p_180543_1_ + 0.26F, p_180543_2_ + 0.25F, p_180543_3_ + 0.5F);
                    p_180543_5_ = 90.0F;
            }
        } else {
            GlStateManager.translate(p_180543_1_ + 0.5F, p_180543_2_, p_180543_3_ + 0.5F);
        }

        float var14 = 0.0625F;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        GlStateManager.enableAlpha();
        skeletonHead.render((Entity) null, 0.0F, 0.0F, 0.0F, p_180543_5_, 0.0F, var14);
        GlStateManager.popMatrix();

        if (destroyProgress >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    public void renderTileEntityAt(TileEntity p_180535_1_, double p_180535_2_, double p_180535_4_, double p_180535_6_, float p_180535_8_, int p_180535_9_) {
        this.func_180542_a((TileEntitySkull) p_180535_1_, p_180535_2_, p_180535_4_, p_180535_6_, p_180535_8_, p_180535_9_);
    }

    static final class SwitchEnumFacing {
        static final int[] field_178458_a = new int[EnumFacing.values().length];
        // private static final String __OBFID = "CL_00002468";

        static {
            try {
                field_178458_a[EnumFacing.NORTH.ordinal()] = 1;
            } catch (NoSuchFieldError var4) {
                ;
            }

            try {
                field_178458_a[EnumFacing.SOUTH.ordinal()] = 2;
            } catch (NoSuchFieldError var3) {
                ;
            }

            try {
                field_178458_a[EnumFacing.WEST.ordinal()] = 3;
            } catch (NoSuchFieldError var2) {
                ;
            }

            try {
                field_178458_a[EnumFacing.EAST.ordinal()] = 4;
            } catch (NoSuchFieldError var1) {
                ;
            }
        }
    }
}
