package alos.stella.module

import alos.stella.Stella
import alos.stella.event.EventTarget
import alos.stella.event.Listenable
import alos.stella.event.events.KeyEvent
import alos.stella.utils.ClientUtils
import alos.stella.module.modules.movement.*
import alos.stella.module.modules.player.*
import alos.stella.module.modules.visual.*
import alos.stella.module.modules.combat.*
import alos.stella.module.modules.misc.MemFix
import alos.stella.module.modules.misc.NoAchievements
import alos.stella.module.modules.misc.Restarter
import alos.stella.module.modules.world.GameSpeed
import java.util.concurrent.CopyOnWriteArrayList


class ModuleManager : Listenable {

    public val modules = CopyOnWriteArrayList<Module>()
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    public var shouldNotify : Boolean = false
    public var toggleSoundMode = 0
    public var toggleVolume = 0F

    init {
        Stella.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")

        registerModules(
            NoAchievements::class.java,
            ChestStealer::class.java,
            Camera::class.java,
            InvMove::class.java,
            CustomModel::class.java,
            PlayerEdit::class.java,
            Sprint::class.java,
            FastUse::class.java,
            HUD::class.java,
            ClickGUI::class.java,
            Speed::class.java,
            Animations::class.java,
            Fly::class.java,
            Velocity::class.java,
            Scaffold::class.java,
            KillAura::class.java,
            GameSpeed::class.java,
            PikaFly::class.java,
            TpFly::class.java,
            BackTrack::class.java,
            Restarter::class.java,
            Disabler::class.java,
            NameTags::class.java,
            AntiFall::class.java,
            Trails::class.java,
            FakeLag::class.java,
            TargetHUD::class.java,
            LegitSpeed::class.java,
            ChinaHat::class.java,
            InvManager::class.java,
            Blink::class.java,
            PingSpoof::class.java,
            MemFix::class.java,
        )

        ClientUtils.getLogger().info("[ModuleManager] Successfully loaded ${modules.size} modules.")
    }

    fun getModuleInCategory(category: ModuleCategory) = modules.filter { it.category == category }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        module.onInitialize()
        generateCommand(module)
        Stella.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        Stella.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        Stella.commandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun <T : Module> getModule(moduleClass: Class<T>): T? = moduleClassMap[moduleClass] as T?

    operator fun <T : Module> get(clazz: Class<T>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
