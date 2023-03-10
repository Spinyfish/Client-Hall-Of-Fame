package store.femboy.spring.impl.module.impl.combat;

import best.azura.eventbus.core.EventPriority;
import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import org.lwjgl.input.Keyboard;
import store.femboy.spring.impl.event.EventType;
import store.femboy.spring.impl.event.impl.EventMotion;
import store.femboy.spring.impl.module.Category;
import store.femboy.spring.impl.module.Module;
import store.femboy.spring.impl.module.settings.impl.BooleanSetting;
import store.femboy.spring.impl.module.settings.impl.NumberSettings;
import store.femboy.spring.impl.notifications.Notification;
import store.femboy.spring.impl.notifications.NotificationType;
import store.femboy.spring.impl.util.*;

public final class KillAura extends Module {

    private BooleanSetting autoBlock = new BooleanSetting("AutoBlock", false);

    private NumberSettings minaps = new NumberSettings("Minimum Attacks/s", 15, 1, 20, 1);
    private NumberSettings maxaps = new NumberSettings("Maximum Attacks/s", 18, 1, 20, 1);

    private NumberSettings range = new NumberSettings("Range", 4f, 1f, 10f, 1f);
    public static BooleanSetting ircFriends = new BooleanSetting("Don't attack IRC users", true);

    private final TimeUtil timer = new TimeUtil();
    public static EntityLivingBase auraTarget;

    public KillAura() {
        super("KillAura", "kill others", Keyboard.KEY_X, Category.COMBAT);
        addSettings(minaps, maxaps, autoBlock, ircFriends, range);
    }

    @Override
    public void onEnable() {
        Notification.addNotification(new Notification(NotificationType.INFO, name + " on", false));
        super.onEnable();
    }

    @Override
    public void onDisable() {
        Notification.addNotification(new Notification(NotificationType.INFO, name + " off", false));
        super.onDisable();
    }

    @EventHandler(EventPriority.HIGHEST)
    public final Listener<EventMotion> onUpdate = e -> {
        auraTarget = EntityUtil.getClosestEntity(range.getValue());


        if (auraTarget != null) {
            if (e.getType() == EventType.PRE) {
                rotate(e, auraTarget);
            } else if (e.getType() == EventType.POST) {
                attack(auraTarget);
            }
        }
    };

    private void rotate(EventMotion e, EntityLivingBase entity){
        final float[] rotations = RotationUtil.doDortRotations(entity);

        final float yaw = rotations[0];
        final float pitch = rotations[1];

        e.setYaw(yaw);
        e.setPitch(pitch);

        mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.renderYawOffset = yaw;
        mc.thePlayer.rotationPitchHead = pitch;
    }

    private void attack(EntityLivingBase e) {
        if (e != null) {
            if (timer.hasTimeElapsed(1000 / MathUtil.getRandInt((int) minaps.getValue(), (int) maxaps.getValue()))) {
                if(autoBlock.isEnabled() && isHoldingSword()){
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                }
                mc.thePlayer.swingItem();
                PacketUtil.sendSilentPacket(new C02PacketUseEntity(e, C02PacketUseEntity.Action.ATTACK));
                timer.reset();
            }
        }
    }

    public static EntityLivingBase getTarget() {
        return auraTarget;
    }
    public boolean isHoldingSword(){
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

}
