package com.nx.netty.reactor.masterSlave;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable {
    private ServerSocketChannel serverSocketChannel;

    private SubReactor subReactor ;
    private  Selector selector;

    public Acceptor(ServerSocketChannel serverSocketChannel) {
        this.serverSocketChannel = serverSocketChannel;
            try {
                selector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
            }
            subReactor = new SubReactor(selector);
            new Thread(subReactor).start();

    }

    @Override
    public void run() {
        try {
            System.out.println("acceptor thread:" + Thread.currentThread().getName());
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("有客户端连接上来了," + socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);
            selector.wakeup();
            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            selectionKey.attach(new WorkHandler(socketChannel));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
