package com.progressoft.brix.domino.test.api;

import com.progressoft.brix.domino.api.server.DominoLoader;
import com.progressoft.brix.domino.api.server.RouterConfigurator;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.web.Router;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class DominoServerRule extends ExternalResource{

    private int actualPort;
    private Router router;
    private JsonObject config;
    private final RunTestOnContext vertxRule;

    public DominoServerRule(RunTestOnContext vertxRule) {
        this.vertxRule = vertxRule;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return super.apply(base, description);
    }

    public void onBeforeDominoLoad(Router router, JsonObject config){
        //can be implemented by the test case if needed
    }

    @Override
    protected void before() throws Throwable {
        RouterConfigurator routerConfigurator=new RouterConfigurator(vertxRule.vertx());
        router = routerConfigurator.configuredRouter();
        config = new JsonObject().put("http.port", 0);
        onBeforeDominoLoad(router, config);
        new DominoLoader(vertxRule.vertx(), router, config).start(server -> actualPort=server.result().actualPort());
        onAfterDominoLoad(router, config);
    }

    public void onAfterDominoLoad(Router router, JsonObject config){
        //can be implemented by the test case if needed
    }

    public Vertx getVertx() {
        return vertxRule.vertx();
    }

    public Router getRouter() {
        return router;
    }

    public JsonObject getConfig() {
        return config;
    }

    public int getActualPort() {
        return actualPort;
    }

    @Override
    protected void after() {
    }
}
