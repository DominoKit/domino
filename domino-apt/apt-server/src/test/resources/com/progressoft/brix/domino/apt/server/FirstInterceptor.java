package com.progressoft.brix.domino.apt.server;

import com.progressoft.brix.domino.api.server.interceptor.Interceptor;
import com.progressoft.brix.domino.api.server.interceptor.RequestInterceptor;
import com.progressoft.brix.domino.api.server.request.RequestContext;

@Interceptor(FirstHandler.class)
public class FirstInterceptor implements RequestInterceptor<FirstRequest, TestServerEntryPointContext> {

    @Override
    public void intercept(RequestContext<FirstRequest> requestContext, TestServerEntryPointContext entryPoint) {
        //for code generation testing only
    }
}

