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
package kevin.utils

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.Vec3
import kotlin.math.floor

typealias Collidable = (Block?) -> Boolean

object BlockUtils : MinecraftInstance() {

    @JvmStatic
    fun floorVec3(vec3: Vec3) = Vec3(floor(vec3.xCoord),floor(vec3.yCoord),floor(vec3.zCoord))

    @JvmStatic
    fun getBlock(blockPos: BlockPos): Block? = mc.theWorld?.getBlockState(blockPos)?.block

    @JvmStatic
    inline fun getMaterial(blockPos: BlockPos): Material? {
        val state = getState(blockPos)

        return state?.block?.material
    }

    @JvmStatic
    inline fun isReplaceable(blockPos: BlockPos) = getMaterial(blockPos)?.isReplaceable ?: false

    @JvmStatic
    inline fun getState(blockPos: BlockPos): IBlockState? = mc.theWorld?.getBlockState(blockPos)

    @JvmStatic
    fun canBeClicked(blockPos: BlockPos) = getBlock(blockPos)?.canCollideCheck(getState(blockPos), false) ?: false &&
            mc.theWorld!!.worldBorder.contains(blockPos)

    @JvmStatic
    fun getBlockName(id: Int): String = Block.getBlockById(id)!!.localizedName

    @JvmStatic
    fun isFullBlock(blockPos: BlockPos): Boolean {
        val axisAlignedBB = getBlock(blockPos)?.getCollisionBoundingBox(mc.theWorld!!, blockPos, getState(blockPos)
            ?: return false)
            ?: return false
        return axisAlignedBB.maxX - axisAlignedBB.minX == 1.0 && axisAlignedBB.maxY - axisAlignedBB.minY == 1.0 && axisAlignedBB.maxZ - axisAlignedBB.minZ == 1.0
    }

    @JvmStatic
    fun getCenterDistance(blockPos: BlockPos) =
        mc.thePlayer!!.getDistance(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5)

    @JvmStatic
    fun searchBlocks(radius: Int): Map<BlockPos, Block> {
        val blocks = mutableMapOf<BlockPos, Block>()

        val thePlayer = mc.thePlayer ?: return blocks

        for (x in radius downTo -radius + 1) {
            for (y in radius downTo -radius + 1) {
                for (z in radius downTo -radius + 1) {
                    val blockPos = BlockPos(thePlayer.posX.toInt() + x, thePlayer.posY.toInt() + y,
                        thePlayer.posZ.toInt() + z)
                    val block = getBlock(blockPos) ?: continue

                    blocks[blockPos] = block
                }
            }
        }

        return blocks
    }

    @JvmStatic
    fun collideBlock(axisAlignedBB: AxisAlignedBB, collide: Collidable): Boolean {
        val thePlayer = mc.thePlayer!!

        for (x in floor(thePlayer.entityBoundingBox.minX).toInt() until
                floor(thePlayer.entityBoundingBox.maxX).toInt() + 1L) {
            for (z in floor(thePlayer.entityBoundingBox.minZ).toInt() until
                    floor(thePlayer.entityBoundingBox.maxZ).toInt() + 1) {
                val block = getBlock(BlockPos(x.toDouble(), axisAlignedBB.minY, z.toDouble()))

                if (!collide(block))
                    return false
            }
        }

        return true
    }

    @JvmStatic
    fun collideBlockIntersects(axisAlignedBB: AxisAlignedBB, collide: Collidable): Boolean {
        val thePlayer = mc.thePlayer!!
        val world = mc.theWorld!!

        for (x in floor(thePlayer.entityBoundingBox.minX).toInt() until
                floor(thePlayer.entityBoundingBox.maxX).toInt() + 1) {
            for (z in floor(thePlayer.entityBoundingBox.minZ).toInt() until
                    floor(thePlayer.entityBoundingBox.maxZ).toInt() + 1) {
                val blockPos = BlockPos(x.toDouble(), axisAlignedBB.minY, z.toDouble())
                val block = getBlock(blockPos)

                if (collide(block)) {
                    val boundingBox = getState(blockPos)?.let { block?.getCollisionBoundingBox(world, blockPos, it) }
                        ?: continue

                    if (thePlayer.entityBoundingBox.intersectsWith(boundingBox))
                        return true
                }
            }
        }
        return false
    }

}