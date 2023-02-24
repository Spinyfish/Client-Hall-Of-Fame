package net.minecraft.client.renderer.entity;

import me.kansio.client.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class RenderEntityItem extends Render<EntityItem>
{
    private final RenderItem itemRenderer;
    public static long tick;
    public static double rotation;
    public static Random random = new Random();
    private Random field_177079_e = new Random();

    public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_)
    {
        super(renderManagerIn);
        this.itemRenderer = p_i46167_2_;
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_)
    {
        ItemStack itemstack = itemIn.getEntityItem();
        Item item = itemstack.getItem();

        if (item == null)
        {
            return 0;
        }
        else
        {
            boolean flag = p_177077_9_.isGui3d();
            int i = this.func_177078_a(itemstack);
            float f = 0.25F;
            float f1 = MathHelper.sin(((float)itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
            float f2 = p_177077_9_.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
            GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f1 + 0.25F * f2, (float)p_177077_6_);

            if (flag || this.renderManager.options != null)
            {
                float f3 = (((float)itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180F / (float)Math.PI);
                GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);
            }

            if (!flag)
            {
                float f6 = -0.0F * (float)(i - 1) * 0.5F;
                float f4 = -0.0F * (float)(i - 1) * 0.5F;
                float f5 = -0.046875F * (float)(i - 1) * 0.5F;
                GlStateManager.translate(f6, f4, f5);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            return i;
        }
    }

    private int func_177078_a(ItemStack stack)
    {
        int i = 1;

        if (stack.stackSize > 48)
        {
            i = 5;
        }
        else if (stack.stackSize > 32)
        {
            i = 4;
        }
        else if (stack.stackSize > 16)
        {
            i = 3;
        }
        else if (stack.stackSize > 1)
        {
            i = 2;
        }

        return i;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if(Client.getInstance().getModuleManager().getModuleByName("Item Physics").isToggled()) {
            Minecraft mc = Minecraft.getMinecraft();
            rotation = (System.nanoTime() - partialTicks) / 50000000000000D;
            if (!mc.inGameHasFocus) {
                rotation = 0;
            }
            EntityItem item = entity;

            ItemStack itemstack = item.getEntityItem();
            int i;

            if (itemstack != null && itemstack.getItem() != null) {
                i = Item.getIdFromItem(itemstack.getItem()) + itemstack.getMetadata();
            } else {
                i = 187;
            }

            random.setSeed(i);

            Minecraft.getMinecraft().getTextureManager().bindTexture(getEntityTexture(entity));
            Minecraft.getMinecraft().getTextureManager().getTexture(getEntityTexture(entity))
                    .setBlurMipmap(false, false);

            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            IBakedModel ibakedmodel = mc.getRenderItem().getItemModelMesher().getItemModel(itemstack);
            boolean flag1 = ibakedmodel.isGui3d();
            boolean is3D = ibakedmodel.isGui3d();
            int j = func_177078_a(itemstack);

            GlStateManager.translate((float) x, (float) y, (float) z);

            if (ibakedmodel.isGui3d()) {
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }

            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(item.rotationYaw, 0.0F, 0.0F, 1.0F);

            if (is3D) {
                GlStateManager.translate(0, 0, -0.08);
            } else {
                GlStateManager.translate(0, 0, -0.04);
            }

            //Handle Rotations
            if (is3D || mc.getRenderManager().options != null) {
                if (is3D) {
                    if (!item.onGround) {
                        double rotation = RenderEntityItem.rotation * 2;
                        item.rotationPitch += rotation;
                    }
                } else {

                    if (!Double.isNaN(item.posX) && !Double.isNaN(item.posY) && !Double
                            .isNaN(item.posZ) && item.worldObj != null) {
                        if (item.onGround) {
                            item.rotationPitch = 0;
                        } else {
                            double rotation = RenderEntityItem.rotation * 2;

                            item.rotationPitch += rotation;
                        }
                    }
                }

                GlStateManager.rotate(item.rotationPitch, 1, 0, 0.0F);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            for (int k = 0; k < j; ++k) {
                if (flag1) {
                    GlStateManager.pushMatrix();

                    if (k > 0) {

                        float f7 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f9 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f6 = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.translate(f7, f9, f6);
                    }

                    mc.getRenderItem().renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();

                    mc.getRenderItem().renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                    GlStateManager.translate(0.0F, 0.0F, 0.05375F);
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            Minecraft.getMinecraft().getTextureManager().bindTexture(getEntityTexture(entity));
            Minecraft.getMinecraft().getTextureManager().getTexture(getEntityTexture(entity)).restoreLastBlurMipmap();
        } else {
            ItemStack itemstack = entity.getEntityItem();
            this.field_177079_e.setSeed(187L);
            boolean flag = false;

            if (this.bindEntityTexture(entity))
            {
                this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
                flag = true;
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            IBakedModel ibakedmodel = this.itemRenderer.getItemModelMesher().getItemModel(itemstack);
            int i = this.func_177077_a(entity, x, y, z, partialTicks, ibakedmodel);

            for (int j = 0; j < i; ++j)
            {
                if (ibakedmodel.isGui3d())
                {
                    GlStateManager.pushMatrix();

                    if (j > 0)
                    {
                        float f = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f1 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f2 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.translate(f, f1, f2);
                    }

                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                    this.itemRenderer.renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                }
                else
                {
                    GlStateManager.pushMatrix();
                    ibakedmodel.getItemCameraTransforms().applyTransform(ItemCameraTransforms.TransformType.GROUND);
                    this.itemRenderer.renderItem(itemstack, ibakedmodel);
                    GlStateManager.popMatrix();
                    float f3 = ibakedmodel.getItemCameraTransforms().ground.scale.x;
                    float f4 = ibakedmodel.getItemCameraTransforms().ground.scale.y;
                    float f5 = ibakedmodel.getItemCameraTransforms().ground.scale.z;
                    GlStateManager.translate(0.0F * f3, 0.0F * f4, 0.046875F * f5);
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            this.bindEntityTexture(entity);

            if (flag)
            {
                this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
            }

            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityItem entity)
    {
        return TextureMap.locationBlocksTexture;
    }
}
