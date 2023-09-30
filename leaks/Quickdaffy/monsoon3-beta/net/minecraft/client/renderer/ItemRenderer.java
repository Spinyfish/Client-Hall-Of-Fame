/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventScaleItem;
import wtf.monsoon.impl.event.EventTransformItem;
import wtf.monsoon.impl.module.combat.Aura;
import wtf.monsoon.impl.module.visual.BlockAnimations;
import wtf.monsoon.impl.module.visual.NoRender;

public class ItemRenderer {
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
    private final Minecraft mc;
    private ItemStack itemToRender;
    private float equippedProgress;
    private float prevEquippedProgress;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;
    private int equippedItemSlot = -1;

    public ItemRenderer(Minecraft mcIn) {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
        if (heldStack != null) {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                if (!(!this.isBlockTranslucent(block) || Config.isShaders() && Shaders.renderItemKeepDepthMask)) {
                    GlStateManager.depthMask(false);
                }
            }
            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);
            if (this.isBlockTranslucent(block)) {
                GlStateManager.depthMask(true);
            }
            GlStateManager.popMatrix();
        }
    }

    private boolean isBlockTranslucent(Block blockIn) {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    private void func_178101_a(float angle, float p_178101_2_) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(p_178101_2_, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void func_178109_a(AbstractClientPlayer clientPlayer) {
        int i = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double)clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
        if (Config.isDynamicLights()) {
            i = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), i);
        }
        float f = i & 0xFFFF;
        float f1 = i >> 16;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private void func_178110_a(EntityPlayerSP entityplayerspIn, float partialTicks) {
        float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - f1) * 0.1f, 0.0f, 1.0f, 0.0f);
    }

    private float func_178100_c(float p_178100_1_) {
        float f = 1.0f - p_178100_1_ / 45.0f + 0.1f;
        f = MathHelper.clamp_float(f, 0.0f, 1.0f);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5f + 0.5f;
        return f;
    }

    private void renderRightArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(64.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(-62.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(0.25f, -0.85f, 0.75f);
        renderPlayerIn.renderRightArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(41.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(-0.3f, -1.1f, 0.45f);
        renderPlayerIn.renderLeftArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer renderplayer = (RenderPlayer)render;
        if (!clientPlayer.isInvisible()) {
            GlStateManager.disableCull();
            this.renderRightArm(renderplayer);
            this.renderLeftArm(renderplayer);
            GlStateManager.enableCull();
        }
    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float p_178097_2_, float p_178097_3_, float p_178097_4_) {
        float f = -0.4f * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float)Math.PI);
        float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float)Math.PI * 2.0f);
        float f2 = -0.2f * MathHelper.sin(p_178097_4_ * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
        float f3 = this.func_178100_c(p_178097_2_);
        GlStateManager.translate(0.0f, 0.04f, -0.72f);
        GlStateManager.translate(0.0f, p_178097_3_ * -1.2f, 0.0f);
        GlStateManager.translate(0.0f, f3 * -0.5f, 0.0f);
        GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f3 * -85.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f);
        this.renderPlayerArms(clientPlayer);
        float f4 = MathHelper.sin(p_178097_4_ * p_178097_4_ * (float)Math.PI);
        float f5 = MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float)Math.PI);
        GlStateManager.rotate(f4 * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f5 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f5 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.38f, 0.38f, 0.38f);
        GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-1.0f, -1.0f, 0.0f);
        GlStateManager.scale(0.015625f, 0.015625f, 0.015625f);
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GL11.glNormal3f((float)0.0f, (float)0.0f, (float)-1.0f);
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(-7.0, 135.0, 0.0).func_181673_a(0.0, 1.0).end();
        worldrenderer.pos(135.0, 135.0, 0.0).func_181673_a(1.0, 1.0).end();
        worldrenderer.pos(135.0, -7.0, 0.0).func_181673_a(1.0, 0.0).end();
        worldrenderer.pos(-7.0, -7.0, 0.0).func_181673_a(0.0, 0.0).end();
        tessellator.draw();
        MapData mapdata = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
        if (mapdata != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
        }
    }

    private void func_178095_a(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_) {
        float f = -0.3f * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float)Math.PI);
        float f1 = 0.4f * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float)Math.PI * 2.0f);
        float f2 = -0.4f * MathHelper.sin(p_178095_3_ * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
        GlStateManager.translate(0.64000005f, -0.6f, -0.71999997f);
        GlStateManager.translate(0.0f, p_178095_2_ * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float f3 = MathHelper.sin(p_178095_3_ * p_178095_3_ * (float)Math.PI);
        float f4 = MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float)Math.PI);
        GlStateManager.rotate(f4 * 70.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f3 * -20.0f, 0.0f, 0.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0f, 3.6f, 3.5f);
        GlStateManager.rotate(120.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(200.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        GlStateManager.translate(5.6f, 0.0f, 0.0f);
        Render render = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        GlStateManager.disableCull();
        RenderPlayer renderplayer = (RenderPlayer)render;
        renderplayer.renderRightArm(this.mc.thePlayer);
        GlStateManager.enableCull();
    }

    private void func_178105_d(float p_178105_1_) {
        float f = -0.4f * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float)Math.PI);
        float f1 = 0.2f * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float)Math.PI * 2.0f);
        float f2 = -0.2f * MathHelper.sin(p_178105_1_ * (float)Math.PI);
        GlStateManager.translate(f, f1, f2);
    }

    private void func_178104_a(AbstractClientPlayer clientPlayer, float p_178104_2_) {
        float f = (float)clientPlayer.getItemInUseCount() - p_178104_2_ + 1.0f;
        float f1 = f / (float)this.itemToRender.getMaxItemUseDuration();
        float f2 = MathHelper.abs(MathHelper.cos(f / 4.0f * (float)Math.PI) * 0.1f);
        if (f1 >= 0.8f) {
            f2 = 0.0f;
        }
        GlStateManager.translate(0.0f, f2, 0.0f);
        float f3 = 1.0f - (float)Math.pow(f1, 27.0);
        GlStateManager.translate(f3 * 0.6f, f3 * -0.5f, f3 * 0.0f);
        GlStateManager.rotate(f3 * 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f3 * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(f3 * 30.0f, 0.0f, 0.0f, 1.0f);
    }

    private void transformFirstPersonItem(float p_178096_1_, float p_178096_2_) {
        EventTransformItem eventTransformItem = new EventTransformItem(0.56f, -0.52f, -0.71999997f);
        Wrapper.getEventBus().post(eventTransformItem);
        GlStateManager.translate(eventTransformItem.getPosX(), eventTransformItem.getPosY(), eventTransformItem.getPosZ());
        GlStateManager.translate(0.0f, p_178096_1_ * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float f = MathHelper.sin(p_178096_2_ * p_178096_2_ * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(p_178096_2_) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f1 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f1 * -80.0f, 1.0f, 0.0f, 0.0f);
        EventScaleItem eventScaleItem = new EventScaleItem(0.4f, 0.4f, 0.4f);
        Wrapper.getEventBus().post(eventScaleItem);
        GlStateManager.scale(eventScaleItem.getScaleX(), eventScaleItem.getScaleY(), eventScaleItem.getScaleZ());
    }

    private void func_178098_a(float p_178098_1_, AbstractClientPlayer clientPlayer) {
        GlStateManager.rotate(-18.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-12.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-8.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-0.9f, 0.2f, 0.0f);
        float f = (float)this.itemToRender.getMaxItemUseDuration() - ((float)clientPlayer.getItemInUseCount() - p_178098_1_ + 1.0f);
        float f1 = f / 20.0f;
        f1 = (f1 * f1 + f1 * 2.0f) / 3.0f;
        if (f1 > 1.0f) {
            f1 = 1.0f;
        }
        if (f1 > 0.1f) {
            float f2 = MathHelper.sin((f - 0.1f) * 1.3f);
            float f3 = f1 - 0.1f;
            float f4 = f2 * f3;
            GlStateManager.translate(f4 * 0.0f, f4 * 0.01f, f4 * 0.0f);
        }
        GlStateManager.translate(f1 * 0.0f, f1 * 0.0f, f1 * 0.1f);
        GlStateManager.scale(1.0f, 1.0f, 1.0f + f1 * 0.2f);
    }

    private void doBlockTransformations() {
        GlStateManager.translate(-0.5f, 0.2f, 0.0f);
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
    }

    public void renderItemInFirstPerson(float partialTicks) {
        if (!Config.isShaders() || !Shaders.isSkipRenderHand()) {
            float f = 1.0f - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
            EntityPlayerSP abstractclientplayer = this.mc.thePlayer;
            float f1 = abstractclientplayer.getSwingProgress(partialTicks);
            float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
            float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
            this.func_178101_a(f2, f3);
            this.func_178109_a(abstractclientplayer);
            this.func_178110_a(abstractclientplayer, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            if (this.itemToRender != null) {
                if (this.itemToRender.getItem() instanceof ItemMap) {
                    this.renderItemMap(abstractclientplayer, f2, f, f1);
                } else if (abstractclientplayer.getItemInUseCount() > 0 || Wrapper.getModule(Aura.class).isEnabled() && Wrapper.getModule(Aura.class).getTarget() != null && !Wrapper.getModule(Aura.class).getTarget().isDead && (double)this.mc.thePlayer.getDistanceToEntity(Wrapper.getModule(Aura.class).getTarget()) <= Wrapper.getModule(Aura.class).getRange().getValue() && this.mc.thePlayer != null && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() != null) {
                    EnumAction enumaction = Wrapper.getModule(Aura.class).isEnabled() && Wrapper.getModule(Aura.class).getTarget() != null ? EnumAction.BLOCK : this.itemToRender.getItemUseAction();
                    block0 : switch (enumaction) {
                        case NONE: {
                            this.transformFirstPersonItem(f, 0.0f);
                            break;
                        }
                        case EAT: 
                        case DRINK: {
                            this.func_178104_a(abstractclientplayer, partialTicks);
                            this.transformFirstPersonItem(f, 0.0f);
                            break;
                        }
                        case BLOCK: {
                            float f8 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.0f);
                            float var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * (float)Math.PI);
                            switch (Wrapper.getMonsoon().getModuleManager().getModule(BlockAnimations.class).animation.getValue()) {
                                case OLD: {
                                    this.transformFirstPersonItem(f, f1);
                                    this.doBlockTransformations();
                                    break block0;
                                }
                                case CHILL: {
                                    GlStateManager.translate(0.055f, 0.12f, 0.0f);
                                    this.transformFirstPersonItem(f, f1);
                                    this.doBlockTransformations();
                                    break block0;
                                }
                                case EXHI: 
                                case EXHIBOBO: {
                                    this.exhibition(f, f1);
                                    this.doBlockTransformations();
                                    break block0;
                                }
                                case EXHI_TAP: {
                                    GL11.glTranslated((double)-0.06, (double)0.17, (double)-0.0);
                                    this.transformFirstPersonItem(f / 2.5f, 0.0f);
                                    GlStateManager.rotate(-f8 * 40.0f / 2.0f, f8 / 2.0f, 1.0f, 4.0f);
                                    GlStateManager.rotate(-f8 * 30.0f, 1.0f, f8 / 3.0f, -0.0f);
                                    this.doBlockTransformations();
                                    break block0;
                                }
                                case SLIDE: {
                                    GlStateManager.translate(-0.02, (double)0.05f, 0.0);
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.05f, 0.2f, 0.2f);
                                    GlStateManager.rotate(-var9 * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-var9 * 70.0f, 1.0f, -0.4f, -0.0f);
                                    break block0;
                                }
                                case ASTOLFO: {
                                    GlStateManager.translate(0.05f, 0.0f, -0.35f);
                                    this.transformFirstPersonItem(f, f1);
                                    this.doBlockTransformations();
                                    break block0;
                                }
                                case SWING: {
                                    this.transformFirstPersonItem(f, f1);
                                    this.doBlockTransformations();
                                    GlStateManager.translate(-0.3f, 0.2f, 0.2f);
                                    break block0;
                                }
                                case OH_THE_MISERY: {
                                    GlStateManager.translate(0.0f, 0.125f, 0.0f);
                                    this.transformFirstPersonItem(f, f1);
                                    float var15 = MathHelper.sin((float)((double)MathHelper.sqrt_float(f1) * Math.PI));
                                    GlStateManager.rotate(var15 * 30.0f / 2.0f, -var15, -0.0f, 9.0f);
                                    GlStateManager.rotate(var15 * 40.0f, 1.0f, -var15 / 2.0f, -0.0f);
                                    this.doBlockTransformations();
                                }
                            }
                            break;
                        }
                        case BOW: {
                            this.transformFirstPersonItem(f, 0.0f);
                            this.func_178098_a(partialTicks, abstractclientplayer);
                        }
                    }
                } else {
                    switch (Wrapper.getModule(BlockAnimations.class).swing.getValue()) {
                        case NORMAL: {
                            this.func_178105_d(f1);
                            this.transformFirstPersonItem(f, f1);
                            break;
                        }
                        case SMOOTH: {
                            this.func_178105_d(0.0f);
                            this.transformFirstPersonItem(f, f1);
                        }
                    }
                }
                this.renderItem(abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
            } else if (!abstractclientplayer.isInvisible()) {
                this.func_178095_a(abstractclientplayer, f, f1);
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
        }
    }

    private void exhibition(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56, -0.52, -0.7199999690055847);
        GlStateManager.translate(0.0, 0.07, 0.0);
        switch (Wrapper.getMonsoon().getModuleManager().getModule(BlockAnimations.class).animation.getValue()) {
            case EXHI: {
                float funny1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
                GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
                GlStateManager.translate(0.0f - funny1 / 100.0f, 0.0f + funny1 / 15.0f, 0.0f + funny1 / 15.0f);
                GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
                float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
                float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
                GlStateManager.rotate(f1 * -40.0f, 1.0f, 0.4f, 0.9f);
                GlStateManager.rotate(f1 * -20.0f, 1.0f, 0.0f, 0.0f);
                EventScaleItem event = new EventScaleItem(0.4f, 0.4f, 0.4f);
                Wrapper.getEventBus().post(event);
                GlStateManager.scale(event.getScaleX(), event.getScaleY(), event.getScaleZ());
                break;
            }
            case EXHIBOBO: {
                float funny2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
                GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f + funny2 / 10.0f);
                GlStateManager.translate(0.0f, 0.0f + funny2 / 12.0f, 0.0f);
                GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
                float var3 = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
                float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float)Math.PI);
                GlStateManager.rotate(var4 * -20.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.rotate(var4 * -20.0f, 1.0f, 0.2f, 1.0f);
                GlStateManager.rotate(var4 * -15.0f, 1.0f, 0.4f, 0.9f);
                EventScaleItem event2 = new EventScaleItem(0.4f, 0.4f, 0.4f);
                Wrapper.getEventBus().post(event2);
                GlStateManager.scale(event2.getScaleX(), event2.getScaleY(), event2.getScaleZ());
            }
        }
    }

    public void renderOverlays(float partialTicks) {
        GlStateManager.disableAlpha();
        if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState iblockstate = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            BlockPos blockpos = new BlockPos(this.mc.thePlayer);
            EntityPlayerSP entityplayer = this.mc.thePlayer;
            for (int i = 0; i < 8; ++i) {
                double d0 = entityplayer.posX + (double)(((float)((i >> 0) % 2) - 0.5f) * entityplayer.width * 0.8f);
                double d1 = entityplayer.posY + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
                double d2 = entityplayer.posZ + (double)(((float)((i >> 2) % 2) - 0.5f) * entityplayer.width * 0.8f);
                BlockPos blockpos1 = new BlockPos(d0, d1 + (double)entityplayer.getEyeHeight(), d2);
                IBlockState iblockstate1 = this.mc.theWorld.getBlockState(blockpos1);
                if (!iblockstate1.getBlock().isVisuallyOpaque()) continue;
                iblockstate = iblockstate1;
                blockpos = blockpos1;
            }
            if (iblockstate.getBlock().getRenderType() != -1) {
                Object object = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);
                if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, Float.valueOf(partialTicks), object, iblockstate, blockpos)) {
                    this.func_178108_a(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iblockstate));
                }
            }
        }
        if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water) && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, Float.valueOf(partialTicks))) {
                this.renderWaterOverlayTexture(partialTicks);
            }
            if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, Float.valueOf(partialTicks))) {
                this.renderFireInFirstPerson(partialTicks);
            }
        }
        GlStateManager.enableAlpha();
    }

    private void func_178108_a(float p_178108_1_, TextureAtlasSprite p_178108_2_) {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f = 0.1f;
        GlStateManager.color(0.1f, 0.1f, 0.1f, 0.5f);
        GlStateManager.pushMatrix();
        float f1 = -1.0f;
        float f2 = 1.0f;
        float f3 = -1.0f;
        float f4 = 1.0f;
        float f5 = -0.5f;
        float f6 = p_178108_2_.getMinU();
        float f7 = p_178108_2_.getMaxU();
        float f8 = p_178108_2_.getMinV();
        float f9 = p_178108_2_.getMaxV();
        worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
        worldrenderer.pos(-1.0, -1.0, -0.5).func_181673_a(f7, f9).end();
        worldrenderer.pos(1.0, -1.0, -0.5).func_181673_a(f6, f9).end();
        worldrenderer.pos(1.0, 1.0, -0.5).func_181673_a(f6, f8).end();
        worldrenderer.pos(-1.0, 1.0, -0.5).func_181673_a(f7, f8).end();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderWaterOverlayTexture(float p_78448_1_) {
        if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
            this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            float f = this.mc.thePlayer.getBrightness(p_78448_1_);
            GlStateManager.color(f, f, f, 0.5f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            float f1 = 4.0f;
            float f2 = -1.0f;
            float f3 = 1.0f;
            float f4 = -1.0f;
            float f5 = 1.0f;
            float f6 = -0.5f;
            float f7 = -this.mc.thePlayer.rotationYaw / 64.0f;
            float f8 = this.mc.thePlayer.rotationPitch / 64.0f;
            worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
            worldrenderer.pos(-1.0, -1.0, -0.5).func_181673_a(4.0f + f7, 4.0f + f8).end();
            worldrenderer.pos(1.0, -1.0, -0.5).func_181673_a(0.0f + f7, 4.0f + f8).end();
            worldrenderer.pos(1.0, 1.0, -0.5).func_181673_a(0.0f + f7, 0.0f + f8).end();
            worldrenderer.pos(-1.0, 1.0, -0.5).func_181673_a(4.0f + f7, 0.0f + f8).end();
            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
        }
    }

    private void renderFireInFirstPerson(float p_78442_1_) {
        if (Wrapper.getModule(NoRender.class).isEnabled() && Wrapper.getModule(NoRender.class).fireOverlay.getValue().booleanValue()) {
            return;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.9f);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float f = 1.0f;
        for (int i = 0; i < 2; ++i) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            float f5 = (0.0f - f) / 2.0f;
            float f6 = f5 + f;
            float f7 = 0.0f - f / 2.0f;
            float f8 = f7 + f;
            float f9 = -0.5f;
            GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24f, -0.3f, 0.0f);
            GlStateManager.rotate((float)(i * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);
            worldrenderer.begin(7, DefaultVertexFormats.field_181707_g);
            worldrenderer.setSprite(textureatlassprite);
            worldrenderer.pos(f5, f7, f9).func_181673_a(f2, f4).end();
            worldrenderer.pos(f6, f7, f9).func_181673_a(f1, f4).end();
            worldrenderer.pos(f6, f8, f9).func_181673_a(f1, f3).end();
            worldrenderer.pos(f5, f8, f9).func_181673_a(f2, f3).end();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayerSP entityplayer = this.mc.thePlayer;
        ItemStack itemstack = entityplayer.inventory.getCurrentItem();
        boolean flag = false;
        if (this.itemToRender != null && itemstack != null) {
            if (!this.itemToRender.getIsItemStackEqual(itemstack)) {
                boolean flag1;
                if (Reflector.ForgeItem_shouldCauseReequipAnimation.exists() && !(flag1 = Reflector.callBoolean(this.itemToRender.getItem(), Reflector.ForgeItem_shouldCauseReequipAnimation, this.itemToRender, itemstack, this.equippedItemSlot != entityplayer.inventory.currentItem))) {
                    this.itemToRender = itemstack;
                    this.equippedItemSlot = entityplayer.inventory.currentItem;
                    return;
                }
                flag = true;
            }
        } else {
            flag = this.itemToRender != null || itemstack != null;
        }
        float f2 = 0.4f;
        float f = flag ? 0.0f : 1.0f;
        float f1 = MathHelper.clamp_float(f - this.equippedProgress, -f2, f2);
        this.equippedProgress += f1;
        if (this.equippedProgress < 0.1f) {
            this.itemToRender = itemstack;
            this.equippedItemSlot = entityplayer.inventory.currentItem;
            if (Config.isShaders()) {
                Shaders.setItemToRenderMain(itemstack);
            }
        }
    }

    public void resetEquippedProgress() {
        this.equippedProgress = 0.0f;
    }

    public void resetEquippedProgress2() {
        this.equippedProgress = 0.0f;
    }
}

