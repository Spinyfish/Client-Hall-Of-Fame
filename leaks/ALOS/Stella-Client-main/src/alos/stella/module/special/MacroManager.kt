package alos.stella.module.special

import alos.stella.Stella
import alos.stella.event.EventTarget
import alos.stella.event.Listenable
import alos.stella.event.events.KeyEvent
import alos.stella.utils.MinecraftInstance

object MacroManager : MinecraftInstance(), Listenable {

    val macroMapping = hashMapOf<Int, String>()

    @EventTarget
    fun onKey(event: KeyEvent) {
        mc.thePlayer ?: return
        Stella.commandManager ?: return
        macroMapping.filter { it.key == event.key }.forEach { 
            if (it.value.startsWith(Stella.commandManager.prefix))
                Stella.commandManager.executeCommands(it.value)
            else
                mc.thePlayer.sendChatMessage(it.value) 
        }
    }

    fun addMacro(keyCode: Int, command: String) {
        macroMapping[keyCode] = command
    }

    fun removeMacro(keyCode: Int) {
        macroMapping.remove(keyCode)
    }

    override fun handleEvents(): Boolean = true

}