package com.nx.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioClient {

    // 通道管理器
    private Selector selector;
    public static void main(String[] args) throws IOException {
        NioClient nioClient = new NioClient();
        nioClient.initClient("127.0.0.1",9000);
        nioClient.connect();
    }

    private void connect() throws IOException {
        // 死循环访问selector
        while (true){
            selector.select();
            //获取selector中的迭代器
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                //删除已选的key防止重复处理
                it.remove();
                if(key.isConnectable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    // 如果正在连接则完成连接
                    if(channel.isConnectionPending()){
                        channel.finishConnect();
                    }
                    //在这里可以给客户端发送消息
                    ByteBuffer buffer = ByteBuffer.wrap("hello server".getBytes());
                    channel.write(buffer);
                    // 在和服务连接成功之后，为了可以接收到服务端的信息，需要给通道设置读的权限
                    channel.register(this.selector,SelectionKey.OP_READ);
                }else if(key.isReadable()){
                    read(key);
                }
            }
        }
    }

    /**
     * 处理服务端发送的数据
     * @param key
     */
    private void read(SelectionKey key) throws IOException {
        // 和服务端read方法一样
        // 服务器可以读取消息：得到事件发生的soekcet通道
        SocketChannel channel = (SocketChannel) key.channel();
        // 创建读取的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int len = channel.read(buffer);
        if(len != -1){
            System.out.println("客户端接收到数据：" + new String(buffer.array(),0,len));
        }
    }

    /**
     * 获得一个socket通道，并对该通道做一些初始化的工作
     * @param ip
     * @param port
     * @throws IOException
     */
    private void initClient(String ip, int port) throws IOException {
        // 获取socket通道
        SocketChannel channel = SocketChannel.open();
        // 设置为非阻塞
        channel.configureBlocking(false);
        // 客户端连接服务器，其实方法执行并没有实现连接，需要调用
        // channl.finishConnect()才能完成连接
        channel.connect(new InetSocketAddress(ip,port));
        // 获得一个通道管理器
        this.selector = Selector.open();

        // 将通道管理器和该通道进行绑定，并为该通道注册OP_CONNECT事件
        channel.register(selector, SelectionKey.OP_CONNECT);

    }
}
