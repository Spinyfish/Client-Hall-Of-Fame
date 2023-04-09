package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.ChatEvent;

public class LeetSpeak extends Module {

    private final EnumValue<LeetSpeak.Mode> enumValue = new EnumValue<>("Mode", this, LeetSpeak.Mode.values());

    public LeetSpeak(ModuleData moduleData) {
        super(moduleData);
        register(enumValue);
    }

    @Subscribe
    public void onChat(ChatEvent event) {
        String message = event.getRawMessage().toLowerCase();

        if ((Client.INSTANCE.getModuleManager().get(Commands.class).isToggled() && message.startsWith(".")) || message.startsWith("/"))
            return;

        switch (enumValue.getValue()) {
            case NUMBERS_ONLY:
                message = message.replaceAll("a", "4");
                message = message.replaceAll("l", "1");
                message = message.replaceAll("i", "1");
                message = message.replaceAll("e", "3");
                message = message.replaceAll("t", "7");
                message = message.replaceAll("o", "0");
                message = message.replaceAll("p", "9");
                message = message.replaceAll("z", "2");
                message = message.replaceAll("s", "5");
                break;

            case MOST:
                message = message.replaceAll("a", "4");
                message = message.replaceAll("b", "|8");
                message = message.replaceAll("c", "(");
                message = message.replaceAll("d", "|)");
                message = message.replaceAll("e", "3");
                message = message.replaceAll("f", "|=");
                message = message.replaceAll("g", "(_-");
                message = message.replaceAll("h", "|-|");
                message = message.replaceAll("i", "1");
                message = message.replaceAll("j", ",_|");
                message = message.replaceAll("k", "|<");
                message = message.replaceAll("l", "|_");
                message = message.replaceAll("o", "0");
                message = message.replaceAll("p", "|°");
                message = message.replaceAll("q", "0~");
                message = message.replaceAll("s", "5");
                message = message.replaceAll("t", "7");
                message = message.replaceAll("u", "|_|");
                message = message.replaceAll("z", "2");
                break;
        }

        event.setRawMessage(message);
    }

    public enum Mode implements INameable {
        NUMBERS_ONLY("Numbers Only"), MOST("Most");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
