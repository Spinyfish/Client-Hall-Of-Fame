package me.wavelength.baseclient.module.modules.player;

import me.wavelength.baseclient.event.events.PreMotionEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class Legitscaffold extends Module {

    public Legitscaffold() {
        super("Legitscaffold", "auto shift!", Keyboard.KEY_G, Category.PLAYER);
    }

    private boolean sneaking;

    @Override
    public void onPreMotion(final PreMotionEvent event) {
        if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock() instanceof BlockAir
                && mc.thePlayer.onGround) {
            sneaking = true;
            mc.gameSettings.keyBindSneak.setPressed(true);
        } else {
            if (sneaking) {
                mc.gameSettings.keyBindSneak.setPressed(false);
                sneaking = false;
            }
        }
    }

    @Override
    public void onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.setPressed(false);
        }
    }

}
