package rpc.proxy;

import rpc.client.Transportation;
import rpc.client.clientImpl.HttpNonStateTransportation;
import rpc.message.CallContent;
import rpc.message.HttpCallContent;
import rpc.utils.Helper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MyProxy {
    private static ConcurrentHashMap<Long, CompletableFuture> requests =
            new ConcurrentHashMap<>();

    public static <T> T getService(Class<?> clazz) {
        ClassLoader loader = clazz.getClassLoader();
        Class<?>[] classes = {clazz};
        return (T) Proxy.newProxyInstance(loader, classes, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String className = clazz.getName();
                String methodName = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();

                // 1. 传送对象的封装
//                CallContent msgContent = new CallContent();
//                {
//                    msgContent.setClassName(className);
//                    msgContent.setMethodName(methodName);
//                    msgContent.setParameters(args);
//                    msgContent.setParameterTypes(parameterTypes);
//                }

                HttpCallContent msgContent = new HttpCallContent();
                {
                    msgContent.setClassName(className);
                    msgContent.setMethodName(methodName);
                    msgContent.setParameters(args);
                    msgContent.setParameterTypes(parameterTypes);
                    msgContent.setState(Helper.getRandomID());
                }
//                Transportation myPTransportation = new MyPTransportation();
//                CompletableFuture cf = myPTransportation.transport(msgContent);
                Transportation transportation = new HttpNonStateTransportation();
                CompletableFuture cf = transportation.transport(msgContent);

                return cf.get();
            }
        });
    }

    public static void register(long requestID, CompletableFuture cf) {
        requests.put(requestID, cf);
    }

    public static void callback(long requestID, Object res) {
        CompletableFuture cf = requests.get(requestID);
        cf.complete(res);
        remove(requestID);
    }

    public static void remove(long requestID) {
        requests.remove(requestID);
    }
}
