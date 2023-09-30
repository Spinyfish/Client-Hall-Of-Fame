/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;

public class HighJump
extends Module {
    public Setting<Mode> mode = new Setting<Mode>("Mode", Mode.HYPIXEL).describedBy("How to prevent fall damage");
    private final Setting<Boolean> onSpace = new Setting<Boolean>("On Space", false).describedBy("If you should only go up while holding space.").visibleWhen(() -> this.mode.getValue() == Mode.VERUS);
    private boolean shouldJump;
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (this.mode.getValue()) {
            case VERUS: {
                if (!this.mc.gameSettings.keyBindJump.isKeyDown() && this.onSpace.getValue().booleanValue()) break;
                this.player.setSpeed(0.0);
                if (this.mc.thePlayer.ticksExisted % 2 != 0) break;
                this.mc.thePlayer.jump();
                break;
            }
            case HYPIXEL: {
                if (this.shouldJump) {
                    this.mc.thePlayer.jump();
                    this.mc.thePlayer.motionY += 2.75;
                    this.player.setSpeed(1.0);
                }
                if (!this.mc.thePlayer.onGround || !this.shouldJump) break;
                this.shouldJump = false;
            }
        }
    };
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            return;
        }
        if (e.getPacket() instanceof S27PacketExplosion && this.mc.thePlayer.hurtTime > 0) {
            this.shouldJump = true;
        }
    };

    public HighJump() {
        super("High Jump", "Jump higher.", Category.MOVEMENT);
        this.setMetadata(() -> StringUtil.formatEnum(this.mode.getValue()));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.shouldJump = false;
    }

    static enum Mode {
        HYPIXEL,
        VERUS;

    }
}

