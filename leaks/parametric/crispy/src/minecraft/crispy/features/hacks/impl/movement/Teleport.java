package crispy.features.hacks.impl.movement;

import crispy.features.event.Event;
import crispy.features.event.impl.render.Event3D;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.Aura;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.util.render.RenderUtils;
import crispy.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;


@HackInfo(name = "Teleport", category = Category.MOVEMENT)
public class Teleport extends Hack {
    ModeValue mode = new ModeValue("Teleport Mode", "Vanilla", "Vanilla");
    NumberValue<Integer> attempts = new NumberValue<>("Attempts", 5, 1, 20);
    private boolean rightClicked;
    private MovingObjectPosition object;
    private final TimeHelper wait = new TimeHelper();
    private final TimeHelper tpTimer = new TimeHelper();
    private Vec3 hitVec;
    private int count;

    @Override
    public void onEnable() {
        wait.reset();
        tpTimer.reset();
        count = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        rightClicked = false;
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
        } else if (e instanceof EventUpdate) {

            EventUpdate event = (EventUpdate) e;
            if (object != null) {

                if (mc.gameSettings.keyBindUseItem.pressed && wait.hasReached(1000)) {
                    rightClicked = true;
                    wait.reset();
                    NotificationPublisher.queue("Blink Teleport", "Attempting to teleport at " + object.getBlockPos() + " please wait.", NotificationType.INFO);

                    hitVec = object.hitVec;
                    count = 0;
                }

                if(tpTimer.hasReached(10)) {
                    tpTimer.reset();
                    count++;
                    if(count < attempts.getObject()) {
                        if(hitVec == null) return;
                        blinkToPosFromPos(mc.thePlayer.getVec(), hitVec, 4);
                    }
                }
            }
        } else if (e instanceof Event3D) {
            if (object != null) {
                final BlockPos blockPos = object.getBlockPos();

                double x = blockPos.getX() - RenderManager.renderPosX;
                double y = blockPos.getY() - RenderManager.renderPosY;
                double z = blockPos.getZ() - RenderManager.renderPosZ;

                RenderUtils.drawOutlinedBlockESP(x, y, z, 1, 1, 1, 1, 1);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            Vec3 entityLook = mc.thePlayer.getLookVec();
            final Vec3 lookVec = new Vec3(entityLook.getXCoord() * 300, entityLook.getYCoord() * 300, entityLook.getZCoord() * 300);
            final Vec3 posVec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + 1.62, mc.thePlayer.posZ);
            object = Minecraft.theWorld.rayTraceBlocks(posVec, posVec.add(lookVec), false, true, false);

        }
    }

    public void blinkToPosFromPos(Vec3 src, Vec3 dest, double maxTP) {

        double range = 0;
        Minecraft mc = Minecraft.getMinecraft();
        double xDist = src.xCoord - dest.xCoord;
        double yDist = src.yCoord - dest.yCoord;
        double zDist = src.zCoord - dest.zCoord;
        double x1 = src.xCoord;
        double y1 = src.yCoord;
        double z1 = src.zCoord;
        double x2 = dest.xCoord;
        double y2 = dest.yCoord;
        double z2 = dest.zCoord;
        range = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
        double step = maxTP / range;
        int steps = 0;
        for (int i = 0; i < range; i++) {
            steps++;
            if (maxTP * steps > range) {
                break;
            }
        }

        for (int i = 0; i < steps; i++) {
            double difX = x1 - x2;
            double difY = y1 - y2;
            double difZ = z1 - z2;
            double divider = step * i;
            double x = x1 - difX * divider;
            double y = y1 - difY * divider;
            double z = z1 - difZ * divider;

            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + Aura.randomNumber(1, -1), z, true));
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2 + Aura.randomNumber(1, -1), z2, true));
    }
}
