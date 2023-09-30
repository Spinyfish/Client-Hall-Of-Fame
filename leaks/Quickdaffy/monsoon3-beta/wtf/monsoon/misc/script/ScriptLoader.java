/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jdk.internal.dynalink.beans.StaticClass
 *  org.luaj.vm2.lib.jse.JsePlatform
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.misc.script;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import jdk.internal.dynalink.beans.StaticClass;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.misc.script.ScriptController;
import wtf.monsoon.misc.script.ScriptHandler;
import wtf.monsoon.misc.script.ScriptManager;
import wtf.monsoon.misc.script.wrapper.ScriptColor;
import wtf.monsoon.misc.script.wrapper.ScriptDraw;
import wtf.monsoon.misc.script.wrapper.ScriptPlayer;
import wtf.monsoon.misc.script.wrapper.ScriptUtil;
import wtf.monsoon.misc.script.wrapper.ScriptWorld;

public class ScriptLoader {
    public File dir;
    public File scriptDir;
    public Map<String, String> toReplace = new HashMap<String, String>();

    public void loadScripts() {
        JsePlatform.standardGlobals();
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine e = mgr.getEngineByName("luaj");
        e.put("manager", ScriptManager.class);
        e.put("module", ScriptHandler.class.getName());
        e.put("player", ScriptPlayer.class);
        e.put("world", ScriptWorld.class);
        e.put("draw", ScriptDraw.class);
        e.put("color", ScriptColor.class);
        e.put("util", ScriptUtil.class);
        e.put("gl11", GL11.class);
        Wrapper.getLogger().info("Implemented script functions");
        this.loadClientPackets(mgr);
        this.loadServerPackets(mgr);
        this.addReplacedText();
        Wrapper.getLogger().info("Implemented packet functions");
        this.dir = new File(Minecraft.getMinecraft().mcDataDir, "Monsoon");
        this.scriptDir = new File(this.dir, "scripts");
        if (!this.dir.exists()) {
            return;
        }
        if (!this.scriptDir.exists()) {
            this.scriptDir.mkdir();
        }
        Wrapper.getLogger().info("Loading scripts from dir " + this.scriptDir.toString());
        for (File file : Objects.requireNonNull(this.scriptDir.listFiles())) {
            if (!file.getName().endsWith(".monsoon.lua")) continue;
            try {
                Wrapper.getLogger().info("Loading script: " + file.getName());
                String script = this.readFile(file.getAbsolutePath());
                for (String string : this.toReplace.keySet()) {
                    if (!script.contains(string)) continue;
                    script = script.replace(string, this.toReplace.get(string));
                }
                e.eval(script);
            }
            catch (Exception ex) {
                Wrapper.getLogger().error("Could not load script: " + file.getName());
                ex.printStackTrace();
            }
        }
        if (Wrapper.getMonsoon().getModuleManager().getModuleByName("Script Options") == null) {
            Wrapper.getMonsoon().getModuleManager().getModules().add(new ScriptController());
        }
    }

    public void unloadScripts() throws Exception {
        for (Module m : Wrapper.getMonsoon().getModuleManager().getModulesByCategory(Category.SCRIPT)) {
            if (!m.getClass().equals(ScriptController.class)) {
                m.setEnabled(false);
            }
            Wrapper.getMonsoon().getModuleManager().getModules().remove(m);
        }
    }

    public void reloadScripts() throws Exception {
        this.unloadScripts();
        this.loadScripts();
    }

    private void addReplacedText() {
        this.toReplace.put("newModule ({", "luajava.createProxy(module, {");
        this.toReplace.put("gui ({", "luajava.createProxy(gui, {");
    }

    private void loadClientPackets(ScriptEngineManager factory) {
        factory.put("C0APacketAnimation", StaticClass.forClass(C0APacketAnimation.class));
        factory.put("C0BPacketEntityAction", StaticClass.forClass(C0BPacketEntityAction.class));
        factory.put("C0CPacketInput", StaticClass.forClass(C0CPacketInput.class));
        factory.put("C0DPacketCloseWindow", StaticClass.forClass(C0DPacketCloseWindow.class));
        factory.put("C0EPacketClickWindow", StaticClass.forClass(C0EPacketClickWindow.class));
        factory.put("C0FPacketConfirmTransaction", StaticClass.forClass(C0FPacketConfirmTransaction.class));
        factory.put("C00PacketKeepAlive", StaticClass.forClass(C00PacketKeepAlive.class));
        factory.put("C01PacketChatMessage", StaticClass.forClass(C01PacketChatMessage.class));
        factory.put("C02PacketUseEntity", StaticClass.forClass(C02PacketUseEntity.class));
        factory.put("C03PacketPlayer", StaticClass.forClass(C03PacketPlayer.class));
        factory.put("C04PacketPlayerPosition", StaticClass.forClass(C03PacketPlayer.C04PacketPlayerPosition.class));
        factory.put("C05PacketPlayerLook", StaticClass.forClass(C03PacketPlayer.C05PacketPlayerLook.class));
        factory.put("C06PacketPlayerPosLook", StaticClass.forClass(C03PacketPlayer.C06PacketPlayerPosLook.class));
        factory.put("C07PacketPlayerDigging", StaticClass.forClass(C07PacketPlayerDigging.class));
        factory.put("C08PacketPlayerBlockPlacement", StaticClass.forClass(C08PacketPlayerBlockPlacement.class));
        factory.put("C09PacketHeldItemChange", StaticClass.forClass(C09PacketHeldItemChange.class));
        factory.put("C10PacketCreativeInventoryAction", StaticClass.forClass(C10PacketCreativeInventoryAction.class));
        factory.put("C11PacketEnchantItem", StaticClass.forClass(C11PacketEnchantItem.class));
        factory.put("C12PacketUpdateSign", StaticClass.forClass(C12PacketUpdateSign.class));
        factory.put("C13PacketPlayerAbilities", StaticClass.forClass(C13PacketPlayerAbilities.class));
        factory.put("C14PacketTabComplete", StaticClass.forClass(C14PacketTabComplete.class));
        factory.put("C15PacketClientSettings", StaticClass.forClass(C15PacketClientSettings.class));
        factory.put("C16PacketClientStatus", StaticClass.forClass(C16PacketClientStatus.class));
        factory.put("C17PacketCustomPayload", StaticClass.forClass(C17PacketCustomPayload.class));
        factory.put("C18PacketSpectate", StaticClass.forClass(C18PacketSpectate.class));
        factory.put("C19PacketResourcePackStatus", StaticClass.forClass(C19PacketResourcePackStatus.class));
    }

    private void loadServerPackets(ScriptEngineManager factory) {
        factory.put("S0APacketUseBed", StaticClass.forClass(S0APacketUseBed.class));
        factory.put("S0BPacketAnimation", StaticClass.forClass(S0BPacketAnimation.class));
        factory.put("S0CPacketSpawnPlayer", StaticClass.forClass(S0CPacketSpawnPlayer.class));
        factory.put("S0DPacketCollectItem", StaticClass.forClass(S0DPacketCollectItem.class));
        factory.put("S0EPacketSpawnObject", StaticClass.forClass(S0EPacketSpawnObject.class));
        factory.put("S0FPacketSpawnMob", StaticClass.forClass(S0FPacketSpawnMob.class));
        factory.put("S00PacketKeepAlive", StaticClass.forClass(S00PacketKeepAlive.class));
        factory.put("S1BPacketEntityAttach", StaticClass.forClass(S1BPacketEntityAttach.class));
        factory.put("S1CPacketEntityMetadata", StaticClass.forClass(S1CPacketEntityMetadata.class));
        factory.put("S1DPacketEntityEffect", StaticClass.forClass(S1DPacketEntityEffect.class));
        factory.put("S1EPacketRemoveEntityEffect", StaticClass.forClass(S1EPacketRemoveEntityEffect.class));
        factory.put("S1FPacketSetExperience", StaticClass.forClass(S1FPacketSetExperience.class));
        factory.put("S01PacketJoinGame", StaticClass.forClass(S01PacketJoinGame.class));
        factory.put("S2APacketParticles", StaticClass.forClass(S2APacketParticles.class));
        factory.put("S2BPacketChangeGameState", StaticClass.forClass(S2BPacketChangeGameState.class));
        factory.put("S2CPacketSpawnGlobalEntity", StaticClass.forClass(S2CPacketSpawnGlobalEntity.class));
        factory.put("S2DPacketOpenWindow", StaticClass.forClass(S2DPacketOpenWindow.class));
        factory.put("S2EPacketCloseWindow", StaticClass.forClass(S2EPacketCloseWindow.class));
        factory.put("S2FPacketSetSlot", StaticClass.forClass(S2FPacketSetSlot.class));
        factory.put("S02PacketChat", StaticClass.forClass(S02PacketChat.class));
        factory.put("S3APacketTabComplete", StaticClass.forClass(S3APacketTabComplete.class));
        factory.put("S3BPacketScoreboardObjective", StaticClass.forClass(S3BPacketScoreboardObjective.class));
        factory.put("S3CPacketUpdateScore", StaticClass.forClass(S3CPacketUpdateScore.class));
        factory.put("S3DPacketDisplayScoreboard", StaticClass.forClass(S3DPacketDisplayScoreboard.class));
        factory.put("S3EPacketTeams", StaticClass.forClass(S3EPacketTeams.class));
        factory.put("S3FPacketCustomPayload", StaticClass.forClass(S3FPacketCustomPayload.class));
        factory.put("S03PacketTimeUpdate", StaticClass.forClass(S03PacketTimeUpdate.class));
        factory.put("S04PacketEntityEquipment", StaticClass.forClass(S04PacketEntityEquipment.class));
        factory.put("S05PacketSpawnPosition", StaticClass.forClass(S05PacketSpawnPosition.class));
        factory.put("S06PacketUpdateHealth", StaticClass.forClass(S06PacketUpdateHealth.class));
        factory.put("S07PacketRespawn", StaticClass.forClass(S07PacketRespawn.class));
        factory.put("S08PacketPlayerPosLook", StaticClass.forClass(S08PacketPlayerPosLook.class));
        factory.put("S09PacketHeldItemChange", StaticClass.forClass(S09PacketHeldItemChange.class));
        factory.put("S10PacketSpawnPainting", StaticClass.forClass(S10PacketSpawnPainting.class));
        factory.put("S11PacketSpawnExperienceOrb", StaticClass.forClass(S11PacketSpawnExperienceOrb.class));
        factory.put("S12PacketEntityVelocity", StaticClass.forClass(S12PacketEntityVelocity.class));
        factory.put("S13PacketDestroyEntities", StaticClass.forClass(S13PacketDestroyEntities.class));
        factory.put("S14PacketEntity", StaticClass.forClass(S14PacketEntity.class));
        factory.put("S18PacketEntityTeleport", StaticClass.forClass(S18PacketEntityTeleport.class));
        factory.put("S19PacketEntityHeadLook", StaticClass.forClass(S19PacketEntityHeadLook.class));
        factory.put("S20PacketEntityProperties", StaticClass.forClass(S20PacketEntityProperties.class));
        factory.put("S21PacketChunkData", StaticClass.forClass(S21PacketChunkData.class));
        factory.put("S22PacketMultiBlockChange", StaticClass.forClass(S22PacketMultiBlockChange.class));
        factory.put("S23PacketBlockChange", StaticClass.forClass(S23PacketBlockChange.class));
        factory.put("S24PacketBlockAction", StaticClass.forClass(S24PacketBlockAction.class));
        factory.put("S25PacketBlockBreakAnim", StaticClass.forClass(S25PacketBlockBreakAnim.class));
        factory.put("S26PacketMapChunkBulk", StaticClass.forClass(S26PacketMapChunkBulk.class));
        factory.put("S27PacketExplosion", StaticClass.forClass(S27PacketExplosion.class));
        factory.put("S28PacketEffect", StaticClass.forClass(S28PacketEffect.class));
        factory.put("S29PacketSoundEffect", StaticClass.forClass(S29PacketSoundEffect.class));
        factory.put("S30PacketWindowItems", StaticClass.forClass(S30PacketWindowItems.class));
        factory.put("S31PacketWindowProperty", StaticClass.forClass(S31PacketWindowProperty.class));
        factory.put("S32PacketConfirmTransaction", StaticClass.forClass(S32PacketConfirmTransaction.class));
        factory.put("S33PacketUpdateSign", StaticClass.forClass(S33PacketUpdateSign.class));
        factory.put("S34PacketMaps", StaticClass.forClass(S34PacketMaps.class));
        factory.put("S35PacketUpdateTileEntity", StaticClass.forClass(S35PacketUpdateTileEntity.class));
        factory.put("S36PacketSignEditorOpen", StaticClass.forClass(S36PacketSignEditorOpen.class));
        factory.put("S37PacketStatistics", StaticClass.forClass(S37PacketStatistics.class));
        factory.put("S38PacketPlayerListItem", StaticClass.forClass(S38PacketPlayerListItem.class));
        factory.put("S39PacketPlayerAbilities", StaticClass.forClass(S39PacketPlayerAbilities.class));
        factory.put("S40PacketDisconnect", StaticClass.forClass(S40PacketDisconnect.class));
        factory.put("S41PacketServerDifficulty", StaticClass.forClass(S41PacketServerDifficulty.class));
        factory.put("S42PacketCombatEvent", StaticClass.forClass(S42PacketCombatEvent.class));
        factory.put("S43PacketCamera", StaticClass.forClass(S43PacketCamera.class));
        factory.put("S44PacketWorldBorder", StaticClass.forClass(S44PacketWorldBorder.class));
        factory.put("S45PacketTitle", StaticClass.forClass(S45PacketTitle.class));
        factory.put("S46PacketSetCompressionLevel", StaticClass.forClass(S46PacketSetCompressionLevel.class));
        factory.put("S47PacketPlayerListHeaderFooter", StaticClass.forClass(S47PacketPlayerListHeaderFooter.class));
        factory.put("S48PacketResourcePackSend", StaticClass.forClass(S48PacketResourcePackSend.class));
        factory.put("S49PacketUpdateEntityNBT", StaticClass.forClass(S49PacketUpdateEntityNBT.class));
    }

    private String readFile(String path) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path, new String[0]));
            return new String(encoded, Charset.defaultCharset());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }
    }
}

