/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

class ELFAnalyser {
    private static final byte[] ELF_MAGIC = new byte[]{127, 69, 76, 70};
    private static final int EF_ARM_ABI_FLOAT_HARD = 1024;
    private static final int EF_ARM_ABI_FLOAT_SOFT = 512;
    private static final int EI_DATA_BIG_ENDIAN = 2;
    private static final int E_MACHINE_ARM = 40;
    private static final int EI_CLASS_64BIT = 2;
    private final String filename;
    private boolean ELF = false;
    private boolean _64Bit = false;
    private boolean bigEndian = false;
    private boolean armHardFloat = false;
    private boolean armSoftFloat = false;
    private boolean arm = false;

    public static ELFAnalyser analyse(String filename) throws IOException {
        ELFAnalyser res = new ELFAnalyser(filename);
        res.runDetection();
        return res;
    }

    public boolean isELF() {
        return this.ELF;
    }

    public boolean is64Bit() {
        return this._64Bit;
    }

    public boolean isBigEndian() {
        return this.bigEndian;
    }

    public String getFilename() {
        return this.filename;
    }

    public boolean isArmHardFloat() {
        return this.armHardFloat;
    }

    public boolean isArmSoftFloat() {
        return this.armSoftFloat;
    }

    public boolean isArm() {
        return this.arm;
    }

    private ELFAnalyser(String filename) {
        this.filename = filename;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void runDetection() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.filename, "r");
        try {
            if (raf.length() > 4L) {
                byte[] magic = new byte[4];
                raf.seek(0L);
                raf.read(magic);
                if (Arrays.equals(magic, ELF_MAGIC)) {
                    this.ELF = true;
                }
            }
            if (!this.ELF) {
                return;
            }
            raf.seek(4L);
            byte sizeIndicator = raf.readByte();
            this._64Bit = sizeIndicator == 2;
            raf.seek(0L);
            ByteBuffer headerData = ByteBuffer.allocate(this._64Bit ? 64 : 52);
            raf.getChannel().read(headerData, 0L);
            this.bigEndian = headerData.get(5) == 2;
            headerData.order(this.bigEndian ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN);
            boolean bl = this.arm = headerData.get(18) == 40;
            if (this.arm) {
                int flags = headerData.getInt(this._64Bit ? 48 : 36);
                this.armHardFloat = (flags & 0x400) == 1024;
                this.armSoftFloat = !this.armHardFloat;
            }
        }
        finally {
            try {
                raf.close();
            }
            catch (IOException magic) {}
        }
    }
}

