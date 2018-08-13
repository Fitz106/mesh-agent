package com.alibaba.dubbo.performance.demo.agent.consumer.async;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.HashMap;

public class AgentRequestAndSessionHolder {


    private HashMap<String,Channel> processingRpc = null;
    public AgentRequestAndSessionHolder(){
        processingRpc = new HashMap<String, Channel>();
    }

    public void put(String requestId,Channel channel){
        processingRpc.put(requestId,channel);
    }

    public  Channel get(String requestId){
        return processingRpc.get(requestId);
    }

    public  void remove(String requestId){
        processingRpc.remove(requestId);
    }
}
