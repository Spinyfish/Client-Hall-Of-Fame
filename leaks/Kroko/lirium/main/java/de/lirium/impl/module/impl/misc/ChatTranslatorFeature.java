/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 13.12.2022
 */
package de.lirium.impl.module.impl.misc;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import com.mojang.realmsclient.gui.ChatFormatting;
import de.lirium.impl.events.ChatRenderEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.misc.TranslatorUtil;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Mouse;

import java.util.HashMap;
import java.util.Map;

@ModuleFeature.Info(name = "Chat Translator", description = "Translates your chat", category = ModuleFeature.Category.MISCELLANEOUS)
public class ChatTranslatorFeature extends ModuleFeature {
    private final Map<ChatLine, Tuple<String, String>> translations = new HashMap<>();
    private final Map<ChatLine, ITextComponent> textComponentMap = new HashMap<>();

    @EventHandler
    public final Listener<ChatRenderEvent> eventListener = e -> {
        if (!(mc.currentScreen instanceof GuiChat)) return;

        if (e.mouseX >= e.left && e.mouseX <= e.right && e.mouseY >= e.top && e.mouseY <= e.bottom) {
            if (Mouse.isButtonDown(0)) {

                final String translated = translations.containsKey(e.chatLine) ? translations.get(e.chatLine).getSecond() : TranslatorUtil.getTranslatedText(ChatFormatting.stripFormatting(e.chatLine.lineString.getFormattedText()), "de");
                if (!translations.containsKey(e.chatLine)) {
                    this.translations.put(e.chatLine, new Tuple<>(e.chatLine.lineString.getFormattedText(), translated));
                    this.textComponentMap.put(e.chatLine, e.chatLine.getChatComponent());
                }
                e.chatLine.lineString = new TextComponentString(translated);
            } else if (Mouse.isButtonDown(1)) {
                if (this.translations.containsKey(e.chatLine)) {
                    e.chatLine.lineString = textComponentMap.get(e.chatLine);
                    this.translations.remove(e.chatLine);
                    this.textComponentMap.remove(e.chatLine);
                }
            }
        }
    };
}