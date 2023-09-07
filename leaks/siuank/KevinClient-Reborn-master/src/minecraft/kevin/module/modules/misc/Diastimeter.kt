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
package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent
import kevin.event.WorldEvent
import kevin.module.BooleanValue
import kevin.module.IntegerValue
import kevin.module.Module
import kevin.utils.*
import net.minecraft.block.BlockAir
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.*
import net.minecraft.world.World
import org.lwjgl.input.Mouse
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

class Diastimeter : Module("Diastimeter","Get distance to a block.(also can get area or volume)") {
    private val areaValue = BooleanValue("Area",true)
    private val volumeValue = BooleanValue("Volume",true)

    private val renderRayTraceBlock = BooleanValue("RenderRayTraceBlock",true)
    private val renderBlocks = BooleanValue("RenderBlock",true)
    private val colorRedValue = IntegerValue("R", 0, 0, 255)
    private val colorGreenValue = IntegerValue("G", 0, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val colorRainbow = BooleanValue("Rainbow", false)

    private var blocks = ArrayList<BlockPos>()
    private var vertexBlock:Pair<BlockPos?,BlockPos?> = null to null
    private var objectPosition: MovingObjectPosition? = null

    private val timer = MSTimer()

    override fun onDisable() {
        blocks.clear()
        vertexBlock = null to null
    }

    @EventTarget
    fun onWorld(event: WorldEvent){
        blocks.clear()
        vertexBlock = null to null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if (mc.currentScreen == null && Mouse.isButtonDown(2) && timer.hasTimePassed(300)){
            timer.reset()
            val targetPos = objectPosition?.blockPos
            if ((BlockUtils.getState(targetPos?:return)?.block?.material ?: return) == Material.air) return
            when{
                vertexBlock.first==null&&vertexBlock.second==null -> {
                    vertexBlock = targetPos to null
                    blocks.add(targetPos)
                    ChatUtils.messageWithStart("§l§7[§l§9Diastimeter§l§7] §l§9First pos set to X:${targetPos.x} Y:${targetPos.y} Z:${targetPos.z}.(Distance: ${getDistanceToBlock(targetPos)})")
                }
                vertexBlock.second==null -> {
                    vertexBlock = vertexBlock.first to targetPos
                    ChatUtils.messageWithStart("§l§7[§l§9Diastimeter§l§7] §l§9Second pos set to X:${targetPos.x} Y:${targetPos.y} Z:${targetPos.z}.")
                    ChatUtils.messageWithStart("§l§7[§l§9Diastimeter§l§7] §l§9To First Distance: ${getDistanceToBlock(vertexBlock.first!!)} To Second Distance: ${getDistanceToBlock(targetPos)}")
                    ChatUtils.messageWithStart("§l§7[§l§9Diastimeter§l§7] §l§9Disparity: X: ${vertexBlock.first!!.getXDisparity(vertexBlock.second!!)} Y: ${vertexBlock.first!!.getYDisparity(vertexBlock.second!!)} Z: ${vertexBlock.first!!.getZDisparity(vertexBlock.second!!)}")
                    if (areaValue.get()) ChatUtils.messageWithStart("§l§7[§l§9Diastimeter§l§7] §l§9Area: ${getArea(vertexBlock.first!!,vertexBlock.second!!)}")
                    if (volumeValue.get()) ChatUtils.messageWithStart("§l§7[§l§9Diastimeter§l§7] §l§9Volume: ${getVolume(vertexBlock.first!!,vertexBlock.second!!)}")
                    when{
                        volumeValue.get() -> {
                            for (x in min(vertexBlock.first!!.x,vertexBlock.second!!.x)..max(vertexBlock.first!!.x,vertexBlock.second!!.x)){
                                blocks.add(BlockPos(x,vertexBlock.first!!.y,vertexBlock.first!!.z))
                                blocks.add(BlockPos(x,vertexBlock.first!!.y,vertexBlock.second!!.z))
                                blocks.add(BlockPos(x,vertexBlock.second!!.y,vertexBlock.first!!.z))
                                blocks.add(BlockPos(x,vertexBlock.second!!.y,vertexBlock.second!!.z))
                            }
                            for (z in min(vertexBlock.first!!.z,vertexBlock.second!!.z)..max(vertexBlock.first!!.z,vertexBlock.second!!.z)){
                                blocks.add(BlockPos(vertexBlock.first!!.x,vertexBlock.first!!.y,z))
                                blocks.add(BlockPos(vertexBlock.second!!.x,vertexBlock.first!!.y,z))
                                blocks.add(BlockPos(vertexBlock.first!!.x,vertexBlock.second!!.y,z))
                                blocks.add(BlockPos(vertexBlock.second!!.x,vertexBlock.second!!.y,z))
                            }
                            for (y in min(vertexBlock.first!!.y,vertexBlock.second!!.y)..max(vertexBlock.first!!.y,vertexBlock.second!!.y)){
                                blocks.add(BlockPos(vertexBlock.first!!.x,y,vertexBlock.first!!.z))
                                blocks.add(BlockPos(vertexBlock.second!!.x,y,vertexBlock.first!!.z))
                                blocks.add(BlockPos(vertexBlock.first!!.x,y,vertexBlock.second!!.z))
                                blocks.add(BlockPos(vertexBlock.second!!.x,y,vertexBlock.second!!.z))
                            }
                        }
                        areaValue.get() -> {
                            val y = max(vertexBlock.first!!.y,vertexBlock.second!!.y)
                            for (x in min(vertexBlock.first!!.x,vertexBlock.second!!.x)..max(vertexBlock.first!!.x,vertexBlock.second!!.x)){
                                blocks.add(BlockPos(x,y,vertexBlock.first!!.z))
                                blocks.add(BlockPos(x,y,vertexBlock.second!!.z))
                            }
                            for (z in min(vertexBlock.first!!.z,vertexBlock.second!!.z)..max(vertexBlock.first!!.z,vertexBlock.second!!.z)){
                                blocks.add(BlockPos(vertexBlock.first!!.x,y,z))
                                blocks.add(BlockPos(vertexBlock.second!!.x,y,z))
                            }
                        }
                        else -> blocks.add(targetPos)
                    }
                }
                else -> {
                    vertexBlock = null to null
                    blocks.clear()
                    ChatUtils.messageWithStart("§l§7[§l§9Diastimeter§l§7] §l§9Clear.")
                }
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent){
        val color = if (colorRainbow.get()) ColorUtils.rainbow() else Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
        if (renderBlocks.get()) blocks.forEach {
            RenderUtils.drawBlockBox(it, color, true)
        }
        val thePlayer = mc.thePlayer ?: return
        val entityLookVec = thePlayer.lookVec ?: return
        val lookVec = Vec3(entityLookVec.xCoord * 300, entityLookVec.yCoord * 300, entityLookVec.zCoord * 300)
        val posVec = Vec3(thePlayer.posX, thePlayer.posY + 1.62, thePlayer.posZ)
        objectPosition = mc.theWorld?.rayTraceBlocksMore(
            posVec,
            posVec.add(lookVec),
            stopOnLiquid = false,
            ignoreBlockWithoutBoundingBox = false,
            returnLastUncollidableBlock = false
        )
        if (objectPosition == null || objectPosition!!.blockPos == null) return
        if (BlockUtils.getBlock(objectPosition!!.blockPos!!) !is BlockAir&&renderRayTraceBlock.get()) {
            RenderUtils.drawBlockBox(objectPosition!!.blockPos, color, true)
        }
    }

    private fun World.rayTraceBlocksMore(
        vec31: Vec3,
        vec32: Vec3,
        stopOnLiquid: Boolean,
        ignoreBlockWithoutBoundingBox: Boolean,
        returnLastUncollidableBlock: Boolean
    ): MovingObjectPosition? {
        var vec311 = vec31
        return if (
            !java.lang.Double.isNaN(vec311.xCoord) &&
            !java.lang.Double.isNaN(vec311.yCoord) &&
            !java.lang.Double.isNaN(vec311.zCoord)
        ) {
            if (!java.lang.Double.isNaN(vec32.xCoord) &&
                !java.lang.Double.isNaN(vec32.yCoord) &&
                !java.lang.Double.isNaN(vec32.zCoord)
            ) {
                val i = MathHelper.floor_double(vec32.xCoord)
                val j = MathHelper.floor_double(vec32.yCoord)
                val k = MathHelper.floor_double(vec32.zCoord)
                var l = MathHelper.floor_double(vec311.xCoord)
                var i1 = MathHelper.floor_double(vec311.yCoord)
                var j1 = MathHelper.floor_double(vec311.zCoord)
                var blockpos = BlockPos(l, i1, j1)
                val iblockstate: IBlockState = this.getBlockState(blockpos)
                val block = iblockstate.block
                if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(
                        this,
                        blockpos,
                        iblockstate
                    ) != null) && block.canCollideCheck(iblockstate, stopOnLiquid)
                ) {
                    val movingobjectposition = block.collisionRayTrace(this, blockpos, vec311, vec32)
                    if (movingobjectposition != null) {
                        return movingobjectposition
                    }
                }
                var movingobjectposition2: MovingObjectPosition? = null
                var k1 = 15000
                while (k1-- >= 0) {
                    if (java.lang.Double.isNaN(vec311.xCoord) || java.lang.Double.isNaN(vec311.yCoord) || java.lang.Double.isNaN(
                            vec311.zCoord
                        )
                    ) {
                        return null
                    }
                    if (l == i && i1 == j && j1 == k) {
                        return if (returnLastUncollidableBlock) movingobjectposition2 else null
                    }
                    var flag2 = true
                    var flag = true
                    var flag1 = true
                    var d0 = 999.0
                    var d1 = 999.0
                    var d2 = 999.0
                    if (i > l) {
                        d0 = l.toDouble() + 1.0
                    } else if (i < l) {
                        d0 = l.toDouble() + 0.0
                    } else {
                        flag2 = false
                    }
                    if (j > i1) {
                        d1 = i1.toDouble() + 1.0
                    } else if (j < i1) {
                        d1 = i1.toDouble() + 0.0
                    } else {
                        flag = false
                    }
                    if (k > j1) {
                        d2 = j1.toDouble() + 1.0
                    } else if (k < j1) {
                        d2 = j1.toDouble() + 0.0
                    } else {
                        flag1 = false
                    }
                    var d3 = 999.0
                    var d4 = 999.0
                    var d5 = 999.0
                    val d6 = vec32.xCoord - vec311.xCoord
                    val d7 = vec32.yCoord - vec311.yCoord
                    val d8 = vec32.zCoord - vec311.zCoord
                    if (flag2) {
                        d3 = (d0 - vec311.xCoord) / d6
                    }
                    if (flag) {
                        d4 = (d1 - vec311.yCoord) / d7
                    }
                    if (flag1) {
                        d5 = (d2 - vec311.zCoord) / d8
                    }
                    if (d3 == -0.0) {
                        d3 = -1.0E-4
                    }
                    if (d4 == -0.0) {
                        d4 = -1.0E-4
                    }
                    if (d5 == -0.0) {
                        d5 = -1.0E-4
                    }
                    var enumfacing: EnumFacing
                    if (d3 < d4 && d3 < d5) {
                        enumfacing = if (i > l) EnumFacing.WEST else EnumFacing.EAST
                        vec311 = Vec3(d0, vec311.yCoord + d7 * d3, vec311.zCoord + d8 * d3)
                    } else if (d4 < d5) {
                        enumfacing = if (j > i1) EnumFacing.DOWN else EnumFacing.UP
                        vec311 = Vec3(vec311.xCoord + d6 * d4, d1, vec311.zCoord + d8 * d4)
                    } else {
                        enumfacing = if (k > j1) EnumFacing.NORTH else EnumFacing.SOUTH
                        vec311 = Vec3(vec311.xCoord + d6 * d5, vec311.yCoord + d7 * d5, d2)
                    }
                    l = MathHelper.floor_double(vec311.xCoord) - if (enumfacing == EnumFacing.EAST) 1 else 0
                    i1 = MathHelper.floor_double(vec311.yCoord) - if (enumfacing == EnumFacing.UP) 1 else 0
                    j1 = MathHelper.floor_double(vec311.zCoord) - if (enumfacing == EnumFacing.SOUTH) 1 else 0
                    blockpos = BlockPos(l, i1, j1)
                    val iblockstate1: IBlockState = this.getBlockState(blockpos)
                    val block1 = iblockstate1.block
                    if (!ignoreBlockWithoutBoundingBox || block1.getCollisionBoundingBox(
                            this,
                            blockpos,
                            iblockstate1
                        ) != null
                    ) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                            val movingobjectposition1 = block1.collisionRayTrace(this, blockpos, vec311, vec32)
                            if (movingobjectposition1 != null) {
                                return movingobjectposition1
                            }
                        } else {
                            movingobjectposition2 = MovingObjectPosition(
                                MovingObjectPosition.MovingObjectType.MISS,
                                vec311,
                                enumfacing,
                                blockpos
                            )
                        }
                    }
                }
                if (returnLastUncollidableBlock) movingobjectposition2 else null
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun getDistanceToBlock(blockPos: BlockPos)
            = mc.thePlayer.getDistance(blockPos.x.toDouble(),blockPos.y.toDouble(),blockPos.z.toDouble())

    private fun getArea(pos1: BlockPos,pos2: BlockPos)
            = pos1.getXDisparity(pos2) * pos1.getZDisparity(pos2)

    private fun getVolume(pos1: BlockPos,pos2: BlockPos)
            = pos1.getXDisparity(pos2) * pos1.getYDisparity(pos2) * pos1.getZDisparity(pos2)

    private fun BlockPos.getXDisparity(blockPos: BlockPos)
            = (min(this.x,blockPos.x)..max(this.x,blockPos.x)).count()

    private fun BlockPos.getYDisparity(blockPos: BlockPos)
            = (min(this.y,blockPos.y)..max(this.y,blockPos.y)).count()

    private fun BlockPos.getZDisparity(blockPos: BlockPos)
            = (min(this.z,blockPos.z)..max(this.z,blockPos.z)).count()
}