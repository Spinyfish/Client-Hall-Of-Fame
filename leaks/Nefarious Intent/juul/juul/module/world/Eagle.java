package juul.module.world;

import juul.event.Event;
import juul.event.EventUpdate;
import juul.inject.wrappers.*;
import juul.module.Module;
import juul.module.settings.RangeSetting;
import juul.util.objects.Timer;
import juul.util.value.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class Eagle extends Module {

    public RangeSetting delay;

    private final Timer tDelay = new Timer();

    public Eagle() {
        super("Eagle", "Assists you with ninja bridging", 0, Category.WORLD);
        addSetting(delay = new RangeSetting("Delay", "delay between placements", 0, 0, 0, 250, 1));

    }

    public void onEvent(Event e) {
        KeyBindingWrapper sneak = mc.getGameSettings().keyBindSneak;

        if (e instanceof EventUpdate && !Keyboard.isKeyDown(sneak.getKeyCode())) {

            double dbDelay = (delay.getStart() > 0) ? Random.dbRandom(delay.getStart(), delay.getEnd()) : 0;
            if (!tDelay.hasTimeElapsed((long) dbDelay, false))
                return;

            WorldWrapper wWorld = new WorldWrapper(constants.world());
            EntityPlayerSPWrapper wEntityPlayerSP = thePlayerSP;
            EntityLivingBaseWrapper wEntityLivingBase = thePlayerSP.getEntityLivingBaseWrapper();

            BlockPos pos = new BlockPos(wEntityLivingBase.posX(), wEntityLivingBase.posY() - 1, wEntityLivingBase.posX());
            IBlockState state = wWorld.getBlockState(pos);

            boolean bShouldSneak = state != null
                    && state.getBlock() == Blocks.air
                    && wEntityPlayerSP.getOnGround();

            sneak.setPressed(bShouldSneak);
        }
    }

}
