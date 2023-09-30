package de.verschwiegener.atero.module.modules.player;

import de.liquiddev.ircclient.client.IrcPlayer;
import de.verschwiegener.atero.friend.FriendManager;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.util.chat.ChatUtil;
import net.minecraft.client.Minecraft;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientFriends extends Module {
    TimeUtils timeUtils;
    public static Setting setting;
    public ClientFriends() {
        super("ClientFriends", "ClientFriends", Keyboard.KEY_NONE, Category.Player);
    }

    public void onEnable() {

        super.onEnable();
    }

    public void onDisable() {
        for (final String s : friends) {
            if (Management.instance.friendmgr.isFriend(s)) {
                Management.instance.friendmgr.removeFriend(s);
                friends.remove(s);
            }
        }
        super.onDisable();
    }
    public CopyOnWriteArrayList<String> friends = new CopyOnWriteArrayList<>();
    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            {
                setExtraTag("All");
                for (final Entity en : mc.theWorld.loadedEntityList) {
                    if (en instanceof EntityOtherPlayerMP) {
                        final EntityOtherPlayerMP entity = (EntityOtherPlayerMP) en;
                        final IrcPlayer user = IrcPlayer.getByIngameName(EnumChatFormatting.getTextWithoutFormattingCodes(entity.getName()));
                        if (user != null
                                && ((user.getClientName().equalsIgnoreCase("Atero"))
                                || (user.getClientName().equalsIgnoreCase("ProjectX")
                                || (user.getClientName().equalsIgnoreCase("Skid") && setting.getItemByName("Skid").isState()
                                ||  (user.getClientName().equalsIgnoreCase("Koks") && setting.getItemByName("Koks").isState() ))
                                && !friends.contains(entity.getName())))) {
                            friends.add(entity.getName());
                        }
                    }
                }

                    for (final String s : friends) {
                        if (!Management.instance.friendmgr.isFriend(s)) {
                            Management.instance.friendmgr.addFriend(s);
                        }

                }
            }
        }
    }


    @Override
    public void setup() {
        super.setup();
        ArrayList<SettingsItem> items = new ArrayList<>();
        items.add(new SettingsItem("Atero", false, ""));
        items.add(new SettingsItem("ProjectX", false, ""));
        items.add(new SettingsItem("Skid", false, ""));
        items.add(new SettingsItem("Koks", false, ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }

}