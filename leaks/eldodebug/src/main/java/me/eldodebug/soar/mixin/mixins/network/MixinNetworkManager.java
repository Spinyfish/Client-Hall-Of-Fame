package me.eldodebug.soar.mixin.mixins.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelHandlerContext;
import me.eldodebug.soar.management.events.impl.EventReceivePacket;
import me.eldodebug.soar.management.events.impl.EventSendPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
	
	@Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void channelRead0(final ChannelHandlerContext context, final Packet<?> packet, final CallbackInfo ci) {
		EventReceivePacket event = new EventReceivePacket(packet);
		event.call();
		
		if(event.isCancelled()) {
			ci.cancel();
		}
	}
	
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void sendPacket(final Packet<?> packet, final CallbackInfo ci) {
    	
    	EventSendPacket event = new EventSendPacket(packet);
    	event.call();
    	
    	if(event.isCancelled()) {
    		ci.cancel();
    	}
    }
}
