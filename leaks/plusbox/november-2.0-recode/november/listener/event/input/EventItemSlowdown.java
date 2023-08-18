/* November.lol © 2023 */
package lol.november.listener.event.input;

import net.minecraft.util.MovementInput;

/**
 * @param input the {@link net.minecraft.util.MovementInputFromOptions} object from the slowed down player
 * @author Gavin
 * @since 2.0.0
 */
public record EventItemSlowdown(MovementInput input) {}
