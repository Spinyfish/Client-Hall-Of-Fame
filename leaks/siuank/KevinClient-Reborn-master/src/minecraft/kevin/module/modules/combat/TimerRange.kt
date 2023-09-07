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
package kevin.module.modules.combat

import kevin.event.EventState
import kevin.event.EventTarget
import kevin.event.MotionEvent
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.Module
import kevin.utils.*
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import kotlin.math.min

/* Modified class:
 * Minecraft.java
 */
object TimerRange: Module("TimerRange", "Make you walk to target faster", category = ModuleCategory.COMBAT) {
    private val mode = ListValue("Mode", arrayOf("RayCast", "Radius"), "RayCast")
    private val minDistance: FloatValue = object : FloatValue("MinDistance", 3F, 0F, 4F) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            if (newValue > maxDistance.get()) set(maxDistance.get())
        }
    }
    private val maxDistance: FloatValue = object : FloatValue("MaxDistance", 4F, 3F, 7F) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            if (newValue < minDistance.get()) set(minDistance.get())
        }
    }
    private val rangeMode = ListValue("RangeMode", arrayOf("Setting", "Smart"), "Smart")
    private val maxTimeValue = IntegerValue("MaxTime", 3, 0, 20)
    private val delayValue = IntegerValue("Delay", 5, 0, 20)
    private val maxHurtTimeValue = IntegerValue("TargetMaxHurtTime", 2, 0, 10)
    private val onlyKillAura = BooleanValue("OnlyKillAura", true)
    private val auraClick = BooleanValue("AuraClick", true)
    private val onlyPlayer = BooleanValue("OnlyPlayer", true)
    private val debug = BooleanValue("Debug", false)
    private val betterAnimation = BooleanValue("BetterAnimation", true)
    private val reverseValue = BooleanValue("Reverse", false)
    private val minReverseRange : FloatValue = object : FloatValue("MinReverseRange", 2.6f, 1f, 4f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            if (newValue > minDistance.get()) set(minDistance.get())
        }
    }
    private val reverseTime : IntegerValue = object : IntegerValue("ReverseStopTime", 3, 1, 10) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            if (newValue < reverseTickTime.get()) set(reverseTickTime.get())
        }
    }
    private val reverseTickTime : IntegerValue = object : IntegerValue("ReverseTickTime", 3, 0, 10) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            if (newValue > reverseTime.get()) set(reverseTime.get())
        }
    }
    private val reverseDelay = IntegerValue("ReverseDelay", 5, 0, 20)
    private val reverseTargetMaxHurtTime = IntegerValue("ReverseTargetMaxHurtTime", 3, 0, 10)
    private val reverseAuraClick = ListValue("ReverseAuraClick", arrayOf("None", "BeforeTimer", "AfterTimer"), "None")

    private val killAura: KillAura by lazy { KevinClient.moduleManager.getModule(KillAura::class.java) }

    @JvmStatic
    private var working = false
    private var stopWorking = false
    private var lastNearest = 10.0
    private var cooldown = 0
    private var freezeTicks = 0
    private var reverseFreeze = true
    private var firstAnimation = true

    @EventTarget fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) return // post event mean player's tick is done
        val thePlayer = mc.thePlayer ?: return
        if (onlyKillAura.get() && !killAura.state) return
        if (mode equal "RayCast") {
            val entity = RaycastUtils.raycastEntity(maxDistance.get() + 1.0, object : RaycastUtils.EntityFilter {
                override fun canRaycast(entity: Entity?): Boolean {
                    return entity != null && entity is EntityLivingBase && (!onlyPlayer.get() || entity is EntityPlayer)
                }
            })
            if (entity == null || entity !is EntityLivingBase) {
                lastNearest = 10.0
                return
            }
            if (!EntityUtils.isSelected(entity, true)) return
            val vecEyes = thePlayer.getPositionEyes(1f)
            val predictEyes = if (rangeMode equal "Smart") {
                thePlayer.getPositionEyes(maxTimeValue.get() + 1f)
            } else thePlayer.getPositionEyes(3f)
            val entityBox = entity.entityBoundingBox.expands(entity.collisionBorderSize.toDouble())
            val box = getNearestPointBB(
                vecEyes,
                entityBox
            )
            val box2 = getNearestPointBB(
                predictEyes,
                if (entity is EntityOtherPlayerMP) {
                    entityBox.offset(entity.otherPlayerMPX - entity.posX, entity.otherPlayerMPY - entity.posY, entity.otherPlayerMPZ - entity.posZ)
                } else entityBox
            )
            val range = box.distanceTo(vecEyes)
            val afterRange = box2.distanceTo(predictEyes)
            if (!working && reverseValue.get()) {
                if (range < minReverseRange.get() && cooldown <= 0 && entity.hurtTime <= maxHurtTimeValue.get()) {
                    freezeTicks = reverseTime.get()
                    firstAnimation = false
                    reverseFreeze = true
                    return
                }
            }
            if (range < minDistance.get()) {
                stopWorking = true
            } else if (((rangeMode equal "Smart" && range > minDistance.get() && afterRange < minDistance.get() && afterRange < range) || (rangeMode equal "Setting" && range <= maxDistance.get() && range < lastNearest && afterRange < range)) && entity.hurtTime <= maxHurtTimeValue.get()) {
                stopWorking = false
                foundTarget()
            }
            lastNearest = range
        } else {
            val entityList = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                thePlayer,
                thePlayer.entityBoundingBox.expands(maxDistance.get() + 1.0)
            )
            if (entityList.isNotEmpty()) {
                val vecEyes = thePlayer.getPositionEyes(1f)
                val afterEyes = if (rangeMode equal "Smart") {
                    thePlayer.getPositionEyes(maxTimeValue.get() + 1f)
                } else thePlayer.getPositionEyes(3f)
                var targetFound = false
                var targetInRange = false
                var nearest = 10.0
                for (entity in entityList) {
                    if (entity !is EntityLivingBase) continue
                    if (onlyPlayer.get() && entity !is EntityPlayer) continue
                    if (!EntityUtils.isSelected(entity, true)) continue
                    val entityBox = entity.entityBoundingBox.expands(entity.collisionBorderSize.toDouble())
                    val box = getNearestPointBB(
                        vecEyes,
                        entityBox
                    )
                    val box2 = getNearestPointBB(
                        afterEyes,
                        if (entity is EntityOtherPlayerMP) {
                            entityBox.offset(entity.otherPlayerMPX - entity.posX, entity.otherPlayerMPY - entity.posY, entity.otherPlayerMPZ - entity.posZ)
                        } else entityBox
                    )
                    val range = box.distanceTo(vecEyes)
                    val afterRange = box2.distanceTo(afterEyes)
                    if (range < minDistance.get()) {
                        targetInRange = true
                        break
                    } else if (range <= maxDistance.get() && afterRange < range && entity.hurtTime <= maxHurtTimeValue.get()) {
                        targetFound = true
                    }
                    nearest = min(nearest, range)
                }
                if (targetInRange) {
                    stopWorking = true
                } else if (targetFound && nearest < lastNearest) {
                    stopWorking = false
                    foundTarget()
                }
                lastNearest = nearest
            } else {
                lastNearest = 10.0
            }
        }
    }

    fun foundTarget() {
        if (cooldown > 0 || freezeTicks != 0 || maxTimeValue.get() == 0) return
        cooldown = delayValue.get()
        working = true
        freezeTicks = 0
        if (betterAnimation.get()) firstAnimation = false
        while (freezeTicks <= maxTimeValue.get() - (if (auraClick.get()) 1 else 0) && !stopWorking) {
            ++freezeTicks
            mc.runTick()
        }
        if (debug.get()) ChatUtils.messageWithStart("Timer-ed")
        if (auraClick.get()) {
            killAura.clicks++
            ++freezeTicks
            mc.runTick()
            if (debug.get()) ChatUtils.messageWithStart("Clicked")
        }
        stopWorking = false
        working = false
    }

    @JvmStatic
    fun handleTick(): Boolean {
        if (working || freezeTicks < 0) return true
        if (state && freezeTicks > 0) {
            --freezeTicks
            return true
        }
        if (reverseFreeze) {
            reverseFreeze = false
            var time = reverseTickTime.get()
            working = true
            if (reverseAuraClick equal "BeforeTimer") killAura.clicks++
            while (time > 0) {
                --time
                mc.runTick()
            }
            working = false
            cooldown = reverseDelay.get()
            if (reverseAuraClick equal "AfterTimer") killAura.clicks++
        }
        if (cooldown > 0) --cooldown
        return false
    }

    @JvmStatic
    fun freezeAnimation(): Boolean {
        if (freezeTicks != 0) {
            if (!firstAnimation) {
                firstAnimation = true
                return false
            }
            return true
        }
        return false
    }
}