package wtf.automn.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import wtf.automn.Automn;

public final class PlayerUtil {


    public static void verusDMG(){
        Minecraft mc = Minecraft.getMinecraft();
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                mc.thePlayer.posY, mc.thePlayer.posZ, false));
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
                mc.thePlayer.posY, mc.thePlayer.posZ, true));

    }
    public static void verusFlyCheckDisabler(){
        Minecraft mc = Minecraft.getMinecraft();
        sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, new ItemStack(Items.water_bucket), 0, 0.5f, 0));
        sendPacketUnlogged(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.5, mc.thePlayer.posZ), 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, new BlockPos(-1, -1, -1))), 0, 0.94f, 0));

    }
    public static void sendPacketUnlogged(Packet<? extends INetHandler> packet) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getNetHandler().getNetworkManager().sendPacket(packet);
    }
}