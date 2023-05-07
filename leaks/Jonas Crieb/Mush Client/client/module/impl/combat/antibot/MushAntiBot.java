package client.module.impl.combat.antibot;

import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.MotionEvent;
import client.event.impl.motion.StrafeEvent;
import client.module.impl.combat.AntiBot;
import client.module.impl.movement.Flight;
import client.module.impl.movement.Speed;
import client.util.chat.ChatUtil;
import client.util.player.MoveUtil;
import client.util.player.PlayerUtil;
import client.value.Mode;
import client.value.impl.NumberValue;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import tv.twitch.chat.Chat;

import java.util.ArrayList;
import java.util.List;

public class MushAntiBot extends Mode<AntiBot> {
    public static List<Entity> bots = new ArrayList<>();
    public MushAntiBot(String name, AntiBot parent) {
        super(name, parent);
    }


    @Override
    public void onDisable(){
        bots.clear();
    }
    @EventLink()
    public final Listener<MotionEvent> onMotion = event -> {
        bots.clear();
        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player != mc.thePlayer) {
                final List<String> names = new ArrayList<>();
                final List<Integer> ids = new ArrayList<>();
                //ChatUtil.display(player.getName());
                if (player.getName().startsWith("§")) {
                    ChatUtil.display(player.bot + " " + player.getName());
                    player.bot = true;
                    if (player.bot) {
                        bots.add(player);
                    }

                } else{
                    player.bot = false;
                //   ChatUtil.display(player.bot + " " + player.getName());
                }
                if (player.getName().startsWith("&")) {
                    ChatUtil.display(player.bot + " " + player.getName());
                    player.bot = true;
                    if (player.bot) {
                        bots.add(player);
                    }

                } else{
                    player.bot = false;
                    //ChatUtil.display(player.bot + " " + player.getName());
                }
            }
        }
    };
}
