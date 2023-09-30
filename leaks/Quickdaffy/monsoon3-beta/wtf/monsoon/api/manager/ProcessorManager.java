/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.manager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.processor.Processor;

public class ProcessorManager {
    private final LinkedHashMap<Class<? extends Processor>, Processor> processors = new LinkedHashMap();

    public void addProcessor(Class<? extends Processor> clazz, Processor processor) {
        this.processors.put(clazz, processor);
        Wrapper.getEventBus().subscribe(processor);
    }

    public List<Processor> getProcessors() {
        return new ArrayList<Processor>(this.processors.values());
    }
}

