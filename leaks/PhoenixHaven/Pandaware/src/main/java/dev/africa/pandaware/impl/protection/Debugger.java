package dev.africa.pandaware.impl.protection;

import dev.africa.pandaware.utils.java.StringUtils;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

public class Debugger {
    private final List<String> flags = Arrays.asList(
            StringUtils.getXDebug(),
            StringUtils.getAgentLib(),
            StringUtils.getXRun(),
            StringUtils.getJavaAgent()
    );

    public Debugger() {
        for (String argument : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            for (String flag : this.flags) {
                if (argument.toLowerCase().contains(flag.toLowerCase())) {
                    //TODO: UNCOMMENT ON RELEASE
                    //System.exit(3);
                }
            }
            if (argument.equals(StringUtils.getDisableAttach())) {
                return;
            }

            //TODO: UNCOMMENT ON RELEASE
            //System.exit(9);
        }
    }
}