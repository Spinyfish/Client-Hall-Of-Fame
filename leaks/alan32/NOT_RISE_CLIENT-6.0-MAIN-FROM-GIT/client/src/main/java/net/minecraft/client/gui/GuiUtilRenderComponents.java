package net.minecraft.client.gui;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class GuiUtilRenderComponents {
    public static String func_178909_a(final String p_178909_0_, final boolean p_178909_1_) {
        return !p_178909_1_ && !Minecraft.getMinecraft().gameSettings.chatColours ? EnumChatFormatting.getTextWithoutFormattingCodes(p_178909_0_) : p_178909_0_;
    }

    public static List<IChatComponent> func_178908_a(final IChatComponent p_178908_0_, final int p_178908_1_, final FontRenderer p_178908_2_, final boolean p_178908_3_, final boolean p_178908_4_) {
        int i = 0;
        IChatComponent ichatcomponent = new ChatComponentText("");
        final List<IChatComponent> list = Lists.newArrayList();
        final List<IChatComponent> list1 = Lists.newArrayList(p_178908_0_);

        for (int j = 0; j < list1.size(); ++j) {
            final IChatComponent ichatcomponent1 = list1.get(j);
            String s = ichatcomponent1.getUnformattedTextForChat();
            boolean flag = false;

            if (s.contains("\n")) {
                final int k = s.indexOf(10);
                final String s1 = s.substring(k + 1);
                s = s.substring(0, k + 1);
                final ChatComponentText chatcomponenttext = new ChatComponentText(s1);
                chatcomponenttext.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                list1.add(j + 1, chatcomponenttext);
                flag = true;
            }

            final String s4 = func_178909_a(ichatcomponent1.getChatStyle().getFormattingCode() + s, p_178908_4_);
            final String s5 = s4.endsWith("\n") ? s4.substring(0, s4.length() - 1) : s4;
            int i1 = p_178908_2_.width(s5);
            ChatComponentText chatcomponenttext1 = new ChatComponentText(s5);
            chatcomponenttext1.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());

            if (i + i1 > p_178908_1_) {
                String s2 = p_178908_2_.trimStringToWidth(s4, p_178908_1_ - i, false);
                String s3 = s2.length() < s4.length() ? s4.substring(s2.length()) : null;

                if (s3 != null && s3.length() > 0) {
                    int l = s2.lastIndexOf(" ");

                    if (l >= 0 && p_178908_2_.width(s4.substring(0, l)) > 0) {
                        s2 = s4.substring(0, l);

                        if (p_178908_3_) {
                            ++l;
                        }

                        s3 = s4.substring(l);
                    } else if (i > 0 && !s4.contains(" ")) {
                        s2 = "";
                        s3 = s4;
                    }

                    final ChatComponentText chatcomponenttext2 = new ChatComponentText(s3);
                    chatcomponenttext2.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                    list1.add(j + 1, chatcomponenttext2);
                }

                i1 = p_178908_2_.width(s2);
                chatcomponenttext1 = new ChatComponentText(s2);
                chatcomponenttext1.setChatStyle(ichatcomponent1.getChatStyle().createShallowCopy());
                flag = true;
            }

            if (i + i1 <= p_178908_1_) {
                i += i1;
                ichatcomponent.appendSibling(chatcomponenttext1);
            } else {
                flag = true;
            }

            if (flag) {
                list.add(ichatcomponent);
                i = 0;
                ichatcomponent = new ChatComponentText("");
            }
        }

        list.add(ichatcomponent);
        return list;
    }
}
