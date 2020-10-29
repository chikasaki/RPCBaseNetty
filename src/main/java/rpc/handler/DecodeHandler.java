package rpc.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import rpc.message.Content;
import rpc.message.MsgHeader;
import rpc.message.PackageMsg;
import rpc.utils.Helper;

import java.util.List;

public class DecodeHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        while(byteBuf.readableBytes() >= MsgHeader.HEADER_LEN) {
            byte[] bs = new byte[MsgHeader.HEADER_LEN];
            byteBuf.getBytes(byteBuf.readerIndex(), bs);

            MsgHeader header = (MsgHeader) Helper.decode(bs);
            if (byteBuf.readableBytes() >= header.getDataLen() + MsgHeader.HEADER_LEN) {
                byte[] contentBs = new byte[header.getDataLen()];
                //移动指针
                byteBuf.readBytes(MsgHeader.HEADER_LEN);
                byteBuf.readBytes(contentBs);
                Content content = (Content) Helper.decode(contentBs);

                PackageMsg packageMsg = new PackageMsg();
                {
                    packageMsg.setHeader(header);
                    packageMsg.setContent(content);
                }

                list.add(packageMsg);
            } else {
                break;
            }
        }
    }
}
