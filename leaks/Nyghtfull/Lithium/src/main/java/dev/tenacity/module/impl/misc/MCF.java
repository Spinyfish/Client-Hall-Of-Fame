package dev.tenacity.module.impl.misc;

import dev.tenacity.commands.impl.FriendCommand;
import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;

public class MCF extends Module {

    private boolean wasDown;

    public MCF() {
        super("MCF", Category.MISC, "middle click friends");
    }

    @Override
    public void onTickEvent(TickEvent event) {
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
    }

}
