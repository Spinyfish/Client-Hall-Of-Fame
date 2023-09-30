package intentions.modules.movement;

import org.lwjgl.input.Keyboard;

import intentions.events.Event;
import intentions.events.listeners.EventUpdate;
import intentions.modules.Module;
import intentions.settings.ModeSetting;
import intentions.util.RenderUtils;
import intentions.util.TeleportUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class ClickTP extends Module {

	private BlockPos hover;
	private boolean tp = false;

	public ClickTP() {
		super("ClickTP", Keyboard.KEY_NONE, Category.MOVEMENT, "Teleports you forward when you right-click. Max 200 blocks.", true);
		this.addSettings(mode);
	}
	
	public static ModeSetting mode = new ModeSetting("Mode", "Default", new String[] {"ACD", "Default"});

	@Override
	public void onEvent(Event e) {
		if(!(e instanceof EventUpdate))return;
		hover = mc.thePlayer.rayTrace(200, mc.timer.renderPartialTicks).getBlockPos();
		if(tp) {
			e.setCancelled(true);
			tp = false;
		}
	}

	@Override
	public void onRightClick() {
		MovingObjectPosition rayTrace = mc.thePlayer.rayTrace(200, mc.timer.renderPartialTicks);
		BlockPos blockPos = rayTrace.getBlockPos();
		if (blockPos == null) {
			return;
		}
		double x = mc.thePlayer.posX;
		double z = mc.thePlayer.posZ;
		
		TeleportUtils.pathFinderTeleportTo(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), TeleportUtils.getVec3(rayTrace.getBlockPos().up()));
		
		mc.thePlayer.setPosition(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);
		tp = true;
	}

	@Override
	public void onRender() {
		if (hover == null) {
			return;
		}
		double x = hover.getX() + 0.5 - mc.getRenderManager().renderPosX;
		double y = hover.getY() + 1 - mc.getRenderManager().renderPosY;
		double z = hover.getZ() + 0.5 - mc.getRenderManager().renderPosZ;
		
		RenderUtils.drawSolidEntityESP(x, y, z, 1 * 0.65, 0.1, 0.6f, 0.2f, 0.2f, 0.3f);
		RenderUtils.drawOutlinedEntityESP(x, y, z, 1 * 0.65, 0.1, 1f, 1f, 1f, 1f);
		super.onRender();
	}

}
