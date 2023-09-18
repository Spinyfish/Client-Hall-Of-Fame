package kevin.module.modules.combat

import kevin.event.EventTarget
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.MSTimer
import kevin.utils.RotationUtils
import net.minecraft.entity.projectile.EntityFireball
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C0APacketAnimation

class AntiFireball : Module("AntiFireball", "", category = ModuleCategory.COMBAT) {
    private val timer = MSTimer()

    private val swingValue = ListValue("Swing", arrayOf("Normal", "Packet", "None"), "Normal")
    private val rotationValue = BooleanValue("Rotation", true)
    private val radius = FloatValue("Radius", 3f, 3f, 6f)

    private var needModifyMove = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityFireball && mc.thePlayer.getDistanceToEntity(entity) < radius.get() && timer.hasTimePassed(300)) {
                needModifyMove = true
                if(rotationValue.get()) {
                    RotationUtils.setTargetRotation(RotationUtils.getRotations(entity))
                }

                if (swingValue equal "Normal") {
                    mc.thePlayer.swingItem()
                } else if (swingValue equal "Packet") {
                    mc.netHandler.addToSendQueue(C0APacketAnimation())
                }
                mc.thePlayer.sendQueue.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

                timer.reset()
                break
            }
        }
    }
}