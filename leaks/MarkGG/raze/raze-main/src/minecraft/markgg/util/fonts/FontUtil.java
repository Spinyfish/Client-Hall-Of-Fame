
package markgg.util.fonts;

import java.awt.Font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontUtil {

	public static Font getFontFromTTF(ResourceLocation fontLocation, float fontSize, int fontType) {
		Font output = null;
		try {
			output = Font.createFont(fontType, Minecraft.getMinecraft().getResourceManager().getResource(fontLocation).getInputStream());
			output = output.deriveFont(fontSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
}
