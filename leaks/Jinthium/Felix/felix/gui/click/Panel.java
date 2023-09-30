package felix.gui.click;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;

import felix.Client;
import felix.gui.click.components.Frame;
import felix.gui.click.components.GuiButton;
import felix.gui.click.components.GuiFrame;
import felix.gui.click.listeners.ClickListener;
import felix.gui.click.listeners.ComponentsListener;
import felix.gui.click.util.FramePosition;
import felix.module.Module;
import felix.module.Module.ModuleCategory;
import felix.util.font.FontRenderer;
import net.minecraft.client.gui.VanillaFontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at 11.11.2020. 
 *         Use is only authorized if given credit!
 * 
 * 		   Some renderstuff requires https://github.com/sendQueue/LWJGLUtil
 * 
 */
public class Panel extends ClickGui {
	public static HashMap<String, FramePosition> framePositions = new HashMap<String, FramePosition>();

	public static FontRenderer fR = Client.INSTANCE.getFontManager().getFont("Display 17", false);
	
	public static int FRAME_WIDTH = 100;
	
	public static int color = new Color(130, 191, 226).getRGB();
	public static int color2 = new Color(64, 85, 150).getRGB();
	
	// colors
	public static int fontColor = Color.white.getRGB();
	public static int grey40_240 = new Color(40, 40, 40, 195).getRGB();
	public static int black195 = new Color(28, 28, 28, 255).getRGB();
	public static int black100 = new Color(50, 50, 50, 255).getRGB();
	
	/**
	 * Initializes Panel
	 * 
	 * @param theme
	 * @param fontSize
	 */
	public Panel() {
	}

	@Override
	public void initGui() {
		int x = 25;
		for (ModuleCategory cat : ModuleCategory.values()) {
			GuiFrame frame;
			// load frame positions
			if (framePositions.containsKey(cat.name())) {
				FramePosition curPos = framePositions.get(cat.name());
				frame = new GuiFrame(cat.name(), curPos.getPosX(), curPos.getPosY(), curPos.isExpanded());
			} else {
				frame = new GuiFrame(cat.name(), x, 50, true);
			}
			for (Module m : Client.INSTANCE.getModuleManager().getModules()) {
				if (cat == m.getCategory()) {
					GuiButton button = new GuiButton(m.getName());
					button.addClickListener(new ClickListener(button));
					button.addExtendListener(new ComponentsListener(button));
					frame.addButton(button);
				}
			}
			addFrame(frame);
			x += 140;
		}
        if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer) {
            if (mc.entityRenderer.theShaderGroup != null) {
                mc.entityRenderer.theShaderGroup.deleteShaderGroup();
            }
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        }
		super.initGui();
	}


	public void onGuiClosed() {
		// save positions to framePositions
		if (!getFrames().isEmpty()) {
			for (Frame frame : getFrames()) {
				GuiFrame guiFrame = ((GuiFrame) frame);
				framePositions.put(guiFrame.getTitle(),
						new FramePosition(guiFrame.getPosX(), guiFrame.getPosY(), guiFrame.isExpaned()));
			}
		}
		Client.INSTANCE.getModuleManager().getModule("ClickGUI").onDisable();
        if (mc.entityRenderer.theShaderGroup != null) {
            mc.entityRenderer.theShaderGroup.deleteShaderGroup();
            mc.entityRenderer.theShaderGroup = null;
        }
	}
}
