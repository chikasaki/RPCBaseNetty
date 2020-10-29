package rpc.server.serverImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import rpc.interfaces.Service;
import rpc.message.CallContent;
import rpc.message.RetContent;
import rpc.server.Dispatcher;
import rpc.server.MethodCall;
import rpc.server.interfaceImpl.MyService;
import rpc.utils.Helper;

public class NettyHttpServer {
    public static void main(String[] args) throws InterruptedException {
        //Dispatcher服务注册
        Dispatcher dispatcher = Dispatcher.getInstance();
        dispatcher.setService(Service.class.getName(), new MyService());

        NioEventLoopGroup boss = new NioEventLoopGroup(5);
        ServerBootstrap bs = new ServerBootstrap();

        ChannelFuture bind = bs.group(boss, boss)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(1024 * 512))
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        FullHttpRequest request = (FullHttpRequest) msg;
                                        System.out.println(request);

                                        ByteBuf byteBuf = request.content();
                                        byte[] contentBs = new byte[byteBuf.readableBytes()];
                                        byteBuf.readBytes(contentBs);

                                        CallContent callContent = (CallContent) Helper.decode(contentBs);
                                        Object res = MethodCall.call(callContent);
                                        RetContent retContent = new RetContent();
                                        retContent.setRes(res);
                                        byte[] retContentBs = Helper.serialize(retContent);

                                        DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                                                HttpVersion.HTTP_1_0,
                                                HttpResponseStatus.OK,
                                                Unpooled.copiedBuffer(retContentBs)
                                        );

                                        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, retContentBs.length);

                                        ctx.writeAndFlush(response);
                                    }
                                });
                    }
                }).bind(Helper.SERVER_ADDR);
        bind.sync().channel().closeFuture().sync();
    }
}
