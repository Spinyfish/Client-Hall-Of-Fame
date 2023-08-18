/* November.lol © 2023 */
package lol.november.protect.checks;

import com.google.common.collect.Lists;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class JavaAgentCheck implements Check {

  /**
   * An {@link java.util.ArrayList} of invalid JVM arguments that could be used for cracking
   */
  private static final List<String> badJvmArgs = Lists.newArrayList(
    "-javaagent"
  );

  @Override
  public boolean check() {
    RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
    for (String argument : runtimeMxBean.getInputArguments()) {
      if (badJvmArgs.contains(argument)) return false;
    }

    return true;
  }

  @Override
  public String name() {
    return "JavaAgent";
  }
}
