package crispy.util.render.wings;

import crispy.util.server.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class CosmeticWitchHat extends CosmeticBase {
    private final HatModel hatModel;

    private static final ResourceLocation TEXTURE = new ResourceLocation("HexenHut.png");

    public CosmeticWitchHat(RenderPlayer renderPlayer) {
        super(renderPlayer);
        this.hatModel = new HatModel(renderPlayer);
    }

    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        GL11.glPushMatrix();

        if (player.isSneaking()) {
            GlStateManager.translate(0, 0.262, 0);
        }
        GlStateManager.rotate(netHeadYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(headPitch, 1.0F, 0.0F, 0.0F);
        if (Minecraft.getMinecraft().thePlayer.getEquipmentInSlot(4) == null) {
            GlStateManager.translate(-0.3125, -0.5, -0.3125);
        } else {
            GlStateManager.translate(-0.3125, -0.69, -0.3125);
        }


        this.renderPlayer.bindTexture(TEXTURE);
        if (CapeManager.coolGuy.contains(player.getCommandSenderName())) {
            this.hatModel.render((Entity) player, limbSwing, limbSwingAmount, ageInTicks, headPitch, headPitch, scale);
            GL11.glColor3d(1, 1, 1);
        }
        String uuid = player.getUniqueID().toString();


        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        this.doRenderLayer((AbstractClientPlayer) entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
    }
}

class HatModel extends CosmeticModelBase {

    private ModelRenderer witchHat;

    public HatModel(RenderPlayer player) {
        super(player);

        this.witchHat = (new ModelRenderer((ModelBase) this)).setTextureSize(64, 128);
        this.witchHat.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
        ModelRenderer modelrenderer = (new ModelRenderer((ModelBase) this)).setTextureSize(64, 128);
        modelrenderer.setRotationPoint(1.75F, -4.0F, 2.0F);
        modelrenderer.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
        modelrenderer.rotateAngleX = -0.05235988F;
        modelrenderer.rotateAngleZ = 0.02617994F;
        this.witchHat.addChild(modelrenderer);
        ModelRenderer modelrenderer1 = (new ModelRenderer((ModelBase) this)).setTextureSize(64, 128);
        modelrenderer1.setRotationPoint(1.75F, -4.0F, 2.0F);
        modelrenderer1.setTextureOffset(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
        modelrenderer1.rotateAngleX = -0.10471976F;
        modelrenderer1.rotateAngleZ = 0.05235988F;
        modelrenderer.addChild(modelrenderer1);
        ModelRenderer modelrenderer2 = (new ModelRenderer((ModelBase) this)).setTextureSize(64, 128);
        modelrenderer2.setRotationPoint(1.75F, -2.0F, 2.0F);
        modelrenderer2.setTextureOffset(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
        modelrenderer2.rotateAngleX = -0.20943952F;
        modelrenderer2.rotateAngleZ = 0.10471976F;
        modelrenderer1.addChild(modelrenderer2);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {

        final ResourceLocation TEXTURE = new ResourceLocation("HexenHut.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        this.witchHat.render(scale);



    }


}

