package de.lirium.base.profile;

import com.google.gson.JsonObject;
import de.lirium.base.profile.util.Action;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Getter
public abstract class ProfileSection {
    private final String sectionName;
    protected final Profile profile;

    public ProfileSection(Profile profile) {
        final Info info = getClass().getAnnotation(Info.class);
        this.sectionName = info.name();
        this.profile = profile;
    }

    public abstract void write(JsonObject base);
    public abstract void read(JsonObject base);

    public void read(JsonObject base, String target, Action<String> action) {
        if(base.has(target))
            action.doAction(base.get(target).getAsString());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Info {
        String name();
    }
}
