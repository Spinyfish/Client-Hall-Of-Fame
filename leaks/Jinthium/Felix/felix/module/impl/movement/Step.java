package felix.module.impl.movement;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.events.player.EventStep;
import felix.module.Module;
import felix.util.other.TimeHelper;
import felix.value.impl.BooleanValue;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.stats.StatList;

public class Step extends Module {
	
    private NumberValue<Float> height = new NumberValue<>("Height", 1.0f, 1.0f, 2.5f, 0.5f);
    public EnumValue<Mode> mode = new EnumValue<>("Mode", Mode.Vanilla);
    private TimeHelper time = new TimeHelper();
    private boolean hasStep;

    public Step() {
        super("Step", 0, ModuleCategory.MOVEMENT);
        addValues(mode, height);
    }

    public enum Mode {
        Vanilla, NCP, Motion;
    }
    
    public void onEnable() {
    	super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.thePlayer != null) {
            mc.thePlayer.stepHeight = 0.6f;
        }
    }

    @Handler
    public void onStep(final EventStep event) {
        boolean vanilla = mode.getValue().equals(Mode.Vanilla);
        if (!mc.thePlayer.isInLava() && !vanilla && shouldStep()) {
            if (event.isPre()) {
                if (mc.thePlayer.isCollidedVertically && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.isOnLadder()) {
                    event.setHeight(height.getValue());
                } else {
                    event.setHeight(0.6f);
                }
            }
        }

        double rheight = mc.thePlayer.getEntityBoundingBox().minY - mc.thePlayer.posY;
        boolean canStep = rheight >= 0.625;
        if (canStep && !vanilla) {
            hasStep = true;
            switch (mode.getValue()) {
                case NCP:
                    fakeJump();
                    ncpStep(rheight);
                    break;
                case Motion:
                	fakeJump();
                	ncpStep(rheight);
                	break;
                default:
                    break;
            }
            time.reset();
        }
    }

    private boolean shouldStep() {
    	final Speed speed = (Speed) Client.INSTANCE.getModuleManager().getModule(Speed.class);
    	final Flight flight = (Flight) Client.INSTANCE.getModuleManager().getModule(Flight.class);
    	final LongJump longJump = (LongJump) Client.INSTANCE.getModuleManager().getModule(LongJump.class);
    	if (speed.isEnabled() && speed != null) {
    		return false;
    	}
    	if (flight.isEnabled() && flight != null) {
    		return false;
    	}
    	if (longJump.isEnabled() && longJump != null) {
    		return false;
    	}
		return true;
	}

	public void fakeJump() {
        mc.thePlayer.isAirBorne = true;
        mc.thePlayer.triggerAchievement(StatList.jumpStat);
    }

    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
    	setSuffix(mode.getValueAsString());
    	boolean motion = mode.getValue().equals(Mode.Motion);
    	boolean vanilla = mode.getValue().equals(Mode.Vanilla);
        if (vanilla)
            mc.thePlayer.stepHeight = height.getValue();
        if (time.reach(80) && hasStep && motion && event.isPre() && !vanilla) {
            mc.timer.timerSpeed = 1F;
            hasStep = false;
        }
    }

    private void ncpStep(double height) {
    	boolean smooth = mode.getValue().equals(Mode.Motion);
        double posX = mc.thePlayer.posX;
        double posZ = mc.thePlayer.posZ;
        double y = mc.thePlayer.posY;
        if (height < 1.1) {
            if (smooth) {
                mc.timer.timerSpeed = 0.4F;
            }
            double first = 0.42f;
            double second = first + 0.3333;
            if (height != 1) {
                first *= height;
                second *= height;
                if (first > 0.425) {
                    first = 0.425;
                }
                if (second > 0.78) {
                    second = 0.78;
                }
                if (second < 0.49) {
                    second = 0.49;
                }
            }
            if (first == 0.42) first = 0.425;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
            if (y + second < y + height)
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + second, posZ, false));
        } else if (height < 1.6) {
            if (smooth) {
                mc.timer.timerSpeed = 0.35F;
            }
            float[] heights = {0.425111231f, 0.821111231f, 0.699111231f, 0.599111231f, 1.022111231f};
            for (double off : heights) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        } else if (height < 2.1) {
            if (smooth) {
                mc.timer.timerSpeed = 0.22F;
            }
            float[] heights = {0.425111231f, 0.821111231f, 0.699111231f, 0.599111231f, 1.022111231f, 1.372111231f, 1.652111231f, 1.869111231f};
            for (double off : heights) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        } else {
            if (smooth) {
                mc.timer.timerSpeed = 0.21F;
            }
            float[] heights = {0.425111231f, 0.821111231f, 0.699111231f, 0.599111231f, 1.022111231f, 1.372111231f, 1.652111231f, 1.869111231f, 2.01895111231f, 1.9052111231f};
            for (double off : heights) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        }
    }
}