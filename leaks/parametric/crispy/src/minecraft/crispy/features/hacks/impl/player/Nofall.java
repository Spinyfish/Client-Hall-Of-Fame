package crispy.features.hacks.impl.player;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.movement.Fly;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.superblaubeere27.valuesystem.ModeValue;

@HackInfo(name = "Nofall", category = Category.PLAYER)
public class Nofall extends Hack {
    ModeValue Nofall = new ModeValue("Nofall Mode", "GroundSp00f", "GroundSp00f", "Verus", "OldHypixel", "ACR FLAG", "ACR Bypass", "ACR Bypass2", "Dev1");

    boolean jumped;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            EventUpdate e = (EventUpdate) event;
            setDisplayName(getName() + "\2477 " + Nofall.getModes()[Nofall.getObject()]);
            if (Nofall.getObject() == 0 && mc.thePlayer.fallDistance > 3.25) {
                e.ground(true);
            }
            if (Nofall.getObject() == 1) {
                if (this.mc.thePlayer.fallDistance > 0 && !Crispy.INSTANCE.getHackManager().getHack(Fly.class).isEnabled()) {
                    {
                        if (mc.thePlayer.ticksExisted % 10 == 0) {
                            mc.thePlayer.motionY = 0;
                            e.ground = true;
                        }
                    }
                }
            }

            if (Nofall.getObject() == 2) {
                if (this.mc.thePlayer.fallDistance > 3.25 && this.isBlockUnder()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                }
            }


            if (Nofall.getObject() == 3) {
                if (this.mc.thePlayer.fallDistance > 3.25 && this.isBlockUnder()) {
                    if (mc.thePlayer.ticksExisted % 5.5 == 0) {

                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));


                    } else {

                        mc.thePlayer.motionY = -0.5;

                    }
                }
            }


            if (Nofall.getObject() == 4) {
                if (mc.thePlayer.ticksExisted % 2 == 0 && mc.thePlayer.fallDistance > 3.25f && this.isBlockUnder()) {
                    e.ground(true);
                }

            }

            if (Nofall.getObject() == 5) {
                if (mc.thePlayer.ticksExisted % 5 == 0 && mc.thePlayer.fallDistance > 3.25f && this.isBlockUnder()) {
                    e.ground(true);
                }

            }


            if (Nofall.getObject() == 6) {
                if (mc.thePlayer.ticksExisted % 5 == 0 && mc.thePlayer.fallDistance > 3.25f && this.isBlockUnder()) {
                    Crispy.addChatMessage("test");
                    if (mc.thePlayer.onGround) {
                        jumped = true;
                        e.sneak = true;
                    }
                    if (!jumped)
                        return;
                    if (mc.thePlayer.ticksExisted % 5 == 0 && mc.thePlayer.fallDistance > 1.25f && this.isBlockUnder()) {

                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.1, mc.thePlayer.posZ, true));

                    }

                    if (mc.thePlayer.ticksExisted % 2 != 0 && !mc.thePlayer.onGround) {
                        e.sneak = false;
                    } else if (!mc.thePlayer.onGround) {
                        e.sneak = true;
                        e.ground = true;

                    }
                }
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
