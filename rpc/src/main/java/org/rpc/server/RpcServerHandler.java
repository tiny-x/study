package org.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.rpc.comm.bean.RpcRequest;
import org.rpc.comm.bean.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Rpc server handler. <br/>
 * 处理RPC 请求
 *
 * @author yefei
 * @date 2017 -06-20 15:41
 */
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    /**
     * 服务仓库
     */
    private Map<String, Object> serviceRepository = new HashMap<>();

    /**
     * Instantiates a new Rpc server handler.
     *
     * @param serviceRepository the service repository
     */
    public RpcServerHandler(Map<String, Object> serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * 处理rpc请求
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        // new response
        RpcResponse response = new RpcResponse();
        try {
            String serviceId = msg.getInterfaceName() + "-" + msg.getServiceVersion();
            Object service = serviceRepository.get(serviceId);
            if (service == null) {
                throw new Exception("can not find service: " + serviceId);
            }
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            method.setAccessible(true);
            response.setRequestId(msg.getRequestId());
            response.setResult(method.invoke(service, msg.getParameters()));
        } catch (Exception e) {
            logger.error("Rpc Provider Handler error", e);
            response.setException(e);
        }
        // 写入 RPC 响应对象并自动关闭连接
        ctx.writeAndFlush(response);//.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server caught exception", cause);
        ctx.close();
    }
}
