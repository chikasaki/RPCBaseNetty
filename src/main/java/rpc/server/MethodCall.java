package rpc.server;

import rpc.message.CallContent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//在服务端，使用反射提供服务
public class MethodCall {
    public static Object call(CallContent content) {
        String className = content.getClassName();
        String methodName = content.getMethodName();
        Class<?>[] parameterTypes = content.getParameterTypes();
        Object[] parameters = content.getParameters();

        try {
            Object o = Dispatcher.getInstance().getService(className);
            if(o == null) throw new IllegalAccessException("have no object");
            Class<?> aClass = o.getClass();
            Method method = aClass.getMethod(methodName, parameterTypes);
            Object res = method.invoke(o, parameters);
            return res;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
