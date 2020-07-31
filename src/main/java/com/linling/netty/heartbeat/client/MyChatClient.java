package com.linling.netty.heartbeat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyChatClient {

    private ExecutorService executor;
//    private Future result;
    private static BufferedReader bufferedReader;
    private PrintTask printTask;

    public static void main(String[] args) throws Exception {
        MyChatClient chatClient = new MyChatClient();
        EventLoopGroup group = new NioEventLoopGroup();
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try {
            chatClient.connect(group);
        } finally {
//            group.shutdownGracefully();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> group.shutdownGracefully()));
    }

    public void connect(EventLoopGroup group) {
        try{
            if (null == executor) {
                executor = Executors.newSingleThreadExecutor();
                
            }

            Bootstrap client = new Bootstrap();
            client.group(group).channel(NioSocketChannel.class).handler(new MyChatClientInitializer(this));

            // ======= 说明 ========
            /**
             * 这种写法在重连接的时候回抛 "io.netty.util.concurrent.BlockingOperationException: DefaultChannelPromise@d21e95e(incomplete)"异常
             * 解决方法：不能在ChannelHandler中调用 ChannelFuture.sync() 。通过注册Listener来实现功能
             */
//            ChannelFuture future = client.connect(new InetSocketAddress("127.0.0.1", 5566)).sync();
            // ======= 说明 ========

            //192.168.1.102
            client.remoteAddress(new InetSocketAddress("127.0.0.1", 5566));
            client.connect().addListener((ChannelFuture future) -> {
                if(future.isSuccess()) {

                    // ======= 说明 ========
                    // 这个 死循环 导致了走到了channelRegistered， 后面的channelActive流程就被它堵塞了，以至于没往下走。。。
                    /*while (!readerExit) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                        future.channel().writeAndFlush(bufferedReader.readLine());
                    }*/
                    // ======= 说明 ========

                    if(printTask == null) {
                        printTask = new PrintTask(future.channel(), bufferedReader);
                        executor.submit(printTask);
                    } else {
                        printTask.setFuture(future.channel());
                    }



                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("==============");
    }

    class PrintTask implements Runnable {

//        private volatile ChannelFuture future;
        private volatile Channel channel;
        private final BufferedReader bufferedReader;

        public PrintTask(Channel channel, BufferedReader bufferedReader) {
            this.bufferedReader = bufferedReader;
            this.channel = channel;
        }

        @Override
        public void run() {
            while (true) {
                try{
                    String line = bufferedReader.readLine();
                    channel.writeAndFlush(line);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void setFuture(Channel channel) {
            this.channel = channel;
        }
    }
}
