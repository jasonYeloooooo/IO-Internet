package com.nx.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

/**
 * Author:   mkp
 * Description:
 */
public class AioClient {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1",9000));
        socketChannel.write(ByteBuffer.wrap("Hello Server".getBytes()));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Integer len = socketChannel.read(buffer).get();
        if(len != -1){
            System.out.println("客户端收到信息：" + new String(buffer.array(),0,len));
        }
    }
}
