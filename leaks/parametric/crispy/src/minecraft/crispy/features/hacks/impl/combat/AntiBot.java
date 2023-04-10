package crispy.features.hacks.impl.combat;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.superblaubeere27.valuesystem.ModeValue;

import java.util.ArrayList;
import java.util.List;

@HackInfo(name = "AntiBot", category = Category.COMBAT)
public class AntiBot extends Hack {

    private static final List<Entity> invalid = new ArrayList<>();
    private static final List<Entity> removed = new ArrayList<>();
    TimeHelper timer = new TimeHelper();
    TimeHelper lastRemoved = new TimeHelper();


    ModeValue AntiBot = new ModeValue("AntiBot Mode", "Mineplex", "Mineplex", "Packet", "Hypixel", "Hypixel2", "XAC", "Skript");

    public static List<Entity> getInvalid() {
        return invalid;
    }

    @Override
    public void onDisable() {
        invalid.clear();
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            EventPacket event = (EventPacket) e;

            if (AntiBot.getObject() == 1 && event.getPacket() instanceof S0CPacketSpawnPlayer) {
                S0CPacketSpawnPlayer packet = (S0CPacketSpawnPlayer) event.getPacket();
                double posX = packet.getX() / 32D;
                double posY = packet.getY() / 32D;
                double posZ = packet.getZ() / 32D;

                double diffX = mc.thePlayer.posX - posX;
                double diffY = mc.thePlayer.posY - posY;
                double diffZ = mc.thePlayer.posZ - posZ;

                double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);

                if (dist <= 17D && posX != mc.thePlayer.posX && posY != mc.thePlayer.posY && posZ != mc.thePlayer.posZ) {
                    event.setCancelled(true);
                }
            }
        }
        if (e instanceof EventUpdate) {
            boolean killer = AntiBot.getObject() == 3;
            setDisplayName(getName() + " \2477" + AntiBot.getMode());

            if (AntiBot.getObject() == 4) {
                for (Object o : Minecraft.theWorld.getLoadedEntityList()) {
                    if (o instanceof EntityPlayer) {
                        EntityPlayer ent = (EntityPlayer) o;
                        if (ent.getDisplayName().getUnformattedText().contains("XAC")) {
                            invalid.add((Entity) o);
                        }
                    }
                }
            }

            if (AntiBot.getObject() == 5) {
                for (Object o : Minecraft.theWorld.getLoadedEntityList()) {
                    if (o instanceof EntityPlayer) {
                        EntityPlayer ent = (EntityPlayer) o;
                        if (ent.getDisplayName().getUnformattedText().contains("WatchCat")) {
                            invalid.add((Entity) o);
                        }
                    }
                }
            }
            if (AntiBot.getMode().equalsIgnoreCase("Skript")) {
                for (Object o : Minecraft.theWorld.loadedEntityList) {
                    if (o instanceof Entity) {
                        Entity entity = (Entity) o;
                        if (entity.getCommandSenderName().contains("Don't Hit Me")) {
                            if (!invalid.contains(entity)) {
                                invalid.add(entity);
                            }
                        }
                        if (entity.getCommandSenderName().contains("CIT")) {
                            if (!invalid.contains(entity)) {
                                invalid.add(entity);
                            }
                        }
                    }
                }
            }


            if (AntiBot.getObject() == 3) {
                if (killer) {
                    if (!removed.isEmpty()) {
                        if (lastRemoved.hasReached(1000)) {
                            if (removed.size() == 1) {
                            } else {
                            }
                            lastRemoved.reset();
                            removed.clear();
                        }
                    }
                }
                if (!invalid.isEmpty() && timer.hasReached(1000)) {
                    invalid.clear();
                    timer.reset();
                }
                if (AntiBot.getObject() == 3) {
                    for (Object o : Minecraft.theWorld.getLoadedEntityList()) {
                        if (o instanceof EntityPlayer) {
                            EntityPlayer ent = (EntityPlayer) o;
                            if (ent != mc.thePlayer && !invalid.contains(ent)) {
                                String formated = ent.getDisplayName().getFormattedText();
                                String custom = ent.getCustomNameTag();
                                String name = ent.getName();
                                if (ent.isInvisible() && !formated.startsWith("�c") && formated.endsWith("�r") && custom.equals(name)) {
                                    double diffX = Math.abs(ent.posX - mc.thePlayer.posX);
                                    double diffY = Math.abs(ent.posY - mc.thePlayer.posY);
                                    double diffZ = Math.abs(ent.posZ - mc.thePlayer.posZ);
                                    double diffH = Math.sqrt(diffX * diffX + diffZ * diffZ);
                                    if (diffY < 13 && diffY > 10 && diffH < 3) {
                                        List<EntityPlayer> list = getTabPlayerList();
                                        if (!list.contains(ent)) {
                                            if (killer) {
                                                lastRemoved.reset();
                                                removed.add(ent);
                                                Minecraft.theWorld.removeEntity(ent);
                                            }
                                            System.out.println("test");
                                            invalid.add(ent);
                                        }
                                    }

                                }
                                if (!formated.startsWith("�") && formated.endsWith("�r")) {
                                    invalid.add(ent);
                                }
                                if (ent.isInvisible()) {
                                    if (!custom.equalsIgnoreCase("") && custom.toLowerCase().contains("�c�c") && name.contains("�c")) {


                                        if (killer) {
                                            lastRemoved.reset();
                                            removed.add(ent);

                                        }

                                        invalid.add(ent);
                                    }
                                }
                                //WATCHDOG BOT
                                if (!custom.equalsIgnoreCase("") && custom.toLowerCase().contains("�c") && custom.toLowerCase().contains("�r")) {
                                    if (killer) {
                                        lastRemoved.reset();
                                        removed.add(ent);
                                        Minecraft.theWorld.removeEntity(ent);
                                    }
                                    invalid.add(ent);
                                }

                                //BOT LOBBY
                                if (formated.contains("�8[NPC]")) {
                                    invalid.add(ent);
                                }
                                if (!formated.contains("�c") && !custom.equalsIgnoreCase("")) {

                                    invalid.add(ent);
                                }
                            }
                        }

                    }
                }
            }

            if (AntiBot.getObject() == 0 && mc.thePlayer.ticksExisted > 40) {

                for (Object o : Minecraft.theWorld.loadedEntityList) {
                    Entity en = (Entity) o;
                    if (en instanceof EntityPlayer && !(en instanceof EntityPlayerSP)) {
                        int ticks = en.ticksExisted;
                        double diffY = Math.abs(mc.thePlayer.posY - en.posY);
                        String name = en.getName();
                        String customname = en.getCustomNameTag();
                        if (customname == "" && !invalid.contains(en)) {
                            invalid.add(en);

                        }
                    }


                }


            }

            if (AntiBot.getObject() == 2 || AntiBot.getObject() == 0)
                for (Object entity : Minecraft.theWorld.loadedEntityList)
                    if (((Entity) entity).isInvisible() && entity != mc.thePlayer)
                        Minecraft.theWorld.removeEntity((Entity) entity);

        }
    }

    public ArrayList<EntityPlayer> getTabPlayerList() {
        final NetHandlerPlayClient var4 = mc.thePlayer.sendQueue;
        final ArrayList<EntityPlayer> list = new ArrayList<>();
        final java.util.List players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(var4.getPlayerInfoMap());
        for (final Object o : players) {
            final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
            if (info == null) {
                continue;
            }
            list.add(Minecraft.theWorld.getPlayerEntityByName(info.getGameProfile().getName()));
        }
        return list;
    }


}
