package dev.echo.module.impl.misc;

import dev.echo.commands.impl.FriendCommand;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.ui.notifications.NotificationManager;
import dev.echo.ui.notifications.NotificationType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;

public class MiddleClickFriends extends Module {

    private boolean wasDown;

    public MiddleClickFriends() {
        super("Middle Click Friends", Category.MISC, "middle click friends");
    }

    @Link
    public Listener<TickEvent> onTickEvent = e -> {
        if (mc.inGameHasFocus) {
            boolean down = mc.gameSettings.keyBindPickBlock.isKeyDown();
            if (down && !wasDown) {
                if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;
                    String name = StringUtils.stripControlCodes(player.getName());
                    if (FriendCommand.isFriend(name)) {
                        FriendCommand.friends.removeIf(f -> f.equalsIgnoreCase(name));
                        NotificationManager.post(NotificationType.SUCCESS, "Friend Manager", "You are no longer friends with " + name + "!", 2);
                    } else {
                        FriendCommand.friends.add(name);
                        NotificationManager.post(NotificationType.SUCCESS, "Friend Manager", "You are now friends with " + name + "!", 2);
                    }
                    FriendCommand.save();
                    wasDown = true;
                }
            } else if (!down) {
                wasDown = false;
            }
        }
    };

}
