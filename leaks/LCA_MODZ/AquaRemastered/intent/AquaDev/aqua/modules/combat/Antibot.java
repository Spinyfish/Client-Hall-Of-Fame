package intent.AquaDev.aqua.modules.combat;

import de.Hero.settings.Setting;
import events.Event;
import events.listeners.EventPacket;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

public class Antibot extends Module {
   public static ArrayList<Entity> bots = new ArrayList<>();

   public Antibot() {
      super("Antibot", Module.Type.Combat, "Antibot", 0, Category.Combat);
      Aqua.setmgr.register(new Setting("Watchdog", this, true));
      Aqua.setmgr.register(new Setting("Matrix", this, false));
   }

   @Override
   public void onEnable() {
      bots.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      bots.clear();
      super.onDisable();
   }

   @Override
   public void onEvent(Event e) {
      if (e instanceof EventPacket) {
         Packet packet = EventPacket.getPacket();
         if (packet instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat)packet;
            String cp21 = s02PacketChat.getChatComponent().getUnformattedText();
            if (cp21.contains("Cages opened! FIGHT!")) {
               bots.clear();
            }
         }

         if (Aqua.setmgr.getSetting("AntibotMatrix").isState()) {
            if (mc.thePlayer.ticksExisted > 110) {
               for(Entity entity : mc.theWorld.loadedEntityList) {
                  if (entity instanceof EntityPlayer && entity != mc.thePlayer && entity.getCustomNameTag() == "" && !bots.contains((EntityPlayer)entity)) {
                     bots.add(entity);
                  }
               }
            } else {
               bots = new ArrayList<>();
            }
         }
      }
   }

   boolean isBot(EntityPlayer player) {
      return !this.isInTablist(player) ? true : this.invalidName(player);
   }

   boolean isInTablist(EntityPlayer player) {
      if (Minecraft.getMinecraft().isSingleplayer()) {
         return false;
      } else {
         for(NetworkPlayerInfo playerInfo : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
            if (playerInfo.getGameProfile().getName().equalsIgnoreCase(player.getName())) {
               return true;
            }
         }

         return false;
      }
   }

   boolean invalidName(Entity e) {
      if (e.getName().contains("-")) {
         return true;
      } else if (e.getName().contains("/")) {
         return true;
      } else if (e.getName().contains("|")) {
         return true;
      } else if (e.getName().contains("<")) {
         return true;
      } else {
         return e.getName().contains(">") ? true : e.getName().contains("ยง");
      }
   }
}
