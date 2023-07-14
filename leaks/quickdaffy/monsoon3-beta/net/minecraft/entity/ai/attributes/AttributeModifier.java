/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ThreadLocalRandom
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Random;
import java.util.UUID;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.Validate;

public class AttributeModifier {
    private final double amount;
    private final int operation;
    private final String name;
    private final UUID id;
    private boolean isSaved = true;

    public AttributeModifier(String nameIn, double amountIn, int operationIn) {
        this(MathHelper.getRandomUuid((Random)ThreadLocalRandom.current()), nameIn, amountIn, operationIn);
    }

    public AttributeModifier(UUID idIn, String nameIn, double amountIn, int operationIn) {
        this.id = idIn;
        this.name = nameIn;
        this.amount = amountIn;
        this.operation = operationIn;
        Validate.notEmpty((CharSequence)nameIn, (String)"Modifier name cannot be empty", (Object[])new Object[0]);
        Validate.inclusiveBetween((long)0L, (long)2L, (long)operationIn, (String)"Invalid operation");
    }

    public UUID getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getOperation() {
        return this.operation;
    }

    public double getAmount() {
        return this.amount;
    }

    public boolean isSaved() {
        return this.isSaved;
    }

    public AttributeModifier setSaved(boolean saved) {
        this.isSaved = saved;
        return this;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            AttributeModifier attributemodifier = (AttributeModifier)p_equals_1_;
            return !(this.id != null ? !this.id.equals(attributemodifier.id) : attributemodifier.id != null);
        }
        return false;
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public String toString() {
        return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + this.name + '\'' + ", id=" + this.id + ", serialize=" + this.isSaved + '}';
    }
}

