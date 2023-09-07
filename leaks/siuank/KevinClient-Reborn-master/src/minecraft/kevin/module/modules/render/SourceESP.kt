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
package kevin.module.modules.render

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent
import kevin.module.*
import kevin.utils.BlockUtils
import kevin.utils.ColorUtils
import kevin.utils.MSTimer
import kevin.utils.RenderUtils
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList

class SourceESP: Module("SourceESP", "Allow you to see the source of lava/water.", category = ModuleCategory.RENDER) {
    private val waterMode = ListValue("WaterMode", arrayOf("Box", "2D"), "Box")
    private val waterRadiusValue = IntegerValue("WaterRadius", 40, 5, 120)
    private val waterLimitValue = IntegerValue("WaterLimit", 128, 0, 1024)
    private val waterRedValue = IntegerValue("WaterR", 52, 0, 255)
    private val waterGreenValue = IntegerValue("WaterG", 129, 0, 255)
    private val waterBlueValue = IntegerValue("WaterB", 184, 0, 255)
    private val waterRainbow = BooleanValue("WaterRainbow", false)
    private val waterEnable
    get() = waterLimitValue.get() != 0
    private val waterSearchTimer = MSTimer()
    private val waterPosList: MutableList<BlockPos> = ArrayList()
    private var waterThread: Thread? = null

    private val lavaMode = ListValue("LavaMode", arrayOf("Box", "2D"), "Box")
    private val lavaRadiusValue = IntegerValue("LavaRadius", 40, 5, 120)
    private val lavaLimitValue = IntegerValue("LavaLimit", 128, 0, 1024)
    private val lavaRedValue = IntegerValue("LavaR", 250, 0, 255)
    private val lavaGreenValue = IntegerValue("LavaG", 132, 0, 255)
    private val lavaBlueValue = IntegerValue("LavaB", 43, 0, 255)
    private val lavaRainbow = BooleanValue("LavaRainbow", false)
    private val lavaEnable
        get() = lavaLimitValue.get() != 0
    private val lavaSearchTimer = MSTimer()
    private val lavaPosList: MutableList<BlockPos> = ArrayList()
    private var lavaThread: Thread? = null

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if(waterEnable) if (waterSearchTimer.hasTimePassed(1000L) && (waterThread == null || !waterThread!!.isAlive)) {
            val radius = waterRadiusValue.get()
            waterThread = Thread({
                val blockList: MutableList<BlockPos> = ArrayList()
                for (x in -radius until radius) {
                    for (y in radius downTo -radius + 1) {
                        for (z in -radius until radius) {
                            val thePlayer = mc.thePlayer ?: return@Thread
                            val xPos = thePlayer.posX.toInt() + x
                            val yPos = thePlayer.posY.toInt() + y
                            val zPos = thePlayer.posZ.toInt() + z
                            val blockPos = BlockPos(xPos, yPos, zPos)
                            val block = BlockUtils.getBlock(blockPos)
                            if (block == Blocks.water && (mc.theWorld?.getBlockState(blockPos)?.getValue(BlockLiquid.LEVEL) ?: 1) == 0 && blockList.size < waterLimitValue.get()) blockList.add(blockPos)
                        }
                    }
                }
                waterSearchTimer.reset()
                synchronized(waterPosList) {
                    waterPosList.clear()
                    waterPosList.addAll(blockList)
                }
            }, "SourceESP-WaterFinder")
            waterThread!!.start()
        }
        if(lavaEnable) if (lavaSearchTimer.hasTimePassed(1000L) && (lavaThread == null || !lavaThread!!.isAlive)) {
            val radius = lavaRadiusValue.get()
            lavaThread = Thread({
                val blockList: MutableList<BlockPos> = ArrayList()
                for (x in -radius until radius) {
                    for (y in radius downTo -radius + 1) {
                        for (z in -radius until radius) {
                            val thePlayer = mc.thePlayer ?: return@Thread
                            val xPos = thePlayer.posX.toInt() + x
                            val yPos = thePlayer.posY.toInt() + y
                            val zPos = thePlayer.posZ.toInt() + z
                            val blockPos = BlockPos(xPos, yPos, zPos)
                            val block = BlockUtils.getBlock(blockPos)
                            if (block == Blocks.lava && (mc.theWorld?.getBlockState(blockPos)?.getValue(BlockLiquid.LEVEL) ?: 1) == 0 && blockList.size < lavaLimitValue.get()) blockList.add(blockPos)
                        }
                    }
                }
                lavaSearchTimer.reset()
                synchronized(lavaPosList) {
                    lavaPosList.clear()
                    lavaPosList.addAll(blockList)
                }
            }, "SourceESP-LavaFinder")
            lavaThread!!.start()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        if (waterEnable) synchronized(waterPosList) {
            for (blockPos in waterPosList) {
                val color = if (waterRainbow.get()) ColorUtils.rainbow() else Color(
                    waterRedValue.get(),
                    waterGreenValue.get(),
                    waterBlueValue.get()
                )
                when (waterMode.get().lowercase(Locale.getDefault())) {
                    "box" -> RenderUtils.drawBlockBox(blockPos, color, true)
                    "2d" -> RenderUtils.draw2D(blockPos, color.rgb, Color.BLACK.rgb)
                }
            }
        }
        if (lavaEnable) synchronized(lavaPosList) {
            for (blockPos in lavaPosList) {
                val color = if (lavaRainbow.get()) ColorUtils.rainbow() else Color(
                    lavaRedValue.get(),
                    lavaGreenValue.get(),
                    lavaBlueValue.get()
                )
                when (lavaMode.get().lowercase(Locale.getDefault())) {
                    "box" -> RenderUtils.drawBlockBox(blockPos, color, true)
                    "2d" -> RenderUtils.draw2D(blockPos, color.rgb, Color.BLACK.rgb)
                }
            }
        }
    }
}