/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class RenderItemFrame
extends Render<EntityItemFrame> {
    private static final ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ModelResourceLocation itemFrameModel = new ModelResourceLocation("item_frame", "normal");
    private final ModelResourceLocation mapModel = new ModelResourceLocation("item_frame", "map");
    private RenderItem itemRenderer;
    private static double itemRenderDistanceSq = 4096.0;

    public RenderItemFrame(RenderManager renderManagerIn, RenderItem itemRendererIn) {
        super(renderManagerIn);
        this.itemRenderer = itemRendererIn;
    }

    @Override
    public void doRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        BlockPos blockpos = entity.getHangingPosition();
        double d0 = (double)blockpos.getX() - entity.posX + x;
        double d1 = (double)blockpos.getY() - entity.posY + y;
        double d2 = (double)blockpos.getZ() - entity.posZ + z;
        GlStateManager.translate(d0 + 0.5, d1 + 0.5, d2 + 0.5);
        GlStateManager.rotate(180.0f - entity.rotationYaw, 0.0f, 1.0f, 0.0f);
        this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
        ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        IBakedModel ibakedmodel = entity.getDisplayedItem() != null && entity.getDisplayedItem().getItem() == Items.filled_map ? modelmanager.getModel(this.mapModel) : modelmanager.getModel(this.itemFrameModel);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5f, -0.5f, -0.5f);
        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        GlStateManager.translate(0.0f, 0.0f, 0.4375f);
        this.renderItem(entity);
        GlStateManager.popMatrix();
        this.renderName(entity, x + (double)((float)entity.facingDirection.getFrontOffsetX() * 0.3f), y - 0.25, z + (double)((float)entity.facingDirection.getFrontOffsetZ() * 0.3f));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityItemFrame entity) {
        return null;
    }

    private void renderItem(EntityItemFrame itemFrame) {
        ItemStack itemstack = itemFrame.getDisplayedItem();
        if (itemstack != null) {
            if (!this.isRenderItem(itemFrame)) {
                return;
            }
            if (!Config.zoomMode) {
                EntityPlayerSP entity = this.mc.thePlayer;
                double d0 = itemFrame.getDistanceSq(entity.posX, entity.posY, entity.posZ);
                if (d0 > 4096.0) {
                    return;
                }
            }
            EntityItem entityitem = new EntityItem(itemFrame.worldObj, 0.0, 0.0, 0.0, itemstack);
            Item item = entityitem.getEntityItem().getItem();
            entityitem.getEntityItem().stackSize = 1;
            entityitem.hoverStart = 0.0f;
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            int i = itemFrame.getRotation();
            if (item instanceof ItemMap) {
                i = i % 4 * 2;
            }
            GlStateManager.rotate((float)i * 360.0f / 8.0f, 0.0f, 0.0f, 1.0f);
            if (!Reflector.postForgeBusEvent(Reflector.RenderItemInFrameEvent_Constructor, itemFrame, this)) {
                if (item instanceof ItemMap) {
                    this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
                    GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
                    float f = 0.0078125f;
                    GlStateManager.scale(f, f, f);
                    GlStateManager.translate(-64.0f, -64.0f, 0.0f);
                    MapData mapdata = Items.filled_map.getMapData(entityitem.getEntityItem(), itemFrame.worldObj);
                    GlStateManager.translate(0.0f, 0.0f, -1.0f);
                    if (mapdata != null) {
                        this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
                    }
                } else {
                    TextureAtlasSprite textureatlassprite = null;
                    if (item == Items.compass) {
                        textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite(TextureCompass.field_176608_l);
                        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                        if (textureatlassprite instanceof TextureCompass) {
                            TextureCompass texturecompass = (TextureCompass)textureatlassprite;
                            double d1 = texturecompass.currentAngle;
                            double d2 = texturecompass.angleDelta;
                            texturecompass.currentAngle = 0.0;
                            texturecompass.angleDelta = 0.0;
                            texturecompass.updateCompass(itemFrame.worldObj, itemFrame.posX, itemFrame.posZ, MathHelper.wrapAngleTo180_float(180 + itemFrame.facingDirection.getHorizontalIndex() * 90), false, true);
                            texturecompass.currentAngle = d1;
                            texturecompass.angleDelta = d2;
                        } else {
                            textureatlassprite = null;
                        }
                    }
                    GlStateManager.scale(0.5f, 0.5f, 0.5f);
                    if (!this.itemRenderer.shouldRenderItemIn3D(entityitem.getEntityItem()) || item instanceof ItemSkull) {
                        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
                    }
                    GlStateManager.pushAttrib();
                    RenderHelper.enableStandardItemLighting();
                    this.itemRenderer.func_181564_a(entityitem.getEntityItem(), ItemCameraTransforms.TransformType.FIXED);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popAttrib();
                    if (textureatlassprite != null && textureatlassprite.getFrameCount() > 0) {
                        textureatlassprite.updateAnimation();
                    }
                }
            }
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected void renderName(EntityItemFrame entity, double x, double y, double z) {
        if (Minecraft.isGuiEnabled() && entity.getDisplayedItem() != null && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity) {
            float f2;
            float f = 1.6f;
            float f1 = 0.016666668f * f;
            double d0 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f3 = f2 = entity.isSneaking() ? 32.0f : 64.0f;
            if (d0 < (double)(f2 * f2)) {
                String s = entity.getDisplayedItem().getDisplayName();
                if (entity.isSneaking()) {
                    FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)x + 0.0f, (float)y + entity.height + 0.5f, (float)z);
                    GL11.glNormal3f((float)0.0f, (float)1.0f, (float)0.0f);
                    GlStateManager.rotate(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
                    GlStateManager.scale(-f1, -f1, f1);
                    GlStateManager.disableLighting();
                    GlStateManager.translate(0.0f, 0.25f / f1, 0.0f);
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(770, 771);
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    int i = fontrenderer.getStringWidth(s) / 2;
                    GlStateManager.disableTexture2D();
                    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    worldrenderer.pos(-i - 1, -1.0, 0.0).func_181666_a(0.0f, 0.0f, 0.0f, 0.25f).end();
                    worldrenderer.pos(-i - 1, 8.0, 0.0).func_181666_a(0.0f, 0.0f, 0.0f, 0.25f).end();
                    worldrenderer.pos(i + 1, 8.0, 0.0).func_181666_a(0.0f, 0.0f, 0.0f, 0.25f).end();
                    worldrenderer.pos(i + 1, -1.0, 0.0).func_181666_a(0.0f, 0.0f, 0.0f, 0.25f).end();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.depthMask(true);
                    fontrenderer.drawString(s, (float)(-fontrenderer.getStringWidth(s) / 2), 0.0f, 0x20FFFFFF);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GlStateManager.popMatrix();
                } else {
                    this.renderLivingLabel(entity, s, x, y, z, 64);
                }
            }
        }
    }

    private boolean isRenderItem(EntityItemFrame p_isRenderItem_1_) {
        if (Shaders.isShadowPass) {
            return false;
        }
        if (!Config.zoomMode) {
            Entity entity = this.mc.getRenderViewEntity();
            double d0 = p_isRenderItem_1_.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (d0 > itemRenderDistanceSq) {
                return false;
            }
        }
        return true;
    }

    public static void updateItemRenderDistance() {
        Minecraft minecraft = Config.getMinecraft();
        double d0 = Config.limit(minecraft.gameSettings.fovSetting, 1.0f, 120.0f);
        double d1 = Math.max(6.0 * (double)minecraft.displayHeight / d0, 16.0);
        itemRenderDistanceSq = d1 * d1;
    }
}

