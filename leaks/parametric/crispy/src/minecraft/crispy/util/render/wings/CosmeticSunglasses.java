package crispy.util.render.wings;

import crispy.util.server.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class CosmeticSunglasses extends CosmeticBase {
    private final Sunglasses sunglassesModel;

    public CosmeticSunglasses(RenderPlayer renderPlayer) {
        super(renderPlayer);
        this.sunglassesModel = new Sunglasses(renderPlayer);
    }

    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        GL11.glPushMatrix();
        if (player.isSneaking()) {
            GlStateManager.translate(0, 0.262, -0);
        }
        GlStateManager.rotate(netHeadYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(headPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0, 0.0, 0);
        String uuid = player.getUniqueID().toString();
        GL11.glColor3d(0, 0, 0);
        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("background.jpg"));


        if (CapeManager.coolGuy.contains(player.getCommandSenderName())) {
            GlStateManager.translate(0, 0, -0.02);
            this.sunglassesModel.render((Entity) player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

        }
        GL11.glPopMatrix();
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        this.doRenderLayer((AbstractClientPlayer) entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
    }


    public class Sunglasses extends CosmeticModelBase {

        ModelRenderer Glasses1;
        ModelRenderer Glasses2;
        ModelRenderer Glasses3;
        ModelRenderer Glasses4;
        ModelRenderer Glasses5;
        ModelRenderer Glasses6;
        ModelRenderer Glasses7;

        public Sunglasses(RenderPlayer player) {
            super(player);

            textureWidth = 64;
            textureHeight = 32;

            Glasses1 = new ModelRenderer(this, 0, 0);
            Glasses1.addBox(0F, 0F, 0F, 1, 1, 6);
            Glasses1.setRotationPoint(4F, -4F, -4F);
            Glasses1.setTextureSize(64, 32);
            Glasses1.mirror = true;
            setRotation(Glasses1, 0F, 0F, 0F);
            Glasses2 = new ModelRenderer(this, 0, 0);
            Glasses2.addBox(0F, 0F, 0F, 1, 1, 1);
            Glasses2.setRotationPoint(4F, -3F, 2F);
            Glasses2.setTextureSize(64, 32);
            Glasses2.mirror = true;
            setRotation(Glasses2, 0F, 0F, 0F);
            Glasses3 = new ModelRenderer(this, 0, 0);
            Glasses3.addBox(0F, 0F, 0F, 1, 1, 6);
            Glasses3.setRotationPoint(-5F, -4F, -4F);
            Glasses3.setTextureSize(64, 32);
            Glasses3.mirror = true;
            setRotation(Glasses3, 0F, 0F, 0F);
            Glasses4 = new ModelRenderer(this, 0, 0);
            Glasses4.addBox(0F, 0F, 0F, 1, 1, 1);
            Glasses4.setRotationPoint(-5F, -3F, 2F);
            Glasses4.setTextureSize(64, 32);
            Glasses4.mirror = true;
            setRotation(Glasses4, 0F, 0F, 0F);
            Glasses5 = new ModelRenderer(this, 0, 0);
            Glasses5.addBox(0F, 0F, 0F, 10, 1, 1);
            Glasses5.setRotationPoint(-5F, -4F, -5F);
            Glasses5.setTextureSize(64, 32);
            Glasses5.mirror = true;
            setRotation(Glasses5, 0F, 0F, 0F);
            Glasses6 = new ModelRenderer(this, 0, 0);
            Glasses6.addBox(0F, 0F, 0F, 4, 1, 1);
            Glasses6.setRotationPoint(1F, -3F, -5F);
            Glasses6.setTextureSize(64, 32);
            Glasses6.mirror = true;
            setRotation(Glasses6, 0F, 0F, 0F);
            Glasses7 = new ModelRenderer(this, 0, 0);
            Glasses7.addBox(0F, 0F, 0F, 4, 1, 1);
            Glasses7.setRotationPoint(-5F, -3F, -5F);
            Glasses7.setTextureSize(64, 32);
            Glasses7.mirror = true;
            setRotation(Glasses7, 0F, 0F, 0F);
        }

        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {


            GlStateManager.pushMatrix();
            GlStateManager.color(0, 0, 0);
            this.Glasses1.render(scale);
            this.Glasses2.render(scale);
            this.Glasses3.render(scale);
            this.Glasses4.render(scale);
            this.Glasses5.render(scale);
            this.Glasses6.render(scale);
            this.Glasses7.render(scale);
            GlStateManager.popMatrix();

        }

        private void setRotation(ModelRenderer model, float x, float y, float z) {
            model.rotateAngleX = x;
            model.rotateAngleY = y;
            model.rotateAngleZ = z;
        }
    }
}

