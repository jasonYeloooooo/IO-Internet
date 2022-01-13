package com.nx.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NioServer {

    public static void main(String[] args) throws IOException {
        // 创建一个监听本地端口的服务socket通道，并设置为非阻塞
        ServerSocketChannel ssc = ServerSocketChannel.open();
        // 必须设置为非阻塞才能往selector上注册，因为seletor模式本身就是非阻塞的
        ssc.configureBlocking(false);
        ssc.socket().bind(new InetSocketAddress("127.0.0.1",9000));
        //创建一个选择器
        Selector selecor = Selector.open();
        // 将通道注册到选择器上，并设置感兴趣的操作，在注册的同时会在selector中形成一个selectionKey
        //这个selectionKey是讲channel和感兴趣事件绑定在一起
        SelectionKey register = ssc.register(selecor, SelectionKey.OP_ACCEPT);
        while (true){
            System.out.println("等待事件的发生......");
            // 轮训查看channel中的key,select是阻塞的，acceptor也是阻塞的
            selecor.select();
            System.out.println("有事件发生了");
            Iterator<SelectionKey> it = selecor.selectedKeys().iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                // 删除本次已经处理的key，防止select重复处理
                it.remove();
                handler(key);
            }
        }
    }

    private static void handler(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            System.out.println("有客户端连接事件发生");
            ServerSocketChannel scc = (ServerSocketChannel) key.channel();
            // nio非阻塞的体现： 此处的accept是阻塞的，但是这里是在发生连接事件的前提下，才执行到这里的，所以不会阻塞
            // 直接执行
            SocketChannel socketChannel = scc.accept();
            socketChannel.configureBlocking(false);
            // 通过selector监听channel时对读事件感兴趣
            socketChannel.register(key.selector(),SelectionKey.OP_READ);
        }else if(key.isReadable()){
            System.out.println("有客户端数据可读事件发生......");
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // nio非阻塞体现：首先read方法不会阻塞，因为这种响应事件模型，当调用到read方法的时候,客户端发送数据的事件
            int len = sc.read(buffer);
            if(len != -1){
                System.out.println("读取客户端的发送数据：" + new String(buffer.array(),0,len));
            }
            ByteBuffer bufferWriter = ByteBuffer.wrap("Hello client".getBytes());
            sc.write(bufferWriter);
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }else if(key.isWritable()){
            SocketChannel sc = (SocketChannel) key.channel();
            System.out.println("发生了write事件......");

            // NIO事件
            //使用java的NIO编程的时候，在没有数据可以往外写的时候要取消写的事件
            // 在有数据往外写的时候，再注册写的事件
            key.interestOps(SelectionKey.OP_READ);
        }


    }
}


































