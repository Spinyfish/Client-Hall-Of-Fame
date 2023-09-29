package alos.stella.module

import alos.stella.Stella
import alos.stella.event.Listenable
import alos.stella.utils.ClientUtils
import alos.stella.utils.MinecraftInstance
import alos.stella.value.*
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

open class Module : MinecraftInstance(), Listenable {

//    val valueTranslate = Translate(0F, 0F)
//    val moduleTranslate = Translate(0F, 0F)

//    @JvmField
//    var showSettings = false

//    @JvmField
//    var yPos1 = 30F

//    val animationHelper: AnimationHelper

    // Module information
    // TODO: Remove ModuleInfo and change to constructor (#Kotlin)
    var name: String
    var spacedName: String
    var description: String
    var category: ModuleCategory
    var keyBind = Keyboard.CHAR_NONE
        set(keyBind) {
            field = keyBind

            if (!Stella.isStarting)
                Stella.fileManager.saveConfig(Stella.fileManager.modulesConfig)
        }
    var array = true
        set(array) {
            field = array

            if (!Stella.isStarting)
                Stella.fileManager.saveConfig(Stella.fileManager.modulesConfig)
        }
    private val canEnable: Boolean
    private val onlyEnable: Boolean
    private val forceNoSound: Boolean

    //    var slideStep = 0F
    var animation = 0F
//    var autoDisables = mutableListOf<DisableEvent>()


    init {
        val moduleInfo = javaClass.getAnnotation(ModuleInfo::class.java)!!

        name = moduleInfo.name
        spacedName = if (moduleInfo.spacedName == "") name else moduleInfo.spacedName
        description = moduleInfo.description
        category = moduleInfo.category
        keyBind = moduleInfo.keyBind
        array = moduleInfo.array
        canEnable = moduleInfo.canEnable
        onlyEnable = moduleInfo.onlyEnable
        forceNoSound = moduleInfo.forceNoSound
//        animationHelper = AnimationHelper(this)
    }

    // Current state of module
    var state = false
        set(value) {
            if (field == value || !canEnable) return

            // Call toggle
            onToggle(value)

            // Play sound and add notification
            if (!Stella.isStarting && !forceNoSound) {
                when (Stella.moduleManager.toggleSoundMode) {
                    1 -> mc.soundHandler.playSound(
                        PositionedSoundRecord.create(
                            ResourceLocation("random.click"),
                            1F
                        )
                    )
//                    2 -> (if (value) Stella.tipSoundManager.enableSound else Stella.tipSoundManager.disableSound).asyncPlay(Stella.moduleManager.toggleVolume)
                }
//                if (Stella.moduleManager.shouldNotify)
//                    Stella.hud.addNotification(Notification("Module","${if (value) "Enabled" else "Disabled"} §r$name", if (value) NotifyType.SUCCESS else NotifyType.ERROR, 1000))
            }

            // Call on enabled or disabled
            if (value) {
                onEnable()

                if (!onlyEnable)
                    field = true
            } else {
                onDisable()
                field = false
            }

            // Save module state
            Stella.fileManager.saveConfig(Stella.fileManager.modulesConfig)
        }


    // HUD
    val hue = Math.random().toFloat()
    var slide = 0F
    var arrayY = 0F

    // Tag
    open val tag: String?
        get() = null
    /*
    val tagName: String
        get() = "$name${if (tag == null) "" else "§7 - $tag"}"

    val colorlessTagName: String
        get() = "$name${if (tag == null) "" else " - " + stripColor(tag)}"
*/
    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    /**
     * Print [msg] to chat
     */
    protected fun chat(msg: String) = ClientUtils.displayChatMessage("§8[§9§l${Stella.CLIENT_NAME}§8] §3$msg")

    /**
     * Called when module toggled
     */
    open fun onToggle(state: Boolean) {}

    /**
     * Called when module enabled
     */
    open fun onEnable() {}

    /**
     * Called when module disabled
     */
    open fun onDisable() {}

    /**
     * Called when module initialized
     */
    open fun onInitialize() {}

    /**
     * Get module by [valueName]
     */
    open fun getValue(valueName: String) = values.find { it.name.equals(valueName, ignoreCase = true) }

    val numberValues: List<Value<*>>
        get() = values.filter { it is IntegerValue || it is FloatValue }

    val booleanValues: List<BoolValue>
        get() = values.filterIsInstance<BoolValue>()

    val listValues: List<ListValue>
        get() = values.filterIsInstance<ListValue>()

    /**
     * Get all values of module
     */
    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Events should be handled when module is enabled
     */
    override fun handleEvents() = state
}