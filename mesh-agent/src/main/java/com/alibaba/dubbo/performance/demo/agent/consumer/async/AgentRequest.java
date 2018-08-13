package com.alibaba.dubbo.performance.demo.agent.consumer.async;

import java.util.concurrent.atomic.AtomicLong;

public class AgentRequest {
    private static AtomicLong atomicLong = new AtomicLong();
    private long id;
    public AgentRequest(){
        id = atomicLong.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
