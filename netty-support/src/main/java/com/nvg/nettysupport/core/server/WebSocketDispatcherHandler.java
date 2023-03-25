package com.nvg.nettysupport.core.server;

import com.google.gson.JsonElement;
import com.nvg.nettysupport.core.protocol.Request;
import com.nvg.nettysupport.core.protocol.RequestParser;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;

@ChannelHandler.Sharable
@RequiredArgsConstructor
public class WebSocketDispatcherHandler extends ChannelInboundHandlerAdapter {

    private final WebSocketHandlerMapping handlerMapping;
    private final WebSocketHandlerAdapter handlerAdapter;
    private final RequestParser requestParser;

    @Override
    public void channelRead(ChannelHandlerContext context, Object message)
            throws InvocationTargetException, IllegalAccessException {
        boolean isTextMsg = message instanceof TextWebSocketFrame;
        if (isTextMsg) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) message;
            this.doDispatch(context, textFrame);
        }
        context.fireChannelRead(message);
    }

    private void doDispatch(ChannelHandlerContext context, TextWebSocketFrame textFrame)
            throws InvocationTargetException, IllegalAccessException {
        Request incoming = requestParser.decode(textFrame.text());
        Object handler = handlerMapping.getHandler(incoming.getHandlerUrl());
        JsonElement outgoingData = handlerAdapter.handle(handler, context, textFrame, incoming.getData());
        Request outgoingRequest = new Request(incoming.getHandlerUrl(), outgoingData);
        String outgoing = requestParser.encode(outgoingRequest);
        context.writeAndFlush(new TextWebSocketFrame(outgoing));
    }

}
