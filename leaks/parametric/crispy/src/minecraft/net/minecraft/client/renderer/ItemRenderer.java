package net.minecraft.client.renderer;

import crispy.Crispy;
import crispy.features.hacks.impl.combat.Aura;
import crispy.features.hacks.impl.render.BlockHit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {
    private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");

    /**
     * A reference to the Minecraft object.
     */
    private final Minecraft mc;
    private ItemStack itemToRender;

    /**
     * How far the current item has been equipped (0 disequipped and 1 fully up)
     */
    private float equippedProgress;
    private float prevEquippedProgress;
    private final RenderManager renderManager;
    private final RenderItem itemRenderer;

    /**
     * The index of the currently held item (0-8, or -1 if not yet updated)
     */
    private int equippedItemSlot = -1;
    private static final String __OBFID = "CL_00000953";

    public ItemRenderer(Minecraft mcIn) {
        this.mc = mcIn;
        this.renderManager = mcIn.getRenderManager();
        this.itemRenderer = mcIn.getRenderItem();
    }

    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
        if (heldStack != null) {
            Item var4 = heldStack.getItem();
            Block var5 = Block.getBlockFromItem(var4);
            GlStateManager.pushMatrix();
            BlockHit blockHit = Crispy.INSTANCE.getHackManager().getHack(BlockHit.class);
            GlStateManager.scale(blockHit.scale.getObject(), blockHit.scale.getObject(), blockHit.scale.getObject());
            if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);

                if (this.isBlockTranslucent(var5)) {
                    GlStateManager.depthMask(false);
                }
            }

            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);

            if (this.isBlockTranslucent(var5)) {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }
    }
    public void renderItemOver(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform) {
        if (heldStack != null) {
            Item var4 = heldStack.getItem();
            Block var5 = Block.getBlockFromItem(var4);
            GlStateManager.pushMatrix();
            if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);

                if (this.isBlockTranslucent(var5)) {
                    GlStateManager.depthMask(false);
                }
            }

            this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);

            if (this.isBlockTranslucent(var5)) {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }
    }

    /**
     * Returns true if given block is translucent
     */
    private boolean isBlockTranslucent(Block blockIn) {
        return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    private void func_178101_a(float angle, float p_178101_2_) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(p_178101_2_, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void func_178109_a(AbstractClientPlayer clientPlayer) {
        int var2 = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double) clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
        float var3 = (float) (var2 & 65535);
        float var4 = (float) (var2 >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var3, var4);
    }

    private void func_178110_a(EntityPlayerSP entityplayerspIn, float partialTicks) {
        float var3 = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float var4 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - var3) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - var4) * 0.1F, 0.0F, 1.0F, 0.0F);
    }

    private float func_178100_c(float p_178100_1_) {
        float var2 = 1.0F - p_178100_1_ / 45.0F + 0.1F;
        var2 = MathHelper.clamp_float(var2, 0.0F, 1.0F);
        var2 = -MathHelper.cos(var2 * (float) Math.PI) * 0.5F + 0.5F;
        return var2;
    }

    private void renderRightArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.25F, -0.85F, 0.75F);
        renderPlayerIn.renderRightArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderLeftArm(RenderPlayer renderPlayerIn) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(-0.3F, -1.1F, 0.45F);
        renderPlayerIn.renderLeftArm(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        Render var2 = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer var3 = (RenderPlayer) var2;

        if (!clientPlayer.isInvisible()) {
            this.renderRightArm(var3);
            this.renderLeftArm(var3);
        }
    }

    private void renderItemMap(AbstractClientPlayer clientPlayer, float p_178097_2_, float p_178097_3_, float p_178097_4_) {
        float var5 = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI);
        float var6 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI * 2.0F);
        float var7 = -0.2F * MathHelper.sin(p_178097_4_ * (float) Math.PI);
        GlStateManager.translate(var5, var6, var7);
        float var8 = this.func_178100_c(p_178097_2_);
        GlStateManager.translate(0.0F, 0.04F, -0.72F);
        GlStateManager.translate(0.0F, p_178097_3_ * -1.2F, 0.0F);
        GlStateManager.translate(0.0F, var8 * -0.5F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var8 * -85.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        this.renderPlayerArms(clientPlayer);
        float var9 = MathHelper.sin(p_178097_4_ * p_178097_4_ * (float) Math.PI);
        float var10 = MathHelper.sin(MathHelper.sqrt_float(p_178097_4_) * (float) Math.PI);
        GlStateManager.rotate(var9 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var10 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var10 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.38F, 0.38F, 0.38F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-1.0F, -1.0F, 0.0F);
        GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
        this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
        Tessellator var11 = Tessellator.getInstance();
        WorldRenderer var12 = var11.getWorldRenderer();
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        var12.startDrawingQuads();
        var12.addVertexWithUV(-7.0D, 135.0D, 0.0D, 0.0D, 1.0D);
        var12.addVertexWithUV(135.0D, 135.0D, 0.0D, 1.0D, 1.0D);
        var12.addVertexWithUV(135.0D, -7.0D, 0.0D, 1.0D, 0.0D);
        var12.addVertexWithUV(-7.0D, -7.0D, 0.0D, 0.0D, 0.0D);
        var11.draw();
        MapData var13 = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);

        if (var13 != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(var13, false);
        }
    }

    private void func_178095_a(AbstractClientPlayer clientPlayer, float p_178095_2_, float p_178095_3_) {
        float var4 = -0.3F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI);
        float var5 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI * 2.0F);
        float var6 = -0.4F * MathHelper.sin(p_178095_3_ * (float) Math.PI);
        GlStateManager.translate(var4, var5, var6);
        GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
        GlStateManager.translate(0.0F, p_178095_2_ * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var7 = MathHelper.sin(p_178095_3_ * p_178095_3_ * (float) Math.PI);
        float var8 = MathHelper.sin(MathHelper.sqrt_float(p_178095_3_) * (float) Math.PI);
        GlStateManager.rotate(var8 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var7 * -20.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        GlStateManager.translate(5.6F, 0.0F, 0.0F);
        Render var9 = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
        RenderPlayer var10 = (RenderPlayer) var9;
        var10.renderRightArm(this.mc.thePlayer);
    }

    private void func_178105_d(float p_178105_1_) {
        float var2 = -0.4F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI);
        float var3 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(p_178105_1_) * (float) Math.PI * 2.0F);
        float var4 = -0.2F * MathHelper.sin(p_178105_1_ * (float) Math.PI);
        GlStateManager.translate(var2, var3, var4);
    }

    private void func_178104_a(AbstractClientPlayer clientPlayer, float p_178104_2_) {
        float var3 = (float) clientPlayer.getItemInUseCount() - p_178104_2_ + 1.0F;
        float var4 = var3 / (float) this.itemToRender.getMaxItemUseDuration();
        float var5 = MathHelper.abs(MathHelper.cos(var3 / 4.0F * (float) Math.PI) * 0.1F);

        if (var4 >= 0.8F) {
            var5 = 0.0F;
        }

        GlStateManager.translate(0.0F, var5, 0.0F);
        float var6 = 1.0F - (float) Math.pow((double) var4, 27.0D);
        GlStateManager.translate(var6 * 0.6F, var6 * -0.5F, var6 * 0.0F);
        GlStateManager.rotate(var6 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var6 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(var6 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    /**
     * Performs transformations prior to the rendering of a held item in first person.
     *
     * @param equipProgress The progress of the animation to equip (raise from out of frame) while switching held items.
     * @param swingProgress The progress of the arm swing animation.
     */
    private void transformFirstPersonItem(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.42F, -0.71999997F);

        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);

        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    //Duplicate to avoid issues with equipting
    private void blocky(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.42F, -0.71999997F);
        if (BlockHit.booleanValue.getObject()) {
            GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        }
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(var3 * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }


    private void func_178098_a(float p_178098_1_, AbstractClientPlayer clientPlayer) {
        GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.9F, 0.2F, 0.0F);
        float var3 = (float) this.itemToRender.getMaxItemUseDuration() - ((float) clientPlayer.getItemInUseCount() - p_178098_1_ + 1.0F);
        float var4 = var3 / 20.0F;
        var4 = (var4 * var4 + var4 * 2.0F) / 3.0F;

        if (var4 > 1.0F) {
            var4 = 1.0F;
        }

        if (var4 > 0.1F) {
            float var5 = MathHelper.sin((var3 - 0.1F) * 1.3F);
            float var6 = var4 - 0.1F;
            float var7 = var5 * var6;
            GlStateManager.translate(var7 * 0.0F, var7 * 0.01F, var7 * 0.0F);
        }

        GlStateManager.translate(var4 * 0.0F, var4 * 0.0F, var4 * 0.1F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F + var4 * 0.2F);
    }

    private void func_178103_d() {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
    }


    private void avatar(float equipProgress, float swingProgress) {

        GlStateManager.translate(0.56F, -0.40, -0.71999997F);
        if (BlockHit.booleanValue.getObject()) {
            GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        }
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 4.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -30.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }
    private void half(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.62F, -0.28F, -0.71999997F);

        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);

        GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
        float var3 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float var4 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(var3 * 0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(var4 * 0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(var4 * -40.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }


    private void boop(float equipProgress, float swingProgress) {

        GlStateManager.translate(0.56F, -0.40, -0.71999997F);
        if (BlockHit.booleanValue.getObject()) {
            GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        }
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f * 20.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f1 * 20.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f1 * 20.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    /**
     * Renders the active item in the player's hand when in first person mode. Args: partialTickTime
     *
     * @param partialTicks The amount of time passed during the current tick, ranging from 0 to 1.
     */
    public void renderItemInFirstPerson(float partialTicks) {
        float var2 = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        EntityPlayerSP var3 = this.mc.thePlayer;
        float var4 = var3.getSwingProgress(partialTicks);
        float var5 = var3.prevRotationPitch + (var3.rotationPitch - var3.prevRotationPitch) * partialTicks;
        float var6 = var3.prevRotationYaw + (var3.rotationYaw - var3.prevRotationYaw) * partialTicks;
        this.func_178101_a(var5, var6);
        this.func_178109_a(var3);
        this.func_178110_a((EntityPlayerSP) var3, partialTicks);
        GlStateManager.enableRescaleNormal();
        BlockHit block = Crispy.INSTANCE.getHackManager().getHack(BlockHit.class);

        GlStateManager.pushMatrix();
        float angle = (int) ((System.currentTimeMillis() / 1.5) % 360);
        angle = (angle > 180 ? 360 - angle : angle) * 2;
        angle /= 180f;

        float angle2 = (int) ((System.currentTimeMillis() / 3.5) % 120);
        angle2 = (angle2 > 30 ? 120 - angle2 : angle2) * 2;
        angle2 /= 1f;


        float angle3 = (int) ((System.currentTimeMillis() / 3.5) % 110);
        angle3 = (angle3 > 30 ? 110 - angle3 : angle3) * 2;
        angle3 /= 1f;
        int random1 = 0;
        int random2 = 0;
        int random3 = 0;
        int random4 = 0;
        float random5 = 0;
        int random6 = 0;
        int random7 = 0;
        float random8 = 0;
        int random10 = 0;
        if (this.itemToRender != null) {
            if (this.itemToRender.getItem() == Items.filled_map) {
                this.renderItemMap(var3, var5, var2, var4);
            } else if (var3.getItemInUseCount() > 0) {
                EnumAction var7 = this.itemToRender.getItemUseAction();


                switch (ItemRenderer.SwitchEnumAction.ENUMACTION_INT_REMAP[var7.ordinal()]) {
                    case 1:
                        this.transformFirstPersonItem(var2, 0.0F);
                        break;

                    case 2:
                    case 3:

                        this.func_178104_a(var3, partialTicks);
                        this.transformFirstPersonItem(var2, 0.0F);
                        break;

                    case 4:


                        if (!block.isEnabled()) {
                            this.transformFirstPersonItem(var2, 0.0F);
                            this.func_178103_d();
                        } else {

                            switch (block.modes.getMode()) {
                                case "Slide": {

                                    avatar(var2, var4);
                                    this.func_178103_d();

                                    break;
                                }
                                case "SpinnyBoi": {

                                    random4 = (int) 0.2f;
                                    random1 = (int) (System.currentTimeMillis() / 2 % 360);
                                    random2 = 1;
                                    random5 = 1;
                                    random4 = -59;
                                    random6 = -1;
                                    random7 = 0;
                                    random10 = 3;
                                    GlStateManager.translate(random8, 0.2f, -random5);
                                    GlStateManager.rotate(random4, random6, random7, random10);
                                    GlStateManager.rotate(-random1, random2, random3, 0.0F);
                                    GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);

                                    break;
                                }
                                case "1.7": {

                                    this.transformFirstPersonItem(var2, var4);
                                    this.func_178103_d();

                                    break;
                                }
                                case "Boop": {

                                    boop(var2, var4);
                                    this.func_178103_d();

                                    break;
                                }
                                case "Slide2": {
                                    half(var2, var4);
                                    this.func_178103_d();
                                    break;
                                }
                            }
                        }
                        break;

                    case 5:

                        this.transformFirstPersonItem(var2, 0.0F);
                        this.func_178098_a(partialTicks, var3);


                }
            } else {
                if (Aura.target != null && Aura.autoBlock.getObject() && Aura.BlockMode.getMode().equalsIgnoreCase("Fake")) {

                    switch (block.modes.getMode()) {
                        case "Slide": {

                            avatar(var2, var4);
                            this.func_178103_d();

                            break;
                        }
                        case "SpinnyBoi": {

                            random4 = (int) 0.2f;
                            random1 = (int) (System.currentTimeMillis() / 2 % 360);
                            random2 = 1;
                            random5 = 1;
                            random4 = -59;
                            random6 = -1;
                            random7 = 0;
                            random10 = 3;
                            GlStateManager.translate(random8, 0.2f, -random5);
                            GlStateManager.rotate(random4, random6, random7, random10);
                            GlStateManager.rotate(-random1, random2, random3, 0.0F);
                            GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);

                            break;
                        }
                        case "1.7": {

                            this.transformFirstPersonItem(var2, var4);
                            this.func_178103_d();

                            break;
                        }
                        case "Boop": {

                            boop(var2, var4);
                            this.func_178103_d();

                            break;
                        }
                        case "Slide2": {
                            half(var2, var4);
                            this.func_178103_d();
                            break;
                        }
                    }

                } else {
                    this.func_178105_d(var4);
                    this.transformFirstPersonItem(var2, var4);
                }

            }

            this.renderItem(var3, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!var3.isInvisible()) {
            this.func_178095_a(var3, var2, var4);
        }

        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * Renders all the overlays that are in first person mode. Args: partialTickTime
     */
    public void renderOverlays(float partialTicks) {
        GlStateManager.disableAlpha();

        if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState var2 = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            EntityPlayerSP var3 = this.mc.thePlayer;

            for (int var4 = 0; var4 < 8; ++var4) {
                double var5 = var3.posX + (double) (((float) ((var4 >> 0) % 2) - 0.5F) * var3.width * 0.8F);
                double var7 = var3.posY + (double) (((float) ((var4 >> 1) % 2) - 0.5F) * 0.1F);
                double var9 = var3.posZ + (double) (((float) ((var4 >> 2) % 2) - 0.5F) * var3.width * 0.8F);
                BlockPos var11 = new BlockPos(var5, var7 + (double) var3.getEyeHeight(), var9);
                IBlockState var12 = this.mc.theWorld.getBlockState(var11);

                if (var12.getBlock().isVisuallyOpaque()) {
                    var2 = var12;
                }
            }

            if (var2.getBlock().getRenderType() != -1) {
                this.func_178108_a(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(var2));
            }
        }

        if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.water)) {
                this.renderWaterOverlayTexture(partialTicks);
            }

            if (this.mc.thePlayer.isBurning()) {
                this.renderFireInFirstPerson(partialTicks);
            }
        }

        GlStateManager.enableAlpha();
    }

    private void func_178108_a(float p_178108_1_, TextureAtlasSprite p_178108_2_) {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator var3 = Tessellator.getInstance();
        WorldRenderer var4 = var3.getWorldRenderer();
        float var5 = 0.1F;
        GlStateManager.color(var5, var5, var5, 0.5F);
        GlStateManager.pushMatrix();
        float var6 = -1.0F;
        float var7 = 1.0F;
        float var8 = -1.0F;
        float var9 = 1.0F;
        float var10 = -0.5F;
        float var11 = p_178108_2_.getMinU();
        float var12 = p_178108_2_.getMaxU();
        float var13 = p_178108_2_.getMinV();
        float var14 = p_178108_2_.getMaxV();
        var4.startDrawingQuads();
        var4.addVertexWithUV((double) var6, (double) var8, (double) var10, (double) var12, (double) var14);
        var4.addVertexWithUV((double) var7, (double) var8, (double) var10, (double) var11, (double) var14);
        var4.addVertexWithUV((double) var7, (double) var9, (double) var10, (double) var11, (double) var13);
        var4.addVertexWithUV((double) var6, (double) var9, (double) var10, (double) var12, (double) var13);
        var3.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders a texture that warps around based on the direction the player is looking. Texture needs to be bound
     * before being called. Used for the water overlay. Args: parialTickTime
     */
    private void renderWaterOverlayTexture(float p_78448_1_) {
        this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
        Tessellator var2 = Tessellator.getInstance();
        WorldRenderer var3 = var2.getWorldRenderer();
        float var4 = this.mc.thePlayer.getBrightness(p_78448_1_);
        GlStateManager.color(var4, var4, var4, 0.5F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        float var5 = 4.0F;
        float var6 = -1.0F;
        float var7 = 1.0F;
        float var8 = -1.0F;
        float var9 = 1.0F;
        float var10 = -0.5F;
        float var11 = -this.mc.thePlayer.rotationYaw / 64.0F;
        float var12 = this.mc.thePlayer.rotationPitch / 64.0F;
        var3.startDrawingQuads();
        var3.addVertexWithUV((double) var6, (double) var8, (double) var10, (double) (var5 + var11), (double) (var5 + var12));
        var3.addVertexWithUV((double) var7, (double) var8, (double) var10, (double) (0.0F + var11), (double) (var5 + var12));
        var3.addVertexWithUV((double) var7, (double) var9, (double) var10, (double) (0.0F + var11), (double) (0.0F + var12));
        var3.addVertexWithUV((double) var6, (double) var9, (double) var10, (double) (var5 + var11), (double) (0.0F + var12));
        var2.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    /**
     * Renders the fire on the screen for first person mode. Arg: partialTickTime
     */
    private void renderFireInFirstPerson(float p_78442_1_) {
        Tessellator var2 = Tessellator.getInstance();
        WorldRenderer var3 = var2.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float var4 = 1.0F;

        for (int var5 = 0; var5 < 2; ++var5) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite var6 = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float var7 = var6.getMinU();
            float var8 = var6.getMaxU();
            float var9 = var6.getMinV();
            float var10 = var6.getMaxV();
            float var11 = (0.0F - var4) / 2.0F;
            float var12 = var11 + var4;
            float var13 = 0.0F - var4 / 2.0F;
            float var14 = var13 + var4;
            float var15 = -0.5F;
            GlStateManager.translate((float) (-(var5 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((float) (var5 * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            var3.startDrawingQuads();
            var3.addVertexWithUV((double) var11, (double) var13, (double) var15, (double) var8, (double) var10);
            var3.addVertexWithUV((double) var12, (double) var13, (double) var15, (double) var7, (double) var10);
            var3.addVertexWithUV((double) var12, (double) var14, (double) var15, (double) var7, (double) var9);
            var3.addVertexWithUV((double) var11, (double) var14, (double) var15, (double) var8, (double) var9);
            var2.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayerSP var1 = this.mc.thePlayer;
        ItemStack var2 = var1.inventory.getCurrentItem();
        boolean var3 = false;

        if (this.itemToRender != null && var2 != null) {
            if (!this.itemToRender.getIsItemStackEqual(var2)) {
                var3 = true;
            }
        } else if (this.itemToRender == null && var2 == null) {
            var3 = false;
        } else {
            var3 = true;
        }

        float var4 = 0.4F;
        float var5 = var3 ? 0.0F : 1.0F;
        float var6 = MathHelper.clamp_float(var5 - this.equippedProgress, -var4, var4);
        this.equippedProgress += var6;

        if (this.equippedProgress < 0.1F) {
            this.itemToRender = var2;
            this.equippedItemSlot = var1.inventory.currentItem;
        }
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress() {
        this.equippedProgress = 0.0F;
    }

    /**
     * Resets equippedProgress
     */
    public void resetEquippedProgress2() {
        this.equippedProgress = 0.0F;
    }

    static final class SwitchEnumAction {
        static final int[] ENUMACTION_INT_REMAP = new int[EnumAction.values().length];
        private static final String __OBFID = "CL_00002537";

        static {
            try {
                ENUMACTION_INT_REMAP[EnumAction.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError var5) {
                ;
            }

            try {
                ENUMACTION_INT_REMAP[EnumAction.EAT.ordinal()] = 2;
            } catch (NoSuchFieldError var4) {
                ;
            }

            try {
                ENUMACTION_INT_REMAP[EnumAction.DRINK.ordinal()] = 3;
            } catch (NoSuchFieldError var3) {
                ;
            }

            try {
                ENUMACTION_INT_REMAP[EnumAction.BLOCK.ordinal()] = 4;
            } catch (NoSuchFieldError var2) {
                ;
            }

            try {
                ENUMACTION_INT_REMAP[EnumAction.BOW.ordinal()] = 5;
            } catch (NoSuchFieldError var1) {
                ;
            }
        }
    }
}
