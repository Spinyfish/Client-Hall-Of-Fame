package client.module;

import client.Client;
import client.event.Listener;
import client.event.Priorities;
import client.event.annotations.EventLink;
import client.event.impl.input.KeyboardInputEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ModuleManager extends ArrayList<Module> {

    public void init() {
        this.stream().filter(module -> module.getModuleInfo().autoEnabled()).forEach(module -> module.setEnabled(true));

        Client.INSTANCE.getEventBus().register(this);
    }

    public List<Module> getAll() {
        return new ArrayList<>(this);
    }

    public <T extends Module> T get(final String name) {
        // noinspection unchecked
        return (T) this.stream()
                .filter(module -> module.getDisplayName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }

    public <T extends Module> T get(final Class<T> clazz) {
        // noinspection unchecked
        return (T) this.stream()
                .filter(module -> module.getClass() == clazz)
                .findAny().orElse(null);
    }

    public List<Module> get(final Category category) {
        return this.stream()
                .filter(module -> module.getModuleInfo().category() == category)
                .collect(Collectors.toList());
    }

    @EventLink(value = Priorities.VERY_HIGH)
    public final Listener<KeyboardInputEvent> onKey = event -> {

        if (event.getScreen() != null) return;

        this.stream()
                .filter(module -> module.getKeyIndex() == event.getKeyIndex())
                .forEach(Module::toggle);
    };

    @Override
    public boolean add(final Module module) {
        final boolean result = super.add(module);
        return result;
    }

    @Override
    public void add(final int i, final Module module) {
        super.add(i, module);
    }

    @Override
    public Module remove(final int i) {
        final Module result = super.remove(i);
        return result;
    }

    @Override
    public boolean remove(final Object o) {
        final boolean result = super.remove(o);
        return result;
    }
}
