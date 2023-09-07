package kevin.module.modules.movement.flys.other

import kevin.event.EventState
import kevin.event.MotionEvent
import kevin.event.PacketEvent
import kevin.module.modules.movement.flys.FlyMode
import net.minecraft.network.play.client.C03PacketPlayer
import kotlin.math.floor

object Spartan : FlyMode("Spartan") { // From Rise
    private var needSpoofC03 = false
    override fun onEnable() {
        needSpoofC03 = false
    }

    override fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST) return
        if (mc.thePlayer.onGround) {
            mc.thePlayer.jump()
        }

        if (mc.thePlayer.fallDistance > 1) {
            mc.thePlayer.motionY = -((mc.thePlayer.posY) - floor(mc.thePlayer.posY))
        }

        if (mc.thePlayer.motionY == 0.0) {
            mc.thePlayer.jump()

            mc.thePlayer.onGround = true
            needSpoofC03 = true
            mc.thePlayer.fallDistance = 0.0f
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (needSpoofC03 && packet is C03PacketPlayer) {
            packet.onGround = true
            needSpoofC03 = false
        }
    }
}