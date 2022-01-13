package com.nx.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Author:   mkp
 * Description:
 */
public class SocketClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 9000);
        System.out.println("已经和服务端建立连接.....");
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("Hello server".getBytes());
        outputStream.flush();
        System.out.println("向服务端发送数据完毕.....");
        byte[] bytes = new byte[1024];
        System.out.println("接收服务端返回的数据.....阻塞");
        InputStream inputStream = socket.getInputStream();
        int read = inputStream.read(bytes);
        if(read != -1){
            System.out.println("从服务端获取数据：" + new String(bytes,0,read));
            socket.close();
        }

    }
}
