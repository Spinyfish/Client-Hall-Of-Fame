package igbt.astolfy.utils;

import net.minecraft.client.renderer.GlStateManager;

public class AnimationUtils {
	public AnimationType animationType;
	public double value = 0;


	public AnimationUtils(AnimationType animationType) {
		this.animationType = animationType;
	}
	
	public void Render(double amount, double centerX, double centerY) {
		value = amount;
		GlStateManager.translate(centerX, centerY, 0);
		GlStateManager.scale(amount, amount, amount);
		GlStateManager.translate(-(centerX), -centerY, 0);
	}
	
	/*public void RenderCut(double percent,double x, double y, double x2, double y2) {

		switch(animationType) {
		case CUT:
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			int k = (int)((x - x2)*(percent/100.0f));
			RenderUtils.prepareScissorBox((float)x, (float)y, (float)x2, (float)y2);
			break;
		
		}
		
	}
	public void endCut() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}*/
	
	public enum AnimationType {
		SCALE,
		CUT,
	}

}
