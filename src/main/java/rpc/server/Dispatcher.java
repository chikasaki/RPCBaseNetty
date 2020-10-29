package rpc.server;

import java.util.HashMap;

public class Dispatcher {

    private static final Dispatcher dispatcher;
    private static HashMap<String, Object> services = new HashMap<>();

    static {
        dispatcher = new Dispatcher();
    }

    private Dispatcher() {}

    public static Dispatcher getInstance() {
        return dispatcher;
    }

    public Object getService(String className) {
        Object o = services.get(className);
        return o;
    }

    public void setService(String className, Object o) {
        services.put(className, o);
    }
}
