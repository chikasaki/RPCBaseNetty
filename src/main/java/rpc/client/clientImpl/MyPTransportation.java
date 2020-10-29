package rpc.client.clientImpl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import rpc.client.Transportation;
import rpc.connects.ClientSocketFactory;
import rpc.message.CallContent;
import rpc.message.MsgHeader;
import rpc.proxy.MyProxy;
import rpc.utils.Helper;

import java.util.concurrent.CompletableFuture;

//使用自定义协议的，transport
public class MyPTransportation implements Transportation {

    @Override
    public CompletableFuture transport(CallContent content) {
        //封装头部
        byte[] contentBytes = Helper.serialize(content);
        MsgHeader header = MsgHeader.constructHeader(contentBytes, MsgHeader.CALL_TYPE);

        //2. 获取socket连接
        ClientSocketFactory factory = ClientSocketFactory.getInstance();
        NioSocketChannel client = factory.getClient(Helper.SERVER_ADDR);

        //3. 使用socket连接发送消息，并注册相关的唤醒事件
        byte[] headerBs = Helper.serialize(header);
        ByteBuf buf = PooledByteBufAllocator.DEFAULT.directBuffer(headerBs.length + contentBytes.length);
        buf.writeBytes(headerBs);
        buf.writeBytes(contentBytes);
//                System.out.println(headerBs.length);

        CompletableFuture cf = new CompletableFuture();
        MyProxy.register(header.getRequestID(), cf);
        ChannelFuture channelFuture = client.writeAndFlush(buf);
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return cf;
    }
}
