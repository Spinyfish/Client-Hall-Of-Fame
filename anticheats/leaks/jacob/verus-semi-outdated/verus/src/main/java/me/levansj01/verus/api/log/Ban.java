package me.levansj01.verus.api.log;

import lombok.Data;
import me.levansj01.verus.api.check.Check;

@Data
public class Ban {
    private final Check check;
    private final long timestamp;
}