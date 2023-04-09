package tech.dort.dortware.impl.modules.render;

import com.google.common.eventbus.Subscribe;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.network.Packet;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.KeyboardEvent;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.RenderHUDEvent;
import tech.dort.dortware.impl.events.enums.PacketDirection;
import tech.dort.dortware.impl.gui.tab.Tab;
import tech.dort.dortware.impl.modules.exploit.Disabler;
import tech.dort.dortware.impl.modules.render.hud.MemeTheme;

import java.util.HashMap;
import java.util.Map;

public final class Hud extends Module {

    public final EnumValue<TargetHudMode> targetHudMode = new EnumValue<>("Target HUD Mode", this, TargetHudMode.values());
    public final EnumValue<AlternativeNameMode> alternativeNameMode = new EnumValue<>("Name Mode", this, AlternativeNameMode.values());
    public final EnumValue<ArraylistMode> arraylistMode = new EnumValue<>("Arraylist mode", this, ArraylistMode.values());
    public final NumberValue red = new NumberValue("Red", this, 255, 0, 255, true);
    public final NumberValue green = new NumberValue("Green", this, 50, 0, 255, true);
    public final NumberValue blue = new NumberValue("Blue", this, 50, 0, 255, true);
    public final NumberValue targetX = new NumberValue("Target HUD X", this, 20, 0, 300, SliderUnit.X, true);
    public final NumberValue targetY = new NumberValue("Target HUD Y", this, 20, 0, 200, SliderUnit.Y, true);
    public final BooleanValue tabGui = new BooleanValue("Tab UI", this, true);
    public final BooleanValue armorHUD = new BooleanValue("Armor HUD", this, true);
    public final BooleanValue targetHUD = new BooleanValue("Target HUD", this, true);
    public final BooleanValue playerModel = new BooleanValue("Player Model", this, false);
    public final BooleanValue watermark = new BooleanValue("Watermark", this, true);
    public final BooleanValue mpStatus = new BooleanValue("Mineplex Status", this, false);
    final Tab tab = new Tab();
    private final MemeTheme hud;
    private final Map<Long, Packet> in = new HashMap<>();
    private final Map<Long, Packet> out = new HashMap<>();

    public Hud(ModuleData moduleData) {
        super(moduleData);
        this.hud = new MemeTheme(this);
        register(targetHudMode, alternativeNameMode, arraylistMode, red, green, blue, targetX, targetY, tabGui, armorHUD, targetHUD, playerModel, watermark, mpStatus);
    }

    public void drawEntityOnScreen(int posX, int posY, EntityLivingBase entityLivingBase) {
        if (entityLivingBase instanceof EntityPlayerSP) {
            EntityPlayerSP entityPlayer = (EntityPlayerSP) entityLivingBase;
            float oldYaw = entityPlayer.rotationYaw;
            float oldPitch = entityPlayer.rotationPitch;
            float oldYawOffset = entityPlayer.renderYawOffset;
            float oldYawHead = entityPlayer.rotationYawHead;
            float oldRYawHead = entityPlayer.renderYawHead;
            float oldRPitchHead = entityPlayer.renderPitchHead;
            entityPlayer.rotationYaw = 0;
            entityPlayer.rotationPitch = 0;
            entityPlayer.renderYawOffset = 0;
            entityPlayer.rotationYawHead = 0;
            entityPlayer.renderYawHead = 0;
            entityPlayer.renderPitchHead = 0;
            GuiInventory.drawEntityOnScreen(posX, posY, 35, 0, 0, entityPlayer);
            entityPlayer.rotationYaw = oldYaw;
            entityPlayer.rotationPitch = oldPitch;
            entityPlayer.renderYawOffset = oldYawOffset;
            entityPlayer.rotationYawHead = oldYawHead;
            entityPlayer.renderYawHead = oldRYawHead;
            entityPlayer.renderPitchHead = oldRPitchHead;
        } else {
            float oldYaw = entityLivingBase.rotationYaw;
            float oldPitch = entityLivingBase.rotationPitch;
            float oldYawOffset = entityLivingBase.renderYawOffset;
            float oldYawHead = entityLivingBase.rotationYawHead;
            entityLivingBase.rotationYaw = 0;
            entityLivingBase.rotationPitch = 0;
            entityLivingBase.renderYawOffset = 0;
            entityLivingBase.rotationYawHead = 0;
            GuiInventory.drawEntityOnScreen(posX, posY, 35, 0, 0, entityLivingBase);
            entityLivingBase.rotationYaw = oldYaw;
            entityLivingBase.rotationPitch = oldPitch;
            entityLivingBase.renderYawOffset = oldYawOffset;
            entityLivingBase.rotationYawHead = oldYawHead;
        }
    }

    @Subscribe
    public void updatePacketCounter(PacketEvent event) {
        try {
            Map<Long, Packet> packetMap = event.getPacketDirection() == PacketDirection.OUTBOUND ? out : in;
            packetMap.put(System.currentTimeMillis(), event.getPacket());
            packetMap.forEach((key, packet) -> {
                if (System.currentTimeMillis() - 1000L >= key) {
                    packetMap.remove(key);
                }
            });
        } catch (Exception ignore) {
        }
    }

    @Subscribe
    public void onRender(RenderHUDEvent event) {
        if (mc.gameSettings.showDebugInfo)
            return;

        if (this.mpStatus.getValue()) {
            Disabler disabler = Client.INSTANCE.getModuleManager().get(Disabler.class);
            String status = "\u00a79Bot Status: " + disabler.mpTest.currentStatus;
            final ScaledResolution sr = new ScaledResolution(mc);
            mc.fontRendererObj.drawStringWithShadow(status, (sr.getScaledWidth() >> 1) - mc.fontRendererObj.getStringWidth(status) / 2F, BossStatus.statusBarTime == 0 ? 5 : 20, -1);
        }
        mc.fontRendererObj.drawStringWithShadow("Packets (outbound): " + out.size(), 115, 3, -1);
        mc.fontRendererObj.drawStringWithShadow("Packets (inbound): " + in.size(), 115, 12, -1);
        try {
            this.hud.render(event);
            if (tabGui.getValue()) this.tab.render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onKeyboard(KeyboardEvent event) {
        if (mc.gameSettings.showDebugInfo)
            return;

        tab.updateKeys(event);
    }

    public enum TargetHudMode implements INameable {
        SIMPLE("Simple"), HELIUM("Helium");
        public final String name;

        TargetHudMode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }

    public enum AlternativeNameMode implements INameable {
        NORMAL("Normal"), MEMES("Memes"), DUMB("Orialeng");
        public final String name;

        AlternativeNameMode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }

    public enum LineMode implements INameable {
        RIGHT("Right"), LEFT("Left"), BOTH("Both"), NONE("None");
        public final String name;

        LineMode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }

    public enum ArraylistMode implements INameable {
        NORMAL("Normal"), OLDMEME("Old Meme");
        public final String name;

        ArraylistMode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
