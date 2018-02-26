package remoting;

import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.remoting.api.RemotingCommandFactory;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.RpcClient;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.api.payload.RequestCommand;
import org.rpc.remoting.api.payload.ResponseCommand;
import org.rpc.remoting.netty.NettyClient;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.remoting.netty.NettyServer;
import org.rpc.remoting.netty.NettyServerConfig;
import org.rpc.remoting.api.procotol.ProtocolHead;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

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
            public ResponseCommand process(ChannelHandlerContext context, RequestCommand request) {
                String info = "hi client";
                System.out.printf("------- > receive client message: %s\n", new String(request.getBody()));

                ResponseCommand response = RemotingCommandFactory.createResponseCommand(
                        request.getSerializerCode(),
                        info.getBytes(),
                        request.getInvokeId()
                );

                return response;
            }

            @Override
            public boolean rejectRequest() {
                return false;
            }
        }, Executors.newCachedThreadPool());

        RequestCommand request = new RequestCommand(ProtocolHead.REQUEST, ProtocolHead.PROTO_STUFF, "hello register".getBytes());
        UnresolvedAddress address = new UnresolvedAddress("127.0.0.1", 9180);
        rpcClient.connect(address);

        ResponseCommand response = rpcClient.invokeSync(address,
                request,
                3000L
        );
        System.out.printf("------- > receive register message: %s\n", new String(response.getBody()));
    }

    @Test
    public void testInvokeAsync() throws Exception {
        rpcServer.registerRequestProcess(new RequestProcessor() {
            @Override
            public ResponseCommand process(ChannelHandlerContext context, RequestCommand request) {
                String info = "hi client";
                System.out.printf("------- > receive client message: %s\n", new String(request.getBody()));

                ResponseCommand response = RemotingCommandFactory.createResponseCommand(
                        request.getSerializerCode(),
                        info.getBytes(),
                        request.getInvokeId()
                );
                return response;
            }

            @Override
            public boolean rejectRequest() {
                return false;
            }
        }, Executors.newCachedThreadPool());

        RequestCommand request = new RequestCommand(ProtocolHead.REQUEST, ProtocolHead.PROTO_STUFF, "hello register".getBytes());

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UnresolvedAddress address = new UnresolvedAddress("127.0.0.1", 9180);
        rpcClient.connect(address);

        rpcClient.invokeAsync(address,
                request,
                3000L,
                (future) -> {
                    ResponseCommand response = null;
                    try {
                        response = future.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("------- > receive register message: %s\n", new String(response.getBody()));
                    //countDownLatch.countDown();
                }
        );
        System.out.println("------> 异步执行！");
        countDownLatch.await();
    }
}
