package com.nx.netty.splitpacket;

import com.nx.netty.heartbeat.HeartbeatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author mkp
 * @Description:
 */
public class LengthServer {
    public static void main(String[] args) {
        {
            NioEventLoopGroup bossgroup = new NioEventLoopGroup();
            NioEventLoopGroup workergroup = new NioEventLoopGroup();
            try{
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossgroup,workergroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline
                                        .addLast(new MessageDecoder())
                                        .addLast(new MessageEncoder())
                                        .addLast(new ServerHandler());
                            }
                        });
                System.out.println("netty server start....");
                ChannelFuture future = bootstrap.bind(9000).sync();
                future.channel().closeFuture().sync();
            }catch (Exception e){
                bossgroup.shutdownGracefully();
                workergroup.shutdownGracefully();
            }
        }
    }
}
