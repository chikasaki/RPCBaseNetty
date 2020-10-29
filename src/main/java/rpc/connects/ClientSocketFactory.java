package rpc.connects;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSocketFactory {
    private NioEventLoopGroup group = new NioEventLoopGroup(1);

    private final int size = 1;
    private static final ClientSocketFactory factory;
    private ConcurrentHashMap<InetSocketAddress, ClientSocketPool> pools;
    private ConcurrentHashMap<InetSocketAddress, HttpClientSocketPool> httpPools;

    static {
        factory = new ClientSocketFactory();
    }

    private ClientSocketFactory(){

        pools = new ConcurrentHashMap<>();
        httpPools = new ConcurrentHashMap<>();
    }

    public static ClientSocketFactory getInstance() {
        return factory;
    }

    public NioSocketChannel getClient(InetSocketAddress address) {
        ClientSocketPool pool = pools.get(address);
        if(pool == null) {
            synchronized (pools) {
                if(pools.get(address) == null) {
                    pool = new ClientSocketPool(size, group, address);
                    pools.put(address, pool);
                }
            }
            pool = pools.get(address);
        }

        return pool.getClient();
    }

    public NioSocketChannel getHttpClient(InetSocketAddress address) {
        HttpClientSocketPool pool = httpPools.get(address);
        if(pool == null) {
            synchronized (httpPools) {
                if(pools.get(address) == null) {
                    pool = new HttpClientSocketPool(size, group, address);
                    httpPools.put(address, pool);
                }
            }
            pool = httpPools.get(address);
        }

        return pool.getClient();
    }
}
