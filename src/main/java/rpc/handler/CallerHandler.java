package rpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import rpc.message.CallContent;
import rpc.message.MsgHeader;
import rpc.message.PackageMsg;
import rpc.message.RetContent;
import rpc.proxy.MyProxy;

public class CallerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PackageMsg packageMsg = (PackageMsg) msg;
        MsgHeader header = packageMsg.getHeader();
        RetContent content = (RetContent) packageMsg.getContent();

        MyProxy.callback(header.getRequestID(), content);
    }
}
