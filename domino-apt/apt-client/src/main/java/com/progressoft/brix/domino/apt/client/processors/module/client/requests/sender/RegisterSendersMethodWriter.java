package com.progressoft.brix.domino.apt.client.processors.module.client.requests.sender;

import com.progressoft.brix.domino.api.client.request.LazyRequestRestSenderLoader;
import com.progressoft.brix.domino.api.client.request.RequestRestSender;
import com.progressoft.brix.domino.api.client.request.RequestRestSendersRegistry;
import com.progressoft.brix.domino.apt.client.processors.module.client.AbstractRegisterMethodWriter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class RegisterSendersMethodWriter extends AbstractRegisterMethodWriter<SenderEntry> {

    public RegisterSendersMethodWriter(TypeSpec.Builder clientModuleTypeBuilder) {
        super(clientModuleTypeBuilder);
    }

    @Override
    protected String methodName() {
        return "registerRequestRestSenders";
    }

    @Override
    protected Class<?> registryClass() {
        return RequestRestSendersRegistry.class;
    }

    @Override
    protected void registerItem(SenderEntry entry, MethodSpec.Builder methodBuilder) {
        MethodSpec makeMethod = MethodSpec.methodBuilder("make")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(RequestRestSender.class)
                .addStatement("return new $T()", ClassName.bestGuess(entry.sender))
                .build();
        TypeSpec lazyLoaderType = TypeSpec.anonymousClassBuilder("")
                .superclass(LazyRequestRestSenderLoader.class)
                .addMethod(makeMethod)
                .build();
        methodBuilder.addStatement("registry.registerRequestRestSender($T.class.getCanonicalName(), $L)",
                ClassName.bestGuess(entry.request), lazyLoaderType);
    }

    @Override
    protected SenderEntry parseEntry(String senderEntry) {
        String[] senderValues = senderEntry.split(":");
        return new SenderEntry(senderValues[0], senderValues[1]);
    }

}
