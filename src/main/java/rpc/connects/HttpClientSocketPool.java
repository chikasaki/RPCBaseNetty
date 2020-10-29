package rpc.connects;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import rpc.handler.CallerHandler;
import rpc.handler.DecodeHandler;
import rpc.message.RetContent;
import rpc.proxy.MyProxy;
import rpc.utils.Helper;

import java.net.InetSocketAddress;
import java.util.Random;

public class HttpClientSocketPool {
    private NioEventLoopGroup group;
    private InetSocketAddress address;
    private NioSocketChannel[] sockets;
    private Object[] locks;
    private int size;

    private Random random = new Random();

    public HttpClientSocketPool(int size, NioEventLoopGroup group, InetSocketAddress address) {
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
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(1024 * 512));
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                FullHttpResponse response = (FullHttpResponse) msg;

                                ByteBuf byteBuf = response.content();
                                byte[] retContentBs = new byte[byteBuf.readableBytes()];
                                byteBuf.readBytes(retContentBs);
                                RetContent retContent = (RetContent) Helper.decode(retContentBs);

                                MyProxy.callback(retContent.getState(), retContent);
                            }
                        });
                    }
                }).connect(address);
        return (NioSocketChannel) connect.sync().channel();
    }
}
