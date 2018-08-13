package com.alibaba.dubbo.performance.demo.agent.consumer;

import org.asynchttpclient.AsyncHttpClient;

public class AsyncHttpUtil {
    private static AsyncHttpClient asyncHttpClient = null;

    public static AsyncHttpClient getAsyncHttpClient() {
        return asyncHttpClient;
    }

   public AsyncHttpUtil(AsyncHttpClient asyncHttpClient){
        this.asyncHttpClient = asyncHttpClient;
   }
}
