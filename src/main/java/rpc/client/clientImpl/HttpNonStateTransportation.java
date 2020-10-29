package rpc.client.clientImpl;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import rpc.client.Transportation;
import rpc.message.CallContent;
import rpc.message.RetContent;
import rpc.utils.Helper;

import java.util.concurrent.CompletableFuture;

public class HttpNonStateTransportation implements Transportation {

    private static NioEventLoopGroup group = new NioEventLoopGroup(1);

    @Override
    public CompletableFuture transport(CallContent content) {

        CompletableFuture res = new CompletableFuture();

        Bootstrap bs = new Bootstrap();
        ChannelFuture connect = bs.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new HttpClientCodec())
                                .addLast(new HttpObjectAggregator(1024 * 512))
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        FullHttpResponse response = (FullHttpResponse) msg;
//                                        System.out.println(response);

                                        ByteBuf byteBuf = response.content();
                                        byte[] contentBs = new byte[byteBuf.readableBytes()];
                                        byteBuf.readBytes(contentBs);
                                        RetContent retContent = (RetContent) Helper.decode(contentBs);

                                        res.complete(retContent);
                                    }
                                });
                    }
                }).connect(Helper.SERVER_ADDR);
        try {
            Channel channel = connect.sync().channel();

            byte[] contentBs = Helper.serialize(content);
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_0,
                    HttpMethod.POST,
                    "/",
                    Unpooled.copiedBuffer(contentBs)
            );
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentBs.length);
            ChannelFuture channelFuture = channel.writeAndFlush(request);
            channelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return res;
    }
}
