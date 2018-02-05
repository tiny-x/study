package org.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.rpc.comm.bean.RpcRequest;
import org.rpc.comm.bean.RpcResponse;
import org.rpc.comm.bean.RpcService;
import org.rpc.comm.coder.RpcDecoder;
import org.rpc.comm.coder.RpcEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Rpc server.
 *
 * @author yefei
 * @date 2017 -06-20 13:23
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RpcServer.class);

    /**
     * 服务仓库
     */
    private Map<String, Object> serviceRepository = new HashMap<>();

    private int port;

    /**
     * Sets port.
     *
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start(port);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> service = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (service != null && !service.isEmpty()) {
            RpcService rpcService;
            for (Map.Entry<String, Object> entry : service.entrySet()) {
                rpcService = entry.getValue().getClass().getAnnotation(RpcService.class);
                // key:className + version
                serviceRepository.put(rpcService.value().getName() + "-" + rpcService.version(), entry.getValue());
            }
        }
    }

    /**
     * Start server
     *
     * @param port the port
     */
    public void start(int port) {
        EventLoopGroup main = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(main, worker) //
                    .channel(NioServerSocketChannel.class) //
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcEncoder(RpcResponse.class));
                            ch.pipeline().addLast(new RpcDecoder(RpcRequest.class));
                            ch.pipeline().addLast(new RpcServerHandler(serviceRepository));
                        }
                    });

            // 服务端接受连接的队列长度
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1027);
            // 探测空闲连接的有效性
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            logger.info("server start listener port:{}", port);
            channelFuture.channel().closeFuture().sync();

        } catch (Throwable e) {
            logger.error("start server error!", e);
        } finally {
            worker.shutdownGracefully();
            main.shutdownGracefully();
        }

    }
}
