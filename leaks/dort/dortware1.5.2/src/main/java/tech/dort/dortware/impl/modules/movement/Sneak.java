package tech.dort.dortware.impl.modules.movement;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.BlockContainer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.networking.PacketUtil;

public class Sneak extends Module {

    private int containerStage;
    private Packet lastPacket;

    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, Sneak.Mode.values());

    public Sneak(ModuleData moduleData) {
        super(moduleData);
        register(enumValue);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        Mode mode = enumValue.getValue();
        switch (mode) {
            case VERUS: {
                if (!event.isPre() && !mc.thePlayer.isSneaking()) {
                    switch (containerStage) {
                        case 0: {
                            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                            break;
                        }
                        case 1: {
                            PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                            containerStage++;
                            break;
                        }
                        case 2: {
                            PacketUtil.sendPacketNoEvent(lastPacket);
                            lastPacket = null;
                            containerStage = 0;
                            break;
                        }
                    }
                }
                break;
            }
            case PACKET: {
                if (event.isPre())
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                else
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                break;
            }
            case SOLID: {
                mc.gameSettings.keyBindSneak.pressed = true;
                break;
            }
        }
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        Mode mode = enumValue.getValue();
        if (mode == Mode.VERUS) {
            if (event.getPacket() instanceof C0BPacketEntityAction) {
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
                C08PacketPlayerBlockPlacement packet = event.getPacket();
                if (mc.theWorld.getBlockState(packet.func_179724_a()).getBlock() instanceof BlockContainer) {
                    lastPacket = packet;
                    event.setCancelled(true);
                    containerStage = 1;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSneak.pressed = false;
        containerStage = 0;
    }

    public enum Mode implements INameable {
        PACKET("Packet"), SOLID("Solid"), VERUS("Verus");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
