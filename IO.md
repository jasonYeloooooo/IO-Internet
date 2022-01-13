# 第一章 BIO,NIO,AIO课程介绍

### 1.1 课程说明

早期java的网络信息架构存在一些缺陷，***其中最令人烦恼的式基于性能低下的同步阻塞的I/O通信***。随着互联网开发下通信性能的搞要求，java在2002年开始支持了非阻塞的I/O通信技术（NIO）。本次课程将通过大量的清晰直接的案例从最基础的bio同行开始介绍到nio，aio。

### 1.3 通信技术整体解决的问题

* 局域网内的通信要求
* 多系统见的底层消息传递机制
* 高并发下，大数据量的通信场景需要
* 游戏行业。无论是手游服务端，还是大型的网络游戏。java语言都唔那个得到广泛的应用



# 第二章 Java的I/O演进之路

###  2.1 I/O模型基本说明

I/O模型：就是用什么的通道或者说式通信模式和架构进行数据的传输和接受。很大程度上决定了程序通信的性能，java共支持3中网络编程的I/O模型：**BIO,NIO,AIO**

实际通信需求下，要根据不同的业务场景和性能需求决定选择不同的I/O米星



### 2.2 I/O模型

#### **Java BIO**

同步并阻塞（传统阻塞型），服务器实现模式为一个连接一个线程，即客户端有链接请求时服务器段就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销

![image-20211231103656230](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20211231103656230.png)

#### Java NIO

Java Nio: 同步非阻塞，服务器实现模式为一个线程处理多个请求（连接），即客户端发送的连接请求都会注册到多复用器上，多复用器轮询到连接又I/O请求就进行处理

![image-20211231104143491](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20211231104143491.png)

#### Java AIO

Java AIO 异步异步非阻塞，服务器实现模式为一个有效请求一个线程，客户端的I/O请求都是由OS先完成了再通知服务器应用去启动线程进行处理，一般适用于连接数较多且连接时间较长的应用



### 2.3 BIO,NIO,AIO 使用场景分析

1. **BIO**方式适用于连接数目比较小且固定的架构，这种方式对服务器资源要求比较高，并发局限于应用中，JDK1.4以前唯一选择，但程序简单易理解
2. **NIO**方式适用于连接数目多且连接比较短（轻操作）的架构，比如聊天服务器，弹幕服务器，服务器之间通讯等。编程比较复杂，JDK1.4开始支持。
3. AIO方式适用于连接数目多且连接比较长（重操作）的架构，比如相册服务器，充分调用OS参与并发操作，编程比较复杂，JDK7开始支持



#  第三章 JAVA BIO 深入剖析

### 3.1 Java BIO 基本介绍

* java BIO 就是传统的java io编程，其相关的类和接口在java.io
* BIO(blocking I/O):同步阻塞，服务器 实现模式位一个连接一个线程，即客户端有连接请求时服务器端就需要启动一个线程进行处理，如果这个连接不做任何事情会造成不必要的线程开销，可以通过线程池机制改善（实现多个用户连接服务器）



### 3.2 Java BIO 工作机制



![image-20220110151055973](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20220110151055973.png)



### 3.3 传统的BIO编程实例回顾

​	网络编程的基本模型是Client/server模型，也就是 两个进程之间进行相互通信，其中服务端提供位置信（绑定IP地址和端口），客户端通过连接操作向服务端监听的端口地址发起连接请求，基于TCP协议下进行三次握手连接，连接成功后，双方通过网络套接字（socket）进行通信

​	传统的同步阻塞模型开发中，服务端ServerSocket负责绑定IP地址，启动监听端口；客户端Socket负责发起连接操作。连接成功后，双方通过输入和输出流进行同步阻塞式 通信

​	基于BIO模式下的通信，客户端 - 服务端是完全同步，完全耦合的

##### client

```java
package com.jason.io.bio.one;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
/*
客户端
 */
public class Client {

    public static void main(String[] args) {
        //创建socket对象 请求服务端的连接

        try {
            Socket socket = new Socket("127.0.0.1",9999);
        //从socket对象中获取一个字节输出流
            OutputStream os = socket.getOutputStream();
        //3. 八字节输出流包装成一个打印流
            PrintStream ps = new PrintStream(os);
            ps.println("hello world! 你好");
            ps.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

##### server

```java
package com.jason.io.bio.one;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
    目标：客户端发送消息，服务端能接受消息

 */
public class Server {

    public static void main(String[] args) {
        //定义serverSocket对象进行服务端的接口注册
        try {
            ServerSocket ss = new ServerSocket(9999);
        //2.监听客户端的socket连接请求
            Socket socket = ss.accept();
        //3. 从socket管道中得到一个字节输入对象
            InputStream is = socket.getInputStream();
        //4. 八字节输入流包装成一个缓冲字符输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String msg;
            if((msg =br.readLine())!= null){
                System.out.println("服务端接收到了："+msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```



##### 小结

* 在以上通信中，服务端会一致等待客户端的消息，如果客户端没有进行消息的发送，服务端将一致进入阻塞状态
* 同时服务端是按照**行**获取消息的，这意味着客户端也必须按照行消息的发送，否则服务端将进入等待消息的阻塞状态！
  

### 3.4 BIO模式下多发和多收消息

​	在上一个案例中，**只能实现客户端发送消息，服务端接收消息**，并不能实现反复的收消息和反复的发消息，我们只需要在客户端案例中，加上反复按照行发送消息的逻辑即可！

code：

```java
public class Client {

    public static void main(String[] args) {
        //创建socket对象 请求服务端的连接

        try {
            Socket socket = new Socket("127.0.0.1",9999);
        //从socket对象中获取一个字节输出流
            OutputStream os = socket.getOutputStream();
        //3. 八字节输出流包装成一个打印流
            PrintStream ps = new PrintStream(os);
            Scanner sc = new Scanner(System.in);
            while(true){
                System.out.println("请说： ");
                String msg = sc.nextLine();
                ps.println(msg);
                ps.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Server {

    public static void main(String[] args) {
        //定义serverSocket对象进行服务端的接口注册
        try {
            ServerSocket ss = new ServerSocket(9999);
        //2.监听客户端的socket连接请求
            Socket socket = ss.accept();
        //3. 从socket管道中得到一个字节输入对象
            InputStream is = socket.getInputStream();
        //4. 八字节输入流包装成一个缓冲字符输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String msg;
            while((msg =br.readLine())!= null){
                System.out.println("服务端接收到了："+msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

```



### 3.5 BIO模式下接收多个客户端

##### 概述

​	在上述的案例中，一个服务端只能接受一个客户端的通信请求，那么如果**服务端需要处理很多个客户端的消息通信请求应该如何处理**，此时我们就需要在服务端引入线程了，也就是说客户端没法其一个请求，服务端就创建一个新的线程处理这个客户端的请求，这样就实现了一个客户端一个线程的模型。

#### 小结

1. 每个Socket接收到，都会创建一个线程，西安测绘给你的竞争，切换上下文影响性能
2. 每个线程都会占用栈空间喝CPU资源
3. 并不是每个socket都进行io操作，无意义的线程处理
4. 客户端的并发访问增加时，服务端将呈现1：1的线程开销，访问量越大，系统将发生线程栈溢出，线程创建失败，最终导致进程死机或者僵死，从而不能对哇提供服务



### 3.6 伪异步I/O编程

#### 概述

​	在上述案例中：客户端的并发访问增加时。服务端将呈现1：1的线程开销，访问量越大，系统将发生线程栈溢出，线程创建失败，最终导致进程死机或则僵死，从而不能对外提供服务。

​	接下来我们采用一个伪异步I/O的通信框架，采用线程池喝任务队列实现，当客户端接入时，将客户端的Socket封装成一个TASK，交给后端的线程池中进行处理。JDK的线程池维护一个消息队列和N 个活跃的线程，对消息队列中Socket任务进行处理，由于线程池可以设置消息队列的大小和最大线程数，因此，他的资源占用是可控的，无论多少个客户端并发访问，都不会导致资源的耗尽和死机

![image-20220110185819085](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20220110185819085.png)





#### 客户端

```java
public class Client {

    public static void main(String[] args) {
      //1.请求与服务端的Socket对象连接
        try {
            Socket socket = new Socket("127.0.0.1",9999);
            //2.得到一个打印流
            PrintStream ps = new PrintStream(socket.getOutputStream());
            //3.使用循环不断的发送消息给服务端接受
            Scanner sc = new Scanner(System.in);
            while(true){
                System.out.print("请说：");
                String msg = sc.nextLine();
                ps.println(msg);
                ps.flush();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
```

#### 线程池处理类

```java
public class HandlerSocketServerPool {
    //1.创建一个线程池的成员变量 用于存储一个线程池对象

    private ExecutorService executorService;

    //2.在创建这个类的定对象的时候就需要初始化线程池对象

    public HandlerSocketServerPool(int maxThread,int queueSize){
        executorService = new ThreadPoolExecutor(3,maxThread,120,
                TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(queueSize));

    }
    //3, 提供一个方法来提交任务给线程池的任务队列来暂存，等真线程池来处理

    public void execute(Runnable target){
        executorService.execute(target);
    }
}

```

server

```java
try {
            //1.注册端口
            ServerSocket ss = new ServerSocket(9999);
            //2.定义一个循环接收客户端的Socket连接请求

            //初始化线池对象
            HandlerSocketServerPool pool = new HandlerSocketServerPool( 6,10);

            while(true){
                Socket socket = ss.accept();
                //3.把socket对象交给一个线程池进行处理
                //把socket封装成任务对象交给线程池处理
                Runnable target = new ServerRunnableTarget(socket);
                pool.execute(target);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
```

#### 小结

* 伪异步io采用了线程池实现，因此避免了为每个请求创建一个独立线程造成线程资源耗尽的问题，但由于底层依然是采用的同步阻塞模型，因此无法从根本上解决问题
* 如果单个消息处理的缓慢，或者服务器线程池中的全部线程都被阻塞，那么后续socket的i/o消息将在队列中排队，新的socket请求将被拒绝，客户端会发生大量连接超时的武器·



### 3.7 基于BIO形式下的文件上传

#### 目标

支持任意类型文件形式的上传

#### 客户端开发

```java
public static void main(String[] args) {
        try{
            //1.请求与服务端的socket连接
            Socket socket = new Socket("127.0.0.1",8888);
            //2. 把字节输出流包装成一个数据输出流
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            //3. 先发送上传文件的后缀给服务端
            dos.writeUTF(".jpg");
            //4. 把文件数据发送给服务端进行接受
            InputStream is = new FileInputStream("C:\\Users\\ROG\\Desktop\\image\\Camera\\IMG_20190203_171441.jpg");
            byte[] buffer = new byte[1024];
            int len;
            while((len = is.read(buffer))>0){
                dos.write(buffer,0,len);
            }
            dos.flush();
            socket.shutdownOutput();//通知服务端这边的数据发送完毕了


        }catch (Exception e){e.printStackTrace();}
    }
```

服务端

```java
 try {
            ServerSocket ss = new ServerSocket(8888);
            while(true){
                Socket socket = ss.accept();
                //交给一个独立的线程来处理雨这个客户端的文件通信需求
                new ServerReaderThread(socket).start();


            }
```



### 3.9 Java BIO模式下的端口转发思想

需求：需求实现一个客户端的消息可以发送给所有的客户端去接受（群聊实现）

![image-20220111123631132](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20220111123631132.png)



 ### 3.10 基于BIO模式下即时通信

1. **客户端登陆功能**

* 可以启动客户端进行登录，客户端登录只需要输入用户名和服务端ip地址即可

2. **在线人数实时更新**

* 客户端用户登录以后，需要同步更新所有客户端的联系人信息栏

3. **离线人数更新**

* 检测到又客户端下线后，需要同步更新所有客户端的联系人信息栏

4. **群聊**

* 任意一个客户端的消息，可以推送给当前所有客户端接受

5. **私聊**

* 可以选择某个员工，点击私聊按钮，然后发出的消息可以被该客户端单独接受

6. **@消息**

* 可以选择某个员工，然后发出的消息可以@该用户，但是其他所有人都能

7. 消息用户和消息时间点

* 服务端可以实时记录



# 第四章 JAVA NIO深入刨析

在讲解利用Nio实现通信架构之前，我们需要先来了解一下NIO的基本特点和使用

### 4.1 Java NIO  基本使用

* java NIO（new IO）也有人称之为java non-blocking IO 是从java1.4版本开始引入的一个全新的IO API，可以替代标准的java IO API。NIO与原来的IO有同样的作用和目的，但是使用的方式完全不同，NIO支持**面向缓冲区的，基于通道的io操作**，NIO将以更加高效的方式进行文件的读写操作。NIO 可以理解为非阻塞IO，传统的IO的read和write只能阻塞执行，线程在读写IO期间不能干其他事情，比如调用socket.read方法时，如果服务器一直没有数据传输过来，线程就一直阻塞，而NIO中可以配置socket为非阻塞模式
* NIO相关类都放在java.nio包及子包下，并且对原java.io包中的很多类进行改写
* NIO有三大核心部分：**Channel（通道），buffer(缓冲区)，Selector（选择器）**
* java NIO 的非阻塞模式，是一个线程从某通道发送请求或者读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取，而不是保持线程阻塞，所以直至数据边的可以读取之前，该线程可以继续做其他的事情。非阻塞写也是如此，一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情
* 同属理解：NIO是可以做到用一个线程来处理多个操作的

### 4.2 NIO 和 BIO 比较

* BIO 以流的方式处理数据，而NIO以块的方式处理数据，块IO的效率比流IO高很多
* BIO时阻塞的，NIO则时非阻塞的
* BIO基于字节流和字符流进行操作，而NIO基于Channel（通道）和buffer（缓冲区）进行操作，数据总事从通道读取到缓冲区中，或者从缓冲区写入到通道中。Selector（选择器）用于监听多个通道的事件，因此使用单个线程就可以监听多个客户端通道



### 4.3 NIO 三大核心原理示意图

NIO有三大核心部分：**Channel（通道）Buffer（缓冲区）Selector（选择器）**

#### Buffer 缓冲区

缓冲区本质上就是一块可以写入数据，然后可以从中读取数据的内存。这块内存被包装成NIO Buffer对象，并提供了一组方法，用来方便的访问该块内存。相比叫直接对数组的操作，Buffer API更加容易操作和管理

#### Channel 通道

Java NIO的通道类似流，但又有些不同：既可以从通道中读取数据，又可以写数据到通道。但流的（input或output）读写通常时单向的。通道可以非阻塞读取和写入通道，通道可以支持读取或写入缓冲区，也可以支持异步地读写

#### Selector选择器

Selector是一个Java Nio组件，可以能够检查一个或多个NIO通道，并确定那些通道已经准备好进行读取或写入。这样，一个单独的线程可以管理多个channel，从而管理多个网络连接，提高效率

![image-20220112101419125](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20220112101419125.png)



* 每个channel都会对一个buffer
* 一个线程对应一个选择器，一个选择器对应多个channel
* 程序切换到那个channel是由时间决定的
* Selector会根据不同的时间，在各个通道上切换
* buffer就是一个内存块，底层是一个数组
* 数据的读取写入是通过Buffer完成的，BIO中要么是输入流，或者是输出流，不能双向，但是NIO的buffer是可以读也可以写的
* Java NIO 系统的核心在于：channel和buffer。通道表示打开到IO设备的连接。若需要使用NIO系统，需要获取用于连接IO设备的通道以及用于容纳数据的缓冲区。然后操作缓冲区，对数据进行处理。简而言之，channel负责传输，buffer负责存储数据

### 4.4 NIO 核心一：缓冲区（Buffer）

#### **缓冲区（Buffer）**

一个用于特定基本数据类型的容器。由java.nio包定义，所有缓冲区都是Buffer抽象类的自类。java NIO 中的Buffer主要用于与NIO 通道进行交互，数据是从通道读取缓冲区，从缓冲区写入通道中的



#### Buffer 类及其子类

**Buffer**就像一个数组，可以保存多个相同类型的数据。根据数据类型不同，有一下Buffer常用自类：

* ByteBuffer
* CharBuffer
* ShortBuffer
* IntBuffer
* LongBuffer
* FloatBuffer
* DoubleBuffer

上述Buffer类他们都采用相似的方法进行管理数据，只是各自管理的数据类型不同而已。都是通过如下方法获取一个buffer对象：

```java
static XxxBuffer allocate(int capacity): 创建一个容量为capacity的XxxBuffer对象0
```



#### 缓冲区的基本属性

Buffer中的重要概念：

* **容量**，作为一个内存块，Buffer具有一定的固定大小，也称为“容量”，缓冲区容量不能为负，并且创建后不能更改。

* **限制**，表示缓冲区中可以操作数据的大小（limit后数据不能进行读写）缓冲区的限制不能为负，并且不能大于其容量。**写入模式，限制等于buffer的容量。读取模式下，limit等于写入的数据量**

* **位置（position**：下一个读取或者写入的数据的索引。缓冲区的位置不能为负，并且不饿能大于其限制

* **标记（mark）与重置（reset）**：标记是一个索引，通过Buffer中的mark（）方法指定Buffer一个特定的position，之后可以通过调用reset（）方法恢复到这个position

  **标记，位置，限制，容量遵守一下不变式：0《=mark<=position<=limit<=capacity**

* 

![image-20220112151726008](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20220112151726008.png)

![image-20220112151750998](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20220112151750998.png)

![image-20220112151854680](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20220112151854680.png)

#### buffer常见方法

```java
Buffer clear() //清空缓冲区并返回对缓冲区的引用
Buffer flip() //为将缓冲区的界限设置为当前位置，并将当前位置设置为 0 0
int capacity() //返沪buffer 的容量大小
boolean hasRemaining() //判断缓冲区中是否还有元素
int limit() //返回buffer的界限 limit的位置
Buffer limit(int n) //将设置缓冲区界限为n，并返回一个具有新limit的缓冲区对象
Buffer mark() //对缓冲区设置标记
int position() //返回缓冲区的当前位置position
Buffer position(int n) //将设置缓冲区的当前位置为n，并返回修改后的Buffer对象
int remaining() //返回position和limit之间的元素个数
Buffer reset()  //将位置position转到以前设置的mark所在的位置
Buffer rewind() //将位置设置为0，取消设置的mark
```

#### 缓冲区的数据操作

```java
buffer 所有子类提供了两个用于数据操作的方法：get() put()
获取buffer中的数据
get(): 读取单个字节
get(byte[] dst) 批量读取多个字节倒dst中
get(int index)   读取指定索引位置的字节（不会移动position）
 
放到 如数据倒Buffer中
put(byte b); 将给定单个字节写入缓冲区的当前位置
put(byte[] src): 将src中的字节写入缓冲区的当前位置
put(int index,byte b):将指定字节写入缓冲区的索引位置
    
```



####  使用Buffer读写数据一般遵循一下四个步骤：

1. 写入数据倒buffer
2. 调用flip()方法，转换为读取模式
3. 从buffer中读取数据
4. 调用buffer.clear()放发或者buffer.compact()方法清除缓冲区



 #### 案例

```java
package com.jason.io.nio.itHeiMa;

import org.junit.Test;

import java.nio.ByteBuffer;

/*

目标：对缓冲区buffer的常用API进行案例实现

    Buffer clear() //清空缓冲区并返回对缓冲区的引用
    Buffer flip() //为将缓冲区的界限设置为当前位置，并将当前位置设置为 0 0
    int capacity() //返沪buffer 的容量大小
    boolean hasRemaining() //判断缓冲区中是否还有元素
    int limit() //返回buffer的界限 limit的位置
    Buffer limit(int n) //将设置缓冲区界限为n，并返回一个具有新limit的缓冲区对象
    Buffer mark() //对缓冲区设置标记
    int position() //返回缓冲区的当前位置position
    Buffer position(int n) //将设置缓冲区的当前位置为n，并返回修改后的Buffer对象
    int remaining() //返回position和limit之间的元素个数
    Buffer reset()  //将位置position转到以前设置的mark所在的位置
    Buffer rewind() //将位置设置为0，取消设置的mark
 */
public class BufferTest {

    @Test
    public void test01(){
        //1.分配一个缓冲区，容量设置为10
        ByteBuffer buffer = ByteBuffer.allocate(10);
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());
        System.out.println("===========");
        //2.put缓冲区中添加数据
        String name = "jasonyel";
        buffer.put(name.getBytes());
        System.out.println(buffer.position());//8
        System.out.println(buffer.limit());//10
        System.out.println(buffer.capacity());//10
        System.out.println("===========");
        //3. buffer flip() 将缓冲区的界限设置为担负起按位置，并设置当前位置为0: 可读模式
        buffer.flip();
        System.out.println(buffer.position());//0
        System.out.println(buffer.limit());//8
        System.out.println(buffer.capacity());//10
        System.out.println("===========");
        //4，get 数据的读取
        char ch = (char) buffer.get();
        System.out.println(ch);
        System.out.println(buffer.position());//1
        System.out.println(buffer.limit());//8
        System.out.println(buffer.capacity());//10
        System.out.println("===========");
    }

    @Test
    public void test02(){
        //1.分配一个缓冲区，容量设置为10
        ByteBuffer buffer = ByteBuffer.allocate(10);
        System.out.println(buffer.position());
        System.out.println(buffer.limit());
        System.out.println(buffer.capacity());
        System.out.println("===========");
        String name = "jasonyel";
        buffer.put(name.getBytes());
        System.out.println(buffer.position());//8
        System.out.println(buffer.limit());//10
        System.out.println(buffer.capacity());//10
        System.out.println("===========");
        //2.clear清除缓冲区中的数据
        buffer.clear();
        System.out.println(buffer.position());//0
        System.out.println(buffer.limit());//10
        System.out.println(buffer.capacity());//10
        System.out.println((char) buffer.get());//j
        System.out.println("===========");

        //3. 定义一个新的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(10);
        String n = "yelojason";
        buf.put(n.getBytes());
        buf.flip();

        //读取数据
        byte[] b = new byte[2];
        buf.get(b);
        String rs = new String(b);
        System.out.println(rs);
        System.out.println(buf.position());//2
        System.out.println(buf.limit());//9
        System.out.println(buf.capacity());//10
        System.out.println("========");


        buf.mark();//标记此刻这个位置 2
        byte[] b2 = new byte[3];
        buf.get(b2);
        System.out.println(new String(b2));
        System.out.println(buf.position());//5
        System.out.println(buf.limit());//9
        System.out.println(buf.capacity());//10
        System.out.println("-==========");

        buf.reset();//回到标记位置
        if(buf.hasRemaining()){
            System.out.println(buf.remaining());
        }
    }
}

```

#### 直接与非直接缓冲区

什么是直接内存与非直接内存

根据官方文档的描述：

byte buffer可以是两种类型，一种是基于直接内存（也就是非堆内存）；另一种是非直接内存（也就是堆内存）。对于直接内存来说，JVM将会在IO操作上具有更高的性能，因为他直接利用与本体系统的IO操作。而非直接内存，也就是堆内存中的数据，如果要做IO 操作，会先从本地进程内存赋值倒直接内存，再利用本地IO处理。

从数据流的角度，非直接内存是下面的作用链：

```ja
本地IO->直接内存-->非直接内存->直接内存->本地 Io
```

而直接内存是：

```java
本地IO-->直接内存-->本地IO
```

很明显，在做IO处理时，比如网络发送大量 数据的时候，直接内存会具有更高的效率。直接内存使用allocateDirect创建，但是他申请普通的堆内存需要耗费更高的性能。不过，这部分的数据是在JVM之外的，因此他不会占用应用的内存。所以，当你有很大的数据要缓存，并且他的生命周期又很场，那么就比较适合使用直接内存。只是一般来说，如果不是能带来很明显的性能提升，还是推介直接使用堆内存。字节缓冲区是直接缓冲区还是非直接缓冲区可以通过调用isDirect()方法来确定

```java
        //1.创建一个直接内存的缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        System.out.println(buffer.isDirect());//true
        //创建一个非直接缓冲区
        ByteBuffer buffer02 = ByteBuffer.allocateDirect(1024);
        System.out.println(buffer02.isDirect());//false
```

**使用场景**

* 1 有很大的数据需要存储，他的生命周期又很长
* 2 适合频繁的IO操作，比如网络并发场景



### 4.5 NIO核心二： 通道（channel）

#### 通道channel概述

通道channel，由java.nio.channels包定义的。channel表示IO原与目标打开的连接。Channel类似于传统的‘流’。只不过channel本身不能直接访问数据，channel只能与buffer进行交互。

1. Nio的通道类似于流，但有些区别如下：
   * 通道可以同时进行读写，而流只能读或者写
   * 通道可以实现异步读写数据
   * 通道可以从缓冲读数据，也可以写数据倒缓冲
2. BIO中的Stream是单项的，例如FileInputStream对象只能进行读取数据的操作。而NIO中的通道是双向的，可以读操作，也可以写操作
3. channel在NIO中是一个接口

```java
public interface Channel extends Closedable{}
```

#### 常用的Channel实现类

* FileChannel: 用于读取，写入，映射和操作文件的通道。
* DatagramChannel:通过udp读写网络中的数据通道
* SocketChannel：通过TCP读写网络中的数据
* ServerSocketChannel：可以监听新进来的TCP连接，对每一个新进来的连接都会创建一个SocketChannel



#### FileChannel类

获取通道的一种方式是对支持通道的对象调用getChannel()方法。支持通道的类如下：

* FileInputStream
* FileOutputStream
* RandomAccessFile
* DatagramSocket
* Socket
* ServerSocket

获取通道的其他方法 时使用files类的静态方法newByteChannel()获取字节通道。或者通过通道的静态方法open()打开并返回指定通道



#### FileChannel的常用方法

```java
int read(ByteBuffer dst) 从Channel 到中读取数据ByteBuffer
long read(ByteBuffer[] dsts) 将channel到中的数据“分散”到 ByteBuffer[]
int write(ByteBuffer src) 将ByteBuffer中的数据写入到channel中
long write(ByteBuffer[] srcs)将bytebuffer[] 中的数据channel1
long position() 返回此通道的文件的当前大小
FileChannel truncate(long s) 将此通道的文件截取为给定大小
void force(boolean metaData) 强制将所有对此通道的文件更新写入到存储设备中
```

#### FileChannel写数据

```java
    @Test
    public void write(){
        //1. 自己输出流通向目标文件
        try {
            FileOutputStream fos = new FileOutputStream("data01.txt");
            //2.得到字节输出流对应的通道Channel
            FileChannel channel = fos.getChannel();
            //3.分配缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("hello, jason".getBytes());
            //4.把缓冲区切换成写出模式
            buffer.flip();
            channel.write(buffer);
            channel.close();
            System.out.println("写数据到文件中" );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```

#### FileChannel读数据

```java

    @Test
    public void read(){

        try {
            //1.定义一个文件字节输入流与源文件接通
            FileInputStream fis = new FileInputStream("data01.txt");
            //2.得到文件字节输入流的文件通道
            FileChannel channel = fis.getChannel();
            //3.定义一个缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //4.需要通过缓冲区 加载数据
            channel.read(buffer);
            //5.读取出缓冲区的数据并输出
            buffer.flip();//buffer 到开始位置
            String rs = new String(buffer.array(),0,buffer.remaining());//从0开始到剩下的元素
            System.out.println(rs);



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

#### 使用buffer完成文件的复制

```java
 @Test
    public void copy() throws Exception {
        //源文件
        File srcFile = new File("C:\\Users\\ROG\\Desktop\\java\\JAVAWEB\\users.xml");
        //得到一个字节输出流和输入流
        FileInputStream fis = new FileInputStream(srcFile);
        //得到字节输出流
        FileOutputStream fos = new FileOutputStream("user.xml");
        //得到文件通道
        FileChannel isChannel = fis.getChannel();
        FileChannel fosChannel = fos.getChannel();
        //分配缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while(true){
            //先清空缓冲区 然后再写入数据到缓冲区
            buffer.clear();
            //开始读去一次数据
            int flag = isChannel.read(buffer);
            if (flag==-1){
                break;
            }
            //已经读取数据,把缓冲区切换成可读模式
            buffer.flip();
            fosChannel.write(buffer);
        }
        isChannel.close();
        fosChannel.close();
        System.out.println("复制完成");
    }
```



#### 分散和聚集

分散读取scatter： 是指把Channel通道的数据读入到多个缓冲区中去

聚集写入Gathering：是指将多个Buffer中的数据聚集到channel

```java

    @Test
    public void test() throws Exception {
        //1 字节输入管道
        FileInputStream fis = new FileInputStream("data01.txt");
        FileChannel isChannel = fis.getChannel();
        //2 字节输出流管道
        FileOutputStream fos = new FileOutputStream("data02.txt");
        FileChannel osChannel = fos.getChannel();
        //3，定义多个缓冲区 做数据分散
        ByteBuffer buffer1 = ByteBuffer.allocate(4);
        ByteBuffer buffer2 = ByteBuffer.allocate(1024);
        ByteBuffer[] buffers = {buffer1,buffer2};
        //从通道中读取数据分散到各个缓冲区
        isChannel.read(buffers);
        //5 从每个缓冲区中查看是否有数据读取到的
        for (ByteBuffer buffer : buffers){
            buffer.flip();//切换到读模式
            System.out.println(new String(buffer.array(),0,buffer.remaining()));
        }
        //聚集写入到通道
        osChannel.write(buffers);

        isChannel.close();
        osChannel.close();
        System.out.println("文件复制");
    }
```

#### transferFrom() 和 transferTo

从目标通道中去复制原通道数据

```java

    @Test
    public  void test02() throws Exception {
        //1 字节输入管道
        FileInputStream fis = new FileInputStream("data01.txt");
        FileChannel isChannel = fis.getChannel();
        //2 字节输出流管道
        FileOutputStream fos = new FileOutputStream("data03.txt");
        FileChannel osChannel = fos.getChannel();
        
        //3.复制数据
        //transferFrom
        osChannel.transferFrom(isChannel, isChannel.position(), isChannel.size());
        //transferTo
        isChannel.transferTo(isChannel.position(),isChannel.size(),osChannel);
        isChannel.close();
        osChannel.close();
        System.out.println("finish");

    }
```



### 4.6 NIO 核心三： 选择器 Selector

#### 概述

选择器是SelectableChannel 对象的多路复用器，Selector可以同时监控多个SelectableChannel的IO状况，也就是说，利用Selector可是一个单独的线程管理多个Channel。Selector是非阻塞IO的核心

![image-20220113091548033](C:\Users\ROG\AppData\Roaming\Typora\typora-user-images\image-20220113091548033.png)

*  java的NIO，用非阻塞的IO方式。可以用一个线程，处理多个的客户端连接，就会使用到Selector选择器
* Selector能都检测多个组测的通道上是否有时间发生，如果有时间发生，便会获取事件然后针对每个事件进行相应的处理。这样就可以只用一份线程去过还礼多个通道，也就是管理多个连接和请求。







