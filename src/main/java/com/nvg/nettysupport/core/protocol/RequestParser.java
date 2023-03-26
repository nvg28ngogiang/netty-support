package com.nvg.nettysupport.core.protocol;

public interface RequestParser {

    Request decode(String incoming);

    String encode(Request outgoing);
}
