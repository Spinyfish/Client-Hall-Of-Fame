package alos.stella.module.modules.movement

import alos.stella.event.EventTarget
import alos.stella.event.events.MoveEvent
import alos.stella.event.events.PacketEvent
import alos.stella.event.events.UpdateEvent
import alos.stella.module.Module
import alos.stella.module.ModuleCategory
import alos.stella.module.ModuleInfo
import alos.stella.utils.MovementUtils
import alos.stella.utils.PosLookInstance
import alos.stella.value.BoolValue
import alos.stella.value.FloatValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "TpFly", spacedName = "TpFly", description = "Allows you to move out of your body.", category = ModuleCategory.MOVEMENT)
class TpFly : Module() {

    private val speedValue = FloatValue("Speed", 0.8F, 0.1F, 2F, "m")
    private val flyValue = BoolValue("Fly", true)
    private val groundspoof = BoolValue("Ground", true)
    private val sprinting = BoolValue("Damage", true)
    val undetectableValue = BoolValue("Undetectable", true)

    private var fakePlayer: EntityOtherPlayerMP? = null
    private var oldX = 0.0
    private var oldY = 0.0
    private var oldZ = 0.0

    private var lastOnGround = false

    private val posLook = PosLookInstance()

    override fun onEnable() {
        if (sprinting.get()) {
            mc.thePlayer.sendQueue.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 3.05,
                    mc.thePlayer.posZ,
                    false
                )
            )
            mc.thePlayer.sendQueue.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    false
                )
            )
            mc.thePlayer.sendQueue.addToSendQueue(
                C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 0.41999998688697815,
                    mc.thePlayer.posZ,
                    true
                )
            )
        }
        mc.thePlayer ?: return

        oldX = mc.thePlayer.posX
        oldY = mc.thePlayer.posY
        oldZ = mc.thePlayer.posZ
        lastOnGround = mc.thePlayer.onGround

        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
        fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
        mc.theWorld.addEntityToWorld(-1000, fakePlayer!!)

    }

    override fun onDisable() {
        mc.thePlayer ?: return
        fakePlayer ?: return

        mc.thePlayer.posX = fakePlayer!!.posX
        mc.thePlayer.posY = fakePlayer!!.posY
        mc.thePlayer.posZ = fakePlayer!!.posZ

        mc.thePlayer.posX = fakePlayer!!.lastTickPosX
        mc.thePlayer.posY = fakePlayer!!.lastTickPosY
        mc.thePlayer.posZ = fakePlayer!!.lastTickPosZ

        mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
        fakePlayer = null
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.fallDistance = 0F

        if (flyValue.get()) {
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            MovementUtils.strafe(speedValue.get())
        }
        if (groundspoof.get()) {
            mc.thePlayer.onGround=true
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        fakePlayer ?: return

        val packet = event.packet
        if (undetectableValue.get()) {
            if (packet is C04PacketPlayerPosition || packet is C05PacketPlayerLook) {
                event.cancelEvent()
                mc.netHandler.addToSendQueue(C03PacketPlayer(lastOnGround))
            } else if (packet is C06PacketPlayerPosLook) {
                if (posLook.equalFlag(packet)) {
                    fakePlayer!!.setPosition(packet.x, packet.y, packet.z)
                    fakePlayer!!.onGround = packet.onGround
                    lastOnGround = packet.onGround
                    fakePlayer!!.rotationYaw = packet.yaw
                    fakePlayer!!.rotationPitch = packet.pitch
                    fakePlayer!!.rotationYawHead = packet.yaw
                    posLook.reset()
                } else if (mc.thePlayer.positionUpdateTicks >= 20) {
                    packet.x = fakePlayer!!.posX
                    packet.y = fakePlayer!!.posY
                    packet.z = fakePlayer!!.posZ
                    packet.onGround = lastOnGround
                    packet.yaw = fakePlayer!!.rotationYaw
                    packet.pitch = fakePlayer!!.rotationPitch
                } else {
                    event.cancelEvent()
                    mc.netHandler.addToSendQueue(C03PacketPlayer(lastOnGround))
                }
            }
        } else if (packet is C03PacketPlayer)
            event.cancelEvent()
        if (packet is C0BPacketEntityAction)
            event.cancelEvent()
        if (packet is S08PacketPlayerPosLook) {
            event.cancelEvent()
            posLook.set(packet)
        }
    }

}