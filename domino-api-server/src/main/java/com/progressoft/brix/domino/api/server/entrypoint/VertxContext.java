package com.progressoft.brix.domino.api.server.entrypoint;

import com.progressoft.brix.domino.api.server.config.ServerConfiguration;
import com.progressoft.brix.domino.api.server.endpoint.EndpointsRegistry;
import com.progressoft.brix.domino.service.discovery.VertxServiceDiscovery;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class VertxContext implements ServerContext {

    private final Router router;
    private final ServerConfiguration config;
    private final Vertx vertx;
    private final VertxServiceDiscovery serviceDiscovery;

    private VertxContext(Vertx vertx, Router router, ServerConfiguration config, VertxServiceDiscovery serviceDiscovery) {
        this.router = router;
        this.config = config;
        this.vertx = vertx;
        this.serviceDiscovery = serviceDiscovery;
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
        if (klass.isInstance(this))
            return (T) this;
        throw new InvalidContextTypeException();
    }

    @Override
    public void publishEndPoint(String path, EndpointsRegistry.EndpointHandlerFactory factory) {
        router.route().path(path).handler(factory.get());
    }

    public Router router() {
        return this.router;
    }

    public Vertx vertx() {
        return this.vertx;
    }

    public VertxServiceDiscovery serviceDiscovery() {
        return this.serviceDiscovery;
    }

    public static class VertxContextBuilder {
        private Router router;
        private ServerConfiguration config;
        private Vertx vertx;
        private VertxServiceDiscovery serviceDiscovery;

        private VertxContextBuilder(Vertx vertx) {
            this.vertx = vertx;
        }

        public VertxContextBuilder router(Router router) {
            this.router = router;
            return this;
        }

        public VertxContextBuilder serverConfiguration(ServerConfiguration config) {
            this.config = config;
            return this;
        }

        public static VertxContextBuilder vertx(Vertx vertx) {
            return new VertxContextBuilder(vertx);
        }

        public VertxContextBuilder vertxServiceDiscovery(VertxServiceDiscovery serviceDiscovery) {
            this.serviceDiscovery = serviceDiscovery;
            return this;
        }

        public VertxContext build() {
            return new VertxContext(vertx, router, config, serviceDiscovery);
        }
    }
}
