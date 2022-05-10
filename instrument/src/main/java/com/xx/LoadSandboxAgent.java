package com.xx;


import com.sun.tools.attach.VirtualMachine;

/**
 * @author yefei
 */
public class LoadSandboxAgent {

    public static void main(String[] args) throws Exception {
        VirtualMachine virtualMachine = VirtualMachine.attach("29945");
        virtualMachine.loadAgent("/opt/chaosblade-1.3.1/lib/sandbox/lib/sandbox-agent.jar",
                "home=/opt/chaosblade-1.3.1/lib/sandbox;token=179927390947;server.ip=127.0.0.1;server.port=62878;namespace=chaosblade");
        virtualMachine.detach();
    }
}
