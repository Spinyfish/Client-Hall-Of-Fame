package dev.africa.pandaware.manager.user;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.container.Container;
import dev.africa.pandaware.impl.module.render.HUDModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;

@Getter
public class UserManager extends Container<UserManager.User> implements MinecraftInstance {
    public String replaceString(String input) {
        final HUDModule hudModule = Client.getInstance().getModuleManager().getByClass(HUDModule.class);

        final boolean ircEnabled = hudModule != null &&
                hudModule.getData().isEnabled() &&
                hudModule.getIrc().getValue();

        if (ircEnabled && !this.getItems().isEmpty() && input != null && !input.isEmpty()) {
            for (User user : this.getItems()) {
                if (input.contains(user.getMinecraftName())) {
                    input = input.replace(
                            user.getMinecraftName(),
                            user.getMinecraftName() + " §7[" + user.getName() + "§7]§r"
                    );
                    break;
                }
            }
        }

        return input;
    }

    public boolean isUser(EntityPlayer player) {
        if (mc.thePlayer != null && player == mc.thePlayer) return true;

        return this.getUserFromMinecraftName(player.getName()) != null;
    }

    public User getUserFromMinecraftName(String name) {
        if (!this.getItems().isEmpty()) {
            return this.getItems()
                    .stream()
                    .filter(user -> user.getMinecraftName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    @Getter
    @AllArgsConstructor
    public static class User {
        private final String name;
        private final String minecraftName;
    }
}
