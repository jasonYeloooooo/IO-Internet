package com.nx.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Author:   mkp
 * Description:
 */
public class AioServer {
    public static void main(String[] args) throws Exception {
        final AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(9000));
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

            @Override
            public void completed(final AsynchronousSocketChannel socketChannel, Object attachment) {
                try {
                    // 接受客户端端的连接，如果不写这代码后面的客户端链接不上客户端服务器
                    serverChannel.accept(attachment,this);
                    System.out.println(socketChannel.getRemoteAddress());
                    final ByteBuffer buffer = ByteBuffer.allocate(1024);
                    socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            buffer.flip();
                            System.out.println("接收到客户端信息：" +new String(buffer.array(),0,result));
                            socketChannel.write(ByteBuffer.wrap("hello client".getBytes()));
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            exc.printStackTrace();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();

            }
        });
        Thread.sleep(Integer.MAX_VALUE);
    }
}
