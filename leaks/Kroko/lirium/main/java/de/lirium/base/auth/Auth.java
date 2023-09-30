package de.lirium.base.auth;

import de.lirium.base.feature.Feature;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Auth implements Feature {
    public final String name, session, auth;

    @Override
    public String getName() {
        return name;
    }
}
