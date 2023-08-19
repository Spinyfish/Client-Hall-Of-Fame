package dev.thread.api.util.render.font;

import org.lwjgl.BufferUtils;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryUtil;
import org.lwjglx.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.nanovg.NVGTextRow.SIZEOF;
import static org.lwjgl.nanovg.NanoVG.*;

public class FontRenderer {
    private ByteBuffer font;
    private final String path, name;

    public FontRenderer(String path, String name) {
        this.path = path;
        this.name = name;

        try {
            font = getResourceBytes(path, 1024);
            nvgCreateFontMem(Display.nanoContext, name, font, false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addToFrame(Runnable runnable) {
        nvgBeginFrame(Display.nanoContext, Display.getWidth(), Display.getHeight(), 1);
        runnable.run();
        nvgEndFrame(Display.nanoContext);
    }

    public ByteBuffer getResourceBytes(String resource, int bufferSize) throws IOException {
        InputStream source = ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
        ReadableByteChannel rbc = Channels.newChannel(source);
        ByteBuffer buffer = BufferUtils.createByteBuffer(bufferSize);

        while (true) {
            int bytes = rbc.read(buffer);
            if (bytes == -1) {
                break;
            }
            if (buffer.remaining() == 0) {
                buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2);
            }
        }

        buffer.flip();
        return MemoryUtil.memSlice(buffer);
    }

    private ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    private NVGColor convert(Color color) {
        NVGColor nvgColor = NVGColor.calloc();
        nvgColor.r(color.getRed() / 255.0f);
        nvgColor.g(color.getGreen() / 255.0f);
        nvgColor.b(color.getBlue() / 255.0f);
        nvgColor.a(color.getAlpha() / 255.0f);
        return nvgColor;
    }

    public void drawString(float size, String text, float x, float y, Color color) {
        addToFrame(() -> {
            NVGColor nvgColor = convert(color);
            nvgBeginPath(Display.nanoContext);
            nvgFontFace(Display.nanoContext, name);
            nvgFontSize(Display.nanoContext, size);
            nvgTextAlign(Display.nanoContext, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
            nvgFillColor(Display.nanoContext, nvgColor);
            nvgText(Display.nanoContext, x * 2, y * 2, text);
            nvgClosePath(Display.nanoContext);
            nvgColor.free();
        });
    }

    public float getStringWidth(float fontSize, String text) {
        nvgFontFace(Display.nanoContext, name);
        nvgFontSize(Display.nanoContext, fontSize);
        return nvgTextBounds(Display.nanoContext, 0f, 0f, text, ByteBuffer.allocateDirect(SIZEOF).asFloatBuffer());
    }
}
