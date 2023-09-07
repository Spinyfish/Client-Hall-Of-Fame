package kevin.module.modules.movement.flys.other

import kevin.event.EventState
import kevin.event.MotionEvent
import kevin.event.MoveEvent
import kevin.event.PacketEvent
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.RandomUtils
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.server.S08PacketPlayerPosLook


object HypixelFly : FlyMode("Hypixel") {
    private var shouldJump = false
    private var sent = false
    private var startY = 0.0

    override fun onEnable() {
        startY = mc.thePlayer.posY
        shouldJump = true
    }

    override fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            if (shouldJump) {
                mc.thePlayer.jump()
                shouldJump = false
            }
        }
    }

    override fun onMove(event: MoveEvent) {
        if (mc.thePlayer.posY < startY) {
            if (!sent) {
                sent = true
                mc.netHandler.addToSendQueue(
                    C04PacketPlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.posY - 22,
                        mc.thePlayer.posZ,
                        false
                    )
                )
            }
            event.zero()
        } else {
            sent = false
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is S08PacketPlayerPosLook) {
            shouldJump = true
        }
    }
}