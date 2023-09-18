package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.PacketEvent
import kevin.main.KevinClient
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.module.Value
import kevin.utils.ChatUtils
import net.minecraft.network.EnumPacketDirection
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.server.*
import net.minecraft.network.play.server.S14PacketEntity.*
import java.util.LinkedList

@Suppress("UNCHECKED_CAST")
class PacketLogger: Module("PacketLogger", "Allow you know what packet we receive and send.", category = ModuleCategory.MISC) {
    private val logClientBoundPacket = BooleanValue("LogClientBoundPacket", true)
    private val logServerBoundPacket = BooleanValue("LogServerBoundPacket", true)
    private val clientBoundPackets: HashMap<Class<out Packet<*>>, BooleanValue> = HashMap()
    private val serverBoundPackets: HashMap<Class<out Packet<*>>, BooleanValue> = HashMap()

    private val vals = LinkedList<BooleanValue>()
    private val start = "§l§7[§l§9Packet§l§7] "

    @EventTarget fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val clz = packet.javaClass
        var out = false
        if (clientBoundPackets.containsKey(clz)) {
            if (!logClientBoundPacket.get()) return
            if (!clientBoundPackets[clz]!!.get()) return
            out = true
        } else if (serverBoundPackets.containsKey(clz)) {
            if (!logServerBoundPacket.get()) return
            if (!serverBoundPackets[clz]!!.get()) return
            out = true
        }
        if (!out) return
        if (clz.isMemberClass)
            output(clz.declaringClass as Class<out Packet<*>>, packet)
        else
            output(clz, packet)
    }

    private fun output(clz: Class<out Packet<*>>, packet: Packet<*>) {
        val strBuilder = StringBuilder()
        strBuilder.append(start)
        strBuilder.append(clz.simpleName).append(':')
        for (field in clz.declaredFields) {
            strBuilder.append("\n§7    ").append(field.name).append(": ")
            try {
                field.isAccessible = true
                strBuilder.append(field.get(packet))
            } catch (_: Exception) {
                strBuilder.append("Error when get")
            }
        }
        ChatUtils.message(strBuilder.toString())
    }

    private fun registerPacket(direction: EnumPacketDirection, clz: Class<out Packet<*>>) {
        val bv = BooleanValue(clz.simpleName, false)
        if (direction == EnumPacketDirection.CLIENTBOUND) {
            clientBoundPackets[clz] = bv
        } else {
            serverBoundPackets[clz] = bv
        }
        vals.add(bv)
    }

    init { // Copied form EnumConnectionState because I am lazy lol~
        vals.add(logClientBoundPacket)
        vals.add(logServerBoundPacket)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S00PacketKeepAlive::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S01PacketJoinGame::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S02PacketChat::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S03PacketTimeUpdate::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S04PacketEntityEquipment::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S05PacketSpawnPosition::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S06PacketUpdateHealth::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S07PacketRespawn::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S08PacketPlayerPosLook::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S09PacketHeldItemChange::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S0APacketUseBed::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S0BPacketAnimation::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S0CPacketSpawnPlayer::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S0DPacketCollectItem::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S0EPacketSpawnObject::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S0FPacketSpawnMob::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S10PacketSpawnPainting::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S11PacketSpawnExperienceOrb::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S12PacketEntityVelocity::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S13PacketDestroyEntities::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S14PacketEntity::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S15PacketEntityRelMove::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S16PacketEntityLook::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S17PacketEntityLookMove::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S18PacketEntityTeleport::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S19PacketEntityHeadLook::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S19PacketEntityStatus::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S1BPacketEntityAttach::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S1CPacketEntityMetadata::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S1DPacketEntityEffect::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S1EPacketRemoveEntityEffect::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S1FPacketSetExperience::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S20PacketEntityProperties::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S21PacketChunkData::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S22PacketMultiBlockChange::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S23PacketBlockChange::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S24PacketBlockAction::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S25PacketBlockBreakAnim::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S26PacketMapChunkBulk::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S27PacketExplosion::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S28PacketEffect::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S29PacketSoundEffect::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S2APacketParticles::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S2BPacketChangeGameState::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S2CPacketSpawnGlobalEntity::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S2DPacketOpenWindow::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S2EPacketCloseWindow::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S2FPacketSetSlot::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S30PacketWindowItems::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S31PacketWindowProperty::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S32PacketConfirmTransaction::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S33PacketUpdateSign::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S34PacketMaps::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S35PacketUpdateTileEntity::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S36PacketSignEditorOpen::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S37PacketStatistics::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S38PacketPlayerListItem::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S39PacketPlayerAbilities::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S3APacketTabComplete::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S3BPacketScoreboardObjective::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S3CPacketUpdateScore::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S3DPacketDisplayScoreboard::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S3EPacketTeams::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S3FPacketCustomPayload::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S40PacketDisconnect::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S41PacketServerDifficulty::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S42PacketCombatEvent::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S43PacketCamera::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S44PacketWorldBorder::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S45PacketTitle::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S46PacketSetCompressionLevel::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S47PacketPlayerListHeaderFooter::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S48PacketResourcePackSend::class.java)
        this.registerPacket(EnumPacketDirection.CLIENTBOUND, S49PacketUpdateEntityNBT::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C00PacketKeepAlive::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C01PacketChatMessage::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C02PacketUseEntity::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C03PacketPlayer::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C04PacketPlayerPosition::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C05PacketPlayerLook::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C06PacketPlayerPosLook::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C07PacketPlayerDigging::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C08PacketPlayerBlockPlacement::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C09PacketHeldItemChange::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C0APacketAnimation::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C0BPacketEntityAction::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C0CPacketInput::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C0DPacketCloseWindow::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C0EPacketClickWindow::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C0FPacketConfirmTransaction::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C10PacketCreativeInventoryAction::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C11PacketEnchantItem::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C12PacketUpdateSign::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C13PacketPlayerAbilities::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C14PacketTabComplete::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C15PacketClientSettings::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C16PacketClientStatus::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C17PacketCustomPayload::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C18PacketSpectate::class.java)
        this.registerPacket(EnumPacketDirection.SERVERBOUND, C19PacketResourcePackStatus::class.java)
    }

    override val values: List<Value<*>>
        get() = vals
}