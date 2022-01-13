package com.nx.netty.reactor.multithreading;

import com.nx.netty.reactor.multithreading.WorkHandler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable {
    ServerSocketChannel serverSocketChannel;

    Selector selector;

    public Acceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }


    @Override
    public void run() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            System.out.println("有客户端连接上来了," + socketChannel.getRemoteAddress());
            socketChannel.configureBlocking(false);
            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            System.out.println("acceptor thread:" + Thread.currentThread().getName());
            selectionKey.attach(new WorkHandler(socketChannel));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
