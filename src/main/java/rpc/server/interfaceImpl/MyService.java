package rpc.server.interfaceImpl;

import rpc.interfaces.Service;

public class MyService implements Service {
    @Override
    public Object call(String param) {
        return param + "zhangsan";
    }
}
