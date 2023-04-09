package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.RandomUtils;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.impl.events.ChatEvent;

public class ChatBypass extends Module {

    private final char[] chars = new char[]{'\u26DF', '\u26E0', '\u26E1', '\u26E2', '\u26E3', '\u26E4', '\u2753', '\u2E3B', '\u0fd8'};

    public ChatBypass(ModuleData moduleData) {
        super(moduleData);
    }

    @Subscribe
    public void handleChat(ChatEvent chatEvent) {
        if (chatEvent.getRawMessage().startsWith(".") || chatEvent.getRawMessage().startsWith("/"))
            return;
        chatEvent.setRawMessage(chatEvent.getRawMessage().replace("ez", String.join("", "e", String.valueOf(chars[0]), "z")));
        final StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        if (!chatEvent.getRawMessage().startsWith("/")) {
            for (char character : chatEvent.getRawMessage().toCharArray()) {
                stringBuilder.append(character);
                if (count % 2 == 0 && character != ' ') {
                    stringBuilder.append(chars[RandomUtils.nextInt(0, chars.length)]);
                }
                count++;
            }
            chatEvent.setRawMessage(stringBuilder.toString());
        }
    }
}
