package dev.echo.module.impl.movement;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.listener.event.impl.network.PacketReceiveEvent;
import dev.echo.listener.event.impl.player.BoundingBoxEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.utils.player.AlwaysUtil;
import dev.echo.utils.player.ChatUtil;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class ClickTP extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Vulcan", "Vanilla", "Vulcan");
    private final TimerUtil timeHelper = new TimerUtil();
    private double[] xyz = new double[3];
    private boolean shouldTeleport;
    private boolean teleported = false;
    private int funny = 0;

    public ClickTP() {
        super("ClickTP", Category.MOVEMENT, "Teleports you");
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        funny = 0;
        this.xyz = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        this.shouldTeleport = false;
        this.teleported = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.xyz = new double[3];
        this.shouldTeleport = false;
        super.onDisable();
    }

    @Link
    public Listener<PacketReceiveEvent> onPacketReceiveEvent = e -> {
        if (!AlwaysUtil.isPlayerInGame()) {
            toggle();
            return;
        }
        if (mode.is("Vulcan")) {
            if (funny <= 80) {
                if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                    e.setCancelled(true);
                }
            } else {
                toggle();
            }
        }
    };
    @Link
    public Listener<BoundingBoxEvent> onBoundingBoxEvent = event -> {
        final AxisAlignedBB axisAlignedBB = AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1, 5).offset(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
        event.setBoundingBox(axisAlignedBB);
    };

    @Link
    public Listener<TickEvent> tickEventListener = (event) -> {
        setSuffix(mode.getMode());
        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.xyz[0] == Double.POSITIVE_INFINITY) {
            final BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            final Block block = mc.theWorld.getBlockState(blockPos).getBlock();
            this.xyz = new double[]{mc.objectMouseOver.getBlockPos().getX() + 0.5, mc.objectMouseOver.getBlockPos().getY() + block.getBlockBoundsMaxY(), mc.objectMouseOver.getBlockPos().getZ() + 0.5};
            this.shouldTeleport = true;
            this.timeHelper.reset();
            funny = 0;
        }

        if (this.shouldTeleport) {
            mc.thePlayer.setPosition(this.xyz[0], this.xyz[1] + 0.3, this.xyz[2]);
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.xyz[0], this.xyz[1] + 0.3, this.xyz[2], false));
            this.shouldTeleport = false;
            this.teleported = true;
        }
        if (teleported) {
            funny++;
        }
    };
}
