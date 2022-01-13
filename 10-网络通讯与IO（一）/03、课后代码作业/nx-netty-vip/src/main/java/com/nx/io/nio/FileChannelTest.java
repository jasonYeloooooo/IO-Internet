package com.nx.io.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileChannelTest {
    public static void main(String[] args) throws IOException {
        // 从文件获取一个FileChannel
        FileChannel fileChannel = new RandomAccessFile("G:\\tools\\nx.txt", "rw").getChannel();
        //        // 分配一个Byte类型的Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 将FileChannel中的数据读出到buffer中，-1表示读取完毕
        // buffer默认为写模式
        // read()方法是相对channel而言的，相对buffer就是写
        while ((fileChannel.read(buffer)) != -1) {
            // buffer切换为读模式
            buffer.flip();
            // buffer中是否有未读数据
            while (buffer.hasRemaining()) {
                // 读取数据
                System.out.print((char)buffer.get());
            }
            // 清空buffer，为下一次写入数据做准备
            // clear()会将buffer再次切换为写模式
            buffer.clear();
        }
    }
}