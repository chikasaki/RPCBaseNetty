package rpc.utils;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.UUID;

public class Helper {

    public static final InetSocketAddress SERVER_ADDR = new InetSocketAddress("localhost", 9090);

    public static byte[] serialize(Object msg) {
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(bout);

            oout.writeObject(msg);
            return bout.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object decode(byte[] bs) {
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(bs);
            ObjectInputStream oin = new ObjectInputStream(bin);
            Object object = oin.readObject();
            return object;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getRandomID() {
        return Math.abs(UUID.randomUUID().getLeastSignificantBits());
    }
}
