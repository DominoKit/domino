package com.progressoft.brix.domino.apt.server;


import com.progressoft.brix.domino.api.server.ServerApp;
import com.progressoft.brix.domino.api.server.entrypoint.VertxEntryPointContext;
import com.progressoft.brix.domino.api.server.handler.CallbackRequestHandler;
import com.progressoft.brix.domino.api.server.handler.RequestHandler;
import com.progressoft.brix.domino.apt.commons.*;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

public class EndpointHandlerSourceWriter extends JavaSourceWriter {

    private static final String OVERRIDE = "@Override";
    private static final int REQUEST_IMPORT_INDEX = 1;
    private static final int RESPONSE_IMPORT_INDEX = 2;
    private final JavaSourceBuilder sourceBuilder;
    private final FullClassName request;
    private final FullClassName response;

    public EndpointHandlerSourceWriter(ProcessorElement element) {
        super(element);
        this.sourceBuilder = new JavaSourceBuilder(element.simpleName() +
                "EndpointHandler");
        List<String> handlerImports;
        if (element.isImplementsGenericInterface(RequestHandler.class)) {
            handlerImports = new FullClassName(processorElement.getInterfaceFullQualifiedGenericName(RequestHandler.class)).allImports();
        } else {
            handlerImports = new FullClassName(processorElement.getInterfaceFullQualifiedGenericName(CallbackRequestHandler.class)).allImports();
        }

        this.request = new FullClassName(handlerImports.get(REQUEST_IMPORT_INDEX));
        this.response = new FullClassName(handlerImports.get(RESPONSE_IMPORT_INDEX));
    }

    @Override
    public String write() {
        sourceBuilder.onPackage(processorElement.elementPackage())
                .imports(Handler.class.getCanonicalName())
                .imports(Json.class.getCanonicalName())
                .imports(RoutingContext.class.getCanonicalName())
                .imports(ServerApp.class.getCanonicalName())
                .imports(request.asImport())
                .imports(response.asImport())
                .imports(VertxEntryPointContext.class.getCanonicalName())
                .withModifiers(new ModifierBuilder().asPublic())
                .implement("Handler<RoutingContext>");

        writeBody();

        String result=sourceBuilder.build();
        System.out.println(result);

        return result;
    }


    private void writeBody() {
        writeHandleMethod();
    }

    private void writeHandleMethod() {

        MethodBuilder methodBuilder = sourceBuilder.method("handle")
                .annotate(OVERRIDE)
                .withModifier(new ModifierBuilder().asPublic())
                .returns("void")
                .takes("RoutingContext", "routingContext")
                .block("try {")
                .block("ServerApp serverApp = ServerApp.make();")
                .block(request.asSimpleName() + " requestBody = Json.decodeValue(routingContext.getBodyAsString(), " +
                        request.asSimpleName() + ".class);");
        if (processorElement.isImplementsGenericInterface(RequestHandler.class))
            completeHandler(methodBuilder);
        else
            completeHandlerCallback(methodBuilder);
    }

    private void completeHandler(MethodBuilder methodBuilder) {
        methodBuilder.block(response.asSimpleName() + " response=(" + response.asSimpleName() +
                ")serverApp.executeRequest(requestBody, new VertxEntryPointContext(routingContext, serverApp.serverContext().config(), routingContext.vertx()));")
                .block("routingContext.response()\n" +
                        "                .putHeader(\"content-type\", \"application/json\")\n" +
                        "                .end(Json.encode(response));")
                .block("} catch(Exception e) {")
                .block("\troutingContext.fail(e);")
                .block("}")
                .end();
    }

    private void completeHandlerCallback(MethodBuilder methodBuilder) {
       sourceBuilder.imports(CallbackRequestHandler.class.getCanonicalName());
        methodBuilder.block("serverApp.executeCallbackRequest(requestBody, new VertxEntryPointContext(routingContext, serverApp.serverContext().config(), routingContext.vertx()), new CallbackRequestHandler.ResponseCallback() {", false)
                .block("@Override\n" +
                        "public void complete(Object response) {\n")
                .block("\troutingContext.response()\n" +
                        "                .putHeader(\"content-type\", \"application/json\")\n" +
                        "                .end(Json.encode((" + response.asSimpleName() + ")response));")
                .block("}")
                .block("@Override\n" +
                        "public void redirect(String location) {\n" +
                        "\troutingContext.response().setStatusCode(302).putHeader(\"Location\",location).end();"+
                        "}")
                .block("});")
                .block("} catch(Exception e) {")
                .block("\troutingContext.fail(e);")
                .block("}")
                .end();
    }
}
