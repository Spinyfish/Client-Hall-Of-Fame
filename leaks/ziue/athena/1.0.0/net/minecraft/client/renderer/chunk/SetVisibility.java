package net.minecraft.client.renderer.chunk;

import net.minecraft.util.*;
import java.util.*;

public class SetVisibility
{
    private static final int COUNT_FACES;
    private final BitSet bitSet;
    
    public SetVisibility() {
        this.bitSet = new BitSet(SetVisibility.COUNT_FACES * SetVisibility.COUNT_FACES);
    }
    
    public void setManyVisible(final Set<EnumFacing> p_178620_1_) {
        for (final EnumFacing enumfacing : p_178620_1_) {
            for (final EnumFacing enumfacing2 : p_178620_1_) {
                this.setVisible(enumfacing, enumfacing2, true);
            }
        }
    }
    
    public void setVisible(final EnumFacing facing, final EnumFacing facing2, final boolean p_178619_3_) {
        this.bitSet.set(facing.ordinal() + facing2.ordinal() * SetVisibility.COUNT_FACES, p_178619_3_);
        this.bitSet.set(facing2.ordinal() + facing.ordinal() * SetVisibility.COUNT_FACES, p_178619_3_);
    }
    
    public void setAllVisible(final boolean visible) {
        this.bitSet.set(0, this.bitSet.size(), visible);
    }
    
    public boolean isVisible(final EnumFacing facing, final EnumFacing facing2) {
        return this.bitSet.get(facing.ordinal() + facing2.ordinal() * SetVisibility.COUNT_FACES);
    }
    
    @Override
    public String toString() {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(' ');
        for (final EnumFacing enumfacing : EnumFacing.values()) {
            stringbuilder.append(' ').append(enumfacing.toString().toUpperCase().charAt(0));
        }
        stringbuilder.append('\n');
        for (final EnumFacing enumfacing2 : EnumFacing.values()) {
            stringbuilder.append(enumfacing2.toString().toUpperCase().charAt(0));
            for (final EnumFacing enumfacing3 : EnumFacing.values()) {
                if (enumfacing2 == enumfacing3) {
                    stringbuilder.append("  ");
                }
                else {
                    final boolean flag = this.isVisible(enumfacing2, enumfacing3);
                    stringbuilder.append(' ').append(flag ? 'Y' : 'n');
                }
            }
            stringbuilder.append('\n');
        }
        return stringbuilder.toString();
    }
    
    static {
        COUNT_FACES = EnumFacing.values().length;
    }
}
