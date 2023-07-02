package me.eldodebug.soar.mixin.mixins.gui;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryLanDetected;
import net.minecraft.client.gui.ServerSelectionList;

@Mixin(ServerSelectionList.class)
public class MixinServerSelectionList {

    @Shadow 
    @Final 
    private List<ServerListEntryLanDetected> serverListLan;
    
    @Shadow 
    @Final 
    private GuiListExtended.IGuiListEntry lanScanEntry;

    @Inject(method = "getListEntry", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/ServerSelectionList;serverListLan:Ljava/util/List;"), cancellable = true)
    private void resolveIndexError(int index, CallbackInfoReturnable<GuiListExtended.IGuiListEntry> cir) {
        if (index >= this.serverListLan.size()) {
            cir.setReturnValue(this.lanScanEntry);
        }
    }
}
