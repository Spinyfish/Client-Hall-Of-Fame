package club.marsh.bloom.impl.mods.combat;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.combat.KillAuraUtils;
import club.marsh.bloom.impl.utils.combat.RotationUtils;
import club.marsh.bloom.impl.utils.path.PathFinder;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.impl.events.UpdateEvent;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.input.Keyboard;

import java.util.ArrayList;


public class PVPBot extends Module {
	public PVPBot() {
		super("PVP Bot",Keyboard.KEY_NONE,Category.COMBAT);
	}
	
	private ArrayList<PathFinder.Vec3> path = new ArrayList<>();
	NumberValue<Double> range = new NumberValue<>("Range",100D,0D,500D,0);
	BooleanValue strafe = new BooleanValue("Strafe", true);
	Thread pvpBotThread = new Thread(() -> {
		try {
			EntityLivingBase target = KillAuraUtils.getClosest(range.getObject().doubleValue());
			if (target != null) {
				PathFinder pathFinder = new PathFinder(new PathFinder.Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new PathFinder.Vec3(target.posX, target.posY, target.posZ));
				pathFinder.compute();
				path = pathFinder.getPath();
				if (path.size() == 0)
					return;
				PathFinder.Vec3 targetVector = path.get(path.size() - 1);
				if (KillAura.target == null) {
					mc.thePlayer.rotationYaw = getBlockRotations(targetVector.getX(), targetVector.getY(), targetVector.getZ())[0];
					mc.thePlayer.rotationPitch = getBlockRotations(targetVector.getX(), targetVector.getY(), targetVector.getZ())[1];
				}
				mc.gameSettings.keyBindForward.pressed = true;
				mc.gameSettings.keyBindBack.pressed = false;
				if (KillAura.target != null && strafe.isOn()) {
					BlockPos strafingPos = new BlockPos(mc.thePlayer).crossProduct(new BlockPos(KillAura.target));
					if (Math.hypot(strafingPos.getX(),strafingPos.getZ()) < -0.5) {
						mc.gameSettings.keyBindLeft.pressed = true;
						mc.gameSettings.keyBindRight.pressed = false;
					} else if (Math.hypot(strafingPos.getX(),strafingPos.getZ()) > 0.5) {
						mc.gameSettings.keyBindLeft.pressed = false;
						mc.gameSettings.keyBindRight.pressed = true;
					} else {
						mc.gameSettings.keyBindLeft.pressed = false;
						mc.gameSettings.keyBindRight.pressed = false;
					}
				}
				if (!mc.thePlayer.isSprinting() && mc.gameSettings.keyBindSprint.pressed)
					mc.gameSettings.keyBindSprint.pressed = false;
				else mc.gameSettings.keyBindSprint.pressed = true;
				boolean shouldJump = mc.thePlayer.isInWater();
				if (!mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer).add(0, 1, 0)) && !mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer).add(0, -1, 0)))
					shouldJump = true;
				if (targetVector.getY() > mc.thePlayer.posY)
					shouldJump = true;
				if (shouldJump && (mc.thePlayer.onGround || mc.thePlayer.isInWater()))
					mc.gameSettings.keyBindJump.pressed = true;
				else mc.gameSettings.keyBindJump.pressed = false;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	});
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		pvpBotThread.run();
	}
	
	public float[] getlookdir(Vec3 spot) {

        double xdif = ((mc.thePlayer.boundingBox.minX + mc.thePlayer.boundingBox.maxX) / 2) - spot.xCoord;
        double ydif = (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) - (spot.yCoord);
        double zdif = ((mc.thePlayer.boundingBox.minZ + mc.thePlayer.boundingBox.maxZ) / 2) - spot.zCoord;

        float yaw = (float) (Math.atan2(zdif, xdif) * 180.0D / Math.PI - 270.0D);
        float pitch = (float) (Math.atan2(ydif, Math.hypot(xdif, zdif)) * 180.0D / Math.PI);

        return new float[] { MathHelper.wrapAngleTo180_float(yaw), pitch };
    }

	public float[] getBlockRotations(double x, double y, double z) {
        return RotationUtils.getFixedGCD(getlookdir(new Vec3(x + ((x - mc.thePlayer.posX)/2),(y + ((y - mc.thePlayer.posY)/2)),z + ((z - mc.thePlayer.posZ)/2))));
    }
}
