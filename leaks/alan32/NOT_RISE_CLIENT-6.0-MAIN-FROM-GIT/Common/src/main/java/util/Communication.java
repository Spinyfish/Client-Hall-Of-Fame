package util;

import encryption.impl.AESEncryption;
import encryption.impl.RSAEncryption;
import lombok.Getter;
import packet.Packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.UUID;

@Getter
public final class Communication {

    private final Socket socket;

    private RSAEncryption rsaEncryption;
    private AESEncryption aesEncryption;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Communication(final Socket socket, final boolean server) {
        this.socket = socket;

        try {
            System.out.println("CMA");
            this.output = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("CMI");
            this.input = new ObjectInputStream(socket.getInputStream());

            if (server) {
                this.rsaEncryption = new RSAEncryption();

                this.output.writeObject(this.rsaEncryption.getPublicKey());

                this.aesEncryption = new AESEncryption(new String(this.rsaEncryption.decrypt(Base64.getDecoder().decode(this.input.readUTF()))));
            } else {
                System.out.println("CMC");
                this.rsaEncryption = new RSAEncryption(null, (PublicKey) this.input.readObject());

                final String key = UUID.randomUUID().toString().replace("-", "");
                System.out.println("CMK");

                this.aesEncryption = new AESEncryption(key);
                this.output.writeUTF(Base64.getEncoder().encodeToString(this.rsaEncryption.encrypt(key.getBytes(StandardCharsets.UTF_8))));
                System.out.println("CME");
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void write(final Packet<?> packet) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(bos);

            out.writeObject(packet);
            out.flush();
            out.close();
            byte[] packetData = bos.toByteArray();
            bos.close();

            this.output.writeInt(packetData.length);
            this.output.write(packetData);
            this.output.reset();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public Packet<?> read() {
        try {
            int length = this.input.readInt();
            byte[] packetData = new byte[length];
            this.input.readFully(packetData, 0, length);
            final ByteArrayInputStream bis = new ByteArrayInputStream(packetData);
            final ObjectInputStream ois = new ObjectInputStream(bis);
            bis.close();
            Packet<?> p = (Packet<?>) ois.readObject();
            ois.close();

            return p;

        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /*@Data
    public static class EncryptedTraffic implements Serializable {
        private static final long serialVersionUID = 2L;
        private static final int CURRENT_SERIAL_VERSION  = 2;

        private byte[] content;

        public EncryptedTraffic() {}

        public EncryptedTraffic(byte[] c) {
            this.content = c;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            int version = in.readInt();
            if (version != CURRENT_SERIAL_VERSION )
                throw new ClassNotFoundException("Mismatched versions: wanted " + CURRENT_SERIAL_VERSION  + ", got " + version);

            int length = in.readInt();
            this.content = new byte[length];
            in.readFully(content);
        }


        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(CURRENT_SERIAL_VERSION);
            out.writeInt(content.length);
            out.write(content);
        }
    }*/
}
