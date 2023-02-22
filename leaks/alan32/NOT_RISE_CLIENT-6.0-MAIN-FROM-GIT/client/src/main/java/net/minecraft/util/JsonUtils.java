package net.minecraft.util;

import com.google.gson.*;

public class JsonUtils {
    /**
     * Does the given JsonObject contain a string field with the given name?
     */
    public static boolean isString(final JsonObject p_151205_0_, final String p_151205_1_) {
        return isJsonPrimitive(p_151205_0_, p_151205_1_) && p_151205_0_.getAsJsonPrimitive(p_151205_1_).isString();
    }

    /**
     * Is the given JsonElement a string?
     */
    public static boolean isString(final JsonElement p_151211_0_) {
        return p_151211_0_.isJsonPrimitive() && p_151211_0_.getAsJsonPrimitive().isString();
    }

    public static boolean isBoolean(final JsonObject p_180199_0_, final String p_180199_1_) {
        return isJsonPrimitive(p_180199_0_, p_180199_1_) && p_180199_0_.getAsJsonPrimitive(p_180199_1_).isBoolean();
    }

    /**
     * Does the given JsonObject contain an array field with the given name?
     */
    public static boolean isJsonArray(final JsonObject p_151202_0_, final String p_151202_1_) {
        return hasField(p_151202_0_, p_151202_1_) && p_151202_0_.get(p_151202_1_).isJsonArray();
    }

    /**
     * Does the given JsonObject contain a field with the given name whose type is primitive (String, Java primitive, or
     * Java primitive wrapper)?
     */
    public static boolean isJsonPrimitive(final JsonObject p_151201_0_, final String p_151201_1_) {
        return hasField(p_151201_0_, p_151201_1_) && p_151201_0_.get(p_151201_1_).isJsonPrimitive();
    }

    /**
     * Does the given JsonObject contain a field with the given name?
     */
    public static boolean hasField(final JsonObject p_151204_0_, final String p_151204_1_) {
        return p_151204_0_ != null && p_151204_0_.get(p_151204_1_) != null;
    }

    /**
     * Gets the string value of the given JsonElement.  Expects the second parameter to be the name of the element's
     * field if an error message needs to be thrown.
     */
    public static String getString(final JsonElement p_151206_0_, final String p_151206_1_) {
        if (p_151206_0_.isJsonPrimitive()) {
            return p_151206_0_.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + p_151206_1_ + " to be a string, was " + toString(p_151206_0_));
        }
    }

    /**
     * Gets the string value of the field on the JsonObject with the given name.
     */
    public static String getString(final JsonObject p_151200_0_, final String p_151200_1_) {
        if (p_151200_0_.has(p_151200_1_)) {
            return getString(p_151200_0_.get(p_151200_1_), p_151200_1_);
        } else {
            throw new JsonSyntaxException("Missing " + p_151200_1_ + ", expected to find a string");
        }
    }

    /**
     * Gets the string value of the field on the JsonObject with the given name, or the given default value if the field
     * is missing.
     */
    public static String getString(final JsonObject p_151219_0_, final String p_151219_1_, final String p_151219_2_) {
        return p_151219_0_.has(p_151219_1_) ? getString(p_151219_0_.get(p_151219_1_), p_151219_1_) : p_151219_2_;
    }

    /**
     * Gets the boolean value of the given JsonElement.  Expects the second parameter to be the name of the element's
     * field if an error message needs to be thrown.
     */
    public static boolean getBoolean(final JsonElement p_151216_0_, final String p_151216_1_) {
        if (p_151216_0_.isJsonPrimitive()) {
            return p_151216_0_.getAsBoolean();
        } else {
            throw new JsonSyntaxException("Expected " + p_151216_1_ + " to be a Boolean, was " + toString(p_151216_0_));
        }
    }

    /**
     * Gets the boolean value of the field on the JsonObject with the given name.
     */
    public static boolean getBoolean(final JsonObject p_151212_0_, final String p_151212_1_) {
        if (p_151212_0_.has(p_151212_1_)) {
            return getBoolean(p_151212_0_.get(p_151212_1_), p_151212_1_);
        } else {
            throw new JsonSyntaxException("Missing " + p_151212_1_ + ", expected to find a Boolean");
        }
    }

    /**
     * Gets the boolean value of the field on the JsonObject with the given name, or the given default value if the
     * field is missing.
     */
    public static boolean getBoolean(final JsonObject p_151209_0_, final String p_151209_1_, final boolean p_151209_2_) {
        return p_151209_0_.has(p_151209_1_) ? getBoolean(p_151209_0_.get(p_151209_1_), p_151209_1_) : p_151209_2_;
    }

    /**
     * Gets the float value of the given JsonElement.  Expects the second parameter to be the name of the element's
     * field if an error message needs to be thrown.
     */
    public static float getFloat(final JsonElement p_151220_0_, final String p_151220_1_) {
        if (p_151220_0_.isJsonPrimitive() && p_151220_0_.getAsJsonPrimitive().isNumber()) {
            return p_151220_0_.getAsFloat();
        } else {
            throw new JsonSyntaxException("Expected " + p_151220_1_ + " to be a Float, was " + toString(p_151220_0_));
        }
    }

    /**
     * Gets the float value of the field on the JsonObject with the given name.
     */
    public static float getFloat(final JsonObject p_151217_0_, final String p_151217_1_) {
        if (p_151217_0_.has(p_151217_1_)) {
            return getFloat(p_151217_0_.get(p_151217_1_), p_151217_1_);
        } else {
            throw new JsonSyntaxException("Missing " + p_151217_1_ + ", expected to find a Float");
        }
    }

    /**
     * Gets the float value of the field on the JsonObject with the given name, or the given default value if the field
     * is missing.
     */
    public static float getFloat(final JsonObject p_151221_0_, final String p_151221_1_, final float p_151221_2_) {
        return p_151221_0_.has(p_151221_1_) ? getFloat(p_151221_0_.get(p_151221_1_), p_151221_1_) : p_151221_2_;
    }

    /**
     * Gets the integer value of the given JsonElement.  Expects the second parameter to be the name of the element's
     * field if an error message needs to be thrown.
     */
    public static int getInt(final JsonElement p_151215_0_, final String p_151215_1_) {
        if (p_151215_0_.isJsonPrimitive() && p_151215_0_.getAsJsonPrimitive().isNumber()) {
            return p_151215_0_.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + p_151215_1_ + " to be a Int, was " + toString(p_151215_0_));
        }
    }

    /**
     * Gets the integer value of the field on the JsonObject with the given name.
     */
    public static int getInt(final JsonObject p_151203_0_, final String p_151203_1_) {
        if (p_151203_0_.has(p_151203_1_)) {
            return getInt(p_151203_0_.get(p_151203_1_), p_151203_1_);
        } else {
            throw new JsonSyntaxException("Missing " + p_151203_1_ + ", expected to find a Int");
        }
    }

    /**
     * Gets the integer value of the field on the JsonObject with the given name, or the given default value if the
     * field is missing.
     */
    public static int getInt(final JsonObject p_151208_0_, final String p_151208_1_, final int p_151208_2_) {
        return p_151208_0_.has(p_151208_1_) ? getInt(p_151208_0_.get(p_151208_1_), p_151208_1_) : p_151208_2_;
    }

    /**
     * Gets the given JsonElement as a JsonObject.  Expects the second parameter to be the name of the element's field
     * if an error message needs to be thrown.
     */
    public static JsonObject getJsonObject(final JsonElement p_151210_0_, final String p_151210_1_) {
        if (p_151210_0_.isJsonObject()) {
            return p_151210_0_.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + p_151210_1_ + " to be a JsonObject, was " + toString(p_151210_0_));
        }
    }

    public static JsonObject getJsonObject(final JsonObject base, final String key) {
        if (base.has(key)) {
            return getJsonObject(base.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonObject");
        }
    }

    /**
     * Gets the JsonObject field on the JsonObject with the given name, or the given default value if the field is
     * missing.
     */
    public static JsonObject getJsonObject(final JsonObject p_151218_0_, final String p_151218_1_, final JsonObject p_151218_2_) {
        return p_151218_0_.has(p_151218_1_) ? getJsonObject(p_151218_0_.get(p_151218_1_), p_151218_1_) : p_151218_2_;
    }

    /**
     * Gets the given JsonElement as a JsonArray.  Expects the second parameter to be the name of the element's field if
     * an error message needs to be thrown.
     */
    public static JsonArray getJsonArray(final JsonElement p_151207_0_, final String p_151207_1_) {
        if (p_151207_0_.isJsonArray()) {
            return p_151207_0_.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + p_151207_1_ + " to be a JsonArray, was " + toString(p_151207_0_));
        }
    }

    /**
     * Gets the JsonArray field on the JsonObject with the given name.
     */
    public static JsonArray getJsonArray(final JsonObject p_151214_0_, final String p_151214_1_) {
        if (p_151214_0_.has(p_151214_1_)) {
            return getJsonArray(p_151214_0_.get(p_151214_1_), p_151214_1_);
        } else {
            throw new JsonSyntaxException("Missing " + p_151214_1_ + ", expected to find a JsonArray");
        }
    }

    /**
     * Gets the JsonArray field on the JsonObject with the given name, or the given default value if the field is
     * missing.
     */
    public static JsonArray getJsonArray(final JsonObject p_151213_0_, final String p_151213_1_, final JsonArray p_151213_2_) {
        return p_151213_0_.has(p_151213_1_) ? getJsonArray(p_151213_0_.get(p_151213_1_), p_151213_1_) : p_151213_2_;
    }

    /**
     * Gets a human-readable description of the given JsonElement's type.  For example: "a number (4)"
     */
    public static String toString(final JsonElement p_151222_0_) {
        final String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf(p_151222_0_), "...", 10);

        if (p_151222_0_ == null) {
            return "null (missing)";
        } else if (p_151222_0_.isJsonNull()) {
            return "null (json)";
        } else if (p_151222_0_.isJsonArray()) {
            return "an array (" + s + ")";
        } else if (p_151222_0_.isJsonObject()) {
            return "an object (" + s + ")";
        } else {
            if (p_151222_0_.isJsonPrimitive()) {
                final JsonPrimitive jsonprimitive = p_151222_0_.getAsJsonPrimitive();

                if (jsonprimitive.isNumber()) {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean()) {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }
}
