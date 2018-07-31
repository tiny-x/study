package com.xy.spring;

public class Response<T> {

    private int code;

    private T t;

    public static Response ofSuccess() {
        Response response = new Response();
        response.setCode(200);
        return response;
    }

    public static <T> Response ofSuccess(T t) {
        Response response = new Response();
        response.setResult(t);
        response.setCode(200);
        return response;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setResult(T t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", t=" + t +
                '}';
    }
}
