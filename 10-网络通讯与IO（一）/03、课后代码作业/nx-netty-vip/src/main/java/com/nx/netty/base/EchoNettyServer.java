package com.nx.netty.base;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;

public final class EchoNettyServer {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8008"));

    /**
     * select 转化为epoll
     *  NioEventLoopGroup 替换成 EpollEventLoopGroup NioServerSocketChannel 替换成 EpollServerSocketChannel
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // 1. 声明线程池  select 用NioEventLoopGroup  epoll用EpollEventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        try {
            // 2. 服务端引导器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 3. 设置线程池
            serverBootstrap.group(bossGroup, workerGroup)
                    // 4. 设置ServerSocketChannel的类型
                    //EpollServerSocketChannel.class  epoll模型只有在linux kernel 2.6以上才能支持，在windows和mac都是不支持的
                    .channel(NioServerSocketChannel.class)
                    // 5. 设置参数
                    .option(ChannelOption.SO_BACKLOG, 100)
                    // 6. 设置ServerSocketChannel对应的Handler，只能设置一个
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 7. 设置SocketChannel对应的Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                          ChannelPipeline p = ch.pipeline();
                            // 可以添加多个子Handler
                      /*      p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new IdleStateHandler(3,0,0));*/
                            p.addLast();
                        }
                    });

            // 8. 绑定端口
            ChannelFuture f = serverBootstrap.bind(PORT).sync();
            // 9. 等待服务端监听端口关闭，这里会阻塞主线程
           f.channel().closeFuture().sync();
        } finally {
            // 10. 优雅地关闭两个线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}