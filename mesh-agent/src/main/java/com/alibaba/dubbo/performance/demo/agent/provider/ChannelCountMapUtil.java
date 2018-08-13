package com.alibaba.dubbo.performance.demo.agent.provider;

import com.sun.org.apache.xml.internal.dtm.ref.dom2dtm.DOM2DTM;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class ChannelCountMapUtil {
    private static Map<Channel,Integer> ChannelCount = null;
    public ChannelCountMapUtil(){
        ChannelCount = new HashMap<>();
    }
    public  static int getCount(Channel channel){
        return ChannelCount.get(channel);
    }
    public static void addCount(Channel channel){
        if(ChannelCount.get(channel) == null){
            System.out.println(channel == null);
            System.out.println("sdfasdfas");
            System.out.println(null == ChannelCount);
            ChannelCount.put(channel,1);
        }
        ChannelCount.put(channel,ChannelCount.get(channel)+1);
    }
    public static void ResetCount(Channel channel){
        ChannelCount.put(channel,0);
    }
}
