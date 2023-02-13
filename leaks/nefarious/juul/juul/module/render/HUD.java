package juul.module.render;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import juul.Juul;
import juul.event.Event;
import juul.event.EventHUD;
import juul.module.Module;
import juul.util.image.Draw;
import juul.util.image.Resources;
import juul.util.image.TextureImage;

public class HUD extends Module {

	public HUD() {
		super("HUD", "Draws informational interface about the client", Keyboard.KEY_APOSTROPHE, Category.RENDER);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventHUD) {
			EventHUD event = (EventHUD)e;
			
			GL11.glPushMatrix();
	        
			//constants.drawStringWithShadow("Hey there", 10, 10, -1);

			TextureImage logo = Resources.downloadTexture("https://intent.store/juul/logo.png");
			
	        if(logo.pixels != null) {
	        	//Texture bind = TextureImpl.getLastBind();
				TextureImpl.bindNone();
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				Draw.rectTexture(5, 5, 60, 54.5f, logo.getTexture(), 0xffffffff);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
	        }
			
			//constants.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("https://intent.store/juul/logo.png"));
			
			/*if(logoTexture == null) {
				logoTexture = new ThreadDownloadImageData(null, "https://intent.store/juul/logo.png", null, new ImageBufferDownload());
            try {
            	logoTexture.loadTexture(constants.getMinecraft().getResourceManager());
			} catch (IOException e1) { e1.printStackTrace(); }
			}else {
				//System.out.println(logoTexture);
				
				GlStateManager.func_179144_i(logoTexture.getGlTextureId());
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				GL11.glColor4f(1, 1, 1, 1);
	            Gui.drawModalRectWithCustomSizedTexture(5, 5, 0, 0, 60, (int)54.5f, 60, 54.0f);
	            GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}*/
			
				constants.drawString(Juul.VERSION, 65, 8, 0, 0xffdddddd);
				
				
				int count = 0;
				for(Module module : Juul.INSTANCE.manager.modules) {
					if(module.isEnabled()) {

						constants.drawString(module.name, event.getWidth() - 5 - constants.getStringWidth(module.name), 5 + count * 10, 0, 0xffdddddd);
						
						count++;
					}
				}
	        //}
			
			GL11.glPopMatrix();
		}
	}
	
}
