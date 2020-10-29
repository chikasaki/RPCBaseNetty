package rpc.client.clientImpl;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import rpc.client.Transportation;
import rpc.connects.ClientSocketFactory;
import rpc.message.CallContent;
import rpc.message.HttpCallContent;
import rpc.proxy.MyProxy;
import rpc.utils.Helper;

import java.util.concurrent.CompletableFuture;

public class HttpStateTransportation implements Transportation {
    @Override
    public CompletableFuture transport(CallContent content) {
        CompletableFuture cf = new CompletableFuture();

        ClientSocketFactory factory = ClientSocketFactory.getInstance();
        NioSocketChannel httpClient = factory.getHttpClient(Helper.SERVER_ADDR);

        byte[] contentBs = Helper.serialize(content);

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_0,
                HttpMethod.POST,
                "/",
                Unpooled.copiedBuffer(contentBs)
        );
        if(content instanceof HttpCallContent) {
            HttpCallContent httpCallContent = (HttpCallContent) content;
            MyProxy.register(httpCallContent.getState(), cf);
        }
        ChannelFuture channelFuture = httpClient.writeAndFlush(request);
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return cf;
    }
}
