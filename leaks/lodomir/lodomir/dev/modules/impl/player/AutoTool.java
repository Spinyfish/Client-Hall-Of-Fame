/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 *  org.lwjgl.input.Mouse
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

public class AutoTool
extends Module {
    private int oldSlot = -1;
    private boolean wasBreaking = false;

    public AutoTool() {
        super("AutoTool", 0, Category.PLAYER);
    }

    /*
     * Enabled aggressive block sorting
     */
    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (AutoTool.mc.currentScreen == null) {
            if (AutoTool.mc.thePlayer != null) {
                if (AutoTool.mc.theWorld != null) {
                    if (AutoTool.mc.objectMouseOver != null) {
                        if (AutoTool.mc.objectMouseOver.getBlockPos() != null) {
                            if (AutoTool.mc.objectMouseOver.entityHit == null && Mouse.isButtonDown((int)0)) {
                                float bestSpeed = 1.0f;
                                int bestSlot = -1;
                                Block block = AutoTool.mc.theWorld.getBlockState(AutoTool.mc.objectMouseOver.getBlockPos()).getBlock();
                                for (int k = 0; k < 9; ++k) {
                                    float speed;
                                    ItemStack item = AutoTool.mc.thePlayer.inventory.getStackInSlot(k);
                                    if (item == null || !((speed = item.getStrVsBlock(block)) > bestSpeed)) continue;
                                    bestSpeed = speed;
                                    bestSlot = k;
                                }
                                if (bestSlot != -1) {
                                    if (AutoTool.mc.thePlayer.inventory.currentItem != bestSlot) {
                                        AutoTool.mc.thePlayer.inventory.currentItem = bestSlot;
                                        this.wasBreaking = true;
                                        return;
                                    }
                                }
                                if (bestSlot != -1) return;
                                if (this.wasBreaking) {
                                    AutoTool.mc.thePlayer.inventory.currentItem = this.oldSlot;
                                    this.wasBreaking = false;
                                }
                                this.oldSlot = AutoTool.mc.thePlayer.inventory.currentItem;
                                return;
                            }
                        }
                    }
                }
            }
        }
        if (AutoTool.mc.thePlayer == null) return;
        if (AutoTool.mc.theWorld == null) return;
        if (this.wasBreaking) {
            AutoTool.mc.thePlayer.inventory.currentItem = this.oldSlot;
            this.wasBreaking = false;
        }
        this.oldSlot = AutoTool.mc.thePlayer.inventory.currentItem;
    }
}

