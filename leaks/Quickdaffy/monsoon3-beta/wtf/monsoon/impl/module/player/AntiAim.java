/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Random;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.MathHelper;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;

public class AntiAim
extends Module {
    private final Setting<ViewModes> viewModeProperty = new Setting<ViewModes>("View Mode", ViewModes.CLIENT);
    private final Setting<Modes> modeProperty = new Setting<Modes>("Mode", Modes.CUSTOM);
    private final Setting<Boolean> antiaimProperty = new Setting<Boolean>("Anti-Aim", false);
    private final Setting<PitchModes> pitchProperty = new Setting<PitchModes>("Pitch", PitchModes.DOWN).visibleWhen(this.antiaimProperty::getValue);
    private final Setting<YawModes> yawProperty = new Setting<YawModes>("Yaw Base", YawModes.BACKWARD).visibleWhen(this.antiaimProperty::getValue);
    private final Setting<Integer> yawAddProperty = new Setting<Integer>("Yaw Add", 0).minimum(-179).maximum(179).incrementation(1).visibleWhen(this.antiaimProperty::getValue);
    private final Setting<YawModModes> yawModProperty = new Setting<YawModModes>("Yaw Modifier", YawModModes.JITTER).visibleWhen(this.antiaimProperty::getValue);
    private final Setting<Integer> modSpinSpeedProperty = new Setting<Integer>("Spin Speed", 60).minimum(0).maximum(100).incrementation(1).visibleWhen(() -> this.antiaimProperty.getValue() != false || this.yawModProperty.getValue().equals((Object)YawModModes.JITTER));
    private final Setting<Integer> modJitterRangeProperty = new Setting<Integer>("Jitter Base", 0).minimum(-90).maximum(90).incrementation(1).visibleWhen(() -> this.antiaimProperty.getValue() != false || this.yawModProperty.getValue().equals((Object)YawModModes.SPIN));
    private final Setting<Boolean> fakeAngleProperty = new Setting<Boolean>("Fake Angles", false);
    Random rand = new Random();
    private EntityOtherPlayerMP fakePlayer;
    private float[] lastAngles;
    private boolean tick;
    @EventLink
    private final Listener<EventPreMotion> eventPreMotionListener = event -> {
        double posX = this.mc.thePlayer.posX;
        double posY = this.mc.thePlayer.posY;
        double posZ = this.mc.thePlayer.posZ;
        float pPitch = this.mc.thePlayer.rotationPitch;
        float pYaw = this.mc.thePlayer.rotationYaw;
        float eYaw = event.getYaw();
        if (this.lastAngles == null) {
            this.lastAngles = new float[]{pYaw, pPitch};
        }
        if (this.mc.thePlayer.ticksExisted % 10 == 0) {
            this.updateTick();
        }
        switch (this.modeProperty.getValue()) {
            case VERUS: {
                double valX = this.rand.nextDouble() / 1.6;
                double valZ = this.rand.nextDouble() / 1.6;
                if (this.mc.thePlayer.hurtTime < 9) break;
                this.mc.thePlayer.setPosition(posX - valX, posY, posZ - valZ);
                break;
            }
            case CUSTOM: {
                if (Wrapper.getModule(Aura.class).getTarget() != null && this.mc.thePlayer.isSwingInProgress) break;
                if (this.antiaimProperty.getValue().booleanValue()) {
                    switch (this.pitchProperty.getValue()) {
                        case UP: {
                            this.updateServerPitch((EventPreMotion)event, -90.0f);
                            this.updateClientPitch(-90.0f);
                            break;
                        }
                        case DOWN: {
                            this.updateServerPitch((EventPreMotion)event, 90.0f);
                            this.updateClientPitch(90.0f);
                            break;
                        }
                        case HALFDOWN: {
                            this.updateServerPitch((EventPreMotion)event, 60.0f);
                            this.updateClientPitch(60.0f);
                            break;
                        }
                        case STUTTER: {
                            if (this.mc.thePlayer.ticksExisted % 10 != 0) {
                                this.updateServerPitch((EventPreMotion)event, 90.0f);
                                this.updateClientPitch(90.0f);
                                break;
                            }
                            this.updateServerPitch((EventPreMotion)event, -45.0f);
                            this.updateClientPitch(-45.0f);
                            break;
                        }
                        case MEME: {
                            float lastMeme = pPitch;
                            lastMeme += 10.0f;
                            if (lastMeme > 90.0f) {
                                lastMeme = -90.0f;
                            }
                            this.updateServerPitch((EventPreMotion)event, lastMeme);
                            this.updateClientPitch(lastMeme);
                            break;
                        }
                    }
                    switch (this.yawProperty.getValue()) {
                        case FORWARD: {
                            this.updateServerYaw((EventPreMotion)event, this.lastAngles[0]);
                            this.updateClientYaw(this.lastAngles[0]);
                            break;
                        }
                        case BACKWARD: {
                            float backwardYaw = this.lastAngles[0] - 180.0f;
                            this.lastAngles = new float[]{MathHelper.wrapAngleTo180_float(backwardYaw), this.lastAngles[1]};
                            this.updateServerYaw((EventPreMotion)event, backwardYaw);
                            this.mc.thePlayer.renderYawOffset = this.mc.thePlayer.rotationYawHead = backwardYaw;
                        }
                    }
                    switch (this.yawModProperty.getValue()) {
                        case NONE: {
                            break;
                        }
                        case SPIN: {
                            float spinYaw;
                            int speed = this.modSpinSpeedProperty.getValue();
                            this.mc.thePlayer.renderYawOffset = this.mc.thePlayer.rotationYawHead = (spinYaw = pYaw + (float)speed);
                            break;
                        }
                        case JITTER: {
                            int lower = this.modJitterRangeProperty.getValue();
                            int higher = this.modJitterRangeProperty.getValue() + 1;
                            this.mc.thePlayer.renderYawOffset = this.mc.thePlayer.rotationYawHead += (float)((double)pYaw + MathUtils.randomNumber(7.0, -7.0));
                            break;
                        }
                        case LISP: {
                            float lispYaw = this.lastAngles[0] + 150000.0f;
                            this.lastAngles = new float[]{lispYaw, this.lastAngles[1]};
                            this.mc.thePlayer.renderYawOffset = this.mc.thePlayer.rotationYawHead = lispYaw;
                        }
                    }
                }
                if (this.fakeAngleProperty.getValue().booleanValue()) {
                    if (this.fakePlayer == null || this.mc.thePlayer.ticksExisted % 20 != 0) break;
                    this.fakePlayer.setLocationAndAngles(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
                    this.updateFakeAngles(this.fakePlayer, pPitch, pYaw);
                    this.fakePlayer.setSneaking(this.mc.thePlayer.isSneaking());
                    break;
                }
                if (this.fakePlayer == null) break;
                this.mc.theWorld.removeEntityFromWorld(this.fakePlayer.getEntityId());
                this.fakePlayer = null;
            }
        }
    };

    public AntiAim() {
        super("Anti Aim", "Automatically switch to the correct tool when mining a block.", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.fakeAngleProperty.getValue().booleanValue()) {
            this.fakePlayer = new EntityOtherPlayerMP(this.mc.theWorld, this.mc.thePlayer.getGameProfile());
            this.fakePlayer.clonePlayer(this.mc.thePlayer, true);
            this.fakePlayer.setLocationAndAngles(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
            this.fakePlayer.rotationYawHead = this.mc.thePlayer.rotationYawHead;
            this.fakePlayer.setEntityId(1337);
            this.fakePlayer.setSneaking(this.mc.thePlayer.isSneaking());
            this.mc.theWorld.addEntityToWorld(this.fakePlayer.getEntityId(), this.fakePlayer);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.fakeAngleProperty.getValue().booleanValue() && this.fakePlayer != null) {
            this.mc.theWorld.removeEntityFromWorld(this.fakePlayer.getEntityId());
            this.fakePlayer = null;
        }
    }

    private void updateTick() {
        this.tick = !this.tick;
    }

    private void updateServerPitch(EventPreMotion event, float pitch) {
        if (this.viewModeProperty.getValue().equals((Object)ViewModes.SERVER)) {
            event.setPitch(pitch);
        }
    }

    private void updateServerYaw(EventPreMotion event, float yaw) {
        if (this.viewModeProperty.getValue().equals((Object)ViewModes.SERVER)) {
            event.setYaw(yaw);
        }
    }

    private void updateClientPitch(float pitch) {
        this.mc.thePlayer.rotationPitchHead = pitch;
    }

    private void updateClientYaw(float yaw) {
        this.mc.thePlayer.rotationYawHead = yaw;
        this.mc.thePlayer.renderYawOffset = yaw;
    }

    private void updateFakeAngles(EntityOtherPlayerMP fake, float pitch, float yaw) {
        fake.rotationPitchHead = pitch;
        fake.rotationYawHead = yaw;
        fake.renderYawOffset = yaw;
    }

    private static enum LBYModes {
        OPPOSITE("Opposite"),
        SWAY("Sway");

        private final String name;

        private LBYModes(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    private static enum FakeOptionsModes {
        JITTER("Jitter"),
        IDK("idk");

        private final String name;

        private FakeOptionsModes(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    private static enum ViewModes {
        CLIENT("Client"),
        SERVER("Server");

        private final String name;

        private ViewModes(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    private static enum YawModModes {
        NONE("None"),
        SPIN("Spin"),
        LISP("Lisp"),
        JITTER("Jitter");

        private final String name;

        private YawModModes(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    private static enum YawModes {
        FORWARD("Forward"),
        BACKWARD("Backward");

        private final String name;

        private YawModes(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    private static enum PitchModes {
        UP("Up"),
        DOWN("Down"),
        HALFDOWN("Halfdown"),
        STUTTER("Stutter"),
        MEME("Meme"),
        NORMAL("Normal");

        private final String name;

        private PitchModes(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    private static enum Modes {
        CUSTOM("Custom"),
        VERUS("Verus");

        private final String name;

        private Modes(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

