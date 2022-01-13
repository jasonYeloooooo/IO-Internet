package com.nx.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author:   mkp
 * Description:
 */
public class SocketServer {

    public static void main(String[] args) throws IOException {

//      ExecutorService executorService = Executors.newFixedThreadPool(10);
        // 启动服务端，绑定9000端口
        ServerSocket serverSocket = new ServerSocket(9000);
        while(true){
            System.out.println("等待客户端连接....阻塞....");
            // 开始接受客户端连接 阻塞
            Socket socket = serverSocket.accept();
            System.out.println("获取客户端连接.......");
            handler(socket);
            // 启动线程处理连接数据
//           new Thread(new Runnable() {
////                 @Override
////                 public void run() {
////                     try {
////                         handler(socket);
////                     } catch (IOException e) {
////                         e.printStackTrace();
////                     }
////                 }
////             }).start();
//            executorService.submit(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        handler(socket);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
           //


        }

    }

    /**
     * 获取读取的数据
     * @param socket
     * @throws IOException
     */
    private static void handler(Socket socket) throws IOException {
        byte[] bytes = new byte[1024];
        System.out.println("等待读取数据....");
        int read = socket.getInputStream().read(bytes);
        System.out.println("读取数据完毕.....");
        if(read != -1){
            System.out.println("接收到客户端的数据：" + new String(bytes,0,read));
        }
        System.out.println("向客户端写数据.......");
        socket.getOutputStream().write("Hello client".getBytes());
        socket.getOutputStream().flush();
    }
}
