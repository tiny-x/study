package remoting.listener;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.exception.RemotingConnectException;
import org.rpc.remoting.api.RpcClient;
import org.rpc.remoting.netty.NettyClient;
import org.rpc.remoting.netty.NettyClientConfig;

public class Client {

    public static void main(String[] args) throws RemotingConnectException, InterruptedException {
        RpcClient client = new NettyClient(new NettyClientConfig());
        client.start();
        client.connect(new UnresolvedAddress("127.0.0.1", 9180));
    }
}
