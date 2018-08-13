package com.alibaba.dubbo.performance.demo.agent.registry;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.InetSocketAddress;

public class Endpoint {
    private final String host;
    private final int port;
    private  URI uri;
    private InetSocketAddress agentAddr;
    
    
    public Endpoint(String host,int port)  throws URISyntaxException {
        this.host = host;
        this.port = port;
        this.uri = new URI("http://"+host+":"+port);
        this.agentAddr = new InetSocketAddress(host,port);
    }

    public InetSocketAddress getAgentAddr() {
        return agentAddr;
    }
    public URI getUri() {
        return uri;
    }
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String toString(){
        return host + ":" + port;
    }

    public boolean equals(Object o){
        if (!(o instanceof Endpoint)){
            return false;
        }
        Endpoint other = (Endpoint) o;
        return other.host.equals(this.host) && other.port == this.port;
    }

    public int hashCode(){
        return host.hashCode() + port;
    }
}
