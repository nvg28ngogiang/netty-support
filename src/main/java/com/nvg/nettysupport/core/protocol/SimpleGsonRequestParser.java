package com.nvg.nettysupport.core.protocol;

import com.google.gson.Gson;

public class SimpleGsonRequestParser implements RequestParser {
    private final Gson gson = new Gson();
    @Override
    public Request decode(String incoming) {
        return gson.fromJson(incoming, Request.class);
    }

    @Override
    public String encode(Request outgoing) {
        return gson.toJson(outgoing);
    }
}
