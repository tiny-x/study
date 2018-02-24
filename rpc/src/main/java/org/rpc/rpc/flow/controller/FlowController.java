package org.rpc.rpc.flow.controller;

import java.util.concurrent.RejectedExecutionException;

public interface FlowController {

    void flowController() throws RejectedExecutionException;
}
