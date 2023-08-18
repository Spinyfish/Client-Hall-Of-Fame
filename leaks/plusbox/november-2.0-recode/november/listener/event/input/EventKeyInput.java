/* November.lol © 2023 */
package lol.november.listener.event.input;

import org.lwjgl.input.Keyboard;

/**
 * @param keyCode the key code from {@link Keyboard#getEventKey()}
 * @author Gavin
 * @since 2.0.0
 */
public record EventKeyInput(int keyCode) {}
