/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import java.util.ArrayList;
import optifine.Lang;
import shadersmod.client.ShaderOption;
import shadersmod.client.ShaderProfile;
import shadersmod.client.ShaderUtils;
import shadersmod.client.Shaders;

public class ShaderOptionProfile
extends ShaderOption {
    private ShaderProfile[] profiles = null;
    private ShaderOption[] options = null;
    private static final String NAME_PROFILE = "<profile>";
    private static final String VALUE_CUSTOM = "<custom>";

    public ShaderOptionProfile(ShaderProfile[] profiles, ShaderOption[] options) {
        super(NAME_PROFILE, "", ShaderOptionProfile.detectProfileName(profiles, options), ShaderOptionProfile.getProfileNames(profiles), ShaderOptionProfile.detectProfileName(profiles, options, true), null);
        this.profiles = profiles;
        this.options = options;
    }

    @Override
    public void nextValue() {
        super.nextValue();
        if (this.getValue().equals(VALUE_CUSTOM)) {
            super.nextValue();
        }
        this.applyProfileOptions();
    }

    public void updateProfile() {
        ShaderProfile shaderprofile = this.getProfile(this.getValue());
        if (shaderprofile == null || !ShaderUtils.matchProfile(shaderprofile, this.options, false)) {
            String s = ShaderOptionProfile.detectProfileName(this.profiles, this.options);
            this.setValue(s);
        }
    }

    private void applyProfileOptions() {
        ShaderProfile shaderprofile = this.getProfile(this.getValue());
        if (shaderprofile != null) {
            String[] astring = shaderprofile.getOptions();
            for (int i = 0; i < astring.length; ++i) {
                String s = astring[i];
                ShaderOption shaderoption = this.getOption(s);
                if (shaderoption == null) continue;
                String s1 = shaderprofile.getValue(s);
                shaderoption.setValue(s1);
            }
        }
    }

    private ShaderOption getOption(String name) {
        for (int i = 0; i < this.options.length; ++i) {
            ShaderOption shaderoption = this.options[i];
            if (!shaderoption.getName().equals(name)) continue;
            return shaderoption;
        }
        return null;
    }

    private ShaderProfile getProfile(String name) {
        for (int i = 0; i < this.profiles.length; ++i) {
            ShaderProfile shaderprofile = this.profiles[i];
            if (!shaderprofile.getName().equals(name)) continue;
            return shaderprofile;
        }
        return null;
    }

    @Override
    public String getNameText() {
        return Lang.get("of.shaders.profile");
    }

    @Override
    public String getValueText(String val) {
        return val.equals(VALUE_CUSTOM) ? Lang.get("of.general.custom", VALUE_CUSTOM) : Shaders.translate("profile." + val, val);
    }

    @Override
    public String getValueColor(String val) {
        return val.equals(VALUE_CUSTOM) ? "\u00a7c" : "\u00a7a";
    }

    private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts) {
        return ShaderOptionProfile.detectProfileName(profs, opts, false);
    }

    private static String detectProfileName(ShaderProfile[] profs, ShaderOption[] opts, boolean def) {
        ShaderProfile shaderprofile = ShaderUtils.detectProfile(profs, opts, def);
        return shaderprofile == null ? VALUE_CUSTOM : shaderprofile.getName();
    }

    private static String[] getProfileNames(ShaderProfile[] profs) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < profs.length; ++i) {
            ShaderProfile shaderprofile = profs[i];
            list.add(shaderprofile.getName());
        }
        list.add(VALUE_CUSTOM);
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }
}

