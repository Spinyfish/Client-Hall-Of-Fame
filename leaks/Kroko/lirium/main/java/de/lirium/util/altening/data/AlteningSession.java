package de.lirium.util.altening.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.Session;

@AllArgsConstructor @Getter
public class AlteningSession {
    private final Session session;
    private final String name;
}
