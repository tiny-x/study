package RemotingTest;

import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.remoting.RequestProcessor;
import org.rpc.remoting.RpcClient;
import org.rpc.remoting.RpcServer;
import org.rpc.remoting.netty.NettyClient;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.remoting.netty.NettyServer;
import org.rpc.remoting.netty.NettyServerConfig;
import org.rpc.remoting.payload.RequestBytes;
import org.rpc.remoting.payload.ResponseBytes;
import org.rpc.remoting.procotol.ProtocolHead;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RemotingServerTest {

    RpcServer rpcServer;
    RpcClient rpcClient;

    @Before
    public void before() {
        rpcServer = new NettyServer(new NettyServerConfig());
        rpcClient = new NettyClient(new NettyClientConfig());
        rpcServer.start();
        rpcClient.start();
    }

    @Test
    public void testInvokeSync() throws Exception {
        rpcServer.registerRequestProcess(new RequestProcessor() {
            @Override
            public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {
                String info = "hi client";
                System.out.printf("------- > receive client message: %s\n", new String(request.getBody()));

                ResponseBytes response = new ResponseBytes(
                        request.getSerializerCode(),
                        info.getBytes());
                response.setStatus(ProtocolHead.STATUS_SUCCESS);
                response.setInvokeId(request.getInvokeId());
                return response;
            }

            @Override
            public boolean rejectRequest() {
                return false;
            }
        }, Executors.newCachedThreadPool());

        RequestBytes request = new RequestBytes(ProtocolHead.JSON, "hello server".getBytes());
        ResponseBytes response = rpcClient.invokeSync(new UnresolvedAddress("127.0.0.1", 9000),
                request,
                3000L,
                TimeUnit.SECONDS
        );
        System.out.printf("------- > receive server message: %s\n", new String(response.getBody()));
    }

    @Test
    public void testInvokeAsync() throws Exception {
        rpcServer.registerRequestProcess(new RequestProcessor() {
            @Override
            public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {
                String info = "hi client";
                System.out.printf("------- > receive client message: %s\n", new String(request.getBody()));

                ResponseBytes response = new ResponseBytes(
                        request.getSerializerCode(),
                        info.getBytes());
                response.setStatus(ProtocolHead.STATUS_SUCCESS);
                response.setInvokeId(request.getInvokeId());
                return response;
            }

            @Override
            public boolean rejectRequest() {
                return false;
            }
        }, Executors.newCachedThreadPool());

        RequestBytes request = new RequestBytes(ProtocolHead.JSON, "hello server".getBytes());

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        rpcClient.invokeAsync(new UnresolvedAddress("127.0.0.1", 9000),
                request,
                3000L,
                TimeUnit.SECONDS,
                (future) -> {
                    ResponseBytes response = future.get();
                    System.out.printf("------- > receive server message: %s\n", new String(response.getBody()));
                    //countDownLatch.countDown();
                }
        );
        System.out.println("------> 异步执行！");
        countDownLatch.await();
    }
}
