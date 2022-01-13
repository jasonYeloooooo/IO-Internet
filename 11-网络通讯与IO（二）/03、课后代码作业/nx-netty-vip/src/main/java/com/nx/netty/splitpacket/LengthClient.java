package com.nx.netty.splitpacket;

import com.nx.netty.heartbeat.HeartbeatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Random;

/**
 * @author mkp
 * @Description:
 */
public class LengthClient{
    public static void main(String[] args) {
        {
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MessageDecoder())
                                .addLast(new MessageEncoder())
                                .addLast(new ClientHandler());
                    }
                });
                System.out.println("netty client start");

                ChannelFuture cf = bootstrap.connect("127.0.0.1", 9000).sync();

                //对通道关闭进行监听
                cf.channel().closeFuture().sync();


            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
        }
    }
}
