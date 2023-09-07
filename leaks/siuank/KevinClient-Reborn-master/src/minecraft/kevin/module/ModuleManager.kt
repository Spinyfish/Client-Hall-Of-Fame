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
@file:Suppress("UNCHECKED_CAST")

package kevin.module

import kevin.event.EventTarget
import kevin.event.KeyEvent
import kevin.event.Listenable
import kevin.main.KevinClient
import kevin.module.modules.*
import kevin.module.modules.combat.*
import kevin.module.modules.exploit.*
import kevin.module.modules.misc.*
import kevin.module.modules.movement.*
import kevin.module.modules.player.*
import kevin.module.modules.render.*
import kevin.module.modules.world.*

class ModuleManager : Listenable {

    private val modules = ArrayList<Module>()

    private var combatList:ArrayList<Module>? = null
    private var exploitList:ArrayList<Module>? = null
    private var miscList:ArrayList<Module>? = null
    private var movementList:ArrayList<Module>? = null
    private var playerList:ArrayList<Module>? = null
    private var renderList:ArrayList<Module>? = null
    private var worldList:ArrayList<Module>? = null

    val xRay by lazy { getModule(XRay::class.java) }

    fun load(){
        combatList = arrayListOf(
            AntiKnockback(),
            AutoArmor(),
            AutoBow(),
            AutoClicker(),
            AntiFireball(),
            AutoWeapon(),
            BackTrack(),
            BowAimbot(),
            BowAura(),
            Criticals(),
            FastBow(),
            HitBox(),
            KeepRange(),
            KillAura(),
            SuperKnockback(),
            TeleportAttack(),
            TimerRange
        )
        exploitList = arrayListOf(
            AbortBreaking(),
            AntiHunger(),
            ClientSpoof,
            Clip(),
            Disabler,
            ForceUnicodeChat(),
            Ghost(),
            GhostHand(),
            IllegalItems,
            KeepContainer(),
            Kick(),
            Log4j2(),
            MultiActions(),
            NoPitchLimit(),
            Phase(),
            PingSpoof(),
            Plugins(),
            PortalMenu(),
            ServerCrasher(),
            ServerSidePacketDelayer(),
            TP(),
            VehicleOneHit()
        )
        miscList = arrayListOf(
            AdminDetector,
            AntiBot,
            AntiInvalidBlockPlacement(),
            AntiShop(),
            AutoCommand(),
            AutoDisable,
            AutoGC,
            AutoL(),
            ChatControl,
            ComponentOnHover,
            ConfigsManager,
            Diastimeter(),
            ClientFriend,
            HackDetector.INSTANCE,
            HideAndSeekHack,
            KillerDetector(),
            LagBackDetector(),
            NameProtect(),
            NoAchievement,
            NoCommand,
            NoRotateSet,
            NoScoreboard,
            PacketLogger(),
            PerformanceBooster,
            ResourcePackSpoof(),
            SuperSpammer(),
            Teams(),
            Translator
        )
        movementList = arrayListOf(
            AirJump(),
            AirLadder(),
            AntiVoid(),
            Fly(),
            Freeze(),
            HighJump(),
            InvMove(),
            LiquidWalk(),
            LongJump(),
            MoveCorrection(),
            NoClip(),
            NoJumpDelay,
            NoSlow(),
            NoWeb(),
            Parkour(),
            SafeWalk(),
            Speed(),
            Sprint(),
            Step(),
            Strafe(),
            TargetStrafe,
            VehicleJump(),
            WallClimb(),
            WaterSpeed()
        )
        playerList = arrayListOf(
            AntiAFK(),
            AntiCactus(),
            AutoFish(),
            AutoRespawn(),
            AutoSneak(),
            AutoTool(),
            Blink(),
            FastUse(),
            InventoryCleaner(),
            NoC03(),
            NoFall(),
            Reach(),
            Regen()
        )
        renderList = arrayListOf(
            Animations(),
            AntiBlind(),
            BlockESP(),
            BlockOverlay(),
            CameraClip(),
            CapeManager(),
            Chams,
            ClickGui(),
            ChinaHat(),
            Crosshair,
            DamageParticle(),
            ESP(),
            FreeCam(),
            FullBright(),
            HUD(0),
            HudDesigner(),
            ItemESP(),
            NameTags(),
            NoBob(),
            NoFOV(),
            NoHurtCam(),
            NoSwing(),
            Particles,
            Projectiles(),
            Renderer,
            RenderSettings,
            Rotations,
            SourceESP(),
            StorageESP(),
            TNTESP(),
            Tracers(),
            Trajectories(),
            TrueSight(),
            XRay()
        )
        worldList = arrayListOf(
//            BlockFly(),
            Breaker(),
            ChestStealer(),
            FastBreak(),
            FastPlace(),
            LightningDetector,
            LegitScaffold(),
            NoSlowBreak(),
            Nuker(),
            OldScaffold(),
            Scaffold(),
            TeleportUse(),
            Timer(),
            World
        )

        modules.add(Targets())
        modules.addAll(combatList!!)
        modules.addAll(exploitList!!)
        modules.addAll(miscList!!)
        modules.addAll(movementList!!)
        modules.addAll(playerList!!)
        modules.addAll(renderList!!)
        modules.addAll(worldList!!)
        modules.forEach { KevinClient.eventManager.registerListener(it) }
        KevinClient.eventManager.registerListener(this)
    }

    fun getModules(): ArrayList<Module>{
        return modules
    }

    fun getModuleByName(name: String): Module?{
        for (module in modules){
            if (module.name.equals(name,ignoreCase = true))return module
        }
        return null
    }

    fun <T: Module> getModule(module: Class<T>) = modules.first { it.javaClass == module } as T

    operator fun <T: Module> get(module: Class<T>) = getModule(module)

    @EventTarget
    fun onKey(key: KeyEvent){
        for (module in modules){
            if (module.keyBind == key.key) module.toggle()
        }
    }

    override fun handleEvents(): Boolean {
        return true
    }

    fun registerModule(module: Module) {
        modules += module
        KevinClient.eventManager.registerListener(module)
    }

    fun unregisterModule(module: Module) {
        modules.remove(module)
        KevinClient.eventManager.unregisterListener(module)
    }
}