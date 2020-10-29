package rpc.server.serverImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import rpc.handler.DecodeHandler;
import rpc.handler.RunnerHandler;
import rpc.interfaces.Service;
import rpc.server.Dispatcher;
import rpc.server.interfaceImpl.MyService;

import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws InterruptedException {

        //Dispatcher服务注册
        Dispatcher dispatcher = Dispatcher.getInstance();
        dispatcher.setService(Service.class.getName(), new MyService());

        NioEventLoopGroup boss = new NioEventLoopGroup(20);
        NioEventLoopGroup worker = boss;

        ServerBootstrap bs = new ServerBootstrap();
        ChannelFuture bind = bs.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        System.out.println("Server accept: " + nioSocketChannel.remoteAddress());
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new DecodeHandler());
                        pipeline.addLast(new RunnerHandler());
                    }
                })
                .bind(new InetSocketAddress(9090));
        bind.sync().channel().closeFuture().sync();
    }
}
