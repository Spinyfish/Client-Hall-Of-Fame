package frapppyz.cutefurry.pics.commands;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.commands.impl.Bind;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class CommandMain {

    public static void onCommand(String cmd){

        if(cmd.startsWith("bind ")){
            Bind.onCommand(cmd.replace("bind ", ""));
        }else if(cmd.equals("binds")){ //yes this is fucking retarded yes i am too lazy for bind system
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind killaura r");
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind manager h");
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind stealer y");
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind disabler l");
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind speed b");
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind scaffold f");
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind fly z");
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind velocity v");
            Minecraft.getMinecraft().thePlayer.sendChatMessage(".bind blink c");
        }else if(cmd.startsWith("reload")){
            Wrapper.getLogger().info("Reloading all client mod classes...");
            Wrapper.getModManager().mods.clear();
            Wrapper.getLogger().info("Loading mod manager...");
            Wrapper.getModManager().addMods();
            Wrapper.getLogger().info("Loaded mod manager...");
            Wrapper.getLogger().addChat("Reloaded client!");
            Category.COMBAT.setX(40);
            Category.MOVE.setX(150);
            Category.PLAYER.setX(260);
            Category.WORLD.setX(370);
            Category.RENDER.setX(480);
            Category.EXPLOIT.setX(590);
            for(Category c : Category.values()){
                c.setY(50);
            }

        }else if(cmd.equalsIgnoreCase("kill")){
            Wrapper.getLogger().addChat("uh");
            for(int i = 0; i < 60; i++){
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + 0.42f, Minecraft.getMinecraft().thePlayer.posZ, false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, false));
            }
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
        }
    }
}
