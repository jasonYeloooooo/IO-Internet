package com.nx.netty.splitpacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author mkp
 * @Description:
 */
public class MessageDecoder extends ByteToMessageDecoder {
    int length =0;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("MessageDecoder.decode 被调用");
        if(in.readableBytes() >= 4){
            if(length == 0){
                length = in.readInt();
            }
            if(in.readableBytes() < length){
                System.out.println("当前可短数据不够，继续等待");
                return ;
            }
            byte[] content = new byte[length];
            if(in.readableBytes() >= length){
                in.readBytes(content);
                //封装成MessageProtocol对象，传递给下一个handler业务
                MessageProtocol messageProtocol = new MessageProtocol();
                messageProtocol.setLen(length);
                messageProtocol.setContent(content);
                out.add(messageProtocol);
            }
        }
        length = 0;
    }
}
