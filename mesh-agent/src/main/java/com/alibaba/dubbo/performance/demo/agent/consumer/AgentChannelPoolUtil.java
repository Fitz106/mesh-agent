package com.alibaba.dubbo.performance.demo.agent.consumer;

public class AgentChannelPoolUtil {
    private static NettyPoolClient nettyPoolClient = null;

    public static NettyPoolClient getNettyPoolClient() {
        return nettyPoolClient;
    }

    public AgentChannelPoolUtil(NettyPoolClient nettyPoolClient){
        this.nettyPoolClient = nettyPoolClient;
    }
}
