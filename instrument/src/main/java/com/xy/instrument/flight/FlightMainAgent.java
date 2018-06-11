package com.xy.instrument.flight;

import com.sun.tools.attach.VirtualMachine;

/**
 * @author yefei
 * @date 2018-05-31 17:25
 */
public class FlightMainAgent {

    public static void main(String[] args) throws Exception {
        VirtualMachine attach = VirtualMachine.attach("19224");
        attach.loadAgent("instrument-1.0.0.jar");
    }

}
