package com.nvg.nettysupport.core.protocol;

import com.google.gson.JsonElement;
import lombok.Data;

@Data
public class Request {
    private final String handlerUrl;
    private final JsonElement data;
}
