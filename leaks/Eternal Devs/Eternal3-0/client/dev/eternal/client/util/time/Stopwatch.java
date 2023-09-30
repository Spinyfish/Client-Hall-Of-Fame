package dev.eternal.client.util.time;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stopwatch {

  private long startTime = System.currentTimeMillis();

  public void reset() {
    startTime = System.currentTimeMillis();
  }

  public boolean hasElapsed(long milliseconds) {
    return System.currentTimeMillis() - startTime >= milliseconds;
  }

  public boolean hasElapsed(long milliseconds, boolean reset) {
    boolean b = System.currentTimeMillis() - startTime >= milliseconds;
    if (b && reset)
      reset();
    return b;
  }


}
