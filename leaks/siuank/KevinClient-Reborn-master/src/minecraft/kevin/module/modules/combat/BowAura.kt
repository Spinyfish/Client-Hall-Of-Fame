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

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.EntityUtils
import kevin.utils.RenderUtils
import kevin.utils.RotationUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.Items
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.awt.Color

class BowAura : Module("BowAura","Bow KillAura.",category = ModuleCategory.COMBAT) {

    private val silentValue = BooleanValue("Silent", true)
    private val predictValue = BooleanValue("Predict", true)
    private val throughWallsValue = BooleanValue("ThroughWalls", false)
    private val predictSizeValue = FloatValue("PredictSize", 2F, 0.1F, 5F)
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Direction"), "Direction")
    private val maxDistance = FloatValue("MaxDistance",100F,5F,200F)
    private val markValue = BooleanValue("Mark", true)

    private var target: Entity? = null

    @EventTarget fun onUpdate(event: UpdateEvent){
        target = null
        val invBow = bow ?: return
        target = getTarget(priorityValue.get()) ?: return
        mc.thePlayer!!.inventory.currentItem = invBow
        mc.gameSettings.keyBindUseItem.pressed = true
        RotationUtils.faceBow(target, silentValue.get(), predictValue.get(), predictSizeValue.get())
        if (mc.thePlayer.isUsingItem&&mc.thePlayer.itemInUseDuration > 20){
            mc.gameSettings.keyBindUseItem.pressed = false
            mc.thePlayer.stopUsingItem()
            mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
        }
    }
    @EventTarget fun onRender3D(event: Render3DEvent){
        if (!markValue.get()||target==null) return
        RenderUtils.drawPlatform(target, Color(37, 126, 255, 70))
    }
    override fun onDisable() { target = null }
    private val bow: Int?
    get() {
        var arrow = false
        for (inv in mc.thePlayer.inventory.mainInventory){
            if (inv != null&&inv.item==Items.arrow) {
                arrow = true
                break
            }
        }
        if (!arrow&&!mc.playerController.isInCreativeMode) return null
        for (i in 0..8) {
            val itemStack = mc.thePlayer.inventory.getStackInSlot(i) ?: continue
            if (itemStack.item is ItemBow) {
                return i
            }
        }
        return null
    }
    private fun getTarget(priorityMode: String): Entity? {
        val targets = mc.theWorld!!.loadedEntityList.filter {
            it is EntityLivingBase&&
                    EntityUtils.isSelected(it, true)&&
                    (throughWallsValue.get() || mc.thePlayer!!.canEntityBeSeen(it))&&
                    mc.thePlayer.getDistanceToEntity(it)<=maxDistance.get()
        }
        return when {
            priorityMode.equals("distance", true) -> targets.minByOrNull { mc.thePlayer!!.getDistanceToEntity(it) }
            priorityMode.equals("direction", true) -> targets.minByOrNull { RotationUtils.getRotationDifference(it) }
            priorityMode.equals("health", true) -> targets.minByOrNull { (it as EntityLivingBase).health }
            else -> null
        }
    }
}