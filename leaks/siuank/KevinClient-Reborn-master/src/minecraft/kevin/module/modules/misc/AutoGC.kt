package kevin.module.modules.misc

import kevin.event.EventTarget
import kevin.event.TickEvent
import kevin.module.BooleanValue
import kevin.module.Module
import kevin.module.ModuleCategory
import kevin.utils.ChatUtils
import kevin.utils.MSTimer

object AutoGC: Module("AutoGC", "Automatic garbage collection", category = ModuleCategory.MISC) {
    private val debug = BooleanValue("debug", false)
    private val msTimer = MSTimer()
    @EventTarget fun onTick(t: TickEvent) {
        if (msTimer.hasTimePassed(180000)) {
            val runtime = Runtime.getRuntime()
            val before = (runtime.totalMemory() - runtime.freeMemory()) / 1024.0 / 1024.0
            System.gc()
            val after = (runtime.totalMemory() - runtime.freeMemory()) / 1024.0 / 1024.0
            if (debug.get()) {
                ChatUtils.messageWithStart("Free ${before - after} mb")
            }
            msTimer.reset()
        }
    }
}