/* November.lol © 2023 */
package lol.november.listener.event.input;

import org.lwjgl.input.Mouse;

/**
 * @param mouseButton the mouse button from {@link Mouse#getEventButton()}
 * @author Gavin
 * @since 2.0.0
 */
public record EventMouseInput(int mouseButton) {}
