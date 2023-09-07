package kevin.module.modules.movement.flys.ncp

import kevin.event.PacketEvent
import kevin.event.TickEvent
import kevin.event.UpdateEvent
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.movement.flys.FlyMode
import kevin.utils.MSTimer
import kevin.utils.MovementUtils
import kevin.utils.PacketUtils
import kevin.utils.RandomUtils
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.AxisAlignedBB

// Copied from... I don't know where... directly
// Does it work? I don't know
object  HypixelVanilla : FlyMode("HypixelVanilla") {
    private val speedValue = FloatValue("${valuePrefix}speed", 0.5f, 0.3f, 3f)
    private val debugValue = BooleanValue("${valuePrefix}debug", false)
    private val packetValue = ListValue("${valuePrefix}Packet", arrayOf("WatchDoge", "Jitter"), "Jitter")
    private val jitterRangeValue = FloatValue("${valuePrefix}jitterRange", 2.7f, 0.5f, 15f)
    private val flyTimer = MSTimer()
    var oldX = 0.0
    var oldY = 0.0
    var oldZ = 0.0
    var disable: Long = 0
    var oldYaw = 0f
    var lastFlag = 0
    var oldPitch = 0f
    var player: EntityOtherPlayerMP? = null
    var enabledTicks = 0
    var floatingTickCount = 0
    override fun onEnable() {
        flyTimer.reset()
        lastFlag = 0
        oldX = mc.thePlayer.posX
        oldY = mc.thePlayer.posY
        oldZ = mc.thePlayer.posZ
        oldYaw = mc.thePlayer.rotationYaw
        oldPitch = mc.thePlayer.rotationPitch
        EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile).also { player = it }.copyLocationAndAnglesFrom(mc.thePlayer)
        player!!.rotationYawHead = mc.thePlayer.rotationYawHead
        player!!.renderYawOffset = mc.thePlayer.renderYawOffset
        mc.thePlayer.noClip = true
        enabledTicks = 0
    }

    override fun onDisable() {
        if (true) {
            mc.thePlayer.setPosition(oldX, oldY, oldZ)
            mc.thePlayer.rotationYaw = oldYaw
            mc.thePlayer.rotationPitch = oldPitch
        }
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
        player = null
        mc.timer.timerSpeed = 1.0.toFloat()
        mc.thePlayer.capabilities.allowFlying = false
        mc.thePlayer.capabilities.setFlySpeed(0.05f)
        mc.thePlayer.capabilities.isFlying = false
        mc.thePlayer.speedInAir = 0.02f
    }

    override fun onPacket(event: PacketEvent) {
        if (debugValue.get()) {
            /*            val packet = event.packet
                        val clazz = event.packet.javaClass
                        ClientUtils.displayChatMessage(clazz.getSimpleName())
                        if (clazz.isMemberClass()) {
                            val clazz12 = clazz.getDeclaringClass()
                        }
                        clazz.getDeclaredFields().forEach {
                            it.setAccessible(true);
                            ClientUtils.displayChatMessage("    "+it.getName() + "=" + it.get(packet))
                        }*/

        }
        val p: Packet<*> = event.packet
        if (p is S12PacketEntityVelocity) {
            event.cancelEvent()
            lastFlag = enabledTicks
        }
        if (p is S08PacketPlayerPosLook) {
            oldX = p.x
            oldY = p.y
            oldZ = p.z
            oldYaw = p.yaw
            oldPitch = p.pitch
/*            player!!.posX = oldX
            player!!.posY = oldY
            player!!.posZ = oldZ
            player!!.rotationYaw = oldYaw
            player!!.rotationPitch = oldPitch*/
            player!!.setPositionAndRotation(oldX,oldY,oldZ,oldYaw,oldPitch)
            event.cancelEvent()
            if (true) lastFlag = enabledTicks
            PacketUtils.sendPacketNoEvent(C03PacketPlayer.C06PacketPlayerPosLook(oldX, oldY, oldZ, oldYaw, oldPitch, true))

        }
        if (p is C03PacketPlayer && p !is C03PacketPlayer.C04PacketPlayerPosition && p !is C03PacketPlayer.C05PacketPlayerLook && p !is C03PacketPlayer.C06PacketPlayerPosLook) {
            event.cancelEvent()
        }
        if (p is C03PacketPlayer) {
            floatingTickCount = if (mc.thePlayer.onGround) 0 else floatingTickCount + 1
            if (true) event.cancelEvent()
        }
    }

    override fun onUpdate(event: UpdateEvent) {
        enabledTicks++;
        if (true) {
            if (enabledTicks == 6) {
                KevinClient.hud.addNotification(Notification("exploited You can fly freely until you disable fly.", "WatchDoge PacketClip exploit"))
            }
            if (enabledTicks <= 2) {
                mc.timer.timerSpeed = 0.7F
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionY = 0.05
                mc.thePlayer.motionZ = 0.0
                mc.thePlayer.speedInAir = 0.0f
                return
            }
            mc.timer.timerSpeed = 0.7F
            if (enabledTicks % 2 != 0) {
                return
            }
            if (mc.thePlayer.posY - player!!.posY > 10.0) {
                if (enabledTicks % 20 == 0) {
                    mc.thePlayer.setLocationAndAngles(player!!.posX, player!!.posY, player!!.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
                    KevinClient.hud.addNotification(Notification("You got lag backed! Please land on the ground if it keeps happening.", "WatchDoge PacketClip exploit"))
                }
                return
            }
            if (enabledTicks % 10 == 0 && (enabledTicks - lastFlag >= 6 || mc.thePlayer.posY - player!!.posY > 4.0) && mc.thePlayer.getDistanceToEntity(player) >= 12.0f) {
                mc.thePlayer.setLocationAndAngles(player!!.posX, player!!.posY, player!!.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
                KevinClient.hud.addNotification(Notification("You got lag backed! Please land on the ground if it keeps happening.", "WatchDoge PacketClip exploit"))
            }
            when (packetValue.get().lowercase()) {
                "watchdoge" -> {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true))
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 50.0, mc.thePlayer.posZ, true))
                }

                "jitter" -> {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX + RandomUtils.nextFloat(0F, jitterRangeValue.get()).toDouble(), mc.thePlayer.posY, mc.thePlayer.posZ + RandomUtils.nextFloat(0F, jitterRangeValue.get()).toDouble(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.posY % 0.125 == 0.0))
                }
            }
        }
        mc.thePlayer.fallDistance = 0.0f
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
        if (!MovementUtils.isMoving) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
        }
        if (mc.gameSettings.keyBindJump.isKeyDown) {
            mc.thePlayer.motionY += speedValue.get() / 1.399999976158142
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown) {
            mc.thePlayer.motionY -= speedValue.get() / 1.399999976158142
        }
        MovementUtils.strafe(speedValue.get() / 1.399999976158142F)
//        MovementUtils.strafe(speedValue.get())
        val axisalignedbb2: AxisAlignedBB = mc.thePlayer.entityBoundingBox.expand(0.0625, 0.0625, 0.0625).addCoord(0.0, -0.55, 0.0)
        if (!mc.theWorld.checkBlockCollision(axisalignedbb2)) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.05, mc.thePlayer.posZ)
        }
        /*        if (floatingTickCount < 60.0) {

                } else if (mc.thePlayer.isSneaking() || mc.gameSettings.keyBindJump.isKeyDown()) {
                    mc.thePlayer.motionY = speedValue.get() / 1.399999976158142
                    MovementUtils.strafe(speedValue.get() / 1.399999976158142F)
                    MovementUtils.handleVanillaKickBypass()
        //            MovementUtils.handleVanillaKickBypass()
                    floatingTickCount = 1
                }*/


    }

}
