/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.module.modules.movement

import kevin.event.*
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.movement.flys.verus.VerusAuto
import kevin.module.modules.world.Scaffold
import kevin.utils.*
import net.minecraft.block.BlockAir
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.Packet
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

class AntiVoid : Module("AntiVoid","Automatically setbacks you after falling a certain distance.", category = ModuleCategory.MOVEMENT) {
    private val modeValue = ListValue("Mode", arrayOf("TeleportBack", "FlyFlag", "OnGroundSpoof", "VulcanGhost", "Freeze", "OldHypixel-Flag", "MotionTeleport-Flag", "MineMora-Blink"), "FlyFlag")
    private val maxFallDistance =FloatValue("MaxFallDistance", 10f, 0f, 255f)
    private val maxDistanceWithoutGround = FloatValue("MaxDistanceToSetback", 2.5f, 1f, 30f)
    private val yBoost = FloatValue("BlinkYBoost",1f,0f,5f)
    private val indicator = BooleanValue("Indicator", true)
    private val autoScaffold = BooleanValue("AutoScaffold",true)

    private var detectedLocation: BlockPos? = null
    private var lastFound = 0F
    private var prevX = 0.0
    private var prevY = 0.0
    private var prevZ = 0.0
    private var sent = false

    private val packetCache=ArrayList<Packet<*>>()
    private var blink=false
    private var canBlink=false
    private var posX=0.0
    private var posY=0.0
    private var posZ=0.0
    private var motionX=0.0
    private var motionY=0.0
    private var motionZ=0.0
    private var scaffoldOn = false

    override fun onDisable() {
        prevX = 0.0
        prevY = 0.0
        prevZ = 0.0
    }

    override fun onEnable() {
        sent = false
    }

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        detectedLocation = null

        val thePlayer = mc.thePlayer ?: return

        if (thePlayer.onGround &&(BlockUtils.getBlock(BlockPos(thePlayer.posX, thePlayer.posY - 1.0, thePlayer.posZ)))!is BlockAir) {
            prevX = thePlayer.prevPosX
            prevY = thePlayer.prevPosY
            prevZ = thePlayer.prevPosZ
        }

        if (!thePlayer.onGround && !thePlayer.isOnLadder && !thePlayer.isInWater) {
            val fallingPlayer = FallingPlayer(
                thePlayer.posX,
                thePlayer.posY,
                thePlayer.posZ,
                thePlayer.motionX,
                thePlayer.motionY,
                thePlayer.motionZ,
                thePlayer.rotationYaw,
                thePlayer.moveStrafing,
                thePlayer.moveForward
            )

            detectedLocation = fallingPlayer.findCollision(60)?.pos

            if (detectedLocation != null && abs(thePlayer.posY - detectedLocation!!.y) +
                thePlayer.fallDistance <= maxFallDistance.get()) {
                lastFound = thePlayer.fallDistance
            }

            if (thePlayer.fallDistance - lastFound > maxDistanceWithoutGround.get()) {
                val mode = modeValue.get()

                when (mode.lowercase(Locale.getDefault())) {
                    "teleportback" -> {
                        thePlayer.setPositionAndUpdate(prevX, prevY, prevZ)
                        thePlayer.fallDistance = 0F
                        thePlayer.motionY = 0.0
                    }
                    "flyflag" -> {
                        thePlayer.motionY += 0.1
                        thePlayer.fallDistance = 0F
                    }
                    "ongroundspoof" -> mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                    "freeze" -> KevinClient.moduleManager.getModule(Freeze::class.java).state = true

                    "motionteleport-flag" -> {
                        thePlayer.setPositionAndUpdate(thePlayer.posX, thePlayer.posY + 1f, thePlayer.posZ)
                        mc.netHandler.addToSendQueue(
                            C03PacketPlayer.C04PacketPlayerPosition(
                                thePlayer.posX,
                                thePlayer.posY,
                                thePlayer.posZ,
                                true
                            )
                        )
                        thePlayer.motionY = 0.1

                        MovementUtils.strafe()
                        thePlayer.fallDistance = 0f
                    }
                }
            }
        }
        if(modeValue.get().lowercase() == "minemora-blink" && !KevinClient.moduleManager.getModule(Fly::class.java).state && !KevinClient.moduleManager.getModule(HighJump::class.java).state) {
            if(!blink){
                if(canBlink && thePlayer.fallDistance - lastFound > maxDistanceWithoutGround.get()){
                    posX=mc.thePlayer!!.posX
                    posY=mc.thePlayer!!.posY
                    posZ=mc.thePlayer!!.posZ
                    motionX=mc.thePlayer!!.motionX
                    motionY=mc.thePlayer!!.motionY
                    motionZ=mc.thePlayer!!.motionZ
                    packetCache.clear()
                    mc.thePlayer!!.fallDistance = 0f
                    blink=true
                }

                if(mc.thePlayer!!.onGround){
                    canBlink=true
                }
            }else{
                if(mc.thePlayer!!.fallDistance>5){
                    mc.thePlayer!!.setPositionAndUpdate(posX,posY+yBoost.get(),posZ)

                    mc.thePlayer!!.motionX=motionX
                    mc.thePlayer!!.motionY=0.toDouble()
                    mc.thePlayer!!.motionZ=motionZ

                    if(autoScaffold.get()){
                        KevinClient.moduleManager.getModule(Scaffold::class.java).state = true
                        scaffoldOn = true
                    }

                    packetCache.clear()
                    blink=false
                    canBlink=false
                }else if(mc.thePlayer!!.onGround){
                    blink=false

                    for(packet in packetCache){
                        mc.netHandler.addToSendQueue(packet)
                    }
                }
            }
            if(scaffoldOn){
                if(mc.thePlayer!!.onGround || mc.thePlayer!!.isOnLadder || mc.thePlayer!!.isInWater || mc.thePlayer!!.isInWeb || mc.thePlayer!!.fallDistance > 10){
                    KevinClient.moduleManager.getModule(Scaffold::class.java).state = false
                    scaffoldOn = false
                }
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (detectedLocation == null || !indicator.get() ||
            thePlayer.fallDistance + (thePlayer.posY - (detectedLocation!!.y + 1)) < 3)
            return

        val x = detectedLocation!!.x
        val y = detectedLocation!!.y
        val z = detectedLocation!!.z

        val renderManager = mc.renderManager

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glLineWidth(2f)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        RenderUtils.glColor(Color(255, 0, 0, 90))
        RenderUtils.drawFilledBox(
            AxisAlignedBB(
            x - renderManager.renderPosX,
            y + 1 - renderManager.renderPosY,
            z - renderManager.renderPosZ,
            x - renderManager.renderPosX + 1.0,
            y + 1.2 - renderManager.renderPosY,
            z - renderManager.renderPosZ + 1.0)
        )

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)

        val fallDist = floor(thePlayer.fallDistance + (thePlayer.posY - (y + 0.5))).toInt()

        RenderUtils.renderNameTag("${fallDist}m (~${max(0, fallDist - 3)} damage)", x + 0.5, y + 1.7, z + 0.5)

        GlStateManager.resetColor()
    }
    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet=event.packet
        if (packet is C03PacketPlayer) {
            if (modeValue.get().lowercase(Locale.getDefault()) == "minemora-blink" && blink) {
                packetCache.add(packet)
                event.cancelEvent()
            } else if (modeValue equal "OldHypixel-Flag" && detectedLocation != null && mc.thePlayer.fallDistance - lastFound > maxDistanceWithoutGround.get()) {
                packet.y += RandomUtils.nextDouble(10.0, 12.0)
                mc.thePlayer.fallDistance = -1f
            }
        } else if (modeValue equal "minemora-blink" && blink && (packet is C0APacketAnimation || packet is C0BPacketEntityAction || packet is C02PacketUseEntity || packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement || packet is C09PacketHeldItemChange)) {
            packetCache.add(packet)
            event.cancelEvent()
        }
    }
    @EventTarget
    fun onBB(event: BlockBBEvent) {
        if (modeValue equal "VulcanGhost" && !KevinClient.moduleManager.getModule(Fly::class.java).state && !KevinClient.moduleManager.getModule(HighJump::class.java).state && (mc.thePlayer.fallDistance - lastFound >= maxDistanceWithoutGround.get())) {
            if (event.block is BlockAir && event.y <= mc.thePlayer.posY && event.boundingBox == null) event.boundingBox = AxisAlignedBB.fromBounds(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(), event.x + 1.0, floor(mc.thePlayer.posY), event.z + 1.0)
        }
    }
    override val tag: String
        get() = modeValue.get()
}