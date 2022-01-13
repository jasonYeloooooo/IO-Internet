package com.nx.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mkp
 * @Description:
 */
public class ChatServerHandler extends SimpleChannelInboundHandler {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("客户端 " + channel.remoteAddress() +" "+ sdf.format(new Date()) + "上线" + ",请注意接收消息\n");
        channelGroup.add(channel);
        System.out.println("客户端 " + channel.remoteAddress() + "上线\n");
    }



    SimpleDateFormat sdf = new SimpleDateFormat();
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.forEach(ch -> {
            if(ch != channel){
                ch.writeAndFlush("客戶端 " + channel.remoteAddress() + "发送数据为:" + msg);
            }else{
                ch.writeAndFlush("自己发送数据为:" + msg);
            }
        });
    }




    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("客户端已经下线 " + channel.remoteAddress() + sdf.format(new Date()));
        System.out.println("客户端已经下线 " + channel.remoteAddress() + sdf.format(new Date()));
        System.out.println("channelGroup size: " + channelGroup.size());
    }
}
