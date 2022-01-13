package com.nx.netty.heartbeat;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.commons.lang3.StringUtils;

/**
 * @author mkp
 * @Description:
 */
public class HeartbeatClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("客户端收到消息：" + msg);
        if(StringUtils.isNotBlank(msg) && msg.equals("idle close")){
            System.out.println("服务端已经关闭连接，客户端也需要关闭连接");
            ctx.channel().closeFuture();
        }
    }
}
