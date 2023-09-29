package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.gson.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Vector3f;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

public class BlockPart {
    public final Vector3f positionFrom;
    public final Vector3f positionTo;
    public final Map<EnumFacing, BlockPartFace> mapFaces;
    public final BlockPartRotation partRotation;
    public final boolean shade;

    public BlockPart(final Vector3f positionFromIn, final Vector3f positionToIn, final Map<EnumFacing, BlockPartFace> mapFacesIn, final BlockPartRotation partRotationIn, final boolean shadeIn) {
        this.positionFrom = positionFromIn;
        this.positionTo = positionToIn;
        this.mapFaces = mapFacesIn;
        this.partRotation = partRotationIn;
        this.shade = shadeIn;
        this.setDefaultUvs();
    }

    private void setDefaultUvs() {
        for (final Entry<EnumFacing, BlockPartFace> entry : this.mapFaces.entrySet()) {
            final float[] afloat = this.getFaceUvs(entry.getKey());
            entry.getValue().blockFaceUV.setUvs(afloat);
        }
    }

    private float[] getFaceUvs(final EnumFacing p_178236_1_) {
        final float[] afloat;

        switch (p_178236_1_) {
            case DOWN:
            case UP:
                afloat = new float[]{this.positionFrom.x, this.positionFrom.z, this.positionTo.x, this.positionTo.z};
                break;
            case NORTH:
            case SOUTH:
                afloat = new float[]{this.positionFrom.x, 16.0F - this.positionTo.y, this.positionTo.x, 16.0F - this.positionFrom.y};
                break;
            case WEST:
            case EAST:
                afloat = new float[]{this.positionFrom.z, 16.0F - this.positionTo.y, this.positionTo.z, 16.0F - this.positionFrom.y};
                break;
            default:
                throw new NullPointerException();
        }

        return afloat;
    }

    static class Deserializer implements JsonDeserializer<BlockPart> {
        public BlockPart deserialize(final JsonElement p_deserialize_1_, final Type p_deserialize_2_, final JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            final JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            final Vector3f vector3f = this.parsePositionFrom(jsonobject);
            final Vector3f vector3f1 = this.parsePositionTo(jsonobject);
            final BlockPartRotation blockpartrotation = this.parseRotation(jsonobject);
            final Map<EnumFacing, BlockPartFace> map = this.parseFacesCheck(p_deserialize_3_, jsonobject);

            if (jsonobject.has("shade") && !JsonUtils.isBoolean(jsonobject, "shade")) {
                throw new JsonParseException("Expected shade to be a Boolean");
            } else {
                final boolean flag = JsonUtils.getBoolean(jsonobject, "shade", true);
                return new BlockPart(vector3f, vector3f1, map, blockpartrotation, flag);
            }
        }

        private BlockPartRotation parseRotation(final JsonObject p_178256_1_) {
            BlockPartRotation blockpartrotation = null;

            if (p_178256_1_.has("rotation")) {
                final JsonObject jsonobject = JsonUtils.getJsonObject(p_178256_1_, "rotation");
                final Vector3f vector3f = this.parsePosition(jsonobject, "origin");
                vector3f.scale(0.0625F);
                final EnumFacing.Axis enumfacing$axis = this.parseAxis(jsonobject);
                final float f = this.parseAngle(jsonobject);
                final boolean flag = JsonUtils.getBoolean(jsonobject, "rescale", false);
                blockpartrotation = new BlockPartRotation(vector3f, enumfacing$axis, f, flag);
            }

            return blockpartrotation;
        }

        private float parseAngle(final JsonObject p_178255_1_) {
            final float f = JsonUtils.getFloat(p_178255_1_, "angle");

            if (f != 0.0F && MathHelper.abs(f) != 22.5F && MathHelper.abs(f) != 45.0F) {
                throw new JsonParseException("Invalid rotation " + f + " found, only -45/-22.5/0/22.5/45 allowed");
            } else {
                return f;
            }
        }

        private EnumFacing.Axis parseAxis(final JsonObject p_178252_1_) {
            final String s = JsonUtils.getString(p_178252_1_, "axis");
            final EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.byName(s.toLowerCase());

            if (enumfacing$axis == null) {
                throw new JsonParseException("Invalid rotation axis: " + s);
            } else {
                return enumfacing$axis;
            }
        }

        private Map<EnumFacing, BlockPartFace> parseFacesCheck(final JsonDeserializationContext p_178250_1_, final JsonObject p_178250_2_) {
            final Map<EnumFacing, BlockPartFace> map = this.parseFaces(p_178250_1_, p_178250_2_);

            if (map.isEmpty()) {
                throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
            } else {
                return map;
            }
        }

        private Map<EnumFacing, BlockPartFace> parseFaces(final JsonDeserializationContext p_178253_1_, final JsonObject p_178253_2_) {
            final Map<EnumFacing, BlockPartFace> map = Maps.newEnumMap(EnumFacing.class);
            final JsonObject jsonobject = JsonUtils.getJsonObject(p_178253_2_, "faces");

            for (final Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                final EnumFacing enumfacing = this.parseEnumFacing(entry.getKey());
                map.put(enumfacing, p_178253_1_.deserialize(entry.getValue(), BlockPartFace.class));
            }

            return map;
        }

        private EnumFacing parseEnumFacing(final String name) {
            final EnumFacing enumfacing = EnumFacing.byName(name);

            if (enumfacing == null) {
                throw new JsonParseException("Unknown facing: " + name);
            } else {
                return enumfacing;
            }
        }

        private Vector3f parsePositionTo(final JsonObject p_178247_1_) {
            final Vector3f vector3f = this.parsePosition(p_178247_1_, "to");

            if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
                return vector3f;
            } else {
                throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + vector3f);
            }
        }

        private Vector3f parsePositionFrom(final JsonObject p_178249_1_) {
            final Vector3f vector3f = this.parsePosition(p_178249_1_, "from");

            if (vector3f.x >= -16.0F && vector3f.y >= -16.0F && vector3f.z >= -16.0F && vector3f.x <= 32.0F && vector3f.y <= 32.0F && vector3f.z <= 32.0F) {
                return vector3f;
            } else {
                throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + vector3f);
            }
        }

        private Vector3f parsePosition(final JsonObject p_178251_1_, final String p_178251_2_) {
            final JsonArray jsonarray = JsonUtils.getJsonArray(p_178251_1_, p_178251_2_);

            if (jsonarray.size() != 3) {
                throw new JsonParseException("Expected 3 " + p_178251_2_ + " values, found: " + jsonarray.size());
            } else {
                final float[] afloat = new float[3];

                for (int i = 0; i < afloat.length; ++i) {
                    afloat[i] = JsonUtils.getFloat(jsonarray.get(i), p_178251_2_ + "[" + i + "]");
                }

                return new Vector3f(afloat[0], afloat[1], afloat[2]);
            }
        }
    }
}
