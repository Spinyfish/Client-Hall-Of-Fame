package me.jinthium.straight.impl.modules.ghost;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.impl.event.game.MoveInputEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;

public class Eagle extends Module {

    private final NumberSetting slow = new NumberSetting("Sneak speed multiplier", 0.3, 0.2, 1, 0.05);
    private final BooleanSetting groundOnly = new BooleanSetting("Only onGround", false);
    private final BooleanSetting blocksOnly = new BooleanSetting("Only when holding blocks", false);
    private final BooleanSetting backwardsOnly = new BooleanSetting("Only when moving backwards", false);
    private final BooleanSetting onlyOnSneak = new BooleanSetting("Only on Sneak", true);

    private boolean sneaked;
    private int ticksOverEdge;

    public Eagle(){
        super("Eagle", Category.GHOST);
        this.addSettings(slow, groundOnly, blocksOnly, backwardsOnly, onlyOnSneak);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if(!event.isPre())
            return;

        if (mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) &&
                blocksOnly.isEnabled()) {
            if (sneaked) {
                sneaked = false;
            }
            return;
        }

        if ((mc.thePlayer.onGround || !groundOnly.isEnabled()) &&
                (MovementUtil.blockRelativeToPlayer(0, -1, 0) instanceof BlockAir) &&
                (!mc.gameSettings.keyBindForward.isKeyDown() || !backwardsOnly.isEnabled())) {
            if (!sneaked) {
                sneaked = true;
            }
        } else if (sneaked) {
            sneaked = false;
        }

        if (sneaked) {
            mc.gameSettings.keyBindSprint.pressed = false;
        }

        if (sneaked) {
            ticksOverEdge++;
        } else {
            ticksOverEdge = 0;
        }
    };

    @Override
    public void onDisable() {
        if (sneaked) {
            sneaked = false;
        }
        super.onDisable();
    }

    @Callback
    final EventCallback<MoveInputEvent> moveInputEventEventCallback = event -> {
        event.setSneak((sneaked && (mc.gameSettings.keyBindSneak.isKeyDown() || !onlyOnSneak.isEnabled())) ||
                (mc.gameSettings.keyBindSneak.isKeyDown() && !onlyOnSneak.isEnabled()));

        if (sneaked && ticksOverEdge <= 2) {
            event.setSneakSlowDownMultiplier(slow.getValue());
        }
    };
}
