package com.progressoft.brix.domino.apt.client.processors.handlers;


import com.progressoft.brix.domino.api.client.annotations.HandlerPath;
import com.progressoft.brix.domino.api.client.annotations.RequestSender;
import com.progressoft.brix.domino.api.client.request.RequestRestSender;
import com.progressoft.brix.domino.api.client.request.ServerRequestCallBack;
import com.progressoft.brix.domino.apt.commons.*;

public class RequestSenderSourceWriter extends JavaSourceWriter {

    private final JavaSourceBuilder sourceBuilder;
    private final FullClassName request;
    private final FullClassName response;


    public RequestSenderSourceWriter(ProcessorElement processorElement, FullClassName request, FullClassName response) {
        super(processorElement);
        this.request = request;
        this.response = response;

        this.sourceBuilder = new JavaSourceBuilder(processorElement.simpleName() + "Sender");
    }

    @Override
    public String write() {
        this.sourceBuilder.onPackage(processorElement.elementPackage())
                .imports("com.google.gwt.core.client.GWT")
                .imports(RequestRestSender.class.getCanonicalName())
                .imports(ServerRequestCallBack.class.getCanonicalName())
                .imports(processorElement.fullQualifiedNoneGenericName())
                .imports(request.asImport())
                .imports(response.asImport())
                .imports(RequestSender.class.getCanonicalName())
                .imports("org.fusesource.restygwt.client.Method")
                .imports("org.fusesource.restygwt.client.MethodCallback")
                .imports("org.fusesource.restygwt.client.RestService")
                .imports("javax.ws.rs.Consumes")
                .imports("javax.ws.rs.POST")
                .imports("javax.ws.rs.Path")
                .imports("javax.ws.rs.Produces")
                .imports("javax.ws.rs.core.MediaType")
                .annotate("@RequestSender(" + processorElement.simpleName() + ".class)")
                .withModifiers(new ModifierBuilder().asPublic())
                .implement(RequestRestSender.class.getCanonicalName() + "<" + request.asSimpleName() + ">");
        writeBody();
        return this.sourceBuilder.build();
    }

    private void writeBody() {
        sourceBuilder.withBlock("\n\tpublic interface "+processorElement.simpleName()+"Service extends RestService {\n" +
                "        @POST\n" +
                "        @Path(\""+processorElement.getAnnotation(HandlerPath.class).value()+"\")\n" +
                "        @Produces(MediaType.APPLICATION_JSON)\n" +
                "        @Consumes(MediaType.APPLICATION_JSON)\n" +
                "        void send("+request.asSimpleName()+" request, MethodCallback<"+response.asSimpleName()+"> callback);\n" +
                "    }\n");

        FieldBuilder fieldBuilder = this.sourceBuilder.field("service");
        fieldBuilder.withModifier(new ModifierBuilder().asPrivate())
                .ofType(processorElement.simpleName() + "Service")
                .initializedWith("GWT.create("+processorElement.simpleName() + "Service.class)")
                .end();

        MethodBuilder methodBuilder = this.sourceBuilder.method("send");
        methodBuilder.annotate("@Override")
                .withModifier(new ModifierBuilder().asPublic())
                .returnsVoid()
                .takes(request.asSimpleName(), "request")
                .takes("ServerRequestCallBack", "callBack")

                .line("service.send(request, new MethodCallback<" + response.asSimpleName() + ">() {", false)
                .line("\t@Override", false)
                .line("\tpublic void onFailure(Method method, Throwable throwable) {", false)
                .line("\t\tcallBack.onFailure(throwable);")
                .line("\t}", false)
                .line("", false)
                .line("\t@Override", false)
                .line("\tpublic void onSuccess(Method method, " + response.asSimpleName() + " response) {\n", false)
                .line("\t\tcallBack.onSuccess(response);")
                .line("\t}", false)
                .line("});", false)
                .end();

    }
}
