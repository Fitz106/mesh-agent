package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
public class RpcRequestAndSessionHolder {
    private static HashMap<String,Channel> processingRpc = new HashMap<>();

    public static void put(String requestId,Channel channel){
        processingRpc.put(requestId,channel);
    }

    public static Channel get(String requestId){
        return processingRpc.get(requestId);
    }

    public static void remove(String requestId){
        processingRpc.remove(requestId);
    }
     public static int getSize(){
        return processingRpc.size();
    }
}
