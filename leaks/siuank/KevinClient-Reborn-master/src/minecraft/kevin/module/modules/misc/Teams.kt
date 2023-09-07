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
import kevin.event.UpdateEvent
import kevin.event.WorldEvent
import kevin.hud.element.elements.ConnectNotificationType
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.*
import kevin.utils.BlockUtils.getBlock
import kevin.utils.MSTimer
import kevin.utils.RandomUtils
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import java.util.concurrent.CopyOnWriteArrayList

class Teams : Module("Teams","Prevents Killaura from attacking team mates.", category = ModuleCategory.MISC) {
    private val scoreboardValue = BooleanValue("ScoreboardTeam", true)
    private val armorColorValue = BooleanValue("ArmorColor", false)
    private val armorColorArmorValue = ListValue("ArmorColorArmor", arrayOf("Helmet", "Plate", "Legs", "Boots", "Random", "First", "First(IgnoreNotCorresponding)"), "First")
    private val colorValue = BooleanValue("Color", true)
    private val gommeSWValue = BooleanValue("GommeSW", false)

    val bedCheckValue = BooleanValue("BedCheck",true)
    private val bedCheckWaitTime = IntegerValue("BedCheckWaitTime",1000,500,5000)
    private val bedCheckXRange = IntegerValue("BedCheckXRange",50,10,128)
    private val bedCheckYRange = IntegerValue("BedCheckYRange",50,10,128)
    private val bedCheckZRange = IntegerValue("BedCheckZRange",50,10,128)
    private val bedCheckState = TextValue("BedCheckState","")
    var teamBed = CopyOnWriteArrayList<BlockPos>()
    private var needCheck = true
    private val waitTimer = MSTimer()
    private var thread:Thread? = null

    fun isInYourTeam(entity: EntityLivingBase): Boolean {
        val thePlayer = mc.thePlayer ?: return false

        if (scoreboardValue.get() && thePlayer.team != null && entity.team != null &&
            thePlayer.team!!.isSameTeam(entity.team!!))
            return true

        val displayName = thePlayer.displayName

        if (gommeSWValue.get() && displayName != null && entity.displayName != null) {
            val targetName = entity.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            if (targetName.startsWith("T") && clientName.startsWith("T"))
                if (targetName[1].isDigit() && clientName[1].isDigit())
                    return targetName[1] == clientName[1]
        }

        if (armorColorValue.get()) {
            when(armorColorArmorValue.get()) {
                "Helmet" -> if (checkArmor(0, entity to thePlayer)) return true
                "Plate" -> if (checkArmor(1, entity to thePlayer)) return true
                "Legs" -> if (checkArmor(2, entity to thePlayer)) return true
                "Boots" -> if (checkArmor(3, entity to thePlayer)) return true
                "Random" -> if (checkArmor(RandomUtils.nextInt(0, 3), entity to thePlayer)) return true
                "First" -> for (i in 1..3)
                    if (checkArmor(i, entity to thePlayer))
                        return true
                "First(IgnoreNotCorresponding)" -> {
                    val targetColors = arrayListOf<Int>()
                    val playerColors = arrayListOf<Int>()
                    for (i in 0..3) {
                        val targetArmor = entity.getCurrentArmor(i)
                        if (targetArmor != null && targetArmor.item is ItemArmor && (targetArmor.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER)
                            targetColors.add(targetArmor.getArmorColor())

                        val playerArmor = thePlayer.getCurrentArmor(i)
                        if (playerArmor != null && playerArmor.item is ItemArmor && (targetArmor.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER)
                            playerColors.add(playerArmor.getArmorColor())
                    }
                    for (c in targetColors)
                        if (playerColors.contains(c))
                            return true
                }
            }
        }

        if (colorValue.get() && displayName != null && entity.displayName != null) {
            val targetName = entity.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            return targetName.startsWith("§${clientName[1]}")
        }

        return false
    }
    fun ItemStack.getArmorColor() =
        (this.item as ItemArmor).getColor(this)
    fun checkArmor(i: Int, entities: Pair<EntityLivingBase,EntityLivingBase>): Boolean {
        val firstEntityArmor = entities.first.getCurrentArmor(i) ?: return false
        val secondEntityArmor = entities.second.getCurrentArmor(i) ?: return false
        val firstItem = firstEntityArmor.item
        val secondItem = secondEntityArmor.item
        if (firstItem !is ItemArmor || secondItem !is ItemArmor) return false
        if (firstItem.armorMaterial != ItemArmor.ArmorMaterial.LEATHER || secondItem.armorMaterial != ItemArmor.ArmorMaterial.LEATHER) return false
        return firstEntityArmor.getArmorColor() == secondEntityArmor.getArmorColor()
    }
    @EventTarget
    fun onWorld(event: WorldEvent){
        if (event.worldClient==null) return
        if (!bedCheckValue.get()){
            bedCheckState.set("Bed check is disable.")
            teamBed.clear()
            needCheck = false
            return
        }
        if (mc.isIntegratedServerRunning) {
            bedCheckState.set("Integrated server running.")
            teamBed.clear()
            needCheck = false
            return
        }
        teamBed.clear()
        needCheck = true
        waitTimer.reset()
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if (needCheck&&waitTimer.hasTimePassed(bedCheckWaitTime.get().toLong())&&bedCheckValue.get()&&(thread == null || !thread!!.isAlive)){

            thread = Thread({
                val bedList = ArrayList<BlockPos>()
                val player = mc.thePlayer
                val px = player.posX.toInt()
                val py = player.posY.toInt()
                val pz = player.posZ.toInt()
                for (x in -bedCheckXRange.get() until bedCheckXRange.get()) {
                    for (z in -bedCheckZRange.get() until bedCheckZRange.get()){
                        for (y in bedCheckYRange.get() downTo -bedCheckYRange.get()+1){
                            val blockPos = BlockPos(px+x,py+y,pz+z)
                            val block = getBlock(blockPos)
                            if (block == Blocks.bed) bedList.add(blockPos)
                        }
                    }
                }
                waitTimer.reset()
                teamBed.clear()
                bedList.sortBy { mc.thePlayer.getDistance(it.x.toDouble(),it.y.toDouble(),it.z.toDouble()) }
                if (bedList.isNotEmpty()){
                    teamBed.add(bedList.first())
                    val x = bedList.first().x
                    val y = bedList.first().y
                    val z = bedList.first().z
                    val blockPosList = arrayListOf(
                        BlockPos(x+1,y,z),
                        BlockPos(x-1,y,z),
                        BlockPos(x,y,z+1),
                        BlockPos(x,y,z-1)
                    )
                    teamBed.addAll(blockPosList.filter { getBlock(it) == Blocks.bed })
                }
                if (teamBed.isEmpty()){
                    bedCheckState.set("No bed fond.")
                    //KevinClient.hud.addNotification(Notification("No bed fond."),"Bed Checker")
                } else {
                    val pos = teamBed.first()
                    bedCheckState.set("Fond team bed at X:${pos.x} Y:${pos.y} Z:${pos.z}.")
                    KevinClient.hud.addNotification(Notification("Fond team bed at X:${pos.x} Y:${pos.y} Z:${pos.z}.", "Bed Checker", ConnectNotificationType.OK))
                    needCheck = false
                }
            },"BedCheckerThread")

            thread!!.start()
        }
    }
}