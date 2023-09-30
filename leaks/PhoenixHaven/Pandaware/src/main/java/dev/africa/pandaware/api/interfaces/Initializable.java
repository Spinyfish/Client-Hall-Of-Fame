package dev.africa.pandaware.api.interfaces;

public interface Initializable {
    void init();

    default void shutdown() {

    }
}
