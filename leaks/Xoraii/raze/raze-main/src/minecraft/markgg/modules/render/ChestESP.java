package markgg.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import markgg.modules.Module;
import markgg.settings.BooleanSetting;
import markgg.settings.NumberSetting;
import markgg.util.esp.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;

public class ChestESP extends Module {

	public NumberSetting chsRed = new NumberSetting("Chest Red", this, 0, 0, 1, 0.1);
	public NumberSetting chsGreen = new NumberSetting("Chest Green", this, 0.5, 0, 1, 0.1);
	public NumberSetting chsBlue = new NumberSetting("Chest Blue", this, 1, 0, 1, 0.1);

	public ChestESP() {
		super("ChestESP", "Renders chests", 0, Category.RENDER);
		addSettings(chsRed,chsGreen,chsBlue);
	}

	public void onRender() {
		if(this.isEnabled()) {
			for(Object o : mc.theWorld.loadedTileEntityList) {
				if(o instanceof TileEntityChest) {
					this.chest(((TileEntityChest)o).getPos());
				}
			}
		}
	}

	public void chest(BlockPos blockPos) {

		double x = 
				blockPos.getX()
				- Minecraft.getMinecraft().getRenderManager().renderPosX;
		double y = 
				blockPos.getY()
				- Minecraft.getMinecraft().getRenderManager().renderPosY;
		double z = 
				blockPos.getZ()
				- Minecraft.getMinecraft().getRenderManager().renderPosZ;

		float red = (float) chsRed.getValue();
		float green = (float) chsGreen.getValue();
		float blue = (float) chsBlue.getValue();
		double xPos = x;
		double yPos = y;
		double zPos = z;
		this.render(red, green, blue, xPos, yPos, zPos, 1, 1);
	}

	public void render(float red, float green, float blue, double x, double y, double z, float width, float height) {
		RenderUtil.drawOutlinedBlockESP(x, y, z, red, green, blue, 1, 1);
	}


}
