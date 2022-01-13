package com.nx.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author mkp
 * @Description:
 */
public class HeartbeatServerHandler extends SimpleChannelInboundHandler<String> {

    int readIdleTimes =  0;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("服务端收到消息：" + msg);
     /*   // 收到任何消息就将空闲次数改为0
        readIdleTimes = 0;*/
        if("Heartbeat Package".equals(msg)){
            ctx.channel().writeAndFlush("ok");
        }else{
            System.out.println("其他信息处理......");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.err.println("=== " + ctx.channel().remoteAddress() + " is active ===");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("=== " + ctx.channel().remoteAddress() + " is registered ===");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        IdleStateEvent event = (IdleStateEvent) evt;

        String eventType = null;
        switch (event.state()){
            case READER_IDLE:
                eventType = "读空闲";
                readIdleTimes ++;
                break;
            case WRITER_IDLE:
                eventType ="写空闲";
                break;
            case ALL_IDLE:
                eventType = "读写空闲";
                break;
        }

        System.out.println(ctx.channel().remoteAddress() + "超时事件：" + eventType);

        if(readIdleTimes > 3){
            System.out.println("[服务端] 读空闲超过3次，关闭连接，进行资源的释放");
            ctx.channel().writeAndFlush("idle close");
            ctx.channel().close();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("有异常信息" + cause.getMessage());
    }
}











