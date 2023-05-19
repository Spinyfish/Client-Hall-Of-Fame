package io.github.raze.registry.system.themes;

import io.github.raze.themes.collection.Jello;
import io.github.raze.themes.collection.Sunrise;
import io.github.raze.themes.system.BaseTheme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThemeRegistry {

    public List<BaseTheme> themes;
    public BaseTheme currentTheme;

    public List<BaseTheme> getThemes() {
        return themes;
    }

    public BaseTheme getCurrentTheme() {
        return currentTheme;
    }

    public ThemeRegistry() {
        this.themes = new ArrayList<BaseTheme>();
    }

    public void bootstrap() {
        register(

                // Themes
                new Jello(),
                new Sunrise()

        );

        this.currentTheme = themes.get(0);
    }

    public void register(BaseTheme input) {
        themes.add(input);
    }

    public void register(BaseTheme... input) {
        themes.addAll(Arrays.asList(input));
    }

    public BaseTheme getTheme(String input) {
        for (BaseTheme theme : getThemes()) {
            if (theme.getName().equalsIgnoreCase(input)) {
                return theme;
            }
        }
        return null;
    }

    public BaseTheme getTheme(Class<? extends BaseTheme> input) {
        for (BaseTheme theme : getThemes()) {
            if (theme.getClass().equals(input)) {
                return theme;
            }
        }
        return null;
    }
}
