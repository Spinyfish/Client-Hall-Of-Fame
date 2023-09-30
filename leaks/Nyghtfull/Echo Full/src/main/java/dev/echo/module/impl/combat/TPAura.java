package dev.echo.module.impl.combat;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.game.TickEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.player.AlwaysUtil;
import dev.echo.utils.server.PacketUtils;
import dev.echo.utils.time.TimerUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TPAura extends Module {
    public NumberSetting range = new NumberSetting("Range", 10, 100, 3, 1);
    public List<EntityLivingBase> list;
    public static EntityLivingBase target;
    private final TimerUtil attackTimer = new TimerUtil();

    public TPAura() {
        super("TPAura", Category.COMBAT, "TPAura");
        addSettings(range);
        this.list = new ArrayList<>();
    }

    @Link
    private final Listener<TickEvent> tickEventListener = (event) -> {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }
        if (event.isPost()) {
            return;
        }
        updateTargets();
        target = !this.list.isEmpty() ? this.list.get(0) : null;
        if (target == null) {
            return;
        } else {
            if (attackTimer.hasTimeElapsed((500))) {
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, target);
                PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(target.getPosX(), target.getPosY(), target.getPosZ(), true));
                attackTimer.reset();
            }
        }
    };

    private void updateTargets() {
        if (!AlwaysUtil.isPlayerInGame()) {
            return;
        }
        this.list = mc.theWorld.loadedEntityList
                .stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> (EntityLivingBase) entity)
                .filter(livingEntity -> {
                    if (livingEntity instanceof EntityPlayer && livingEntity != mc.thePlayer && !livingEntity.isInvisible()) {
                        return true;
                    }
                    return livingEntity != mc.thePlayer;
                })
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= range.getValue())
                .sorted(Comparator.comparingDouble(entity -> {
                    return mc.thePlayer.getDistanceToEntity(entity);
                })).collect(Collectors.toList());
    }

    @Override
    public void onEnable() {
        attackTimer.reset();
        super.onEnable();
    }
}
