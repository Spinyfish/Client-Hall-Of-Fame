package de.datasecs.hydra.shared.protocol.packets;

import de.datasecs.hydra.shared.serialization.IgnoreSerialization;
import gay.sukumi.irc.profile.UserProfile;
import io.netty.buffer.ByteBuf;

import java.io.*;
import java.lang.reflect.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with love by DataSecs on 29.09.2017.
 */
public abstract class Packet {

    private Object objectToSerialize;

    public abstract void read(ByteBuf byteBuf);

    public abstract void write(ByteBuf byteBuf);

    protected void writeInt(ByteBuf byteBuf, int integer) {
        byteBuf.writeInt(integer);
    }

    protected int readInt(ByteBuf byteBuf) {
        return byteBuf.readInt();
    }

    protected void writeString(ByteBuf byteBuf, String string) {
        byteBuf.writeInt(string.length());

        try {
            byteBuf.writeBytes(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    protected String readString(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readInt()];
        byteBuf.readBytes(bytes);

        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void writeObject(ByteBuf byteBuf, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null");
        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Object readObject(ByteBuf byteBuf) {
        Object object = null;

        int length = byteBuf.readInt();
        if (length > byteBuf.readableBytes()) {
            throw new IllegalStateException("length cannot be larger than the readable bytes");
        }

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes); ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            object = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }

    protected <T> void writeCustomObject(ByteBuf byteBuf, T customObject, String pathOfCustomClassAtReceiver) {
        writeCustomObject(byteBuf, customObject, "", pathOfCustomClassAtReceiver);
    }


    private <T> void writeCustomObject(ByteBuf byteBuf, T customObject, String prefix, String pathOfCustomClassAtReceiver) {
        // Send path for rebuild of custom class
        if (!prefix.startsWith("#")) {
            writeString(byteBuf, String.format("%s.%s", pathOfCustomClassAtReceiver, customObject.getClass().getSimpleName()));
        }

        boolean isObject = true;
        for (Field field : customObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!Modifier.isTransient(field.getModifiers())) {
                try {
                    objectToSerialize = field.get(customObject);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                if (!(objectToSerialize instanceof Serializable)) {
                    writeCustomObject(byteBuf, objectToSerialize, String.format("%s#", prefix), pathOfCustomClassAtReceiver);
                } else {
                    String fieldName = field.getName();
                    if (prefix.startsWith("#")) {
                        if (isObject) {
                            fieldName = String.format("%s%s;*.%s", prefix, fieldName, customObject.getClass().getSimpleName());
                            isObject = false;
                        } else {
                            fieldName = String.format("%s%s", prefix, fieldName);
                        }
                    }

                    writeString(byteBuf, fieldName);
                    writeObject(byteBuf, objectToSerialize);
                }
            }
        }

        if (!prefix.startsWith("#")) {
            // Signalizes that transmission is over
            writeString(byteBuf, "~");
        }
    }

    protected <T> T readCustomObject(ByteBuf byteBuf) {
        String pathOfCustomClass = readString(byteBuf);
        T customObject = null;
        try {
            customObject = (T) Class.forName(pathOfCustomClass).newInstance();
            pathOfCustomClass = pathOfCustomClass.substring(0, pathOfCustomClass.lastIndexOf("."));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> fields = new ConcurrentHashMap<>();
        Map<String, Object> subFields = new ConcurrentHashMap<>();

        String input;
        while (!(input = readString(byteBuf)).startsWith("~")) {
            if (input.contains(";")) {
                subFields.clear();

                String[] fieldNames = input.replace("*", pathOfCustomClass).split(";");

                subFields.put(fieldNames[0].replaceAll("#", ""), String.valueOf(readObject(byteBuf)));
                fields.put(fieldNames[1], subFields);
            } else {
                if (input.startsWith("#")) {
                    subFields.put(input.replaceAll("#", ""), readObject(byteBuf));
                } else {
                    fields.put(input, readObject(byteBuf));
                }
            }
        }

        return readCustomObject(customObject, fields);
    }

    private <T> T readCustomObject(T customObject, Map<String, Object> fields) {
        for (Method currentMethod : customObject.getClass().getDeclaredMethods()) {
            if (currentMethod.getName().contains("set") && !currentMethod.isAnnotationPresent(IgnoreSerialization.class)) {
                fields.forEach((key, value) -> {
                    Class<?> clazz = null;
                    boolean valueIsMap = value instanceof Map;

                    try {
                        if (valueIsMap) {
                            clazz = Class.forName(key);
                            key = key.substring(key.lastIndexOf(".") + 1);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    // TODO: Think of a better way than comparing the amount of characters, rather compare the class of the parameter and the class of the value
                    if ((currentMethod.getParameterCount() == 1 ? currentMethod.getParameterTypes()[0] : null) != null
                            && currentMethod.getName().toLowerCase().contains(key.toLowerCase())
                            && (currentMethod.getName().length() - 3) == key.length()) {
                        try {
                            currentMethod.invoke(customObject, valueIsMap ? readCustomObject(clazz.newInstance(), (Map<String, Object>) value) : value);
                            fields.remove(key);
                        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        return customObject;
    }

    protected void writeArray(ByteBuf byteBuf, Object[] array) {
        writeObject(byteBuf, array);
    }

    protected <T> T[] readArray(ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        if (length > byteBuf.readableBytes()) {
            throw new IllegalStateException("length can't be larger than the readable bytes");
        }

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (T[]) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected <T> void writeCustomClassArray(ByteBuf byteBuf, T[] array, String pathOfCustomClassAtReceiver) {
        writeInt(byteBuf, array.length);
        writeString(byteBuf, String.format("%s.%s", pathOfCustomClassAtReceiver, array.getClass().getSimpleName().replace("[]", "")));
        for (T t : array) {
            writeCustomObject(byteBuf, t, "", pathOfCustomClassAtReceiver);
        }
    }

    protected <T> T[] readCustomClassArray(ByteBuf byteBuf) {
        int length = readInt(byteBuf);
        String path = readString(byteBuf);
        T[] array = null;
        try {
            array = (T[]) Array.newInstance(Class.forName(path), length);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < length; i++) {
            array[i] = readCustomObject(byteBuf);
        }

        return array;
    }


    /* kitty's edits */
    public void writeUserProfile(ByteBuf byteBuf, UserProfile userProfile) {
        writeString(byteBuf, userProfile.getUsername());
        writeString(byteBuf, userProfile.getUuid());
        writeString(byteBuf, userProfile.getMcUsername());
        writeString(byteBuf, userProfile.getCurrentServer());
        writeString(byteBuf, userProfile.getRank().name());
        writeString(byteBuf, userProfile.getClient().name());
    }

    public UserProfile readUserProfile(ByteBuf byteBuf) {
        return new UserProfile(
                readString(byteBuf),
                readString(byteBuf),
                readString(byteBuf),
                readString(byteBuf),
                UserProfile.Rank.valueOf(readString(byteBuf)),
                UserProfile.Client.valueOf(readString(byteBuf)));
    }

}