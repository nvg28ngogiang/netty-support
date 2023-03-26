package com.nvg.nettysupport.core.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class HandshakeIncomingRequestHandler extends ChannelInboundHandlerAdapter {
    private static final String WEBSOCKET_PROTOCOL_PREFIX = "ws";

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        boolean isHandshakeRequest = message instanceof DefaultHttpRequest;
        if (!isHandshakeRequest) {
            context.fireChannelRead(message);
            return;
        }

        HttpRequest request = (DefaultHttpRequest) message;
        final Channel channel = context.channel();
        String serverSocketURL = WEBSOCKET_PROTOCOL_PREFIX + channel.localAddress();
        WebSocketServerHandshakerFactory handshakerFactory = new WebSocketServerHandshakerFactory(serverSocketURL, null, false);
        handshakerFactory.newHandshaker(request).handshake(channel, request);
    }
}
