package tech.dort.dortware.api.module.interfaces;

/**
 * @author Dort
 */
public interface IToggleable {

    /**
     * Changes this {@code Toggleable}'s state.
     */
    void toggle();

    /**
     * Called when this {@code Toggleable}'s state is changed.
     */
    void onToggled();

    /**
     * Called when this {@code Toggleable} is enabled.
     */
    void onEnable();

    /**
     * Called when this {@code Toggleable} is disabled.
     */
    void onDisable();

}
