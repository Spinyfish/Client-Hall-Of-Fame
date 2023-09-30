package Reality.Realii.mods.modules.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.input.Keyboard;
import Reality.Realii.Client;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.misc.EventKey;
import Reality.Realii.event.events.rendering.EventRender2D;
import Reality.Realii.event.events.world.EventPacketRecieve;
import Reality.Realii.event.events.world.EventPacketSend;
import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.utils.cheats.player.Helper;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class AntiAim extends Module {
    public ArrayList<Packet> packets = new ArrayList<Packet>();
    private boolean save = false;
    private EntityOtherPlayerMP blinkEntity;
    private boolean hasEnabled = false;

    public AntiAim() {
        super("FakeLag", ModuleType.Misc);
    }


    @Override
    public void onEnable() {
        super.onEnable();
        if (this.mc.thePlayer == null) {
            return;
        }
        packets.clear();
        this.blinkEntity = new EntityOtherPlayerMP(this.mc.theWorld, new GameProfile(new UUID(69L, 96L), "You"));
        this.blinkEntity.inventory = this.mc.thePlayer.inventory;
        this.blinkEntity.inventoryContainer = this.mc.thePlayer.inventoryContainer;
        this.blinkEntity.setPositionAndRotation(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ, this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch);
        this.blinkEntity.rotationYawHead = this.mc.thePlayer.rotationYawHead;
        this.mc.theWorld.addEntityToWorld(this.blinkEntity.getEntityId(), this.blinkEntity);
        hasEnabled = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.packets.clear();
        if(hasEnabled) {
            this.mc.theWorld.removeEntityFromWorld(this.blinkEntity.getEntityId());
        }

    }

    @EventHandler
    public void onUpdate(EventPostUpdate e) {
        if(!hasEnabled) {
            this.setEnabled(false);
        }
    }

    @EventHandler
    public void onPacket(EventPacketRecieve e) {

    }

    @EventHandler
    public void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer && packets.size() < 40) {
            packets.add(e.getPacket());
        }


        if (e.getPacket() instanceof C03PacketPlayer) {
            e.setCancelled(true);
            mc.getNetHandler().addToSendQueueWithoutEvent(packets.get(0));
            if (packets.size() >= 10) {
                if (((C03PacketPlayer) packets.get(0)).y != 0) {
                    if (blinkEntity.getDistanceToEntity(mc.thePlayer) > 3) {
                        this.blinkEntity.setPositionAndRotation(((C03PacketPlayer) packets.get(0)).x, ((C03PacketPlayer) packets.get(0)).y, ((C03PacketPlayer) packets.get(0)).z, ((C03PacketPlayer) packets.get(0)).yaw, ((C03PacketPlayer) packets.get(0)).pitch);
                    }
                }
            }
            if (packets.size() == 40) {
                save = false;

            }
            if (!save) {
                if (packets.size() >= 1) {
//                    mc.thePlayer.posX = ((C03PacketPlayer) packets.get(0)).x;
//                    mc.thePlayer.posY = ((C03PacketPlayer) packets.get(0)).y;
//                    mc.thePlayer.posZ = ((C03PacketPlayer) packets.get(0)).z;
                    packets.remove(0);

                }
            }
        }

    }

    @EventHandler
    public void onRender(EventRender2D e) {
        ScaledResolution sr = new ScaledResolution(mc);
        Client.fontLoaders.msFont18.drawCenteredString("æ‚¨æ­£åœ¨å­˜å‚¨åŒ…ï¼� " + packets.size() + "/40", sr.getScaledWidth() / 2, 140, save ? new Color(255, 0, 0).getRGB() : -1);
        RenderUtil.drawRect(sr.getScaledWidth() / 2 - 20, 150, sr.getScaledWidth() / 2 + 20, 152, new Color(200, 200, 200).getRGB());
        RenderUtil.drawRect(sr.getScaledWidth() / 2 - 20, 150, sr.getScaledWidth() / 2 - 20 + 40f * (packets.size() / 40f), 152, new Color(255, 255, 255).getRGB());


    }

    @EventHandler
    public void onKey(EventKey e) {
        if (e.getKey() == Keyboard.KEY_O) {
            save = !save;
        }
        if (e.getKey() == Keyboard.KEY_L) {
            Helper.sendMessage(ChatFormatting.RED + "(AntiAim)backed!");
            mc.thePlayer.setPosition(((C03PacketPlayer) packets.get(0)).x, ((C03PacketPlayer) packets.get(0)).y, ((C03PacketPlayer) packets.get(0)).z);
            packets.clear();
            this.mc.renderGlobal.loadRenderers();
        }
    }

    public ArrayList<Packet> getPackets() {
        return packets;
    }

    public void setPackets(ArrayList<Packet> packets) {
        this.packets = packets;
    }
}
