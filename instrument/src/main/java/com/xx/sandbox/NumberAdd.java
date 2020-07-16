package com.xx.sandbox;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.kohsuke.MetaInfServices;

import javax.annotation.Resource;

@MetaInfServices(Module.class)
@Information(id = "NumberAdd")
public class NumberAdd implements Module {

    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Command("add")
    public void repairCheckState() {
        new EventWatchBuilder(moduleEventWatcher)
                .onClass("com.xx.NumberAdd")
                .onBehavior("add")
                .onWatch(new AdviceListener() {
                    @Override
                    protected void before(Advice advice) throws Throwable {
                        Object[] parameterArray = advice.getParameterArray();
                        parameterArray[0] = 100;
                    }
                });
    }

}