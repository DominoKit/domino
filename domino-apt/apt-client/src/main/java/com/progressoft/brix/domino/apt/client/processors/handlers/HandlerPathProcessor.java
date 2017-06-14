package com.progressoft.brix.domino.apt.client.processors.handlers;

import com.google.auto.service.AutoService;
import com.progressoft.brix.domino.api.client.annotations.HandlerPath;
import com.progressoft.brix.domino.api.client.request.ClientServerRequest;
import com.progressoft.brix.domino.apt.commons.BaseProcessor;
import com.progressoft.brix.domino.apt.commons.FullClassName;
import com.progressoft.brix.domino.apt.commons.ProcessingException;
import com.progressoft.brix.domino.apt.commons.ProcessorElement;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


@AutoService(Processor.class)
public class HandlerPathProcessor extends BaseProcessor {

    private static final Logger LOGGER = Logger.getLogger(HandlerPathProcessor.class.getName());
    public static final int PRESENTER_INDEX = 1;
    public static final int REQUEST_INDEX = 2;
    public static final int RESPONSE_INDEX = 3;

    private final Set<String> supportedAnnotations = new HashSet<>();

    public HandlerPathProcessor() {
        supportedAnnotations.add(HandlerPath.class.getCanonicalName());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return supportedAnnotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(HandlerPath.class).stream().filter(this::extendsClientServerRequest)
                .forEach(this::generateRequestRestfulSender);
        return false;
    }

    private boolean extendsClientServerRequest(Element e) {
        FullClassName fullClassName=new FullClassName(((TypeElement)e).getSuperclass().toString());
        if(!ClientServerRequest.class.getCanonicalName().equals(fullClassName.asImport())){
            messager.printMessage(Diagnostic.Kind.ERROR, "Class does not extends ClientServerRequest", e);
            throw new ProcessingException(e, "Class is not a valid Client - Server Request");
        }
        return true;
    }

    private void generateRequestRestfulSender(Element element) {
        FullClassName fullSuperClassName=new FullClassName(((TypeElement)element).getSuperclass().toString());
        List<String> allSuperClassTypes=fullSuperClassName.allImports();
        FullClassName fullRequestClassName=new FullClassName(allSuperClassTypes.get(REQUEST_INDEX));
        FullClassName fullResponseClassName=new FullClassName(allSuperClassTypes.get(RESPONSE_INDEX));
        ProcessorElement processorElement=newProcessorElement(element);
        try (Writer sourceWriter = obtainSourceWriter(
                processorElement.elementPackage(),processorElement.simpleName()+"Sender")) {
            sourceWriter
                    .write(new RequestSenderSourceWriter(processorElement, fullRequestClassName, fullResponseClassName).write());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not generate classes : ", e);
            messager.printMessage(Diagnostic.Kind.ERROR, "could not generate class");
        }

    }
}
