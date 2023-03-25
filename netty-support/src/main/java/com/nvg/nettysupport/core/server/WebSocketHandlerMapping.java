package com.nvg.nettysupport.core.server;

import java.util.HashMap;
import java.util.Map;

public class WebSocketHandlerMapping {

    private final Map<String, Object> urlMap = new HashMap<>();

    public Object getHandler(String url) {
        return urlMap.get(url);
    }

    public void setUrlMap(Map<String, ?> urlMap) {
        this.urlMap.putAll(urlMap);
    }
}
