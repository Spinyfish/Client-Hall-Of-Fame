package net.minecraft.client.renderer.tileentity;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
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

public class RenderItemFrame extends Render<EntityItemFrame> {
    private static final ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ModelResourceLocation itemFrameModel = new ModelResourceLocation("item_frame", "normal");
    private final ModelResourceLocation mapModel = new ModelResourceLocation("item_frame", "map");
    private final RenderItem itemRenderer;
    private static double itemRenderDistanceSq = 4096.0D;

    public RenderItemFrame(final RenderManager renderManagerIn, final RenderItem itemRendererIn) {
        super(renderManagerIn);
        this.itemRenderer = itemRendererIn;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(final EntityItemFrame entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        GlStateManager.pushMatrix();
        final BlockPos blockpos = entity.getHangingPosition();
        final double d0 = (double) blockpos.getX() - entity.posX + x;
        final double d1 = (double) blockpos.getY() - entity.posY + y;
        final double d2 = (double) blockpos.getZ() - entity.posZ + z;
        GlStateManager.translate(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D);
        GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
        this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        final BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
        final ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
        final IBakedModel ibakedmodel;

        if (entity.getDisplayedItem() != null && entity.getDisplayedItem().getItem() == Items.filled_map) {
            ibakedmodel = modelmanager.getModel(this.mapModel);
        } else {
            ibakedmodel = modelmanager.getModel(this.itemFrameModel);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(ibakedmodel, 1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        GlStateManager.translate(0.0F, 0.0F, 0.4375F);
        this.renderItem(entity);
        GlStateManager.popMatrix();
        this.renderName(entity, x + (double) ((float) entity.facingDirection.getFrontOffsetX() * 0.3F), y - 0.25D, z + (double) ((float) entity.facingDirection.getFrontOffsetZ() * 0.3F));
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(final EntityItemFrame entity) {
        return null;
    }

    private void renderItem(final EntityItemFrame itemFrame) {
        final ItemStack itemstack = itemFrame.getDisplayedItem();

        if (itemstack != null) {
            if (!this.isRenderItem(itemFrame)) {
                return;
            }

            if (!Config.zoomMode) {
                final Entity entity = this.mc.thePlayer;
                final double d0 = itemFrame.getDistanceSq(entity.posX, entity.posY, entity.posZ);

                if (d0 > 4096.0D) {
                    return;
                }
            }

            final EntityItem entityitem = new EntityItem(itemFrame.worldObj, 0.0D, 0.0D, 0.0D, itemstack);
            final Item item = entityitem.getEntityItem().getItem();
            entityitem.getEntityItem().stackSize = 1;
            entityitem.hoverStart = 0.0F;
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            int i = itemFrame.getRotation();

            if (item instanceof ItemMap) {
                i = i % 4 * 2;
            }

            GlStateManager.rotate((float) i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);

            if (!Reflector.postForgeBusEvent(Reflector.RenderItemInFrameEvent_Constructor, itemFrame, this)) {
                if (item instanceof ItemMap) {
                    this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
                    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                    final float f = 0.0078125F;
                    GlStateManager.scale(f, f, f);
                    GlStateManager.translate(-64.0F, -64.0F, 0.0F);
                    final MapData mapdata = Items.filled_map.getMapData(entityitem.getEntityItem(), itemFrame.worldObj);
                    GlStateManager.translate(0.0F, 0.0F, -1.0F);

                    if (mapdata != null) {
                        this.mc.entityRenderer.getMapItemRenderer().renderMap(mapdata, true);
                    }
                } else {
                    TextureAtlasSprite textureatlassprite = null;

                    if (item == Items.compass) {
                        textureatlassprite = this.mc.getTextureMapBlocks().getAtlasSprite(TextureCompass.field_176608_l);
                        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

                        if (textureatlassprite instanceof TextureCompass) {
                            final TextureCompass texturecompass = (TextureCompass) textureatlassprite;
                            final double d1 = texturecompass.currentAngle;
                            final double d2 = texturecompass.angleDelta;
                            texturecompass.currentAngle = 0.0D;
                            texturecompass.angleDelta = 0.0D;
                            texturecompass.updateCompass(itemFrame.worldObj, itemFrame.posX, itemFrame.posZ, MathHelper.wrapAngleTo180_float((float) (180 + itemFrame.facingDirection.getHorizontalIndex() * 90)), false, true);
                            texturecompass.currentAngle = d1;
                            texturecompass.angleDelta = d2;
                        } else {
                            textureatlassprite = null;
                        }
                    }

                    GlStateManager.scale(0.5F, 0.5F, 0.5F);

                    if (!this.itemRenderer.shouldRenderItemIn3D(entityitem.getEntityItem()) || item instanceof ItemSkull) {
                        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
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

    protected void renderName(final EntityItemFrame entity, final double x, final double y, final double z) {
        if (Minecraft.isGuiEnabled() && entity.getDisplayedItem() != null && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity) {
            final float f = 1.6F;
            final float f1 = 0.016666668F * f;
            final double d0 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
            final float f2 = entity.isSneaking() ? 32.0F : 64.0F;

            if (d0 < (double) (f2 * f2)) {
                final String s = entity.getDisplayedItem().getDisplayName();

                if (entity.isSneaking()) {
                    final FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float) x + 0.0F, (float) y + entity.height + 0.5F, (float) z);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GlStateManager.scale(-f1, -f1, f1);
                    GlStateManager.disableLighting();
                    GlStateManager.translate(0.0F, 0.25F / f1, 0.0F);
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(770, 771);
                    final Tessellator tessellator = Tessellator.getInstance();
                    final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    final int i = fontrenderer.width(s) / 2;
                    GlStateManager.disableTexture2D();
                    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    worldrenderer.pos(-i - 1, -1.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos(-i - 1, 8.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos(i + 1, 8.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    worldrenderer.pos(i + 1, -1.0D, 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.depthMask(true);
                    fontrenderer.drawString(s, -fontrenderer.width(s) / 2, 0, 553648127);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.popMatrix();
                } else {
                    this.renderLivingLabel(entity, s, x, y, z);
                }
            }
        }
    }

    private boolean isRenderItem(final EntityItemFrame p_isRenderItem_1_) {
        if (Shaders.isShadowPass) {
            return false;
        } else {
            if (!Config.zoomMode) {
                final Entity entity = this.mc.getRenderViewEntity();
                final double d0 = p_isRenderItem_1_.getDistanceSq(entity.posX, entity.posY, entity.posZ);

                return !(d0 > itemRenderDistanceSq);
            }

            return true;
        }
    }

    public static void updateItemRenderDistance() {
        final Minecraft minecraft = Config.getMinecraft();
        final double d0 = Config.limit(minecraft.gameSettings.fovSetting, 1.0F, 120.0F);
        final double d1 = Math.max(6.0D * (double) minecraft.displayHeight / d0, 16.0D);
        itemRenderDistanceSq = d1 * d1;
    }
}
