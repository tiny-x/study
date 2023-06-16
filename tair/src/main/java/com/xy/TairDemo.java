package com.xy;

import com.taobao.tair.DataEntry;
import com.taobao.tair.Result;
import com.taobao.tair.impl.DefaultTairManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TairDemo {

    public static void main(String[] args) throws Exception {
        testChaos();
    }

    static void test0() throws Exception {
        DefaultTairManager defaultTairManager = new DefaultTairManager();
        List<String> cs = new ArrayList<String>();
        cs.add("10.10.225.128:5191");
        defaultTairManager.setConfigServerList(cs);
        defaultTairManager.setGroupName("group_test");
        defaultTairManager.init();
        defaultTairManager.put(0, "name:001", "zhangfei");
        Result<DataEntry> value = defaultTairManager.get(0, "name:001");
        System.out.println(value);
        //设置版本0 强制更新  过期时间2秒
        defaultTairManager.put(0, "name:002", "zhaoy", 0, 2);
        //defaultTairManager.delete(0, "name:002");
        Result<DataEntry> value2 = defaultTairManager.get(0, "name:002");
        System.out.println(value2);
        Thread.sleep(4000);
        Result<DataEntry> value3 = defaultTairManager.get(0, "name:002");
        System.out.println(value3);
    }

    static void testChaos() throws Exception {
        DefaultTairManager defaultTairManager = new DefaultTairManager();
        List<String> cs = new ArrayList<String>();
        cs.add("10.10.225.128:5198");
        defaultTairManager.setConfigServerList(cs);
        defaultTairManager.setGroupName("group_test");
        defaultTairManager.init();

        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long l = System.currentTimeMillis();
            Result<DataEntry> value = defaultTairManager.get(0, "name:001");
            System.out.printf("耗时：%dms %s", System.currentTimeMillis() - l, value);
        }

    }
}
