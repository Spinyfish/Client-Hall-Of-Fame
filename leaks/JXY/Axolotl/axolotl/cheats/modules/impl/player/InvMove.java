package axolotl.cheats.modules.impl.player;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventPacket;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.ModeSetting;
import axolotl.util.PacketUtils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.CopyOnWriteArrayList;

public class InvMove extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "Bypass");

    private boolean reset;

    public InvMove() {
        super("InvMove", Category.PLAYER, true);
        this.addSettings(mode);
        this.setSpecialSetting(mode);
    }

    private CopyOnWriteArrayList<Packet> packetList = new CopyOnWriteArrayList();

    public void updateKeybinds() {
        mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
        mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
        mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
        mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
        mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
        mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
    }

    public void onEvent(Event e) {

        if(e instanceof EventUpdate) {

            if(mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest) {
                updateKeybinds();
                reset = true;
            } else if (reset) {
                reset = false;
                updateKeybinds();
            }

        } else if (e instanceof EventPacket) {

            EventPacket ep = (EventPacket)e;
            Packet p = ep.getPacket();

            if((!(mc.currentScreen instanceof GuiInventory) && !(mc.currentScreen instanceof GuiChest)) || !mode.getMode().equalsIgnoreCase("Bypass"))
                return;

            if(p instanceof C0EPacketClickWindow) {
                ep.setCancelled(true);
                packetList.add(p);
            } else if (p instanceof C0DPacketCloseWindow) {
                ep.setCancelled(true);
                packetList.forEach(PacketUtils::sendPacketNoEvent);
                PacketUtils.sendPacketNoEvent(p); // Makes the packet send after the clicks
                packetList.clear();
            }

        }

    }

}
