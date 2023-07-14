/*
 * Decompiled with CFR 0.152.
 */
package util;

import encryption.impl.AESEncryption;
import encryption.impl.RSAEncryption;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import packet.Packet;

public final class Communication {
    private final Socket socket;
    private RSAEncryption rsaEncryption;
    private AESEncryption aesEncryption;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Communication(Socket socket, boolean server) {
        this.socket = socket;
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
            if (server) {
                this.rsaEncryption = new RSAEncryption();
                this.output.writeObject(this.rsaEncryption.getPublicKey());
                this.aesEncryption = new AESEncryption(new String(this.rsaEncryption.decrypt(Base64.getDecoder().decode(this.input.readUTF()))));
            } else {
                this.rsaEncryption = new RSAEncryption(null, (PublicKey)this.input.readObject());
                String key = UUID.randomUUID().toString().replace("-", "");
                this.aesEncryption = new AESEncryption(key);
                this.output.writeUTF(Base64.getEncoder().encodeToString(this.rsaEncryption.encrypt(key.getBytes(StandardCharsets.UTF_8))));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void write(Packet<?> packet) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(packet);
            out.flush();
            EncryptedTraffic traffic = new EncryptedTraffic(this.aesEncryption.encrypt(bos.toByteArray()));
            this.output.writeObject(traffic);
            this.output.reset();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public Packet<?> read() {
        try {
            EncryptedTraffic traffic = (EncryptedTraffic)this.input.readObject();
            byte[] data = this.aesEncryption.decrypt(traffic.getContent());
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Packet)ois.readObject();
        }
        catch (Exception exception) {
            return null;
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    public RSAEncryption getRsaEncryption() {
        return this.rsaEncryption;
    }

    public AESEncryption getAesEncryption() {
        return this.aesEncryption;
    }

    public ObjectOutputStream getOutput() {
        return this.output;
    }

    public ObjectInputStream getInput() {
        return this.input;
    }

    static class EncryptedTraffic
    implements Serializable {
        private final byte[] content;

        public EncryptedTraffic(byte[] content) {
            this.content = content;
        }

        public byte[] getContent() {
            return this.content;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof EncryptedTraffic)) {
                return false;
            }
            EncryptedTraffic other = (EncryptedTraffic)o;
            if (!other.canEqual(this)) {
                return false;
            }
            return Arrays.equals(this.getContent(), other.getContent());
        }

        protected boolean canEqual(Object other) {
            return other instanceof EncryptedTraffic;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + Arrays.hashCode(this.getContent());
            return result;
        }

        public String toString() {
            return "Communication.EncryptedTraffic(content=" + Arrays.toString(this.getContent()) + ")";
        }
    }
}

