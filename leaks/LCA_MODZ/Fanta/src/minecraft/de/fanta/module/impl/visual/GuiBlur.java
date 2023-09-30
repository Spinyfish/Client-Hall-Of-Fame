package de.fanta.module.impl.visual;




import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.ColorValue;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiBlur extends Module {
    public GuiBlur() {
        super("GuiBlur",0, Type.Visual, Color.YELLOW);
        this.settings.add(new Setting("Color", new ColorValue(Color.RED.getRGB())));
    }
    public static GuiBlur INSTANCE;
    @Override
    public void onEvent(Event event) {

        
    }
    
    public int getColor2() {
		try {
			return ((ColorValue) getSetting("Color").getSetting()).color;
		} catch (Exception e) {
			return Color.white.getRGB();
		}
	}
    
    public void renderVignetteScaledResolution(ScaledResolution scaledRes)
    {
    	
    	
    	int[] rgb = Colors.getRGB(getColor2());
    	
        //Color color = getColor2();
        GlStateManager.enableTexture2D();
        GlStateManager.resetColor();
        RenderUtil.color(RenderUtil.injectAlpha( Colors.getColor(rgb[0], rgb[1], rgb[2], 255), MathHelper.clamp_int((int) 60, 0,255)).getRGB());
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(771, 770);
        GlStateManager.tryBlendFuncSeparate(771, 770, 1, 0);

        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/misc/vignette.png"));
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();

        tessellator.draw();
        GlStateManager.disableBlend();
    }
}