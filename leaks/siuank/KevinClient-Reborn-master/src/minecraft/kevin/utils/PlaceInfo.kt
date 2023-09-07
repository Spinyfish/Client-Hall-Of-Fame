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

import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3

class PlaceInfo(val blockPos: BlockPos, val enumFacing: EnumFacing,
                var vec3: Vec3 = Vec3(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5)
) {

    companion object {

        /**
         * Allows you to find a specific place info for your [blockPos]
         */
        @JvmStatic
        fun get(blockPos: BlockPos): PlaceInfo? {
            return when {
                BlockUtils.canBeClicked(blockPos.add(0, -1, 0)) ->
                    return PlaceInfo(blockPos.add(0, -1, 0), EnumFacing.UP)
                BlockUtils.canBeClicked(blockPos.add(0, 0, 1)) ->
                    return PlaceInfo(blockPos.add(0, 0, 1), EnumFacing.NORTH)
                BlockUtils.canBeClicked(blockPos.add(-1, 0, 0)) ->
                    return PlaceInfo(blockPos.add(-1, 0, 0), EnumFacing.EAST)
                BlockUtils.canBeClicked(blockPos.add(0, 0, -1)) ->
                    return PlaceInfo(blockPos.add(0, 0, -1), EnumFacing.SOUTH)
                BlockUtils.canBeClicked(blockPos.add(1, 0, 0)) ->
                    PlaceInfo(blockPos.add(1, 0, 0), EnumFacing.WEST)
                else -> null
            }
        }

    }
}