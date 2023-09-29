package de.lirium.impl.events;

import best.azura.eventbus.events.CancellableEvent;
import de.lirium.util.interfaces.IMinecraft;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import optifine.Config;
import shadersmod.client.Shaders;

@Getter
@Setter
@AllArgsConstructor
public class RenderItemEvent extends CancellableEvent implements IMinecraft {
    private AbstractClientPlayer player;
    private EnumHandSide enumHandSide;
    private RenderItem itemRenderer;
    private float swingProgress;
    private float equipProgress;
    private boolean rightHand;
    private ItemStack stack;
    private EnumHand hand;

    public void block() {
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-30.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(10.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 0.0F, 1.0F);
    }

    public void transformFirstPersonItem(float equipProgress, float swingProgress) {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        final float f = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
        final float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    public void transformSideFirstPerson(EnumHandSide p_187459_1_, float p_187459_2_) {
        final int i = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
        GlStateManager.translate((float) i * 0.56F, -0.52F + p_187459_2_ * -0.6F, -0.72F);
    }

    public void transformFirstPerson(EnumHandSide p_187453_1_, float p_187453_2_) {
        final int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
        final float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * (float) Math.PI);
        GlStateManager.rotate((float) i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        final float f1 = MathHelper.sin(MathHelper.sqrt(p_187453_2_) * (float) Math.PI);
        GlStateManager.rotate((float) i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((float) i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        if (!heldStack.func_190926_b()) {
            final Item item = heldStack.getItem();
            final Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            final boolean flag = this.itemRenderer.shouldRenderItemIn3D(heldStack) && block.getBlockLayer() == BlockRenderLayer.TRANSLUCENT;

            if (flag && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask))
                GlStateManager.depthMask(false);

            this.itemRenderer.renderItem(heldStack, entitylivingbaseIn, transform, leftHanded);

            if (flag)
                GlStateManager.depthMask(true);

            GlStateManager.popMatrix();
        }
    }

    public void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
        final boolean flag = p_187456_3_ != EnumHandSide.LEFT;
        final float f = flag ? 1.0F : -1.0F;
        final float f1 = MathHelper.sqrt(p_187456_2_);
        final float f2 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
        final float f3 = 0.4F * MathHelper.sin(f1 * ((float) Math.PI * 2F));
        final float f4 = -0.4F * MathHelper.sin(p_187456_2_ * (float) Math.PI);
        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + p_187456_1_ * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
        final float f5 = MathHelper.sin(p_187456_2_ * p_187456_2_ * (float) Math.PI);
        final float f6 = MathHelper.sin(f1 * (float) Math.PI);
        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        final AbstractClientPlayer abstractclientplayer = this.mc.player;
        this.mc.getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
        final RenderPlayer renderplayer = (RenderPlayer) mc.getRenderManager().<AbstractClientPlayer>getEntityRenderObject(abstractclientplayer);
        GlStateManager.disableCull();

        if (flag)
            renderplayer.renderRightArm(abstractclientplayer);
        else
            renderplayer.renderLeftArm(abstractclientplayer);

        GlStateManager.enableCull();
    }
}