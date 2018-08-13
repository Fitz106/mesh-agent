package com.alibaba.dubbo.performance.demo.agent.provider;

import com.alibaba.dubbo.performance.demo.agent.dubbo.ConnecManager;

public class RpcConnecUtil {
    private static final ConnecManager connecManager = new ConnecManager();
    public static ConnecManager getConnecManager() {
        return connecManager;
    }
}
