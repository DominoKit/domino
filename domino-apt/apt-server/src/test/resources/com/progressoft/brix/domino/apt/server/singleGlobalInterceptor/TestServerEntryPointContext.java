package com.progressoft.brix.domino.apt.server.singleGlobalInterceptor;

import com.progressoft.brix.domino.api.server.entrypoint.ServerEntryPointContext;

public class TestServerEntryPointContext implements ServerEntryPointContext{
    @Override
    public String getName() {
        return TestServerEntryPointContext.class.getCanonicalName();
    }
}
