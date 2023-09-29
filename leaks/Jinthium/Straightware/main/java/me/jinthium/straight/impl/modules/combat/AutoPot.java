package me.jinthium.straight.impl.modules.combat;

import best.azura.irc.utils.RandomUtil;
import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.components.BadPacketsComponent;
import me.jinthium.straight.impl.event.game.SpoofItemEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.network.PacketEvent;
import me.jinthium.straight.impl.modules.player.Scaffold;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.network.PacketUtil;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.RandomUtils;

public class AutoPot extends Module {

    private final NumberSetting health = new NumberSetting("Health", 15, 1, 20, 1);
    private final NumberSetting delay = new NumberSetting("Delay", 500, 50, 5000, 50);
    private final TimerUtil timer = new TimerUtil();

    private int attackTicks, slot;
    private boolean splash;
    private long nextThrow;

    public AutoPot(){
        super("AutoPot", Category.COMBAT);
        this.addSettings(health, delay);
    }

    @Callback
    final EventCallback<SpoofItemEvent> spoofItemEventEventCallback = event -> {
        if(slot != -1)
            event.setCurrentItem(slot);
    };

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            this.attackTicks++;

            KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);

            slot = -1;
            if(killAura.target != null){
                attackTicks = 0;
                return;
            }

            if (mc.thePlayer.onGroundTicks <= 1 || attackTicks < 10 || Client.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled()) {
                return;
            }

            for (int i = 0; i < 9; i++) {
                final ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);

                if (stack == null) {
                    continue;
                }

                final Item item = stack.getItem();

                if (item instanceof ItemPotion potion) {
                    final PotionEffect effect = potion.getEffects(stack).get(0);

                    if (!ItemPotion.isSplash(stack.getMetadata()) ||
                            !PlayerUtil.goodPotion(effect.getPotionID()) ||
                            (effect.getPotionID() == Potion.regeneration.id ||
                                    effect.getPotionID() == Potion.heal.id) &&
                                    mc.thePlayer.getHealth() > this.health.getValue().floatValue()) {
                        continue;
                    }

                    if (mc.thePlayer.isPotionActive(effect.getPotionID()) &&
                            mc.thePlayer.getActivePotionEffect(effect.getPotionID()).getDuration() != 0) {
                        continue;
                    }

                    RotationUtils.setRotations(event, event.getYaw(), 90, 100, false);

                    slot = i;

                    if (!this.splash) {
                        this.splash = true;
                        return;
                    }

                    if (event.getPitch() > 85 && mc.thePlayer.inventory.getStackInSlot(slot).getItem() == item &&
                            !BadPacketsComponent.bad() && timer.hasTimeElapsed(nextThrow, true)) {
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getStackInSlot(slot)));

                        this.nextThrow = delay.getValue().longValue() + RandomUtils.nextLong(0, 100);
                        this.splash = false;
                    }
                }
            }
        }
    };

}
