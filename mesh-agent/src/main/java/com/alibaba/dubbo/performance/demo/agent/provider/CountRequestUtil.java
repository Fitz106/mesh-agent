package com.alibaba.dubbo.performance.demo.agent.provider;

import java.util.concurrent.atomic.AtomicLong;

public class CountRequestUtil {
    private static AtomicLong atomicLong = new AtomicLong();
    private static long count;
    public static long add(){
        return count = atomicLong.getAndIncrement();
    }
    public static long rem(){
        return count = atomicLong.getAndDecrement();
    }

}
