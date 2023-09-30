package io.github.raze.modules.collection.movement;

import io.github.raze.Raze;
import io.github.raze.events.collection.motion.EventMotion;
import io.github.raze.events.system.BaseEvent;
import io.github.raze.events.system.SubscribeEvent;
import io.github.raze.modules.system.BaseModule;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.BooleanSetting;
import io.github.raze.settings.collection.NumberSetting;
import io.github.raze.utilities.collection.MoveUtil;
import io.github.raze.utilities.collection.math.TimeUtil;
import io.github.raze.utilities.collection.visual.ChatUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Flight extends BaseModule {

    public ArraySetting mode, damage;
    public NumberSetting speed;
    public BooleanSetting damageBoost, deathDisable;

    private final TimeUtil timer;

    private double funcraftSpeed = 0.0;
    private boolean wasDamaged, bwHubMessageSaid, jetpackMessageSaid;

    public Flight() {
        super("Flight", "Allows you to Fly.", ModuleCategory.MOVEMENT);


        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(
                mode = new ArraySetting(this, "Mode", "Simple", "Simple", "Motion", "SpoofGround", "BedwarsPractice", "Funcraft", "Vulcan", "BlockDrop", "Verus", "BWHub", "Jetpack"),
                damage = new ArraySetting(this, "Damage Mode", "None", "None", "Normal", "Fake", "Hypixel", "BWHub"),

                speed = new NumberSetting(this, "Speed", 0.1, 9.5, 0.5),

                damageBoost = new BooleanSetting(this, "Damage Boost", false),
                deathDisable = new BooleanSetting(this, "Disable on death", false)

        );

        timer = new TimeUtil();
    }

    @SubscribeEvent
    private void onMotion(EventMotion eventMotion) {
        if (eventMotion.getState() == BaseEvent.State.PRE) {
            if (damageBoost.get()) {
                if (mc.thePlayer.hurtTime != 0) {
                    mc.thePlayer.motionX *= 1.1;
                    mc.thePlayer.motionZ *= 1.1;
                }
            }
            switch (mode.get()) {
                case "Simple":
                    mc.thePlayer.motionY = 0;
                    break;

                case "Motion":
                    eventMotion.setOnGround(true);
                    MoveUtil.setSpeed(speed.get().doubleValue());
                    if(mc.gameSettings.keyBindJump.isKeyDown())
                        mc.thePlayer.motionY = speed.get().doubleValue();
                    else if (mc.gameSettings.keyBindSneak.isKeyDown())
                        mc.thePlayer.motionY = -speed.get().doubleValue();
                    else
                        mc.thePlayer.motionY = 0.0D;
                    break;

                case "SpoofGround":
                    mc.thePlayer.motionY = 0;
                    eventMotion.setOnGround(true);
                    mc.thePlayer.cameraPitch = 0.1F;
                    mc.thePlayer.cameraYaw = 0.1F;
                    mc.thePlayer.onGround = true;
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    break;

                case "BedwarsPractice":
                    MoveUtil.strafe(0.29F);
                    mc.thePlayer.motionX *= 1.07;
                    mc.thePlayer.motionZ *= 1.07;
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.cameraPitch = 0.1F;
                    mc.thePlayer.cameraYaw = 0.1F;
                    mc.thePlayer.onGround = true;
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                    break;

                case "Funcraft":
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.jumpMovementFactor = 0;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.000010, mc.thePlayer.posZ);
                    if (!MoveUtil.isMoving() || mc.thePlayer.isCollidedHorizontally)
                        funcraftSpeed = 0.25;
                    if (funcraftSpeed > 0.25)
                        funcraftSpeed -= funcraftSpeed / 120;
                    MoveUtil.setSpeed((float) funcraftSpeed);
                    break;

                case "Vulcan":
                    mc.thePlayer.motionY = 0;
                    eventMotion.setOnGround(true);
                    mc.timer.timerSpeed = (float) 0.03;
                    MoveUtil.strafe((float) (MoveUtil.getBaseMoveSpeed() * 34.2));
                    break;

                case "BlockDrop":
                    mc.timer.timerSpeed = (float) 0.3;
                    mc.gameSettings.keyBindForward.pressed = false;
                    mc.thePlayer.onGround = true;
                    mc.thePlayer.motionY = 0;
                    double x = -Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * .28;
                    double z = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * .28;
                    mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z, false));
                    mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + 30, mc.thePlayer.posZ + z, true));
                    mc.thePlayer.posX += x;
                    mc.thePlayer.posZ += z;
                    mc.thePlayer.motionX *= 1.02;
                    mc.thePlayer.motionZ *= 1.02;
                    break;

                case "Verus":
                    if(timer.elapsed(545, true)) {
                        mc.thePlayer.jump();
                        mc.thePlayer.onGround = true;
                        mc.timer.timerSpeed = 1;
                    }
                    break;

                case "BWHub":
                    if(mc.thePlayer.hurtTime != 0)
                        wasDamaged = true;

                    if(wasDamaged) {
                        eventMotion.setOnGround(true);
                        MoveUtil.setSpeed(speed.get().doubleValue());
                        if(mc.gameSettings.keyBindJump.isKeyDown())
                            mc.thePlayer.motionY = speed.get().doubleValue();
                        else if (mc.gameSettings.keyBindSneak.isKeyDown())
                            mc.thePlayer.motionY = -speed.get().doubleValue();
                        else
                            mc.thePlayer.motionY = 0.0D;
                    }
                    break;

                case "Jetpack":
                    if(mc.gameSettings.keyBindJump.pressed)
                        mc.thePlayer.motionY += 0.2;
                    break;
            }

            if (deathDisable.get()) {
                if (mc.thePlayer.getHealth() <= 0 || mc.thePlayer.isDead) {
                    this.setEnabled(false);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        wasDamaged = false;
        switch (mode.get()) {
            case "BWHub":
                if (!bwHubMessageSaid) {
                    ChatUtil.addChatMessage("For this mode to work, you need to take damage.", true);
                    bwHubMessageSaid = true;
                }
                break;

            case "Jetpack":
                if (!jetpackMessageSaid) {
                    ChatUtil.addChatMessage("You must hold spacebar to fly while in jetpack mode.", true);
                    jetpackMessageSaid = true;
                }
                break;
        }
        switch (damage.get()) {
            case "Fake":
                mc.thePlayer.performHurtAnimation();
                break;

            case "Normal":
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.5, mc.thePlayer.posZ, false));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                break;

            case "Hypixel":
                double x = mc.thePlayer.posX, y = mc.thePlayer.posY, z = mc.thePlayer.posZ;
                float minimumValue = 3.1F;
                double random1 = randomNumber(0.0890, 0.0849);
                double random2 = Math.random() * 0.0002;
                double random3 = Math.random() * 0.0002;
                double random4 = randomNumber(0.0655, 0.0625);
                double random5 = randomNumber(0.001, 0.01);

                for (int i = 0; i < (int) (minimumValue / (random1 - 0.001 - random2 - random3) + 18); i++) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + random4 - random5 - random2, z, false));
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + Math.random() * 0.0002, z, false));
                }
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                break;

            case "BWHub":
                mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + 7, mc.thePlayer.posZ);
                break;
        }

        if (mode.compare("Funcraft")) {
            funcraftSpeed = 1.7;

            if (mc.thePlayer.onGround)
                mc.thePlayer.jump();
        }
    }

    @Override
    public void onDisable() {
        wasDamaged = false;
        mc.timer.timerSpeed = 1F;
        if (mode.compare("Funcraft")) {
            if (funcraftSpeed > 0.25) {
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionZ = 0;
            }
        }
    }

    private static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }
}