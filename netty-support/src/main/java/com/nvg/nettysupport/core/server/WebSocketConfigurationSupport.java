package com.nvg.nettysupport.core.server;

import com.nvg.nettysupport.core.annotation.WSHandler;
import com.nvg.nettysupport.core.annotation.WSMapping;
import com.nvg.nettysupport.core.protocol.RequestParser;
import com.nvg.nettysupport.core.protocol.SimpleGsonRequestParser;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
public class WebSocketConfigurationSupport {

    @Bean
    public WebSocketHandlerMapping wsHandlerMapping() {
        Map<String, Method> urlMap = new HashMap<>();

        Reflections reflections = new Reflections("com.nvg.nettysupport");
        Set<Class<?>> handlerClasses = reflections.getTypesAnnotatedWith(WSHandler.class);
        log.info("Scanning to find WebSocket handler classes. Detect {} classes", handlerClasses.size());

        handlerClasses.forEach(clazz -> {
            WSMapping mappingOnClass = clazz.getAnnotation(WSMapping.class);
            String urlPrefix = mappingOnClass == null ? "" : mappingOnClass.value();

            Method[] methodsOfClass = clazz.getDeclaredMethods();
            for (Method method : methodsOfClass) {
                WSMapping mappingOnMethod = method.getAnnotation(WSMapping.class);
                if (mappingOnMethod == null) continue;

                String url = urlPrefix + mappingOnMethod.value();
                urlMap.put(url, method);
            }
        });

        WebSocketHandlerMapping handlerMapping = new WebSocketHandlerMapping();
        handlerMapping.setUrlMap(urlMap);

        log.info("Init WebSocket handler mapping success");
        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter wsHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public RequestParser requestParser() {
        return new SimpleGsonRequestParser();
    }

}
