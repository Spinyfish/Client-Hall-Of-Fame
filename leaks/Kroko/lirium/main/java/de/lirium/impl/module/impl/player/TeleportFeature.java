package de.lirium.impl.module.impl.player;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.math.RaytraceUtil;
import god.buddy.aot.BCompiler;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

@ModuleFeature.Info(name = "Teleport", description = "Get the ability to teleport like an Enderman", category = ModuleFeature.Category.PLAYER)
public class TeleportFeature extends ModuleFeature {

    @Value(name = "Mode")
    final ComboBox<String> mode = new ComboBox<>("Vanilla", new String[]{"Sentinel"});

    private ArrayList<Vec3d> path = null;
    private TimeHelper timeHelper = new TimeHelper();

    @EventHandler
    public final Listener<UpdateEvent> updateEvent = e -> {
        setSuffix(mode.getValue());
        switch (mode.getValue()) {
            case "Vanilla":
                if (mc.gameSettings.keyBindUseItem.pressed) {
                    final Vec3d vec3 = getPlayer().getPositionEyes(1.0f);
                    final Vec3d vec31 = RaytraceUtil.getVectorForRotation(getPitch(), getYaw());
                    final Vec3d vec32 = vec3.addVector(vec31.xCoord * 100.0, vec31.yCoord * 100.0, vec31.zCoord * 100.0);
                    final RayTraceResult result = getWorld().rayTraceBlocks(vec3, vec32, false, false, true);
                    if (result == null || getWorld().isAirBlock(result.getBlockPos())) break;
                    final Vec3d pos = new Vec3d(result.getBlockPos().getX() + 0.5, result.getBlockPos().getY() + 1, result.getBlockPos().getZ() + 0.5);
                    path = new ArrayList<>();
                    path.add(pos);
                    mc.gameSettings.keyBindUseItem.pressed = false;
                    path = calculatePath(getPlayer().getPositionVector(),
                            new Vec3d(result.getBlockPos().getX() + 0.5, result.getBlockPos().getY() + 1, result.getBlockPos().getZ() + 0.5), 10);
                    for (Vec3d vec3d : path)
                        sendPacket(new CPacketPlayer.Position(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord, true));
                    setPosition(result.getBlockPos().getX() + 0.5, result.getBlockPos().getY() + 1, result.getBlockPos().getZ() + 0.5);
                }
                break;
            case "Sentinel":
                if (mc.gameSettings.keyBindAttack.pressed && path == null) {
                    final Vec3d vec3 = getPlayer().getPositionEyes(1.0f);
                    final Vec3d vec31 = RaytraceUtil.getVectorForRotation(getPitch(), getYaw());
                    final Vec3d vec32 = vec3.addVector(vec31.xCoord * 100.0, vec31.yCoord * 100.0, vec31.zCoord * 100.0);
                    final RayTraceResult result = getWorld().rayTraceBlocks(vec3, vec32, false, false, true);
                    if (result == null || getWorld().isAirBlock(result.getBlockPos())) break;
                    sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY() + 3.001D, getZ(), false));
                    sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), false));
                    sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), true));
                    setPosition(getX(), getY() + 1, getZ());
                    path = calculatePath(getPlayer().getPositionVector(),
                            new Vec3d(result.getBlockPos().getX() + 0.5, result.getBlockPos().getY() + 1, result.getBlockPos().getZ() + 0.5), 0.25);
                    mc.gameSettings.keyBindAttack.pressed = false;
                }
                if (path != null && !path.isEmpty()) {
                    int count = 0;
                    while (!path.isEmpty() && count++ < 15) {
                        final Vec3d vec3d = path.remove(0);
                        sendPacketUnlogged(new CPacketPlayer.Position(vec3d.xCoord, (int)vec3d.yCoord, vec3d.zCoord, true));
                        setPosition(vec3d.xCoord, (int)vec3d.yCoord, vec3d.zCoord);
                    }
                }
                if (path != null && !path.isEmpty())
                    getPlayer().motionX = getPlayer().motionY = getPlayer().motionZ = 0;
                if (path != null && path.isEmpty())
                    path = null;
                break;
        }
    };

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private ArrayList<Vec3d> calculatePath(final Vec3d start, final Vec3d end, final double offset) {
        final ArrayList<Vec3d> path = new ArrayList<>();
        final double dist = Math.ceil(start.distanceTo(end) / offset);
        for (int i = 1; i < dist; i++) {
            final double diffX = end.xCoord - start.xCoord,
                    diffY = end.yCoord - start.yCoord,
                    diffZ = end.zCoord - start.zCoord;
            path.add(new Vec3d(start.xCoord + diffX * (i / dist), start.yCoord + diffY * (i / dist), start.zCoord + diffZ * (i / dist)));
        }
        return path;
    }
}