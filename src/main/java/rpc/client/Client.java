package rpc.client;

import rpc.interfaces.Service;
import rpc.message.RetContent;
import rpc.proxy.MyProxy;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    public static void main(String[] args) throws IOException {
        int size = 20;
        AtomicInteger ai = new AtomicInteger(0);
        Thread[] threads = new Thread[size];
        for(int i = 0; i < size; i ++) {
            threads[i] = new Thread(() -> {
                Service service = MyProxy.getService(Service.class);
                String param = "hello server!" + ai.getAndIncrement();
//                System.out.println("hello");
                RetContent res = (RetContent) service.call(param);
                System.out.println("client msg: " + param + " ---" + res.getRes());
//                System.out.println("hello");
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        System.in.read();
    }
}
