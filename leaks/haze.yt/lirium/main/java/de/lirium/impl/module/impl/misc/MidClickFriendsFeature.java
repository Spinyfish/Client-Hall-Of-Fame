/*
 * Copyright Felix Hans from MidClickFriends coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package de.lirium.impl.module.impl.misc;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.impl.events.UpdateEvent;
import de.lirium.impl.module.ModuleFeature;
import me.felix.friends.Friend;
import me.felix.friends.FriendData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

@ModuleFeature.Info(name = "Mid Click Friends", category = ModuleFeature.Category.MISCELLANEOUS, description = "Add Friends with mid - clicking on the player")
public class MidClickFriendsFeature extends ModuleFeature {

    private long press, lastPress;

    @EventHandler
    public final Listener<UpdateEvent> eventUpdate = e -> {

        final RayTraceResult rayTraceResult = mc.objectMouseOver;
        final Entity resultEntity = rayTraceResult.entityHit;

        if (resultEntity == null) return;

        if (resultEntity instanceof EntityPlayer) {
            this.lastPress = this.press;
            if (Mouse.isButtonDown(2)) {
                press = System.currentTimeMillis();

                final Friend friend = new Friend(resultEntity.getName(), "");

                final long deltaPress = (press - lastPress);

                if (deltaPress >= 1000) {
                    if (FriendData.isAlreadyFriend(friend))
                        sendMessage(friend.name + " Is already your friend.");
                    else {
                        sendMessage("Added friend: " + friend.name);
                        FriendData.addFriend(friend);
                    }
                }
            }
        }
    };

}
