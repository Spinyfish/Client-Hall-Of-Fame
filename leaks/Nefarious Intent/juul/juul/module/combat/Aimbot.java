package juul.module.combat;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import juul.Juul;
import juul.event.Event;
import juul.event.EventRender;
import juul.inject.wrappers.EntityLivingBaseWrapper;
import juul.inject.wrappers.EntityWrapper;
import juul.inject.wrappers.MinecraftWrapper;
import juul.module.Module;
import juul.module.settings.BooleanSetting;
import juul.module.settings.NumberSetting;
import juul.util.image.Animation;

public class Aimbot extends Module {

	public NumberSetting fov, range, speed/*, ease*/;
	public BooleanSetting center, pressingAttack, movingMouse;
	public Animation yaw = new Animation(), pitch = new Animation();
	public long delta, lastTime;
	
	public Aimbot() {
		super("Aimbot", "Automatically aims at other players.", Keyboard.KEY_F, Category.COMBAT);
		addSetting(fov = new NumberSetting("Field of View", "The delta angle between you and the opponent permitted to aim towards", 90, 1, 180, 1));
		addSetting(range = new NumberSetting("Range", "Amount of blocks away you can lock onto targets from", 4, 1, 8, 0.1));
		addSetting(speed = new NumberSetting("Speed", "How quickly the aim centers on an opponent", 50, 1, 100, 1));
		//addSetting(ease = new NumberSetting("Ease", "How non-linearly the aim rotates", 9, 0, 10, 1));
		addSetting(center = new BooleanSetting("Center", "Whether the aim attempts to fully center", false));
		addSetting(pressingAttack = new BooleanSetting("Holding Attack", "Only aim when holding down attack button", false));
		addSetting(movingMouse = new BooleanSetting("Moving Mouse", "Only aim when contributing your own mouse movements", true));
	}

	public void onEnable() {
		long time = System.nanoTime() / 100000;
		lastTime = time;
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender) {
			Object player = mc.player();
			if(player == null)
				return;
			

			long time = System.nanoTime() / 100000;
			delta = time - lastTime;
			lastTime = time;
			if((pressingAttack.isEnabled() && !mc.getGameSettings().keyBindAttack.getIsKeyPressed()) || (movingMouse.isEnabled() && !(mc.getMouseHelper().deltaX() != 0 || mc.getMouseHelper().deltaY() != 0)) || mc.currentScreen() != null || !mc.inGameHasFocus() || !Display.isActive() || !Mouse.isInsideWindow())
				return;
			
			List<EntityLivingBaseWrapper> targets = new ArrayList<>();
			for(Object o : constants.players()) {
				targets.add(new EntityLivingBaseWrapper(o));
			}

			//List<EntityLivingBase> targets = new ArrayList<EntityLivingBase>(constants.players());
			
			targets.removeIf(entity -> entity.getClassInstance() == player);
			targets.removeIf(entity -> entity.getDistanceToEntity(player) > (float)range.getValue());
			targets.removeIf(entity -> entity.isDead() || entity.getHealth() <= 0.0f);
			targets.removeIf(entity -> Math.abs(Juul.INSTANCE.aimUtil.getRotationsDelta(entity.getClassInstance())[0]) > fov.getValue() || Math.abs(Juul.INSTANCE.aimUtil.getRotationsDelta(entity.getClassInstance())[1]) > fov.getValue());
			
			EntityLivingBaseWrapper closest = null;
			for(EntityLivingBaseWrapper entity : targets) {
				if(closest == null)
					closest = entity;
				else if(entity.getDistanceToEntity(player) < closest.getDistanceToEntity(player))
					closest = entity;
			}
			
			if(closest != null && delta > 0 && (center.isEnabled() || (Math.abs(Juul.INSTANCE.aimUtil.getRotationsDelta(closest.getClassInstance())[0]) > 5 || Math.abs(Juul.INSTANCE.aimUtil.getRotationsDelta(closest.getClassInstance())[1]) > 5))) {

				float[] rotations = Juul.INSTANCE.aimUtil.getRotationsDelta(closest.getClassInstance());
				float targetYaw = rotations[0];
				float targetPitch = rotations[1];
				
				float speed = (1f/delta)*10000 * (1 / (float)this.speed.getValue()*10f);
				if(speed > 0) {
					float addYaw = (-targetYaw / speed);
					float addPitch = (-targetPitch / speed);
					EntityWrapper playerEntity = new EntityWrapper(player);

					playerEntity.rotationYaw(playerEntity.rotationYaw() + addYaw)/* + (addYaw > 0 ? linearSpeed : addYaw < 0 ? -linearSpeed : 0)*/;
					playerEntity.prevRotationYaw(playerEntity.rotationYaw());
					playerEntity.rotationPitch(playerEntity.rotationPitch() + addPitch)/* + (addPitch > 0 ? linearSpeed : addPitch < 0 ? -linearSpeed : 0)*/;
					playerEntity.prevRotationPitch(playerEntity.rotationPitch());
				}
				
			}

		}
	}
	
}
