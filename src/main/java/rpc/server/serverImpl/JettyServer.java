package rpc.server.serverImpl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import rpc.interfaces.Service;
import rpc.server.Dispatcher;
import rpc.server.HandlerServlet;
import rpc.server.interfaceImpl.MyService;
import rpc.utils.Helper;

public class JettyServer {
    public static void main(String[] args) {
        Dispatcher dispatcher = Dispatcher.getInstance();
        dispatcher.setService(Service.class.getName(), new MyService());

        Server server = new Server(Helper.SERVER_ADDR);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        server.setHandler(handler);
        handler.addServlet(HandlerServlet.class, "/*");

        try {
            server.start();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
