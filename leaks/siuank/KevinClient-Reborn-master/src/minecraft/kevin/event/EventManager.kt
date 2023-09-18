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
package kevin.event

import kevin.hud.element.elements.ConnectNotificationType
import kevin.hud.element.elements.Notification
import kevin.main.KevinClient
import net.minecraft.client.Minecraft
import java.lang.reflect.InvocationTargetException

@Suppress("UNCHECKED_CAST")
class EventManager {
    private val registry = HashMap<Class<out Event>, MutableList<EventHook>>()
    fun registerListener(listener: Listenable) {
        for (method in listener.javaClass.declaredMethods) {
            if (method.isAnnotationPresent(EventTarget::class.java) && method.parameterTypes.size == 1) {
                if (!method.isAccessible)
                    method.isAccessible = true

                val eventClass = method.parameterTypes[0] as Class<out Event>
                val eventTarget = method.getAnnotation(EventTarget::class.java)

                val invokableEventTargets = registry.getOrDefault(eventClass, ArrayList())
                invokableEventTargets.add(EventHook(listener, method, eventTarget))
                registry[eventClass] = invokableEventTargets
            }
        }
    }
    fun unregisterListener(listenable: Listenable) {
        for ((key, targets) in registry) {
            targets.removeIf { it.eventClass == listenable }

            registry[key] = targets
        }
    }
    fun callEvent(event: Event) {
        val targets = registry[event.javaClass] ?: return

        for (invokableEventTarget in targets) {
            try {
                if (!invokableEventTarget.eventClass.handleEvents() && !invokableEventTarget.isIgnoreCondition)
                    continue

                invokableEventTarget.method.invoke(invokableEventTarget.eventClass, event)
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                if (KevinClient.debug) {
                    if (throwable is InvocationTargetException) {
                        KevinClient.hud.addNotification(
                            Notification(
                                "Exception caught when calling ${event.javaClass.simpleName} in ${invokableEventTarget.eventClass.javaClass.simpleName}: ${throwable.targetException.message}",
                                "Debug",
                                ConnectNotificationType.Error
                            )
                        )
                        Minecraft.logger.warn("Exception caught when calling ${event.javaClass.simpleName} in listener ${invokableEventTarget.eventClass.javaClass.simpleName}:\n" +
                                throwable.targetException.stackTraceToString()
                        )
                    }
                }
            }
        }
    }
}