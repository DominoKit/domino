package com.progressoft.brix.domino.apt.server.multiMix;

import com.progressoft.brix.domino.api.shared.request.RequestBean;
import com.progressoft.brix.domino.api.server.interceptor.GlobalInterceptor;
import com.progressoft.brix.domino.api.server.interceptor.GlobalRequestInterceptor;

@GlobalInterceptor
public class SecondGlobalInterceptor implements GlobalRequestInterceptor<TestServerEntryPointContext>{

    @Override
    public void intercept(RequestBean request, TestServerEntryPointContext context) {
        //for generation testing only
    }
}

