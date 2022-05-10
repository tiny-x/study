package com.xx;


import com.sun.tools.attach.VirtualMachine;

/**
 * @author yefei
 */
public class LoadAgent {

    public static void main(String[] args) throws Exception {
        VirtualMachine virtualMachine = VirtualMachine.attach("15301");
        virtualMachine.loadAgent("/Users/xf.yefei/Projects/github/tiny-agent/target/tiny-agent-0.0.1-SNAPSHOT.jar");
        virtualMachine.detach();
    }
}
