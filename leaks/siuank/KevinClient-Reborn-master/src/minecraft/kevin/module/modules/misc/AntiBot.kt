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

import kevin.event.*
import kevin.hud.element.elements.ConnectNotificationType
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import kevin.module.*
import kevin.utils.ChatUtils
import kevin.utils.ColorUtils.stripColor
import kevin.utils.ColorUtils.stripColorNoNull
import kevin.utils.CombatManager
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemArmor
import net.minecraft.network.play.server.*
import net.minecraft.world.WorldSettings
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList

object AntiBot : Module("AntiBot","Prevents KillAura from attacking AntiCheat bots.", category = ModuleCategory.MISC) {
    private val modeValue = ListValue("Mode", arrayOf("Custom","NoColorArmor","UnusualArmor"),"Custom")
    private val removeFromWorld = BooleanValue("RemoveFromWord", false)
    private val debugValue = BooleanValue("Debug", true)

    private val tabValue = BooleanValue("Tab", true)
    private val tabModeValue = ListValue("TabMode", arrayOf("Equals", "Contains"), "Contains")
    private val entityIDValue = BooleanValue("EntityID", true)
    private val colorValue = BooleanValue("Color", false)
    private val livingTimeValue = BooleanValue("LivingTime", false)
    private val livingTimeTicksValue = IntegerValue("LivingTimeTicks", 40, 1, 200)
    private val groundValue = BooleanValue("Ground", true)
    private val airValue = BooleanValue("Air", false)
    private val invalidGroundValue = BooleanValue("InvalidGround", true)
    private val swingValue = BooleanValue("Swing", false)
    private val healthValue = BooleanValue("Health", false)
    private val derpValue = BooleanValue("Derp", true)
    private val wasInvisibleValue = BooleanValue("WasInvisible", false)
    private val validNameValue = BooleanValue("ValidName", true)
    private val armorValue = BooleanValue("Armor", false)
    private val pingValue = BooleanValue("Ping", false)
    private val needHitValue = BooleanValue("NeedHit", false)
    private val noClipValue = BooleanValue("NoClip", false)
    private val czechHekValue = BooleanValue("CzechMatrix", false)
    private val czechHekPingCheckValue = BooleanValue("PingCheck", true)
    private val czechHekGMCheckValue = BooleanValue("GamemodeCheck", true)
    private val reusedEntityIdValue = BooleanValue("ReusedEntityId", false)
    private val spawnInCombatValue = BooleanValue("SpawnInCombat", false)
    private val skinValue = BooleanValue("SkinCheck", false)
    private val duplicateInWorldValue = BooleanValue("DuplicateInWorld", false)
    private val duplicateInTabValue = BooleanValue("DuplicateInTab", false)
    private val duplicateCompareModeValue = ListValue("DuplicateCompareMode", arrayOf("OnTime", "WhenSpawn"), "OnTime")
    private val fastDamageValue = BooleanValue("FastDamage", false)
    private val fastDamageTicksValue = IntegerValue("FastDamageTicks", 5, 1, 20)
    private val alwaysInRadiusValue = BooleanValue("AlwaysInRadius", false)
    private val alwaysRadiusValue = FloatValue("AlwaysInRadiusBlocks", 20f, 5f, 30f)
    private val alwaysInRadiusRemoveValue = BooleanValue("AlwaysInRadiusRemove", false)
    private val alwaysInRadiusWithTicksCheckValue = BooleanValue("AlwaysInRadiusWithTicksCheck", false)


    //Helmet
    private val allowDiamondHelmet = BooleanValue("AllowDiamondHelmet",true)
    private val allowGoldenHelmet = BooleanValue("AllowGoldenHelmet",true)
    private val allowIronHelmet = BooleanValue("AllowIronHelmet",true)
    private val allowChainHelmet = BooleanValue("AllowChainHelmet",true)
    private val allowLeatherHelmet = BooleanValue("AllowLeatherHelmet",true)
    private val allowNoHelmet = BooleanValue("AllowNoHelmet",true)

    //Chestplate
    private val allowDiamondChestplate = BooleanValue("AllowDiamondChestplate",true)
    private val allowGoldenChestplate = BooleanValue("AllowGoldenChestplate",true)
    private val allowIronChestplate = BooleanValue("AllowIronChestplate",true)
    private val allowChainChestplate = BooleanValue("AllowChainChestplate",true)
    private val allowLeatherChestplate = BooleanValue("AllowLeatherChestplate",true)
    private val allowNoChestplate = BooleanValue("AllowNoChestplate",true)

    //Leggings
    private val allowDiamondLeggings = BooleanValue("AllowDiamondLeggings",true)
    private val allowGoldenLeggings = BooleanValue("AllowGoldenLeggings",true)
    private val allowIronLeggings = BooleanValue("AllowIronLeggings",true)
    private val allowChainLeggings = BooleanValue("AllowChainLeggings",true)
    private val allowLeatherLeggings = BooleanValue("AllowLeatherLeggings",true)
    private val allowNoLeggings = BooleanValue("AllowNoLeggings",true)

    //Boots
    private val allowDiamondBoots = BooleanValue("AllowDiamondBoots",true)
    private val allowGoldenBoots = BooleanValue("AllowGoldenBoots",true)
    private val allowIronBoots = BooleanValue("AllowIronBoots",true)
    private val allowChainBoots = BooleanValue("AllowChainBoots",true)
    private val allowLeatherBoots = BooleanValue("AllowLeatherBoots",true)
    private val allowNoBoots = BooleanValue("AllowNoBoots",true)

    private val removeNoColorLeatherArmor = BooleanValue("NoColorLeatherArmor",true)

    private val botList = CopyOnWriteArrayList<EntityLivingBase>()

    private val ground = mutableListOf<Int>()
    private val air = mutableListOf<Int>()
    private val invalidGround = mutableMapOf<Int, Int>()
    private val swing = mutableListOf<Int>()
    private val invisible = mutableListOf<Int>()
    private val hitted = mutableListOf<Int>()
    private val spawnInCombat = mutableListOf<Int>()
    private val notAlwaysInRadius = mutableListOf<Int>()
    private val lastDamage = mutableMapOf<Int, Int>()
    private val lastDamageVl = mutableMapOf<Int, Float>()
    private val duplicate = mutableListOf<UUID>()
    private val noClip = mutableListOf<Int>()
    private val hasRemovedEntities = mutableListOf<Int>()
    private val regex = Regex("\\w{3,16}")
    private var wasAdded = mc.thePlayer != null

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onUpdate(event: UpdateEvent){
        val playerEntities = mc.theWorld.playerEntities.toList()
        botList.clear()
        when {
            modeValue equal "NoColorArmor" -> {
                for (player in playerEntities){
                    if (player == mc.thePlayer) continue
                    var isBot = false
                    val armorInventory = player.inventory.armorInventory
                    for (armor in armorInventory) {
                        if (armor == null || armor.item == null) continue
                        val itemArmor: ItemArmor
                        try {
                            itemArmor = armor.item as ItemArmor
                        } catch (e: Exception){
                            continue
                        }
                        if (itemArmor.armorMaterial == ItemArmor.ArmorMaterial.LEATHER){
                            if (!armor.hasTagCompound()) isBot = true
                        }
                    }
                    if (isBot) {
                        botList.add(player)
                    }
                }
            }
            modeValue equal "UnusualArmor" -> {
                for (player in playerEntities) {
                    if (player == mc.thePlayer) continue
                    var isBot = false
                    val armorInventory = player.inventory.armorInventory
                    val boots = armorInventory[0]
                    val leggings = armorInventory[1]
                    val chestPlate = armorInventory[2]
                    val helmet = armorInventory[3]
                    if (
                        //NoArmor
                        ((boots==null||boots.item==null)&&!allowNoBoots.get())
                        ||((leggings==null||leggings.item==null)&&!allowNoLeggings.get())
                        ||((chestPlate==null||chestPlate.item==null)&&!allowNoChestplate.get())
                        ||((helmet==null||helmet.item==null)&&!allowNoHelmet.get())
                        //Diamond
                        ||((helmet!=null&&helmet.item!=null&&helmet.item is ItemArmor)&&(helmet.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.DIAMOND&&!allowDiamondHelmet.get())
                        ||((chestPlate!=null&&chestPlate.item!=null&&chestPlate.item is ItemArmor)&&(chestPlate.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.DIAMOND&&!allowDiamondChestplate.get())
                        ||((leggings!=null&&leggings.item!=null&&leggings.item is ItemArmor)&&(leggings.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.DIAMOND&&!allowDiamondLeggings.get())
                        ||((boots!=null&&boots.item!=null&&boots.item is ItemArmor)&&(boots.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.DIAMOND&&!allowDiamondBoots.get())
                        //Golden
                        ||((helmet!=null&&helmet.item!=null&&helmet.item is ItemArmor)&&(helmet.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.GOLD&&!allowGoldenHelmet.get())
                        ||((chestPlate!=null&&chestPlate.item!=null&&chestPlate.item is ItemArmor)&&(chestPlate.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.GOLD&&!allowGoldenChestplate.get())
                        ||((leggings!=null&&leggings.item!=null&&leggings.item is ItemArmor)&&(leggings.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.GOLD&&!allowGoldenLeggings.get())
                        ||((boots!=null&&boots.item!=null&&boots.item is ItemArmor)&&(boots.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.GOLD&&!allowGoldenBoots.get())
                        //Iron
                        ||((helmet!=null&&helmet.item!=null&&helmet.item is ItemArmor)&&(helmet.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.IRON&&!allowIronHelmet.get())
                        ||((chestPlate!=null&&chestPlate.item!=null&&chestPlate.item is ItemArmor)&&(chestPlate.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.IRON&&!allowIronChestplate.get())
                        ||((leggings!=null&&leggings.item!=null&&leggings.item is ItemArmor)&&(leggings.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.IRON&&!allowIronLeggings.get())
                        ||((boots!=null&&boots.item!=null&&boots.item is ItemArmor)&&(boots.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.IRON&&!allowIronBoots.get())
                        //Chain
                        ||((helmet!=null&&helmet.item!=null&&helmet.item is ItemArmor)&&(helmet.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.CHAIN&&!allowChainHelmet.get())
                        ||((chestPlate!=null&&chestPlate.item!=null&&chestPlate.item is ItemArmor)&&(chestPlate.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.CHAIN&&!allowChainChestplate.get())
                        ||((leggings!=null&&leggings.item!=null&&leggings.item is ItemArmor)&&(leggings.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.CHAIN&&!allowChainLeggings.get())
                        ||((boots!=null&&boots.item!=null&&boots.item is ItemArmor)&&(boots.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.CHAIN&&!allowChainBoots.get())
                        //Leather
                        ||((helmet!=null&&helmet.item!=null&&helmet.item is ItemArmor)&&(helmet.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER&&!allowLeatherHelmet.get())
                        ||((chestPlate!=null&&chestPlate.item!=null&&chestPlate.item is ItemArmor)&&(chestPlate.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER&&!allowLeatherChestplate.get())
                        ||((leggings!=null&&leggings.item!=null&&leggings.item is ItemArmor)&&(leggings.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER&&!allowLeatherLeggings.get())
                        ||((boots!=null&&boots.item!=null&&boots.item is ItemArmor)&&(boots.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER&&!allowLeatherBoots.get())
                        //LeatherNoColor
                        ||((
                              ((helmet!=null&&helmet.item!=null&&helmet.item is ItemArmor)&&(helmet.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER&&!helmet.hasTagCompound())
                            ||((chestPlate!=null&&chestPlate.item!=null&&chestPlate.item is ItemArmor)&&(chestPlate.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER&&!chestPlate.hasTagCompound())
                            ||((leggings!=null&&leggings.item!=null&&leggings.item is ItemArmor)&&(leggings.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER&&!leggings.hasTagCompound())
                            ||((boots!=null&&boots.item!=null&&boots.item is ItemArmor)&&(boots.item as ItemArmor).armorMaterial == ItemArmor.ArmorMaterial.LEATHER&&!boots.hasTagCompound())
                                )&&removeNoColorLeatherArmor.get())
                    )isBot = true
                    if (isBot) {
                        botList.add(player)
                    }
                }
            }
        }
        if (removeFromWorld.get()) {
            if (mc.thePlayer == null || mc.theWorld == null) return
            val bots: MutableList<EntityPlayer> = ArrayList()
            for (player in playerEntities)
                if (player !== mc.thePlayer && isBot(player)) bots.add(player)
            for (bot in bots)
                removeBot(bot)
        }
    }
    private fun removeBot(bot: Entity){
        mc.theWorld.removeEntityFromWorld(bot.entityId)
        if (debugValue.get())
            KevinClient.hud.addNotification(Notification("Removed Bot", "AntiBot", ConnectNotificationType.OK))
    }
    @JvmStatic
    fun isBot(entity: EntityLivingBase): Boolean {
        // Check if entity is a player
        if (entity !is EntityPlayer || entity === mc.thePlayer) {
            return false
        }

        // Check if anti bot is enabled
        if (!state) {
            return false
        }

        if (entity in botList)
            return true

        if (!(modeValue equal "Custom"))
            return false

        if (validNameValue.get() && !entity.name.matches(regex)) {
            return true
        }

        // Anti Bot checks
        if (colorValue.get() && !entity.displayName.formattedText.replace("§r", "").contains("§")) {
            return true
        }

        if (livingTimeValue.get() && entity.ticksExisted < livingTimeTicksValue.get()) {
            return true
        }

        if (groundValue.get() && !ground.contains(entity.entityId)) {
            return true
        }

        if (airValue.get() && !air.contains(entity.entityId)) {
            return true
        }

        if (swingValue.get() && !swing.contains(entity.entityId)) {
            return true
        }

        if(noClipValue.get() && noClip.contains(entity.entityId)) {
            return true
        }

        if(reusedEntityIdValue.get() && hasRemovedEntities.contains(entity.entityId)) {
            return false
        }

        if (healthValue.get() && (entity.health > 20F || entity.health <= 0F)) {
            return true
        }

        if (spawnInCombatValue.get() && spawnInCombat.contains(entity.entityId)) {
            return true
        }

        if (entityIDValue.get() && (entity.entityId >= 1000000000 || entity.entityId <= -1)) {
            return true
        }

        if (derpValue.get() && (entity.rotationPitch > 90F || entity.rotationPitch < -90F)) {
            return true
        }

        if (wasInvisibleValue.get() && invisible.contains(entity.entityId)) {
            return true
        }

        if (armorValue.get()) {
            if (entity.inventory.armorInventory[0] == null && entity.inventory.armorInventory[1] == null &&
                entity.inventory.armorInventory[2] == null && entity.inventory.armorInventory[3] == null) {
                return true
            }
        }

        if (pingValue.get()) {
            if (mc.netHandler.getPlayerInfo(entity.uniqueID)?.responseTime == 0) {
                return true
            }
        }

        if (needHitValue.get() && !hitted.contains(entity.entityId)) {
            return true
        }

        if (invalidGroundValue.get() && invalidGround.getOrDefault(entity.entityId, 0) >= 10) {
            return true
        }

        if (tabValue.get()) {
            val equals = tabModeValue.equals("Equals")
            val targetName = stripColorNoNull(entity.displayName.formattedText)

            for (networkPlayerInfo in mc.netHandler.playerInfoMap) {
                val networkName = stripColorNoNull(networkPlayerInfo.getFullName())

                if (if (equals) targetName == networkName else targetName.contains(networkName)) {
                    return false
                }
            }

            return true
        }

        if (duplicateCompareModeValue.equals("WhenSpawn") && duplicate.contains(entity.gameProfile.id)) {
            return true
        }

        if (duplicateInWorldValue.get() && duplicateCompareModeValue.equals("OnTime") && mc.theWorld.loadedEntityList.count { it is EntityPlayer && it.name == it.name } > 1) {
            return true
        }

        if (duplicateInTabValue.get() && duplicateCompareModeValue.equals("OnTime") && mc.netHandler.playerInfoMap.count { entity.name == it.gameProfile.name } > 1) {
            return true
        }

        if (fastDamageValue.get() && lastDamageVl.getOrDefault(entity.entityId, 0f) > 0) {
            return true
        }

        if (alwaysInRadiusValue.get() && !notAlwaysInRadius.contains(entity.entityId)) {
            return true
        }

        if (skinValue.get()) {
            val info = mc.netHandler.getPlayerInfo(entity.uniqueID) ?: return true
            if (!info.hasLocationSkin()) return true
        }

        return entity.name.isEmpty() || entity.name == mc.thePlayer.name
    }

    private fun NetworkPlayerInfo.getFullName(): String {
        if (displayName != null) {
            return displayName!!.formattedText
        }

        val team = playerTeam
        val name = gameProfile.name
        return team?.formatString(name) ?: name
    }

    override fun onDisable() {
        clearAll()
        super.onDisable()
    }

    private fun processEntityMove(entity: Entity, onGround: Boolean) {
        if (entity is EntityPlayer) {
            if (onGround && !ground.contains(entity.entityId)) {
                ground.add(entity.entityId)
            }

            if (!onGround && !air.contains(entity.entityId)) {
                air.add(entity.entityId)
            }

            if (onGround) {
                if (entity.prevPosY != entity.posY) {
                    invalidGround[entity.entityId] = invalidGround.getOrDefault(entity.entityId, 0) + 1
                }
            } else {
                val currentVL = invalidGround.getOrDefault(entity.entityId, 0) / 2
                if (currentVL <= 0) {
                    invalidGround.remove(entity.entityId)
                } else {
                    invalidGround[entity.entityId] = currentVL
                }
            }

            if (entity.isInvisible && !invisible.contains(entity.entityId)) {
                invisible.add(entity.entityId)
            }

            if (!noClip.contains(entity.entityId)) {
                val cb = mc.theWorld.getCollidingBoundingBoxes(entity, entity.entityBoundingBox.contract(0.0625, 0.0625, 0.0625))
//                alert("NOCLIP[${cb.size}] ${entity.displayName.unformattedText} ${entity.posX} ${entity.posY} ${entity.posZ}")
                if(cb.isNotEmpty()) {
                    noClip.add(entity.entityId)
                }
            }

            if ((!livingTimeValue.get() || entity.ticksExisted > livingTimeTicksValue.get() || !alwaysInRadiusWithTicksCheckValue.get()) && !notAlwaysInRadius.contains(entity.entityId) && mc.thePlayer.getDistanceToEntity(entity) > alwaysRadiusValue.get()) {
                notAlwaysInRadius.add(entity.entityId)
                if (alwaysInRadiusRemoveValue.get()) {
                    mc.theWorld.removeEntity(entity)
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (czechHekValue.get()) {

            val packet = event.packet

            if (packet is S41PacketServerDifficulty) wasAdded = false
            if (packet is S38PacketPlayerListItem) {
                val packetListItem = event.packet as S38PacketPlayerListItem
                val data = packetListItem.entries[0]
                if (data.profile != null && data.profile.name != null) {
                    if (!wasAdded) wasAdded =
                        data.profile.name == mc.thePlayer.name else if (!mc.thePlayer.isSpectator && !mc.thePlayer.capabilities.allowFlying && (!czechHekPingCheckValue.get() || data.ping != 0) && (!czechHekGMCheckValue.get() || data.gameMode != WorldSettings.GameType.NOT_SET)) {
                        event.cancelEvent()
                        if (debugValue.get()) ChatUtils.messageWithStart("§7[§a§lAnti Bot/§6Matrix§7] §fPrevented §r" + data.profile.name + " §ffrom spawning.")
                    }
                }
            }
        }
        val packet = event.packet

        if(packet is S18PacketEntityTeleport) {
            processEntityMove(mc.theWorld.getEntityByID(packet.entityId) ?: return, packet.onGround)
        } else if (packet is S14PacketEntity) {
            processEntityMove(packet.getEntity(mc.theWorld) ?: return, packet.onGround)
        } else if (packet is S0BPacketAnimation) {
            val entity = mc.theWorld.getEntityByID(packet.entityID)

            if (entity != null && entity is EntityLivingBase && packet.animationType == 0 &&
                !swing.contains(entity.entityId)) {
                swing.add(entity.entityId)
            }
        } else if (packet is S38PacketPlayerListItem) {
            if (duplicateCompareModeValue.equals("WhenSpawn") && packet.action == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                packet.entries.forEach { entry ->
                    val name = entry.profile.name
                    if (duplicateInWorldValue.get() && mc.theWorld.playerEntities.any { it.name == name } ||
                        duplicateInTabValue.get() && mc.netHandler.playerInfoMap.any { it.gameProfile.name == name }) {
                        duplicate.add(entry.profile.id)
                    }
                }
            }
        } else if (packet is S0CPacketSpawnPlayer) {
            if(KevinClient.combatManager.inCombat && !hasRemovedEntities.contains(packet.entityID)) {
                spawnInCombat.add(packet.entityID)
            }
        } else if (packet is S13PacketDestroyEntities) {
            hasRemovedEntities.addAll(packet.entityIDs.toTypedArray())
        }

        if (packet is S19PacketEntityStatus && packet.opCode.toInt() == 2 || packet is S0BPacketAnimation && packet.animationType == 1) {
            val entity = if (packet is S19PacketEntityStatus) { packet.getEntity(mc.theWorld) } else if (packet is S0BPacketAnimation) { mc.theWorld.getEntityByID(packet.entityID) } else { null } ?: return

            if (entity is EntityPlayer) {
                lastDamageVl[entity.entityId] = lastDamageVl.getOrDefault(entity.entityId, 0f) + if (entity.ticksExisted - lastDamage.getOrDefault(entity.entityId, 0) <= fastDamageTicksValue.get()) {
                    1f
                } else {
                    -0.5f
                }
                lastDamage[entity.entityId] = entity.ticksExisted
            }
        }
    }

    @EventTarget
    fun onAttack(e: AttackEvent) {
        val entity = e.targetEntity

        if (entity is EntityLivingBase && !hitted.contains(entity.entityId)) {
            hitted.add(entity.entityId)
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        clearAll()
    }

    private fun clearAll() {
        hitted.clear()
        swing.clear()
        ground.clear()
        invalidGround.clear()
        invisible.clear()
        lastDamage.clear()
        lastDamageVl.clear()
        notAlwaysInRadius.clear()
        duplicate.clear()
        spawnInCombat.clear()
        noClip.clear()
        hasRemovedEntities.clear()
    }
}