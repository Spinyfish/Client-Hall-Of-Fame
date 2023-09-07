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
import kevin.module.FloatValue
import kevin.module.ListValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.BlockUtils.collideBlockIntersects
import kevin.utils.MovementUtils
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class WallClimb : Module("WallClimb", "Allows you to climb up walls like a spider.", category = ModuleCategory.MOVEMENT) {
    private val modeValue = ListValue("Mode", arrayOf("Simple", "CheckerClimb", "Clip", "AAC3.3.12", "AACGlide"), "Simple")
    private val clipMode = ListValue("ClipMode", arrayOf("Jump", "Fast"), "Fast")
    private val checkerClimbMotionValue = FloatValue("CheckerClimbMotion", 0f, 0f, 1f)

    private var glitch = false
    private var waited = 0

    @EventTarget
    fun onMove(event: MoveEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (!thePlayer.isCollidedHorizontally || thePlayer.isOnLadder || thePlayer.isInWater || thePlayer.isInLava)
            return

        if ("simple".equals(modeValue.get(), ignoreCase = true)) {
            event.y = 0.2
            thePlayer.motionY = 0.0
        }
    }

    @EventTarget
    fun onUpdate(event: MotionEvent) {
        val thePlayer = mc.thePlayer

        if (event.eventState != EventState.POST || thePlayer == null)
            return


        when (modeValue.get().lowercase(Locale.getDefault())) {
            "clip" -> {
                if (thePlayer.motionY < 0)
                    glitch = true
                if (thePlayer.isCollidedHorizontally) {
                    when (clipMode.get().lowercase(Locale.getDefault())) {
                        "jump" -> if (thePlayer.onGround)
                            thePlayer.jump()
                        "fast" -> if (thePlayer.onGround)
                            thePlayer.motionY = 0.42
                        else if (thePlayer.motionY < 0)
                            thePlayer.motionY = -0.3
                    }
                }
            }
            "checkerclimb" -> {
                val isInsideBlock = collideBlockIntersects(thePlayer.entityBoundingBox) {
                    (it) !is BlockAir
                }
                val motion = checkerClimbMotionValue.get()

                if (isInsideBlock && motion != 0f)
                    thePlayer.motionY = motion.toDouble()
            }
            "aac3.3.12" -> if (thePlayer.isCollidedHorizontally && !thePlayer.isOnLadder) {
                waited++
                if (waited == 1)
                    thePlayer.motionY = 0.43
                if (waited == 12)
                    thePlayer.motionY = 0.43
                if (waited == 23)
                    thePlayer.motionY = 0.43
                if (waited == 29)
                    thePlayer.setPosition(thePlayer.posX, thePlayer.posY + 0.5, thePlayer.posZ)
                if (waited >= 30)
                    waited = 0
            } else if (thePlayer.onGround) waited = 0
            "aacglide" -> {
                if (!thePlayer.isCollidedHorizontally || thePlayer.isOnLadder) return
                thePlayer.motionY = -0.19
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if ((event.packet)is C03PacketPlayer) {
            val packetPlayer = event.packet

            if (glitch) {
                val yaw = MovementUtils.direction.toFloat()
                packetPlayer.x = packetPlayer.x - sin(yaw) * 0.00000001
                packetPlayer.z = packetPlayer.z + cos(yaw) * 0.00000001
                glitch = false
            }
        }
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        val thePlayer = mc.thePlayer ?: return

        val mode = modeValue.get()

        when (mode.lowercase(Locale.getDefault())) {
            "checkerclimb" -> if (event.y > thePlayer.posY) event.boundingBox = null
            "clip" -> if (event.block != null && mc.thePlayer != null && (event.block) is BlockAir && event.y < thePlayer.posY && thePlayer.isCollidedHorizontally && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isInLava) event.boundingBox =
                AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(
                    thePlayer.posX,
                    thePlayer.posY.toInt() - 1.0,
                    thePlayer.posZ
                )
        }
    }
}