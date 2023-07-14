/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventStep;
import wtf.monsoon.impl.event.EventUpdate;

public class Step
extends Module {
    public final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.VANILLA).describedBy("Da mode");
    private final Setting<Float> stepHeight = new Setting<Float>("Step Height", Float.valueOf(1.5f)).minimum(Float.valueOf(0.5f)).maximum(Float.valueOf(2.5f)).incrementation(Float.valueOf(0.5f)).describedBy("How high to step up.");
    public final Setting<Boolean> useTimer = new Setting<Boolean>("Use Timer", true).describedBy("Da mode").visibleWhen(() -> this.mode.getValue() == Mode.NCP);
    private Map<Double, double[]> ncpOffsets = new HashMap<Double, double[]>();
    private boolean timer = false;
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        this.mc.thePlayer.stepHeight = this.stepHeight.getValue().floatValue();
        if (this.timer && this.player.isOnGround()) {
            this.timer = false;
            this.mc.getTimer().timerSpeed = 1.0f;
        }
    };
    @EventLink
    public final Listener<EventStep> eventStepListener = e -> {
        if (e.getEntity() == this.mc.thePlayer) {
            switch (this.mode.getValue()) {
                case NCP: {
                    double height = e.getAxisAlignedBB().minY - this.mc.thePlayer.posY;
                    if (height > (double)this.stepHeight.getValue().floatValue() || !this.player.isOnGround() || this.mc.thePlayer.isInWater() || this.mc.thePlayer.isInLava()) {
                        return;
                    }
                    double[] offsets = this.ncpOffsets.getOrDefault(height, null);
                    if (offsets == null || offsets.length == 0) {
                        return;
                    }
                    if (this.useTimer.getValue().booleanValue()) {
                        this.mc.getTimer().timerSpeed = 1.0f / (1.0f / ((float)offsets.length + 1.0f));
                        this.timer = true;
                    }
                    for (double offset : offsets) {
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + offset, this.mc.thePlayer.posZ, false));
                    }
                    break;
                }
            }
        }
    };

    public Step() {
        super("Step", "Step up blox", Category.MOVEMENT);
        this.ncpOffsets.put(0.875, new double[]{0.39, 0.7, 0.875});
        this.ncpOffsets.put(1.0, new double[]{0.42, 0.75, 1.0});
        this.ncpOffsets.put(1.5, new double[]{0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43});
        this.ncpOffsets.put(2.0, new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.919});
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.mc.thePlayer.stepHeight = 0.6f;
        this.mc.getTimer().timerSpeed = 1.0f;
        this.timer = false;
    }

    static enum Mode {
        VANILLA,
        NCP;

    }
}

