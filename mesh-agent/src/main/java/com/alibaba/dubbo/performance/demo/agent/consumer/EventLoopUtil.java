package com.alibaba.dubbo.performance.demo.agent.consumer;


import io.netty.channel.EventLoop;
import com.alibaba.dubbo.performance.demo.agent.consumer.async.AgentRequestAndSessionHolder;
import java.util.HashMap;
import java.util.Map;

public class EventLoopUtil {

    private static Map<EventLoop,NettyPoolClient> map = new HashMap<>();
    private static  Map<EventLoop,AgentRequestAndSessionHolder> map2= new HashMap<>();
    
    public static Map<EventLoop, AgentRequestAndSessionHolder> getMap2() {
        return map2;
    }

    
    public static Map<EventLoop, NettyPoolClient> getMap() {
        return map;
    }

}
