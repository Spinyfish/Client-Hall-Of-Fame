package niggerlib.mc.auth.data;

import niggerlib.mc.auth.exception.property.SignatureValidateException;
import niggerlib.mc.auth.util.Base64;

import java.security.PublicKey;
import java.security.Signature;
import java.util.*;

/**
 * Information about a user profile.
 */
public class GameProfile {
    private UUID id;
    private String name;

    private List<Property> properties;
    private Map<TextureType, Texture> textures;

    /**
     * Creates a new GameProfile instance.
     *
     * @param id   ID of the profile.
     * @param name Name of the profile.
     */
    public GameProfile(String id, String name) {
        this(id == null || id.equals("") ? null : UUID.fromString(id), name);
    }

    /**
     * Creates a new GameProfile instance.
     *
     * @param id   ID of the profile.
     * @param name Name of the profile.
     */
    public GameProfile(UUID id, String name) {
        if (id == null && (name == null || name.equals(""))) {
            throw new IllegalArgumentException("Name and ID cannot both be blank");
        } else {
            this.id = id;
            this.name = name;
        }
    }

    /**
     * Gets whether the profile is complete.
     *
     * @return Whether the profile is complete.
     */
    public boolean isComplete() {
        return this.id != null && this.name != null && !this.name.equals("");
    }

    /**
     * Gets the ID of the profile.
     *
     * @return The profile's ID.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Gets the ID of the profile as a String.
     *
     * @return The profile's ID as a string.
     */
    public String getIdAsString() {
        return this.id != null ? this.id.toString() : "";
    }

    /**
     * Gets the name of the profile.
     *
     * @return The profile's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets a list of properties contained in the profile.
     *
     * @return The profile's properties.
     */
    public List<Property> getProperties() {
        if (this.properties == null) {
            this.properties = new ArrayList<Property>();
        }

        return this.properties;
    }

    /**
     * Gets a property contained in the profile.
     *
     * @param name Name of the property.
     * @return The property with the specified name.
     */
    public Property getProperty(String name) {
        for (Property property : this.getProperties()) {
            if (property.getName().equals(name)) {
                return property;
            }
        }

        return null;
    }

    /**
     * Gets a map of texture types to textures contained in the profile.
     *
     * @return The profile's textures.
     */
    public Map<TextureType, Texture> getTextures() {
        if (this.textures == null) {
            this.textures = new HashMap<TextureType, Texture>();
        }

        return this.textures;
    }

    /**
     * Gets a texture contained in the profile.
     *
     * @param type Type of texture to get.
     * @return The texture of the specified type.
     */
    public Texture getTexture(TextureType type) {
        return this.getTextures().get(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            GameProfile that = (GameProfile) o;
            return (this.id != null ? this.id.equals(that.id) : that.id == null) && (this.name != null ? this.name.equals(that.name) : that.name == null);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GameProfile{id=" + this.id + ", name=" + this.name + ", properties=" + this.getProperties() + ", textures=" + this.getTextures() + "}";
    }

    /**
     * A property belonging to a profile.
     */
    public static class Property {
        private String name;
        private String value;
        private String signature;

        /**
         * Creates a new Property instance.
         *
         * @param name  Name of the property.
         * @param value Value of the property.
         */
        public Property(String name, String value) {
            this(name, value, null);
        }

        /**
         * Creates a new Property instance.
         *
         * @param name      Name of the property.
         * @param value     Value of the property.
         * @param signature Signature used to verify the property.
         */
        public Property(String name, String value, String signature) {
            this.name = name;
            this.value = value;
            this.signature = signature;
        }

        /**
         * Gets the name of the property.
         *
         * @return The property's name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Gets the value of the property.
         *
         * @return The property's value.
         */
        public String getValue() {
            return this.value;
        }

        /**
         * Gets the signature used to verify the property.
         *
         * @return The property's signature.
         */
        public String getSignature() {
            return this.signature;
        }

        /**
         * Gets whether this property has a signature to verify it.
         *
         * @return Whether this property is signed.
         */
        public boolean hasSignature() {
            return this.signature != null;
        }

        /**
         * Gets whether this property's signature is valid.
         *
         * @param key Public key to validate the signature against.
         * @return Whether the signature is valid.
         * @throws SignatureValidateException If the signature could not be validated.
         */
        public boolean isSignatureValid(PublicKey key) throws SignatureValidateException {
            if (!this.hasSignature()) {
                return false;
            }

            try {
                Signature sig = Signature.getInstance("SHA1withRSA");
                sig.initVerify(key);
                sig.update(this.value.getBytes());
                return sig.verify(Base64.decode(this.signature.getBytes("UTF-8")));
            } catch (Exception e) {
                throw new SignatureValidateException("Could not validate property signature.", e);
            }
        }

        @Override
        public String toString() {
            return "Property{name=" + this.name + ", value=" + this.value + ", signature=" + this.signature + "}";
        }
    }

    /**
     * The type of a profile texture.
     */
    public static enum TextureType {
        SKIN,
        CAPE;
    }

    /**
     * The model used for a profile texture.
     */
    public static enum TextureModel {
        NORMAL,
        SLIM;
    }

    /**
     * A texture contained within a profile.
     */
    public static class Texture {
        private String url;
        private Map<String, String> metadata;

        /**
         * Creates a new Texture instance.
         *
         * @param url      URL of the texture.
         * @param metadata Metadata of the texture.
         */
        public Texture(String url, Map<String, String> metadata) {
            this.url = url;
            this.metadata = metadata;
        }

        /**
         * Gets the URL of the texture.
         *
         * @return The texture's URL.
         */
        public String getURL() {
            return this.url;
        }

        /**
         * Gets the model of the texture.
         *
         * @return The texture's model.
         */
        public TextureModel getModel() {
            String model = this.metadata != null ? this.metadata.get("model") : null;
            return model != null && model.equals("slim") ? TextureModel.SLIM : TextureModel.NORMAL;
        }

        /**
         * Gets the hash of the texture.
         *
         * @return The texture's hash.
         */
        public String getHash() {
            String url = this.url.endsWith("/") ? this.url.substring(0, this.url.length() - 1) : this.url;
            int slash = url.lastIndexOf("/");
            int dot = url.lastIndexOf(".");
            if (dot < slash) {
                dot = url.length();
            }

            return url.substring(slash + 1, dot != -1 ? dot : url.length());
        }

        @Override
        public String toString() {
            return "ProfileTexture{url=" + this.url + ", model=" + this.getModel() + ", hash=" + this.getHash() + "}";
        }
    }
}
