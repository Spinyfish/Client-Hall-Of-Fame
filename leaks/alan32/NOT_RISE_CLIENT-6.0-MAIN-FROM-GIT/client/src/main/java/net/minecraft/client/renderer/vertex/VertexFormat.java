package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class VertexFormat {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<VertexFormatElement> elements;
    private final List<Integer> offsets;

    /**
     * The next available offset in this vertex format
     */
    private int nextOffset;
    private int colorElementOffset;
    private final List<Integer> elementOffsetsById;
    private int normalElementOffset;

    public VertexFormat(final VertexFormat vertexFormatIn) {
        this();

        for (int i = 0; i < vertexFormatIn.getElementCount(); ++i) {
            this.func_181721_a(vertexFormatIn.getElement(i));
        }

        this.nextOffset = vertexFormatIn.getNextOffset();
    }

    public VertexFormat() {
        this.elements = Lists.newArrayList();
        this.offsets = Lists.newArrayList();
        this.nextOffset = 0;
        this.colorElementOffset = -1;
        this.elementOffsetsById = Lists.newArrayList();
        this.normalElementOffset = -1;
    }

    public void clear() {
        this.elements.clear();
        this.offsets.clear();
        this.colorElementOffset = -1;
        this.elementOffsetsById.clear();
        this.normalElementOffset = -1;
        this.nextOffset = 0;
    }

    @SuppressWarnings("incomplete-switch")
    public VertexFormat func_181721_a(final VertexFormatElement p_181721_1_) {
        if (p_181721_1_.isPositionElement() && this.hasPosition()) {
            LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
            return this;
        } else {
            this.elements.add(p_181721_1_);
            this.offsets.add(Integer.valueOf(this.nextOffset));

            switch (p_181721_1_.getUsage()) {
                case NORMAL:
                    this.normalElementOffset = this.nextOffset;
                    break;

                case COLOR:
                    this.colorElementOffset = this.nextOffset;
                    break;

                case UV:
                    this.elementOffsetsById.add(p_181721_1_.getIndex(), Integer.valueOf(this.nextOffset));
            }

            this.nextOffset += p_181721_1_.getSize();
            return this;
        }
    }

    public boolean hasNormal() {
        return this.normalElementOffset >= 0;
    }

    public int getNormalOffset() {
        return this.normalElementOffset;
    }

    public boolean hasColor() {
        return this.colorElementOffset >= 0;
    }

    public int getColorOffset() {
        return this.colorElementOffset;
    }

    public boolean hasElementOffset(final int id) {
        return this.elementOffsetsById.size() - 1 >= id;
    }

    public int getElementOffsetById(final int id) {
        return this.elementOffsetsById.get(id).intValue();
    }

    public String toString() {
        String s = "format: " + this.elements.size() + " elements: ";

        for (int i = 0; i < this.elements.size(); ++i) {
            s = s + this.elements.get(i).toString();

            if (i != this.elements.size() - 1) {
                s = s + " ";
            }
        }

        return s;
    }

    private boolean hasPosition() {
        int i = 0;

        for (final int j = this.elements.size(); i < j; ++i) {
            final VertexFormatElement vertexformatelement = this.elements.get(i);

            if (vertexformatelement.isPositionElement()) {
                return true;
            }
        }

        return false;
    }

    public int func_181719_f() {
        return this.getNextOffset() / 4;
    }

    public int getNextOffset() {
        return this.nextOffset;
    }

    public List<VertexFormatElement> getElements() {
        return this.elements;
    }

    public int getElementCount() {
        return this.elements.size();
    }

    public VertexFormatElement getElement(final int index) {
        return this.elements.get(index);
    }

    public int func_181720_d(final int p_181720_1_) {
        return this.offsets.get(p_181720_1_).intValue();
    }

    public boolean equals(final Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            final VertexFormat vertexformat = (VertexFormat) p_equals_1_;
            return this.nextOffset == vertexformat.nextOffset && (this.elements.equals(vertexformat.elements) && this.offsets.equals(vertexformat.offsets));
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.elements.hashCode();
        i = 31 * i + this.offsets.hashCode();
        i = 31 * i + this.nextOffset;
        return i;
    }
}
