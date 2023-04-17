package me.levansj01.verus.api.log;

import lombok.Data;
import me.levansj01.verus.api.check.Check;

@Data
public class Log {
    private final Check check;
    private final long timestamp;
    private final int vl;
    private final int ping;
    private final int lag;
}
