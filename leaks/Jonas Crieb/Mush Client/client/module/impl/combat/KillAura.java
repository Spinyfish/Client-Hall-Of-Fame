package client.module.impl.combat;

import client.Client;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.motion.UpdateEvent;
import client.event.impl.other.AttackEvent;
import client.module.Category;
import client.module.Module;
import client.module.ModuleInfo;
import client.module.impl.combat.antibot.MushAntiBot;
import client.util.player.MoveUtil;
import client.value.impl.BooleanValue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.item.ItemSword;

import java.util.concurrent.atomic.AtomicBoolean;

@ModuleInfo(name = "Kill Aura", description = "Automatically attacks nearby entities", category = Category.COMBAT)
public class KillAura extends Module {
    public final BooleanValue keepSprint = new BooleanValue("Keep Sprint", this, true);

    @EventLink()
    public final Listener<UpdateEvent> onUpdate = event -> {
        AtomicBoolean clicked = new AtomicBoolean();
        mc.theWorld.loadedEntityList.stream().filter(EntityOtherPlayerMP.class::isInstance).forEachOrdered(entity -> {
            if (!clicked.get() && !MushAntiBot.bots.contains(entity) &&  entity.isEntityAlive() && !entity.isInvisible() && mc.thePlayer.getDistanceToEntity(entity) <= 6.5657) {
                final AttackEvent attackEvent = new AttackEvent(entity);

                    Client.INSTANCE.getEventBus().handle(attackEvent);
                    if (attackEvent.isCancelled()) return;
                    mc.thePlayer.swingItem();

                    mc.playerController.attackEntity(mc.thePlayer, attackEvent.getEntity());
                    clicked.set(true);

                }
                    if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {

                }



        });

    };
}