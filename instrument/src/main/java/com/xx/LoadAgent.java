package com.xx;


import com.sun.tools.attach.VirtualMachine;

/**
 * @author yefei
 */
public class LoadAgent {

    public static void main(String[] args) throws Exception {
        VirtualMachine virtualMachine = VirtualMachine.attach("53522");
        virtualMachine.loadAgent("/temp/instrument-1.0.0.jar");
        virtualMachine.detach();
    }
}
