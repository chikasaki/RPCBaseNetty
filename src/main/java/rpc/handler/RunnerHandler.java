package rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import rpc.message.CallContent;
import rpc.message.MsgHeader;
import rpc.message.PackageMsg;
import rpc.message.RetContent;
import rpc.server.MethodCall;
import rpc.utils.Helper;

public class RunnerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PackageMsg packageMsg = (PackageMsg) msg;
        CallContent content = (CallContent) packageMsg.getContent();

        String ioThreadName = Thread.currentThread().getName();
        ctx.executor().parent().next().execute(() -> {
            String executeThreadName = Thread.currentThread().getName();
            String resContent = "IO Thread: " + ioThreadName
                    + ", Execute Thread: " + executeThreadName
                    + ", Accept MSG: " + content.getParameters()[0];
            System.out.println(resContent);
            RetContent retContent = new RetContent();

//            retContent.setRes(resContent);
            retContent.setRes(MethodCall.call(content));

            byte[] retBs = Helper.serialize(retContent);
            MsgHeader header = MsgHeader.constructHeader(retBs, MsgHeader.RET_TYPE);
            header.setRequestID(packageMsg.getHeader().getRequestID());
            byte[] headerBs = Helper.serialize(header);

            // 开始写出
            ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(headerBs.length + retBs.length);
            buf.writeBytes(headerBs);
            buf.writeBytes(retBs);

            ctx.channel().writeAndFlush(buf);
        });
    }
}
