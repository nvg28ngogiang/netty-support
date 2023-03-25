package com.nvg.nettysupport.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedList;

public class NettySocketServer {

    private final ServerBootstrap bootstrapper = new ServerBootstrap();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final LinkedList<ChannelHandler> handlers = new LinkedList<>();

    public NettySocketServer() {
        this.init();
    }

    private void init() {
        bootstrapper.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    public NettySocketServer addHandler(ChannelHandler newHandler) {
        handlers.add(newHandler);
        return this;
    }

    public NettySocketServer addHandlers(ChannelHandler ...newHandlers) {
        handlers.addAll(Arrays.asList(newHandlers));
        return this;
    }

    public void run(int port) throws InterruptedException {
        DefaultChannelInitializer channelInitializer = new DefaultChannelInitializer();
        channelInitializer.addHandlers(handlers.toArray(new ChannelHandler[handlers.size()]));
        bootstrapper.childHandler(channelInitializer);

        try {
            ChannelFuture future = bootstrapper.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    private static class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {

        private LinkedList<ChannelHandler> handlers = new LinkedList<>();

        public void addHandler(ChannelHandler newHandler) {
            handlers.add(newHandler);
        }

        public void addHandlers(ChannelHandler ...newHandlers) {
            handlers.addAll(Arrays.asList(newHandlers));
        }

        @Override
        protected void initChannel(SocketChannel channel) throws Exception {
            this.withDefaultHandlers(channel);
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(handlers.toArray(new ChannelHandler[handlers.size()]));
        }

        private void withDefaultHandlers(SocketChannel channel) {
            ChannelPipeline pipeline = channel.pipeline();
            pipeline.addLast(new HttpRequestDecoder(4096, 8192, 8192))
                    .addLast(new HttpRequestDecoder())
                    .addLast(new HandshakeIncomingRequestHandler());
        }
    }
}
