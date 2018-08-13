package com.alibaba.dubbo.performance.demo.agent.provider;

import io.netty.channel.EventLoop;

import java.util.HashMap;
import java.util.Map;

public class RpcEventLoopUtil {

    private static Map<EventLoop,RpcNettyPoolClient> map;



    public RpcEventLoopUtil(){
        this.map = new HashMap<>();
    }
    public static Map<EventLoop, RpcNettyPoolClient> getMap() {
        return map;
    }

}
