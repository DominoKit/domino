package com.progressoft.brix.domino.apt.server.singleInterceptor;

import com.google.auto.service.AutoService;
import com.progressoft.brix.domino.api.server.config.ServerModuleConfiguration;
import com.progressoft.brix.domino.api.server.interceptor.InterceptorsRegistry;
import java.lang.Override;
import javax.annotation.Generated;

@Generated("com.progressoft.brix.domino.apt.server.ServerModuleAnnotationProcessor")
@AutoService(ServerModuleConfiguration.class)
public class TestServerModule implements ServerModuleConfiguration{

    @Override
    public void registerInterceptors(InterceptorsRegistry registry) {
        registry.registerInterceptor(FirstRequest.class.getCanonicalName(), TestServerEntryPointContext.class.getCanonicalName(), new FirstInterceptor());
    }
}
