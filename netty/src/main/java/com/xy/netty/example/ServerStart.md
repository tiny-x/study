#### Netty服务端启动流程
```` java
public static void main(String[] args) throws Exception {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 设置TCP数据接收缓冲区大小 1K 验证tcp 拆包
        serverBootstrap.group(boss, work)
                .childOption(ChannelOption.SO_RCVBUF, 1)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("encoder", new Encoder());
                        ch.pipeline().addLast("decoder", new Decoder());
                        ch.pipeline().addLast("ServerHandler", new ServerHandler());
                    }
                });

        ChannelFuture channelFuture = serverBootstrap.bind(9000).sync();
    }
````

### EventLoop任务
- 轮询select
- 处理select产生的事件（I/O任务）
- 处理其它任务 （channel write、channel 注册等）


### channel初始化

- serverBootstrap.bind(9000);
- new channel()
- channelId
- new channelPipeline()
- new unsafe();

### netty unsafe
```` java
interface Unsafe {
   RecvByteBufAllocator.Handle recvBufAllocHandle();
   
   SocketAddress localAddress();
   SocketAddress remoteAddress();

   void register(EventLoop eventLoop, ChannelPromise promise);
   void bind(SocketAddress localAddress, ChannelPromise promise);
   void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise);
   void disconnect(ChannelPromise promise);
   void close(ChannelPromise promise);
   void closeForcibly();
   void beginRead();
   void write(Object msg, ChannelPromise promise);
   void flush();
   
   ChannelPromise voidPromise();
   ChannelOutboundBuffer outboundBuffer();
}
````

### pipeline head 和 tail
+ [head] outbound 出站事件channel.write() 最终会调用到head处理
+ [tail] inbound 最后处理pipeline未处理的消息，异常, 出站事件由tail发出。