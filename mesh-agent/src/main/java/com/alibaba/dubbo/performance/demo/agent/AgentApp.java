package com.alibaba.dubbo.performance.demo.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.alibaba.dubbo.performance.demo.agent.consumer.HttpConsumerServer;
import com.alibaba.dubbo.performance.demo.agent.provider.HttpProviderServer;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdUtil;
import com.alibaba.dubbo.performance.demo.agent.provider.RpcChannelUtil;
import com.alibaba.dubbo.performance.demo.agent.consumer.EventLoopUtil;
import com.alibaba.dubbo.performance.demo.agent.provider.RpcEventLoopUtil;
import com.alibaba.dubbo.performance.demo.agent.provider.ChannelCountMapUtil;
@SpringBootApplication
public class AgentApp {
    // agent会作为sidecar，部署在每一个Provider和Consumer机器上
    // 在Provider端启动agent时，添加JVM参数-Dtype=provider -Dserver.port=30000 -Ddubbo.protocol.port=20889
    // 在Consumer端启动agent时，添加JVM参数-Dtype=consumer -Dserver.port=20000
    // 添加日志保存目录: -Dlogs.dir=/path/to/your/logs/dir。请安装自己的环境来设置日志目录。
    public static void main(String[] args) throws Exception{
        String type = System.getProperty("type");   // 获取type参数
        EtcdUtil etcdutil = new EtcdUtil();
        if (type.equals("consumer")){
            HttpConsumerServer consumerServer = new HttpConsumerServer();
            consumerServer.start(20000);
        }
        else if(type.equals("provider")){
            int PORT = Integer.parseInt(System.getProperty("server.port"));
             ChannelCountMapUtil channelCountMapUtil = new ChannelCountMapUtil();
			RpcEventLoopUtil rpcEventLoopUtil = new RpcEventLoopUtil();
			RpcChannelUtil rpcChannelUtil = new RpcChannelUtil();
            HttpProviderServer providerServer = new HttpProviderServer();
            providerServer.start(PORT);
        }else{
    
        }
    }
}
