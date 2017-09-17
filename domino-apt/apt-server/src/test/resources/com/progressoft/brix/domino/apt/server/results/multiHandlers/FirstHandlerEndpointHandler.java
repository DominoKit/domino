package com.progressoft.brix.domino.apt.server.multiHandlers;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import com.progressoft.brix.domino.api.server.ServerApp;
import com.progressoft.brix.domino.apt.server.multiHandlers.FirstRequest;
import com.progressoft.brix.domino.api.shared.request.ServerResponse;
import com.progressoft.brix.domino.api.server.entrypoint.VertxEntryPointContext;
import io.vertx.core.http.HttpMethod;

public class FirstHandlerEndpointHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {
        try {
            ServerApp serverApp = ServerApp.make();
            FirstRequest requestBody;
            HttpMethod method=routingContext.request().method();
            if(HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method) || HttpMethod.PATCH.equals(method)){
                requestBody=Json.decodeValue(routingContext.getBodyAsString(), FirstRequest.class);
            }else {
                requestBody = new FirstRequest();
            }
            ServerResponse response = (ServerResponse) serverApp
                    .executeRequest(requestBody, new VertxEntryPointContext(routingContext, serverApp.serverContext().config(),
                            routingContext.vertx()));
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(Json.encode(response));
        } catch (Exception e){
            routingContext.fail(e);
        }
    }
}