package alos.stella.event

open class Event

open class CancellableEvent : Event() {

    private var cancelled = false

    open fun isCancelled(): Boolean {
        return cancelled
    }

    open fun cancelEvent(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    open fun cancelEvent() {
        cancelled = true
    }

}

enum class EventState(val stateName: String) {
    PRE("PRE"), POST("POST")
}