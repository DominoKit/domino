package com.progressoft.brix.domino.api.server;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class VertxContext implements ServerContext {

    private final Router router;
    private final ServerConfiguration config;
    private final Vertx vertx;

    public VertxContext(Vertx vertx, Router router, ServerConfiguration config) {
        this.router = router;
        this.config = config;
        this.vertx=vertx;
    }

    @Override
    public ServerConfiguration config() {
        return config;
    }

    @Override
    public void publishService(String path, EndpointsRegistry.EndpointHandlerFactory factory) {
        publishEndPoint("/service/" + path, factory);
    }

    @Override
    public <T> T cast(Class<T> klass) throws InvalidContextTypeException {
        if(klass.isInstance(this))
            return (T)this;
        throw new InvalidContextTypeException();
    }

    @Override
    public void publishEndPoint(String path, EndpointsRegistry.EndpointHandlerFactory factory) {
        router.route().path(path).handler(factory.get());
    }

    public Router router() {
        return this.router;
    }

    public Vertx vertx(){
        return this.vertx;
    }
}
