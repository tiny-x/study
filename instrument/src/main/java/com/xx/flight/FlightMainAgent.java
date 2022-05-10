package com.xx.flight;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.IOException;
import java.util.List;

/**
 * @author yefei
 * @date 2018-05-31 17:25
 */
public class FlightMainAgent {

    public static void main(String[] args) {
        //获取当前系统中所有 运行中的 虚拟机
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : list) {
            //如果虚拟机的名称为 xxx 则 该虚拟机为目标虚拟机，获取该虚拟机的 pid
            //然后加载 agent.jar 发送给该虚拟机
            if (vmd.displayName().endsWith("com.xx.NumberAdd")) {
                VirtualMachine virtualMachine = null;
                try {
                    virtualMachine = VirtualMachine.attach(vmd.id());
                    virtualMachine.loadAgent("/Users/xf.yefei/Projects/github/tiny-agent/target/tiny-agent-0.0.1-SNAPSHOT.jar");
                    //virtualMachine.loadAgent("/Users/xf.yefei/Projects/study/bytebuddy/target/bytebuddy-1.0.0.jar");
                    //virtualMachine.loadAgent("/Users/xf.yefei/Projects/github/tt2016_byte_buddy_agent_demo/target/byte-buddy-agent-demo-1.0-full.jar");
                    //virtualMachine.loadAgent("/Users/xf.yefei/Projects/study/instrument/target/instrument-1.0.0.jar");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        virtualMachine.detach();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
