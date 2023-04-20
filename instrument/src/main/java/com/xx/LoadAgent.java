package com.xx;


import com.sun.tools.attach.VirtualMachine;

/**
 * @author yefei
 */
public class LoadAgent {

    public static void main(String[] args) throws Exception {
        VirtualMachine virtualMachine = VirtualMachine.attach("61967");
        virtualMachine.loadAgent("/Users/xf.yefei/Projects/xhas/xchaos-agent-java/xchaos-agent-java-assembly/target/xchaos-agent.jar=command=create;target=mysql;action=delay");
        virtualMachine.detach();
    }
}
