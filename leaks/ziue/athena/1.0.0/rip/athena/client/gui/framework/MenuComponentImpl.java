package rip.athena.client.gui.framework;

public interface MenuComponentImpl
{
    default void onPreSort() {
    }
    
    default void onRender() {
    }
    
    default boolean onExitGui(final int key) {
        return false;
    }
    
    default void onKeyDown(final char character, final int key) {
    }
    
    default void onMouseClick(final int key) {
    }
    
    default void onMouseScroll(final int scroll) {
    }
    
    default void onMouseClickMove(final int key) {
    }
    
    default void onInitColors() {
    }
}
