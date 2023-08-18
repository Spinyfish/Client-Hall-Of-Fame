package me.jinthium.straight.impl.modules.player;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;

import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.modules.combat.KillAura;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.utils.player.InventoryUtils;
import me.jinthium.straight.impl.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;
import org.checkerframework.checker.units.qual.K;

public class AutoTool extends Module {

    private final BooleanSetting autoSword = new BooleanSetting("Auto Sword", true);

    public AutoTool() {
        super("AutoTool", Category.PLAYER);
        this.addSettings(autoSword);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventEventCallback = event -> {
        if(event.isPre()){
            KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
            if (mc.objectMouseOver != null && mc.gameSettings.keyBindAttack.isKeyDown()) {
                MovingObjectPosition objectMouseOver = mc.objectMouseOver;
                if (objectMouseOver.entityHit != null) {
                    switchSword();
                } else if (objectMouseOver.getBlockPos() != null) {
                    Block block = mc.theWorld.getBlockState(objectMouseOver.getBlockPos()).getBlock();
                    PlayerUtil.updateItemForBlock(block);
                }
            } else if (killAura.target != null) {
                switchSword();
            }
        }
    };

    private void switchSword() {
        if (!autoSword.isEnabled()) return;
        float damage = 1;
        int bestItem = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack is = mc.thePlayer.inventory.mainInventory[i];
            if (is != null && is.getItem() instanceof ItemSword && InventoryUtils.getSwordStrength(is) > damage) {
                damage = InventoryUtils.getSwordStrength(is);
                bestItem = i;
            }
        }
        if (bestItem != -1) {
            mc.thePlayer.inventory.currentItem = bestItem;
        }
    }

}