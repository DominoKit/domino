package com.progressoft.brix.domino.apt.server.multiMix;

import com.progressoft.brix.domino.api.shared.request.ServerResponse;
import com.progressoft.brix.domino.api.server.Handler;
import com.progressoft.brix.domino.api.server.RequestHandler;

@Handler("somePath2")
public class SecondHandler implements RequestHandler<SecondRequest, ServerResponse>{
    @Override
    public ServerResponse handleRequest(SecondRequest request) {
        return null;
    }

}
