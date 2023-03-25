package com.nvg.nettysupport.core.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.nvg.nettysupport.core.annotation.WSRequestBody;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class WebSocketHandlerAdapter implements ApplicationContextAware, InitializingBean {
    private final Gson gson = new Gson();
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.applicationContext, "Can't find application context");
    }

    public JsonElement handle(Object handler, ChannelHandlerContext handlerContext,
                              TextWebSocketFrame rawTextFrame, JsonElement parsedData)
            throws InvocationTargetException, IllegalAccessException {
        Method handlerMethod = (Method) handler;
        Object handlerObj = this.getHandlerObject(handlerMethod);
        Object[] bindingParams = this.getBindingParams(handlerMethod, handlerContext, rawTextFrame, parsedData);
        Object result = handlerMethod.invoke(handlerObj, bindingParams);
        return gson.toJsonTree(result);
    }

    private Object getHandlerObject(Method handlerMethod) {
        Class<?> declaringClass = handlerMethod.getDeclaringClass();
        return applicationContext.getBean(declaringClass);
    }

    private Object[] getBindingParams(Method handlerMethod, ChannelHandlerContext handlerContext,
                                      TextWebSocketFrame rawTextFrame, JsonElement parsedData) {
        Parameter[] params = handlerMethod.getParameters();
        Object[] bindingParams = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            Class<?> paramType = params[i].getType();
            if (paramType == ChannelHandlerContext.class) {
                bindingParams[i] = handlerContext;
            } else if (paramType == TextWebSocketFrame.class) {
                bindingParams[i] = rawTextFrame;
            } else if (params[i].getDeclaredAnnotation(WSRequestBody.class) != null) {
                bindingParams[i] = gson.fromJson(parsedData, paramType);
            }
        }

        return bindingParams;
    }

}
