package alos.stella

import alos.stella.command.CommandManager
import alos.stella.event.EventManager
import alos.stella.event.events.ClientShutdownEvent
import alos.stella.file.FileManager
import alos.stella.module.ModuleManager
import alos.stella.module.special.MacroManager
import alos.stella.ui.client.font.Fonts
import alos.stella.ui.draggble.DragComp
import alos.stella.ui.draggble.DragModern
import alos.stella.utils.ClientUtils

object Stella {

    // Client information
    const val CLIENT_NAME = "Stella"
    const val CLIENT_VERSION = "0.1"
    const val CLIENT_CREATOR = "ALOS"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var draggableHUD: DragModern

    var lastTick : Long = 0L

    fun startClient() {
        isStarting = true
        draggableHUD = DragModern()

        ClientUtils.getLogger().info("Starting $CLIENT_NAME build $CLIENT_VERSION")
        lastTick = System.currentTimeMillis()
//        playTimeStart = System.currentTimeMillis()

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Register listeners
//        eventManager.registerListener(RotationUtils())
//        eventManager.registerListener(AntiForge())
//        eventManager.registerListener(BungeeCordSpoof())
//        eventManager.registerListener(InventoryUtils())
//        eventManager.registerListener(InventoryHelper)
//        eventManager.registerListener(PacketUtils())
//        eventManager.registerListener(SessionUtils())
        eventManager.registerListener(MacroManager)

        // Init Discord RPC
//        clientRichPresence = ClientRichPresence()

        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()

        // Init SoundManager
//        tipSoundManager = TipSoundManager()

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()

        // Remapper
//        try {
//            loadSrg()
//
//            // ScriptManager
//            scriptManager = ScriptManager()
//            scriptManager.loadScripts()
//            scriptManager.enableScripts()
//        } catch (throwable: Throwable) {
//            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
//        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(fileManager.modulesConfig, fileManager.valuesConfig)

        //

//        fileManager.loadConfig(fileManager.clickGuiConfig)

        // Tabs (Only for Forge!)
//        if (hasForge()) {
//            BlocksTab()
//            ExploitsTab()
//            HeadsTab()
//        }

        // Set HUD
//        hud = createDefault()
//        fileManager.loadConfig(fileManager.hudConfig)

        // Load generators
//        GuiAltManager.loadActiveGenerators()
//
        // Setup Discord RPC
//        if (clientRichPresence.showRichPresenceValue) {
//            thread {
//                try {
//                    clientRichPresence.setup()
//                } catch (throwable: Throwable) {
//                    ClientUtils.getLogger().error("Failed to setup Discord RPC.", throwable)
//                }
//            }
//        }
        
        
        ClientUtils.getLogger().info("Finished loading $CLIENT_NAME in ${System.currentTimeMillis() - lastTick}ms.")

        // Set is starting status
        isStarting = false
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()

        // Shutdown discord rpc
//        clientRichPresence.shutdown()
    }
    

}