package com.alibaba.dubbo.performance.demo.agent.provider;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.HashMap;
import java.util.Map;

public class RpcChannelUtil {
    private static Map<Channel,Channel> map;
    public RpcChannelUtil(){
        this.map = new HashMap<>();
    }
    public static Map<Channel, Channel> getMap() {
        return map;
    }

}
