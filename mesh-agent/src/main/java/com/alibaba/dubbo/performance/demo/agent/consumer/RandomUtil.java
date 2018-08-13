package com.alibaba.dubbo.performance.demo.agent.consumer;

import java.util.Random;

public class RandomUtil {
    private static  Random random = new Random(1);
    
    public static Random getRandom() {
        return random;
    }
}
