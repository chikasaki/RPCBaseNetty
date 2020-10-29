package rpc.connects;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import rpc.handler.CallerHandler;
import rpc.handler.DecodeHandler;

import java.net.InetSocketAddress;
import java.util.Random;

public class ClientSocketPool {
    private NioEventLoopGroup group;
    private InetSocketAddress address;
    private NioSocketChannel[] sockets;
    private Object[] locks;
    private int size;

    private Random random = new Random();

    public ClientSocketPool(int size, NioEventLoopGroup group, InetSocketAddress address) {
        this.size = size;
        this.sockets = new NioSocketChannel[size];
        this.locks = new Object[size];
        this.group = group;
        this.address = address;

        for(int i = 0; i < size; i ++) {
            locks[i] = new Object();
        }
    }

    public NioSocketChannel getClient() {
        int i = random.nextInt(size);
        if (sockets[i] != null && sockets[i].isActive()) return sockets[i];

        synchronized (locks[i]) {
            if(sockets[i] == null || !sockets[i].isActive()) {
                try {
                    sockets[i] = createClient();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return sockets[i];
    }

    private NioSocketChannel createClient() throws InterruptedException {
        Bootstrap bs = new Bootstrap();
        ChannelFuture connect = bs.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new DecodeHandler());
                        pipeline.addLast(new CallerHandler());
                    }
                })
                .connect(address);
        return (NioSocketChannel) connect.sync().channel();
    }
}
