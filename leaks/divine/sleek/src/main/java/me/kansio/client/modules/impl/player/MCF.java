package me.kansio.client.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.Client;
import me.kansio.client.event.impl.MouseEvent;
import me.kansio.client.friend.Friend;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.utils.chat.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;

@ModuleData(
        name = "MCF",
        category = ModuleCategory.PLAYER,
        description = "Middle click a player to add them as a friend"
)
public class MCF extends Module {

    @Subscribe
    public void onMouse(MouseEvent event) {
        if (event.getButton() == 2 && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;
            String name = player.getName();
            if (Client.getInstance().getFriendManager().isFriend(name)) {
                Client.getInstance().getFriendManager().removeFriend(name);
                ChatUtil.log("Removed " + name + " as a friend!");
            } else {
                Client.getInstance().getFriendManager().addFriend(new Friend(name, name));
                ChatUtil.log("Added " + name + " as a friend!");
            }
        }
    }
}
