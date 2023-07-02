package me.eldodebug.soar.mixin.mixins.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eldodebug.soar.hooks.NetHandlerPlayClientHook;
import me.eldodebug.soar.management.events.impl.EventDamageEntity;
import me.eldodebug.soar.management.events.impl.EventRespawn;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.util.IChatComponent;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

	@Shadow
	private WorldClient clientWorldController;
	
	@Inject(method = "handleJoinGame", at = @At("TAIL"))
	public void handleJoinGame(S01PacketJoinGame packetIn, CallbackInfo ci) {
		EventRespawn event = new EventRespawn();
		event.call();
	}
	
    @Inject(method = "handleResourcePack", at = @At("HEAD"), cancellable = true)
    private void resourceExploitFix(S48PacketResourcePackSend packetIn, CallbackInfo ci) {
        if (!NetHandlerPlayClientHook.validateResourcePackUrl((NetHandlerPlayClient) (Object) this, packetIn)) {
            ci.cancel();
        }
    }
    
    @Redirect(method = "handleUpdateSign", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=Unable to locate sign at ", ordinal = 0)), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;addChatMessage(Lnet/minecraft/util/IChatComponent;)V", ordinal = 0))
    private void removeDebugMessage(EntityPlayerSP instance, IChatComponent component) {}
    
	@Inject(method = "handleEntityStatus", at = @At("RETURN"))
	public void handleEntityStatus(S19PacketEntityStatus packetIn, CallbackInfo callback) {
		if(packetIn.getOpCode() == 2) {
			EventDamageEntity event = new EventDamageEntity(packetIn.getEntity(clientWorldController));
			event.call();
		}
	}
}
