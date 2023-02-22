package com.alan.clients.module.impl.other;

import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.util.NetworkUtil;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.json.JSONArray;

import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ModuleInfo(name = "module.other.translator.name", description = "Translates your chat, might not work with some VPNs", category = Category.OTHER)
public class Translator extends Module {
    Executor translatorThread = Executors.newFixedThreadPool(1);

    private final ModeValue mode = new ModeValue("Mode", this)
            .add(new SubMode("Delay"))
            .add(new SubMode("Resend"))
            .setDefault("Delay");

    @EventLink
    public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
        Packet<?> p = event.getPacket();

        if (p instanceof S02PacketChat) {
            S02PacketChat wrapper = (S02PacketChat) p;

            IChatComponent component = wrapper.getChatComponent();
            String text = component.getFormattedText();

            if (text.contains("\n")) {
                return;
            }

            switch (this.mode.getValue().getName()) {
                case "Delay": {
                    event.setCancelled();

                    this.translatorThread.execute(() -> {
                        JSONArray array = new JSONArray(NetworkUtil.requestLine("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=" + URLEncoder.encode(text), "GET"));

                        String translated = array.getJSONArray(0).getJSONArray(0).getString(0);
                        ChatComponentText translatedComponent = new ChatComponentText(translated);
                        String language = new Locale(array.getString(2)).getDisplayLanguage(Locale.ENGLISH);

                        if (!translated.equals(text)) {
                            translatedComponent.appendText(" ");

                            ChatComponentText hoverComponent = new ChatComponentText(getTheme().getChatAccentColor() + "[T]");
                            ChatStyle style = new ChatStyle();
                            style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Translated from " + language + "\n" + text)));
                            hoverComponent.setChatStyle(style);

                            translatedComponent.appendSibling(hoverComponent);
                        }

                        mc.thePlayer.addChatMessage(translatedComponent);
                    });
                    break;
                }

                case "Resend": {
                    this.translatorThread.execute(() -> {
                        JSONArray array = new JSONArray(NetworkUtil.requestLine("https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=" + URLEncoder.encode(text), "GET"));

                        String translated = array.getJSONArray(0).getJSONArray(0).getString(0);
                        ChatComponentText translatedComponent = new ChatComponentText(translated);
                        String language = new Locale(array.getString(2)).getDisplayLanguage(Locale.ENGLISH);

                        if (!translated.equals(text)) {
                            translatedComponent.appendText(" ");

                            ChatComponentText hoverComponent = new ChatComponentText(getTheme().getChatAccentColor() + "[T]");
                            ChatStyle style = new ChatStyle();
                            style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Translated from " + language + "\n" + text)));
                            hoverComponent.setChatStyle(style);

                            translatedComponent.appendSibling(hoverComponent);
                        }

                        mc.thePlayer.addChatMessage(translatedComponent);
                    });
                    break;
                }
            }
        }
    };
}
