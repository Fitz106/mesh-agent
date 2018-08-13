package com.alibaba.dubbo.performance.demo.agent.registry;

import java.util.List;

public class EtcdUtil {
    private   static IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private static Object lock = new Object();
    private static  List<Endpoint> endpoints;
    public static IRegistry getRegistry() {
        return registry;
    }

    public static  List<Endpoint> getEndpoints(String interfaceName) {
        if (null == endpoints) {
            //System.out.println("nulllllllll");
            synchronized (lock) {
                if (null == endpoints) {
                    try {
                        endpoints = EtcdUtil.getRegistry().find(interfaceName);
                        return endpoints;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return endpoints;
    }

}

