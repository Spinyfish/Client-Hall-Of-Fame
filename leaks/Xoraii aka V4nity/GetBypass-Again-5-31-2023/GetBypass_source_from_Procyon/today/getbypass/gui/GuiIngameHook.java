// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.gui;

import java.util.Iterator;
import net.minecraft.client.gui.ScaledResolution;
import today.getbypass.module.Module;
import today.getbypass.module.ModuleManager;
import today.getbypass.GetBypass;
import today.getbypass.utils.Wrapper;
import net.minecraft.client.gui.Gui;
import java.awt.Color;
import today.getbypass.utils.RoundedUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;

public class GuiIngameHook extends GuiIngame
{
    protected Minecraft mc;
    
    public GuiIngameHook(final Minecraft mcIn) {
        super(mcIn);
        this.mc = Minecraft.getMinecraft();
    }
    
    @Override
    public void renderGameOverlay(final float p_175180_1_) {
        super.renderGameOverlay(p_175180_1_);
        if (!this.mc.gameSettings.showDebugInfo) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.5f, 1.5f, 1.5f);
            RoundedUtils.drawRoundedRect(2.0f, 2.0f, 58.0f, 14.0f, 4.0f, Integer.MIN_VALUE);
            Gui.drawRect(2, 12.699999809265137, 58, 13.899999618530273, new Color(0, 128, 255).getRGB());
            Wrapper.fr.drawString("GetBypass", 4, 4.0, 33023);
            GlStateManager.popMatrix();
            this.renderArrayList();
        }
    }
    
    private void renderArrayList() {
        int yCount = 0;
        int index = 0;
        long x = 0L;
        final ModuleManager moduleManager = GetBypass.moduleManager;
        for (final Module m : ModuleManager.getModules()) {
            m.onRender();
            final ScaledResolution sr = new ScaledResolution(this.mc);
            final double offset = yCount * (Wrapper.fr.FONT_HEIGHT + 6);
            if (m.isToggled()) {
                Gui.drawRect(sr.getScaledWidth() - Wrapper.fr.getStringWidth(m.getName()) - 15, offset, sr.getScaledWidth(), 6 + Wrapper.fr.FONT_HEIGHT + offset, Integer.MIN_VALUE);
                Gui.drawRect(1278, offset, sr.getScaledWidth(), 6 + Wrapper.fr.FONT_HEIGHT + offset, new Color(0, 128, 255).getRGB());
                Wrapper.fr.drawString("- " + m.getName(), sr.getScaledWidth() - Wrapper.fr.getStringWidth(m.getName()) - 13, 4.0 + offset, 33023);
                ++yCount;
                ++index;
                ++x;
            }
        }
    }
}
