package kevin.module.modules.movement.flys.other

import kevin.event.MotionEvent
import kevin.event.PacketEvent
import kevin.module.modules.movement.flys.FlyMode
import net.minecraft.network.play.server.S27PacketExplosion

object GrimExplosion : FlyMode("GrimExplosion") {
    override fun onMotion(event: MotionEvent) {
        event.posX += 1000
        event.posY -= 1000
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S27PacketExplosion) {
            event.cancelEvent()
        }
    }
}