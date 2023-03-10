/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 */
package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntitySkullRenderer
extends TileEntitySpecialRenderer<TileEntitySkull> {
    private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");
    public static TileEntitySkullRenderer instance;
    private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
    private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();

    @Override
    public void renderTileEntityAt(TileEntitySkull te, double x, double y, double z, float partialTicks, int destroyStage) {
        EnumFacing enumfacing = EnumFacing.getFront(te.getBlockMetadata() & 7);
        this.renderSkull((float)x, (float)y, (float)z, enumfacing, (float)(te.getSkullRotation() * 360) / 16.0f, te.getSkullType(), te.getPlayerProfile(), destroyStage);
    }

    @Override
    public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
        super.setRendererDispatcher(rendererDispatcherIn);
        instance = this;
    }

    public void renderSkull(float p_180543_1_, float p_180543_2_, float p_180543_3_, EnumFacing p_180543_4_, float p_180543_5_, int p_180543_6_, GameProfile p_180543_7_, int p_180543_8_) {
        ModelSkeletonHead modelbase = this.skeletonHead;
        if (p_180543_8_ >= 0) {
            this.bindTexture(DESTROY_STAGES[p_180543_8_]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0f, 2.0f, 1.0f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        } else {
            switch (p_180543_6_) {
                default: {
                    this.bindTexture(SKELETON_TEXTURES);
                    break;
                }
                case 1: {
                    this.bindTexture(WITHER_SKELETON_TEXTURES);
                    break;
                }
                case 2: {
                    this.bindTexture(ZOMBIE_TEXTURES);
                    modelbase = this.humanoidHead;
                    break;
                }
                case 3: {
                    modelbase = this.humanoidHead;
                    ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();
                    if (p_180543_7_ != null) {
                        Minecraft minecraft = Minecraft.getMinecraft();
                        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(p_180543_7_);
                        if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                            resourcelocation = minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                        } else {
                            UUID uuid = EntityPlayer.getUUID(p_180543_7_);
                            resourcelocation = DefaultPlayerSkin.getDefaultSkin(uuid);
                        }
                    }
                    this.bindTexture(resourcelocation);
                    break;
                }
                case 4: {
                    this.bindTexture(CREEPER_TEXTURES);
                }
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        if (p_180543_4_ != EnumFacing.UP) {
            switch (p_180543_4_) {
                case NORTH: {
                    GlStateManager.translate(p_180543_1_ + 0.5f, p_180543_2_ + 0.25f, p_180543_3_ + 0.74f);
                    break;
                }
                case SOUTH: {
                    GlStateManager.translate(p_180543_1_ + 0.5f, p_180543_2_ + 0.25f, p_180543_3_ + 0.26f);
                    p_180543_5_ = 180.0f;
                    break;
                }
                case WEST: {
                    GlStateManager.translate(p_180543_1_ + 0.74f, p_180543_2_ + 0.25f, p_180543_3_ + 0.5f);
                    p_180543_5_ = 270.0f;
                    break;
                }
                default: {
                    GlStateManager.translate(p_180543_1_ + 0.26f, p_180543_2_ + 0.25f, p_180543_3_ + 0.5f);
                    p_180543_5_ = 90.0f;
                    break;
                }
            }
        } else {
            GlStateManager.translate(p_180543_1_ + 0.5f, p_180543_2_, p_180543_3_ + 0.5f);
        }
        float f = 0.0625f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlpha();
        ((ModelBase)modelbase).render(null, 0.0f, 0.0f, 0.0f, p_180543_5_, 0.0f, f);
        GlStateManager.popMatrix();
        if (p_180543_8_ >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}

