package byron.Mono.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import byron.Mono.event.impl.Event3D;
import byron.Mono.font.FontUtil;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import com.google.common.eventbus.Subscribe;

@ModuleInterface(name = "Nametags", description = "See everything clearly!", category = Category.Visual)
public class Nametags extends Module{
	
	@Override
    public void onEnable() {
    	
        super.onEnable();
    }
	
	@Subscribe
    public void onUpdate(Event3D event) {
        int amount = 0;

        for (final EntityPlayer entity : mc.theWorld.playerEntities) {
            if (entity != null) {
                final String name = entity.getName();

                if ((!entity.isInvisible() || !entity.isDead && entity != mc.thePlayer)) {
                    //Changing size
                    final float scale = Math.max(0.02F, mc.thePlayer.getDistanceToEntity(entity) / 300);

                    final double x = (entity).lastTickPosX + ((entity).posX - (entity).lastTickPosX) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosX;
                    final double y = ((entity).lastTickPosY + ((entity).posY - (entity).lastTickPosY) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosY) + scale * 6;
                    final double z = (entity).lastTickPosZ + ((entity).posZ - (entity).lastTickPosZ) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosZ;

                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y + 2.3, z);
                    GlStateManager.disableDepth();

                    GL11.glScalef(-scale, -scale, -scale);

                    GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                    GL11.glRotated(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0D : 1.0D, 0.0D, 0.0D);

                    final float width = (float) FontUtil.normal2.getStringWidth(name);
                    final float progress = Math.min((entity).getHealth(), (entity).getMaxHealth()) / (entity).getMaxHealth();

                    final Color color = new Color(255,255,255);

                    drawSmoothRect((-width / 2.0F - 5.0F), -1, (width / 2.0F + 5.0F), 8, 0x40000000);
                    drawSmoothRect((-width / 2.0F - 5.0F), 7, (-width / 2.0F - 5.0F + (width / 2.0F + 5.0F - -width / 2.0F + 5.0F) * progress), 8, new Color(250, 250, 250).getRGB());

                    
                    
                    GL11.glScalef(0.5f, 0.5f, 0.5f);

                    FontUtil.normal2.drawCenteredStringWithShadow(name, - width / 12.0F, 0.2f, -1);

                    GL11.glScalef(1.9f, 1.9f, 1.9f);

                    
                    GlStateManager.enableDepth();
                    GL11.glPopMatrix();
                    amount++;
                }
            }
        }
    }
	
    private int getHealthColorHEX(EntityPlayer e) {
        int health = Math.round(20.0F * (e.getHealth() / e.getMaxHealth()));
        int color = -1;
        if(health >= 20) {
           color = 5030935;
        } else if(health >= 18) {
           color = 9108247;
        } else if(health >= 16) {
           color = 10026904;
        } else if(health >= 14) {
           color = 12844472;
        } else if(health >= 12) {
           color = 16633879;
        } else if(health >= 10) {
           color = 15313687;
        } else if(health >= 8) {
           color = 16285719;
        } else if(health >= 6) {
           color = 16286040;
        } else if(health >= 4) {
           color = 15031100;
        } else if(health >= 2) {
           color = 16711680;
        } else if(health >= 0) {
           color = 16190746;
        }

        return color;
     }
    
    public static final void drawSmoothRect(float left, float top, float right, float bottom, int color) {
		GL11.glEnable(3042);
		GL11.glEnable(2848);
		Gui.drawRect(left, top, right, bottom, color);
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		Gui.drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
		Gui.drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
		Gui.drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
		Gui.drawRect(left * 2.0f, bottom * 2.0f - 1.0f, right * 2.0f, bottom * 2.0f, color);
		GL11.glDisable(3042);
		GL11.glScalef(2.0f, 2.0f, 2.0f);
	}
    
	@Override
    public void onDisable() {
        super.onDisable();
    }

}
