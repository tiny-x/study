package remoting.listener;

import io.netty.channel.Channel;
import org.rpc.remoting.api.ChannelEventListener;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.netty.NettyServer;
import org.rpc.remoting.netty.NettyServerConfig;

public class Server {

    public static void main(String[] args) {
        RpcServer server = new NettyServer(new NettyServerConfig(), new ChannelEventListener() {
            @Override
            public void onChannelConnect(String remoteAddr, Channel channel) {
                System.out.println("onChannelConnect");
            }

            @Override
            public void onChannelClose(String remoteAddr, Channel channel) {
                System.out.println("onChannelClose");
            }

            @Override
            public void onChannelException(String remoteAddr, Channel channel) {

            }

            @Override
            public void onChannelIdle(String remoteAddr, Channel channel) {

            }

            @Override
            public void onChannelActive(String remoteAddr, Channel channel) {

            }

            @Override
            public void onChannelInActive(String remoteAddr, Channel channel) {

            }
        });

        server.start();
    }
}
