package crispy.features.hacks.impl.misc;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.InfiniteAura;
import crispy.features.hacks.impl.movement.Fly;
import crispy.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S02PacketChat;

@HackInfo(name = "SSMBotter", category = Category.MISC)
public class SsmBotter extends Hack {
    boolean useItem;
    boolean ClickSSMAgain;
    boolean isInSSM;
    int count = 0;
    TimeHelper afktimer = new TimeHelper();
    TimeHelper nojoinHelper = new TimeHelper();
    TimeHelper waitlilbiTimeHelper = new TimeHelper();
    TimeHelper abilityTimer = new TimeHelper();
    boolean useAbilities;

    @Override
    public void onEnable() {
        isInSSM = false;
        useItem = false;
        nojoinHelper.reset();
        ClickSSMAgain = false;
        useAbilities = false;
        abilityTimer.reset();
        afktimer.reset();
        waitlilbiTimeHelper.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if (nojoinHelper.hasReached(5000) && ClickSSMAgain) {
                nojoinHelper.reset();
                count = 0;
                isInSSM = false;
                useItem = false;
                ClickSSMAgain = false;
                useAbilities = false;
                Crispy.INSTANCE.getHackManager().getHack(InfiniteAura.class).setState(false);
                Crispy.INSTANCE.getHackManager().getHack(Fly.class).setState(false);
            }
            if (nojoinHelper.hasReached(5000) && isInSSM) {
                nojoinHelper.reset();
                count = 0;
                isInSSM = false;
                useItem = false;
                ClickSSMAgain = false;
                useAbilities = false;
                Crispy.INSTANCE.getHackManager().getHack(InfiniteAura.class).setState(false);
                Crispy.INSTANCE.getHackManager().getHack(Fly.class).setState(false);

            }

            if (!isInSSM) {
                if (!useItem) {
                    useItem = true;
                    mc.thePlayer.inventory.currentItem = 0;
                    if(mc.thePlayer.inventory.getCurrentItem() != null) {
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem());
                    }
                    //49
                }


                if (waitlilbiTimeHelper.hasReached(2000)) {
                    waitlilbiTimeHelper.reset();
                    if ((this.mc.thePlayer.openContainer != null) && ((this.mc.thePlayer.openContainer instanceof ContainerChest))) {
                        ContainerChest chest = (ContainerChest) this.mc.thePlayer.openContainer;
                        for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                            if (ClickSSMAgain && i == 11) {
                                this.mc.playerController.windowClick(chest.windowId, i, 0, 1, this.mc.thePlayer);

                            }
                            if (i == 50 && !ClickSSMAgain) {
                                ClickSSMAgain = true;
                                this.mc.playerController.windowClick(chest.windowId, i, 0, 1, this.mc.thePlayer);
                            }

                        }
                    }


                }
            }

        } else if (e instanceof EventPacket) {

            if (((EventPacket) e).getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = (S02PacketChat) ((EventPacket) e).getPacket();
                String message = packet.getChatComponent().getUnformattedText();
                if (message.contains("You have been sent from") && message.contains("SSM")) {
                    isInSSM = true;
                }
                if (message.contains("You will be kicked if you do not move in")) {
                    Crispy.INSTANCE.getHackManager().getHack(InfiniteAura.class).setState(false);
                    Crispy.INSTANCE.getHackManager().getHack(Fly.class).setState(false);
                    mc.thePlayer.motionX = 1;
                    mc.thePlayer.motionZ = 2;
                    mc.thePlayer.motionY = 1;

                    if (afktimer.hasReached(1000)) {
                        afktimer.reset();
                        Crispy.INSTANCE.getHackManager().getHack(InfiniteAura.class).setState(true);
                        Crispy.INSTANCE.getHackManager().getHack(Fly.class).setState(true);
                    }
                }

                if(message.contains("1st Place")) {

                    ClickSSMAgain = false;
                    useAbilities = false;
                    Crispy.INSTANCE.getHackManager().getHack(InfiniteAura.class).setState(false);
                    Crispy.INSTANCE.getHackManager().getHack(Fly.class).setState(false);

                }
                if(message.contains("CLICK HERE if you want to change your kit!")) {

                    mc.thePlayer.inventory.currentItem = 0;
                    abilityTimer.reset();
                    useAbilities = true;
                }


                if (message.contains("Get the new IMMORTAL rank today & get access to XP boosters,") || message.contains("This server is full and no longer accepts players.") || message.contains("You completed the Welcome Tutorial")) {
                    isInSSM = false;
                    count = 0;
                    nojoinHelper.reset();
                    useItem = false;
                    ClickSSMAgain = false;
                    useAbilities = false;
                    Crispy.INSTANCE.getHackManager().getHack(InfiniteAura.class).setState(false);
                    Crispy.INSTANCE.getHackManager().getHack(Fly.class).setState(false);
                }
                if(abilityTimer.hasReached(8000) && useAbilities) {
                    Crispy.INSTANCE.getHackManager().getHack(InfiniteAura.class).setState(true);
                    Crispy.INSTANCE.getHackManager().getHack(Fly.class).setState(true);
                }
            }
        }
    }
}
