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
package kevin.module.modules.world

import kevin.event.EventTarget
import kevin.event.Render3DEvent
import kevin.event.UpdateEvent
import kevin.main.KevinClient
import kevin.module.*
import kevin.module.modules.player.AutoTool
import kevin.utils.BlockUtils
import kevin.utils.BlockUtils.getCenterDistance
import kevin.utils.BlockUtils.searchBlocks
import kevin.utils.RenderUtils
import kevin.utils.RotationUtils
import kevin.utils.TickTimer
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockLiquid
import net.minecraft.init.Blocks
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.Vec3
import java.awt.Color
import kotlin.math.roundToInt

class Nuker : Module("Nuker", "Breaks all blocks around you.", category = ModuleCategory.WORLD) {

    /**
     * OPTIONS
     */

    private val radiusValue = FloatValue("Radius", 5.2F, 1F, 6F)
    private val throughWallsValue = BooleanValue("ThroughWalls", false)
    private val priorityValue = ListValue("Priority", arrayOf("Distance", "Hardness"), "Distance")
    private val rotationsValue = BooleanValue("Rotations", true)
    private val layerValue = BooleanValue("Layer", false)
    private val hitDelayValue = IntegerValue("HitDelay", 4, 0, 20)
    private val nukeValue = IntegerValue("Nuke", 1, 1, 20)
    private val nukeDelay = IntegerValue("NukeDelay", 1, 1, 20)

    /**
     * VALUES
     */

    private val attackedBlocks = arrayListOf<BlockPos>()
    private var currentBlock: BlockPos? = null
    private var blockHitDelay = 0

    private var nukeTimer = TickTimer()
    private var nuke = 0

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        // Block hit delay
        if (blockHitDelay > 0 && !KevinClient.moduleManager.getModule(FastBreak::class.java).state) {
            blockHitDelay--
            return
        }

        // Reset bps
        nukeTimer.update()
        if (nukeTimer.hasTimePassed(nukeDelay.get())) {
            nuke = 0
            nukeTimer.reset()
        }

        // Clear blocks
        attackedBlocks.clear()

        val thePlayer = mc.thePlayer!!

        if (!mc.playerController.isInCreativeMode) {
            // Default nuker

            val validBlocks = searchBlocks(radiusValue.get().roundToInt() + 1)
                .filter { (pos, block) ->
                    if (getCenterDistance(pos) <= radiusValue.get() && validBlock(block)) {
                        if (layerValue.get() && pos.y < thePlayer.posY) { // Layer: Break all blocks above you
                            return@filter false
                        }

                        if (!throughWallsValue.get()) { // ThroughWalls: Just break blocks in your sight
                            // Raytrace player eyes to block position (through walls check)
                            val eyesPos = Vec3(thePlayer.posX, thePlayer.entityBoundingBox.minY +
                                    thePlayer.eyeHeight, thePlayer.posZ)
                            val blockVec = Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
                            val rayTrace = mc.theWorld!!.rayTraceBlocks(eyesPos, blockVec,
                                false, true, false)

                            // Check if block is visible
                            rayTrace != null && rayTrace.blockPos == pos
                        }else true // Done
                    }else false // Bad block
                }.toMutableMap()

            do{
                val (blockPos, block) = when(priorityValue.get()) {
                    "Distance" -> validBlocks.minByOrNull { (pos, block) ->
                        val distance = getCenterDistance(pos)
                        val safePos = BlockPos(thePlayer.posX, thePlayer.posY - 1, thePlayer.posZ)

                        if (pos.x == safePos.x && safePos.y <= pos.y && pos.z == safePos.z)
                            Double.MAX_VALUE - distance // Last block
                        else
                            distance
                    }
                    "Hardness" -> validBlocks.maxByOrNull { (pos, block) ->
                        val hardness = block.getPlayerRelativeBlockHardness(thePlayer, mc.theWorld!!, pos).toDouble()

                        val safePos = BlockPos(thePlayer.posX, thePlayer.posY - 1, thePlayer.posZ)
                        if (pos.x == safePos.x && safePos.y <= pos.y && pos.z == safePos.z)
                            Double.MIN_VALUE + hardness // Last block
                        else
                            hardness
                    }
                    else -> return // what? why?
                } ?: return // well I guess there is no block to break :(

                // Reset current damage in case of block switch
                if (blockPos != currentBlock)
                    currentDamage = 0F

                // Change head rotations to next block
                if (rotationsValue.get()) {
                    val rotation = RotationUtils.faceBlock(blockPos) ?: return // In case of a mistake. Prevent flag.
                    RotationUtils.setTargetRotation(rotation.rotation)
                }

                // Set next target block
                currentBlock = blockPos
                attackedBlocks.add(blockPos)

                // Call auto tool
                val autoTool = KevinClient.moduleManager.getModule(AutoTool::class.java)
                if (autoTool.state)
                    autoTool.switchSlot(blockPos)

                // Start block breaking
                if (currentDamage == 0F) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                        blockPos, EnumFacing.DOWN))

                    // End block break if able to break instant
                    if (block.getPlayerRelativeBlockHardness(thePlayer, mc.theWorld!!, blockPos) >= 1F) {
                        currentDamage = 0F
                        thePlayer.swingItem()
                        mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN)
                        blockHitDelay = hitDelayValue.get()
                        validBlocks -= blockPos
                        nuke++
                        continue // Next break
                    }
                }

                // Break block
                thePlayer.swingItem()
                currentDamage += block.getPlayerRelativeBlockHardness(thePlayer, mc.theWorld!!, blockPos)
                mc.theWorld!!.sendBlockBreakProgress(thePlayer.entityId, blockPos, (currentDamage * 10F).toInt() - 1)

                // End of breaking block
                if (currentDamage >= 1F) {
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN))
                    mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN)
                    blockHitDelay = hitDelayValue.get()
                    currentDamage = 0F
                }
                return // Break out
            } while (nuke < nukeValue.get())
        } else {
            // Fast creative mode nuker (CreativeStorm option)

            // Unable to break with swords in creative mode
            if ((thePlayer.heldItem?.item)is ItemSword)
                return

            // Search for new blocks to break
            searchBlocks(radiusValue.get().roundToInt() + 1)
                .filter { (pos, block) ->
                    if (getCenterDistance(pos) <= radiusValue.get() && validBlock(block)) {
                        if (layerValue.get() && pos.y < thePlayer.posY) { // Layer: Break all blocks above you
                            return@filter false
                        }

                        if (!throughWallsValue.get()) { // ThroughWalls: Just break blocks in your sight
                            // Raytrace player eyes to block position (through walls check)
                            val eyesPos = Vec3(thePlayer.posX, thePlayer.entityBoundingBox.minY +
                                    thePlayer.eyeHeight, thePlayer.posZ)
                            val blockVec = Vec3(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5)
                            val rayTrace = mc.theWorld!!.rayTraceBlocks(eyesPos, blockVec,
                                false, true, false)

                            // Check if block is visible
                            rayTrace != null && rayTrace.blockPos == pos
                        } else true // Done
                    } else false // Bad block
                }
                .forEach { (pos, _) ->
                    // Instant break block
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK,
                        pos, EnumFacing.DOWN))
                    thePlayer.swingItem()
                    mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                        pos, EnumFacing.DOWN))
                    attackedBlocks.add(pos)
                }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        // Safe block
        if (!layerValue.get()) {
            val safePos = BlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY - 1, mc.thePlayer!!.posZ)
            val safeBlock = BlockUtils.getBlock(safePos)
            if (safeBlock != null && validBlock(safeBlock))
                RenderUtils.drawBlockBox(safePos, Color.GREEN, true)
        }

        // Just draw all blocks
        for (blockPos in attackedBlocks)
            RenderUtils.drawBlockBox(blockPos, Color.RED, true)
    }

    /**
     * Check if [block] is a valid block to break
     */
    private fun validBlock(block: Block) = (block) !is BlockAir && (block) !is BlockLiquid && (block) != Blocks.bedrock

    var currentDamage = 0F

}