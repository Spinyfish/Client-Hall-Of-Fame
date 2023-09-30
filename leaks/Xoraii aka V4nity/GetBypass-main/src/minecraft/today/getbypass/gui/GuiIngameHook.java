package today.getbypass.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import today.getbypass.utils.Wrapper;
import today.getbypass.GetBypass;
import today.getbypass.module.Module;

public class GuiIngameHook extends GuiIngame{
	protected Minecraft mc = Minecraft.getMinecraft();

	public GuiIngameHook(Minecraft mcIn) {
		super(mcIn);
	}
	
	
	public void renderGameOverlay(float p_175180_1_){
	     super.renderGameOverlay(p_175180_1_);
	     
	     
	     if(!mc.gameSettings.showDebugInfo) {
	    	 GlStateManager.pushMatrix();
	    	 GlStateManager.scale(1.5f, 1.5f, 1.5f);
	    	 drawRect(2, 2, 58, 14, 0x80000000);
		     Wrapper.fr.drawString("GetBypass", 4, 4, 0x0080FF);
		     GlStateManager.popMatrix();
		     renderArrayList();
	     }
	     
	     
	}
	
	private void renderArrayList() {
		int yCount = 0;
		int index = 0;
		long x = 0;
		for(Module m : GetBypass.instance.moduleManager.getModules()) {
			m.onRender();
			
			ScaledResolution sr = new ScaledResolution(mc);
			double offset = yCount*(Wrapper.fr.FONT_HEIGHT + 6);
			
			if(m.isToggled()) {
				Gui.drawRect(sr.getScaledWidth() - Wrapper.fr.getStringWidth(m.getName()) - 15, offset, sr.getScaledWidth(), 6 + Wrapper.fr.FONT_HEIGHT + offset, 0x80000000);
	            Wrapper.fr.drawString("- " + m.getName(), sr.getScaledWidth() - Wrapper.fr.getStringWidth(m.getName()) - 13 , 4 + offset , 0x0080FF);
					yCount ++;
					index++;
					x++;

			}
		}
	}
}


