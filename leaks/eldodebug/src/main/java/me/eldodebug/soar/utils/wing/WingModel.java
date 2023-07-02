package me.eldodebug.soar.utils.wing;

import org.lwjgl.opengl.GL11;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.WingsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class WingModel extends ModelBase implements LayerRenderer<AbstractClientPlayer>{
	
    private Minecraft mc;
    private ResourceLocation location;
    private ModelRenderer wing;
    private ModelRenderer wingTip;
    
    public WingModel() {
        this.mc = Minecraft.getMinecraft();
        this.location = new ResourceLocation("soar/wings.png");
        this.setTextureOffset("wing.bone", 0, 0);
        this.setTextureOffset("wing.skin", -10, 8);
        this.setTextureOffset("wingtip.bone", 0, 5);
        this.setTextureOffset("wingtip.skin", -10, 18);
        (this.wing = new ModelRenderer(this, "wing")).setTextureSize(30, 30);
        this.wing.setRotationPoint(-2.0f, 0.0f, 0.0f);
        this.wing.addBox("bone", -10.0f, -1.0f, -1.0f, 10, 2, 2);
        this.wing.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10);
        (this.wingTip = new ModelRenderer(this, "wingtip")).setTextureSize(30, 30);
        this.wingTip.setRotationPoint(-10.0f, 0.0f, 0.0f);
        this.wingTip.addBox("bone", -10.0f, -0.5f, -0.5f, 10, 1, 1);
        this.wingTip.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10);
        this.wing.addChild(this.wingTip);
    }
    
    public void renderWing(AbstractClientPlayer player) {
        if (Soar.instance.modManager.getModByClass(WingsMod.class).isToggled()) {
            if (!player.isInvisible()) {
                GL11.glPushMatrix();
                GL11.glScaled(-1.0, -1.0, 1.0);
                GL11.glTranslated(0.0, -1.45, 0.0);
                GL11.glTranslated(0.0, 1.3, 0.2);
                if (player.isSneaking()) {
                    GlStateManager.translate(0.0f, -0.142f, -0.0178f);
                }
                GL11.glRotated(180.0, 1.0, 0.0, 0.0);
                GL11.glRotated(180.0, 0.0, 1.0, 0.0);
                this.mc.getTextureManager().bindTexture(this.location);
                for (int j = 0; j < 2; ++j) {
                    GL11.glEnable(2884);
                    final float f11 = System.currentTimeMillis() % 1000L / 1000.0f * 3.1415927f * 2.0f;
                    this.wing.rotateAngleX = (float)Math.toRadians(-80.0) - (float)Math.cos(f11) * 0.2f;
                    this.wing.rotateAngleY = (float)Math.toRadians(20.0) + (float)Math.sin(f11) * 0.4f;
                    this.wing.rotateAngleZ = (float)Math.toRadians(20.0);
                    this.wingTip.rotateAngleZ = -(float)(Math.sin(f11 + 2.0f) + 0.5) * 0.75f;
                    this.wing.render(0.0625f);
                    GL11.glScalef(-1.0f, 1.0f, 1.0f);
                    if (j == 0) {
                        GL11.glCullFace(1028);
                    }
                }
                GL11.glCullFace(1029);
                GL11.glDisable(2884);
                GL11.glPopMatrix();
            }
        }
    }
    
    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

	@Override
	public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_,
			float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
		// TODO Auto-generated method stub
		
	}
}
