/**
 * @project Myth
 * @author CodeMan
 * @at 24.08.22, 16:59
 */
package dev.myth.features.combat;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.events.PacketEvent;
import dev.myth.settings.ListSetting;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.potion.Potion;

import java.util.ArrayList;

@Feature.Info(
        name = "AntiBot",
        description = "Prevents you from attacking bots",
        category = Feature.Category.COMBAT
)
public class AntiBotFeature extends Feature {

    public final ListSetting<Mode> checks = new ListSetting<>("Checks", Mode.HYPIXEL);

    public final ArrayList<EntityPlayer> hit = new ArrayList<>();

    public boolean isBot(EntityLivingBase entityLivingBase) {
        if(!isEnabled()) return false;

        if (checks.isEnabled("Hypixel")) {
            if(entityLivingBase.isInvisible() && !entityLivingBase.isPotionActive(Potion.invisibility)) return true;
            if(entityLivingBase.getName().startsWith("§")) return true;
        }

        if (checks.isEnabled("TabList")) {
            if(entityLivingBase instanceof EntityPlayer) {
                if(!GuiPlayerTabOverlay.getPlayers().contains((EntityPlayer) entityLivingBase)) {
                    return true;
                }
            }
        }

        if (checks.isEnabled("Simple")) {
            if(/*entityLivingBase.getHealth() > (20F + entityLivingBase.getAbsorptionAmount()) || */entityLivingBase.getHealth() <= 0F) return true;
        }

        if(checks.is(Mode.HITBEFORE)) {
            if(entityLivingBase instanceof EntityPlayer) {
                if (!hit.contains(entityLivingBase)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Handler
    public final Listener<PacketEvent> packetEventListener = event -> {
        if(event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();
            if(packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
                if(packet.getEntityFromWorld(getWorld()) instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) packet.getEntityFromWorld(getWorld());
                    hit.add(player);
                }
            }
        }
    };

    @Override
    public String getSuffix() {
        return checks.getValue().isEmpty() ? "no" : checks.getValue().size() == 1 ? checks.getValue().get(0).toString() : checks.getValue().size() + " Enabled";
    }

    public enum Mode {
        HYPIXEL("Hypixel"),
        TABLIST("TabList"),
        HITBEFORE("HitBefore"),
        SIMPLE("Simple");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
