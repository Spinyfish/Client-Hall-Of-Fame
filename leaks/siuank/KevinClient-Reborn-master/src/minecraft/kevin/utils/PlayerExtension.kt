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

import kevin.module.modules.misc.ClientFriend
import kevin.utils.ColorUtils.COLOR_PATTERN
import kevin.utils.ColorUtils.stripColor
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.entity.monster.EntityGolem
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityBat
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MovingObjectPosition
import net.minecraft.util.Vec3
import kotlin.math.*

fun Entity.getDistanceToEntityBox(entity: Entity): Double {
    val eyes = this.getPositionEyes(1F)
    val pos = getNearestPointBB(eyes, entity.entityBoundingBox)
    val xDist = abs(pos.xCoord - eyes.xCoord)
    val yDist = abs(pos.yCoord - eyes.yCoord)
    val zDist = abs(pos.zCoord - eyes.zCoord)
    return sqrt(xDist.pow(2) + yDist.pow(2) + zDist.pow(2))
}
fun Entity.getLookDistanceToEntityBox(entity: Entity=this, rotation: Rotation? = null, range: Double=10.0): Double {
    val eyes = this.getPositionEyes(1F)
    val end = (rotation?: RotationUtils.bestServerRotation()).toDirection().multiply(range).add(eyes)
    return entity.entityBoundingBox.calculateIntercept(eyes, end)?.hitVec?.distanceTo(eyes) ?: Double.MAX_VALUE
}

fun Entity.rayTraceWithServerSideRotation(range: Double): MovingObjectPosition {
    val eyes = this.getPositionEyes(1f)
    val end = RotationUtils.bestServerRotation().toDirection().multiply(range).add(eyes)
    return this.worldObj.rayTraceBlocks(eyes, end, false, false, true)
}

fun getNearestPointBB(eye: Vec3, box: AxisAlignedBB): Vec3 {
    val origin = doubleArrayOf(eye.xCoord, eye.yCoord, eye.zCoord)
    val destMins = doubleArrayOf(box.minX, box.minY, box.minZ)
    val destMaxs = doubleArrayOf(box.maxX, box.maxY, box.maxZ)
    for (i in 0..2) {
        if (origin[i] > destMaxs[i]) origin[i] = destMaxs[i] else if (origin[i] < destMins[i]) origin[i] = destMins[i]
    }
    return Vec3(origin[0], origin[1], origin[2])
}

fun EntityPlayer.getPing(): Int {
    val playerInfo = MinecraftInstance.mc.netHandler.getPlayerInfo(uniqueID)
    return playerInfo?.responseTime ?: 0
}

fun Entity.isAnimal(): Boolean {
    return this is EntityAnimal ||
            this is EntityVillager ||
            this is EntitySquid ||
            this is EntityGolem ||
            this is EntityBat
}

fun Entity.isMob(): Boolean {
    return this is EntityMob ||
            this is EntitySlime ||
            this is EntityGhast ||
            this is EntityDragon
}

fun EntityPlayer.isClientFriend(): Boolean {
    val entityName = name ?: return false
    return ClientFriend.state && ClientFriend.isFriend(COLOR_PATTERN.matcher(entityName).replaceAll(""))
    //return LiquidBounce.fileManager.friendsConfig.isFriend(stripColor(entityName))
//    return false
}