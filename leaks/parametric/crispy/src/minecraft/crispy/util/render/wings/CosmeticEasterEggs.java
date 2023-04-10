package crispy.util.render.wings;

import crispy.util.server.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import org.lwjgl.opengl.GL11;

public class CosmeticEasterEggs extends CosmeticBase {
	private final CosmeticVilligerNose2 EggsModel;

    public CosmeticEasterEggs(RenderPlayer renderPlayer) {
        super(renderPlayer);
        this.EggsModel = new CosmeticVilligerNose2(renderPlayer);
    }

    public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        GL11.glPushMatrix();
        if(player.isSneaking()) {
            GlStateManager.translate(0, 0.262, -0);
        }
        GlStateManager.rotate(netHeadYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(headPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(ageInTicks * 17, 0, 1, 0);
        GlStateManager.rotate(180, 1, 0, 0);
        String uuid = player.getUniqueID().toString();



        if(CapeManager.coolGuy.contains(player.getCommandSenderName())) {
            this.EggsModel.render((Entity)player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        GL11.glPopMatrix();
    }

	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		this.doRenderLayer((AbstractClientPlayer) entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
	}



	public class CosmeticVilligerNose2 extends CosmeticModelBase {

        public CosmeticVilligerNose2(RenderPlayer player) {
            super(player);


        }

        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch, float scale) {


            GlStateManager.pushMatrix();
            GlStateManager.scale(0.25, 0.25, 0.25);
            GlStateManager.translate(2, +1.5, 0);
            ItemStack itemsword = new ItemStack(Items.ender_pearl);
            Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase)entityIn,itemsword, ItemCameraTransforms.TransformType.NONE);
            GlStateManager.translate(-4, 0, 0);
            Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase)entityIn,itemsword, ItemCameraTransforms.TransformType.NONE);
            GlStateManager.translate(2, 0, 2);
            Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase)entityIn,itemsword, ItemCameraTransforms.TransformType.NONE);
            GlStateManager.translate(0, 0, -4);
            Minecraft.getMinecraft().getItemRenderer().renderItem((EntityLivingBase)entityIn,itemsword, ItemCameraTransforms.TransformType.NONE);

            GlStateManager.popMatrix();

        }


    }
}
