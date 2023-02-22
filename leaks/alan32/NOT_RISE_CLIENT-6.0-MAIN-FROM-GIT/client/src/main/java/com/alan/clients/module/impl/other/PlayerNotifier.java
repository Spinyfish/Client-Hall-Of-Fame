package com.alan.clients.module.impl.other;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3i;

import java.util.ArrayList;

@Rise
@ModuleInfo(name = "module.other.playernotifier.name", description = "module.other.playernotifier.description", category = Category.OTHER)
public final class PlayerNotifier extends Module {

    private final NumberValue distance = new NumberValue("Notify Distance", this, 35, 10, 50, 1);
    private final BooleanValue notifyOnce = new BooleanValue("Notify Once", this, false);
    private Vec3i bedPosition = new Vec3i(0, 0, 0);

    private final String[] colors = {"§e", "§6", "§c", "§4"};
    private final ArrayList<EntityLivingBase> notifiedPlayers = new ArrayList<>();


    @Override
    protected void onEnable() {
        bedPosition = new Vec3i(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (mc.thePlayer.ticksExisted % 25 != 0) return;

        threadPool.execute(() -> {
            for (EntityLivingBase entity : mc.theWorld.playerEntities) {
                if (PlayerUtil.sameTeam(entity) || Client.INSTANCE.getBotManager().contains(entity) || entity == mc.thePlayer) {
                    continue;
                }

                int distance = (int) entity.getDistance(bedPosition.getX(), bedPosition.getY(), bedPosition.getZ());

                if (distance < this.distance.getValue().intValue()) {
                    if (!notifiedPlayers.contains(entity) || !notifyOnce.getValue()) {
                        ChatUtil.display(getColor(distance) + entity.getCommandSenderName() + " is " + distance + " blocks away from your bed.");
                    } else {
                        ChatUtil.display("Didn't display: " + entity.getCommandSenderName());
                    }

                    notifiedPlayers.add(entity);
                } else {
                    notifiedPlayers.remove(entity);
                }
            }
        });
    };

    public String getColor(double distance) {
        double maxDistance = this.distance.getValue().intValue();

        if (distance < 9) {
            return colors[colors.length - 1];
        }

        for (String color : colors) {
            maxDistance /= 2;

            if (distance > maxDistance) {
                return color;
            }
        }

        return colors[colors.length - 1];
    }
}
