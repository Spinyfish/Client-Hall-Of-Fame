package haptic.module.impl.render;

import haptic.Haptic;
import haptic.event.Event;
import haptic.event.impl.EventRender;
import haptic.module.Category;
import haptic.module.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;

public class ESP extends Module {

	public ESP() {
		super("ESP", Category.RENDER);
	}
	
	public void onEnable() {
		if(Config.isFastRender()) {
			Haptic.addChatMessage("You need to disable fast render in minecraft settings to use ESP.");
			this.setEnabled(false);
		}
	}
	
}