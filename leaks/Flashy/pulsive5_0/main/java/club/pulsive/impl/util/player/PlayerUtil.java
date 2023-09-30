package club.pulsive.impl.util.player;

import club.pulsive.api.main.Pulsive;
import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.networking.user.User;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.world.WorldUtil;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class PlayerUtil implements MinecraftUtil {

    public final Map<String, Boolean> serverResponses = new HashMap<>();


    @AllArgsConstructor
    public enum TARGETS {
        PLAYERS("Players"),
        MOBS("Mobs"),
        ANIMALS("Animals"),
        INVISIBLE("Invisibles"),
        DEAD("Ded XD"),
        TEAMS("Teams");

        private final String addonName;

        @Override
        public String toString() {return addonName;}
    }

    public String getIrcChatColor(User user) {
        String chatColor;
        switch (user.getRank()) {
            case "Developer": {
                chatColor = EnumChatFormatting.DARK_RED.toString();
                break;
            }
            case "Admin": {
                chatColor = EnumChatFormatting.RED.toString();
                break;
            }
            case "Alpha": {
                chatColor = EnumChatFormatting.YELLOW.toString();
                break;
            }
            default: {
                chatColor = EnumChatFormatting.GRAY.toString();
                break;
            }
        }
        return chatColor;
    }
    public void damage() {
        if(mc.thePlayer.onGround) {
            for(int var1 = 0; var1 < 5; ++var1) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
            }

            for(double var3 = 3.2125D; var3 > 0.0D; var3 -= 0.0544986421D) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0624986421D, mc.thePlayer.posZ, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625D, mc.thePlayer.posZ, false));

                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX+ 1.3579E-6D, mc.thePlayer.posY, mc.thePlayer.posZ+ 1.3579E-6D, false));
            }

            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        }


    }

    public boolean isOnServer(final String server) {
//        if (serverResponses.containsKey(server))
//            return serverResponses.get(server);
        String ip = Pulsive.INSTANCE.getIp();

        if (ip == null) {
            serverResponses.put(server, false);
            return false;
        }

        if(server.equals("karhu")){
            final boolean result = ip.contains("karhu.cc");
            serverResponses.put("karhu", result);
            return result;
        }

        if (server.equals("hypixel")) {
            final boolean result = ip.contains("hypixel.net") && !ip.contains("ruhypixel.net") || ip.contains("2606:4700::6810:4e15");
            serverResponses.put("hypixel", result);
            return result;
        }

        if(server.equals("vulcan")){
            final boolean result = ip.contains("mc.stitch.best") || ip.contains("anticheat.ir") || ip.contains("mc.minebox.es") || ip.contains("play.battleasya.com") || ip.contains("0.tcp.au.ngrok.io:12624") || ip.contains("0.tcp.au.ngrok.io:14328");
            serverResponses.put("vulcan", result);
            return result;
        }

        final boolean result = ip.contains(server);
        serverResponses.put(server, result);
        return result;
    }

    public Integer findItem(final Item item) {
        for (int i = 0; i < 9; i++) {
            final ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || itemStack.getItem() == null) {
                if (item == null) return i;
                continue;
            }

            if (itemStack.getItem() == item) {
                return i;
            }
        }

        return null;
    }
    
    public boolean isInLiquid(){
        return mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
    }

    public boolean isHoldingSword() {
        return mc.thePlayer != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public double getEffectiveHealth(EntityLivingBase entity) {
        return entity.getHealth() * (entity.getMaxHealth() / entity.getTotalArmorValue());
    }
    
    public <T extends MultiSelectEnumProperty<TARGETS>> boolean isValid(EntityLivingBase entityLivingBase, T targetsProperty) {
        if (entityLivingBase == null) return false;
        if (entityLivingBase.isInvisible()) return targetsProperty.isSelected(TARGETS.INVISIBLE);
        if (!entityLivingBase.isEntityAlive()) return targetsProperty.isSelected(TARGETS.DEAD);
        if (entityLivingBase instanceof EntityOtherPlayerMP) {
            final EntityPlayer player = (EntityPlayer) entityLivingBase;
            if (PlayerUtil.isTeammate(player)) return targetsProperty.isSelected(TARGETS.TEAMS);
            return WorldUtil.checkPing(player);
        } else if (entityLivingBase instanceof EntityMob) {
            return targetsProperty.isSelected(TARGETS.MOBS);
        } else if (entityLivingBase instanceof EntityAnimal) {
            return targetsProperty.isSelected(TARGETS.ANIMALS);
        } else {
            return false;
        }
    }

    public boolean isTeammate(EntityPlayer entityPlayer) {
        if (entityPlayer != null) {
            String text = entityPlayer.getDisplayName().getFormattedText();
            String playerText = mc.thePlayer.getDisplayName().getFormattedText();
            if (text.length() < 2 || playerText.length() < 2) return false;
            if (!text.startsWith("\u00A7") || !playerText.startsWith("\u00A7")) return false;
            return text.charAt(1) == playerText.charAt(1);
        }
        return false;
    }
    public boolean isBlockUnder() {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, boundingBox).isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
