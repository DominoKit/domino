package org.dominokit.domino.api.server;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.ServiceLoader;

public class DominoLauncher extends Launcher {

    protected static final ConfigHolder configHolder = new ConfigHolder();
    protected static final RouterHolder routerHolder = new RouterHolder();


    protected static class ConfigHolder {
        protected JsonObject config;
    }

    protected static class RouterHolder {
        protected Router router;
    }

    public static void main(String[] args) {
        new DominoLauncher().dispatch(args);
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {

        RouterConfigurator routerConfigurator = new RouterConfigurator(vertx, configHolder.config, SecretKey.generate());
        routerHolder.router =
                PROCESS_ARGS.contains("-cluster") ?
                        routerConfigurator.configuredClusteredRouter() : routerConfigurator.configuredRouter();

    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        ServiceLoader.load(VertxOptionsHandler.class)
                .iterator()
                .forEachRemaining(vertxOptionsHandler -> {
                    vertxOptionsHandler.onBeforeVertxStart(options, configHolder.config);
                });
    }

    @Override
    public void afterConfigParsed(JsonObject config) {
        configHolder.config = config;
    }

}
