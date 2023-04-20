package com.xy;
import feign.RequestLine;

public interface MyFeignInterface {

    @RequestLine("GET /")
    String mockFeign();

}
