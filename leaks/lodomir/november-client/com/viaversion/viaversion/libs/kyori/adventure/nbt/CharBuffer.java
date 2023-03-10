/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.kyori.adventure.nbt;

import com.viaversion.viaversion.libs.kyori.adventure.nbt.StringTagParseException;

final class CharBuffer {
    private final CharSequence sequence;
    private int index;

    CharBuffer(CharSequence sequence) {
        this.sequence = sequence;
    }

    public char peek() {
        return this.sequence.charAt(this.index);
    }

    public char peek(int offset) {
        return this.sequence.charAt(this.index + offset);
    }

    public char take() {
        return this.sequence.charAt(this.index++);
    }

    public boolean advance() {
        ++this.index;
        return this.hasMore();
    }

    public boolean hasMore() {
        return this.index < this.sequence.length();
    }

    public boolean hasMore(int offset) {
        return this.index + offset < this.sequence.length();
    }

    public CharSequence takeUntil(char until) throws StringTagParseException {
        until = Character.toLowerCase(until);
        int endIdx = -1;
        for (int idx = this.index; idx < this.sequence.length(); ++idx) {
            if (this.sequence.charAt(idx) == '\\') {
                ++idx;
                continue;
            }
            if (Character.toLowerCase(this.sequence.charAt(idx)) != until) continue;
            endIdx = idx;
            break;
        }
        if (endIdx == -1) {
            throw this.makeError("No occurrence of " + until + " was found");
        }
        CharSequence result = this.sequence.subSequence(this.index, endIdx);
        this.index = endIdx + 1;
        return result;
    }

    public CharBuffer expect(char expectedChar) throws StringTagParseException {
        this.skipWhitespace();
        if (!this.hasMore()) {
            throw this.makeError("Expected character '" + expectedChar + "' but got EOF");
        }
        if (this.peek() != expectedChar) {
            throw this.makeError("Expected character '" + expectedChar + "' but got '" + this.peek() + "'");
        }
        this.take();
        return this;
    }

    public boolean takeIf(char token) {
        this.skipWhitespace();
        if (this.hasMore() && this.peek() == token) {
            this.advance();
            return true;
        }
        return false;
    }

    public CharBuffer skipWhitespace() {
        while (this.hasMore() && Character.isWhitespace(this.peek())) {
            this.advance();
        }
        return this;
    }

    public StringTagParseException makeError(String message) {
        return new StringTagParseException(message, this.sequence, this.index);
    }
}

