package crispy.features.hacks.impl.misc;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.movement.Fly;
import crispy.features.hacks.impl.movement.LongJump;
import crispy.util.player.SpeedUtils;
import crispy.util.time.TimeHelper;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;

import java.util.ArrayList;
import java.util.Collections;

@HackInfo(name = "AntiVoid", category = Category.MISC)
public class AntiVoid extends Hack {
    ModeValue mode = new ModeValue("Mode", "Predict", "Predict");
    private final ArrayList<Packet> packets = new ArrayList<>();
    private double flyHeight;
    @Getter
    @Setter
    private boolean flagged;

    @Override
    public void onEnable() {
        packets.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        packets.clear();
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();

            if (packet instanceof C03PacketPlayer) {
                if (!(flyHeight > 40) || !(mc.thePlayer.fallDistance > 0) || Crispy.INSTANCE.getHackManager().getHack(Fly.class).isEnabled() || Crispy.INSTANCE.getHackManager().getHack(LongJump.class).isEnabled() || isFlagged()) {

                    packets.add(packet);
                    if (packets.size() > 10) {
                        packets.remove(0);
                    }
                } else if (mc.thePlayer.fallDistance < 2) {
                    e.setCancelled(true);
                }

            }

        } else if (e instanceof EventUpdate) {
            setDisplayName(getName() + " \2477" + mode.getMode());
            updateFlyHeight();
            if (SpeedUtils.isOnGround(0.05)) {
                setFlagged(false);
            }
            if (flyHeight > 40 && mc.thePlayer.fallDistance > 0 && !Crispy.INSTANCE.getHackManager().getHack(Fly.class).isEnabled() && !Crispy.INSTANCE.getHackManager().getHack(LongJump.class).isEnabled() && !packets.isEmpty() && !isFlagged()) {


                Collections.reverse(packets);
                for (Packet packet : packets) {
                    mc.thePlayer.sendQueue.addToSendNoEvent(packet);
                }

                packets.clear();


            }
        }
    }

    public void updateFlyHeight() {
        double h = 1.0D;
        AxisAlignedBB box = mc.thePlayer.getEntityBoundingBox().expand(0.0625D, 0.0625D, 0.0625D);

        for (this.flyHeight = 0.0D; this.flyHeight < mc.thePlayer.posY; this.flyHeight += h) {
            AxisAlignedBB nextBox = box.offset(0.0D, -this.flyHeight, 0.0D);
            if (Minecraft.theWorld.checkBlockCollision(nextBox)) {
                if (h < 0.0625D) {
                    break;
                }

                this.flyHeight -= h;
                h /= 2.0D;
            }
        }

    }
    public boolean isBlockUnder() {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

            if (!Minecraft.theWorld.getCollidingBoundingBoxes(mc.thePlayer, boundingBox).isEmpty())
                return true;
        }
        return false;
    }
}
