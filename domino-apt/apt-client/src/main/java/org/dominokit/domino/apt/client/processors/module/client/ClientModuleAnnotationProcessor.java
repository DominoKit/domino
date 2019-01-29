package org.dominokit.domino.apt.client.processors.module.client;

import com.google.auto.service.AutoService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dominokit.domino.api.client.annotations.*;
import org.dominokit.domino.apt.client.processors.module.client.initialtasks.InitialTasksCollector;
import org.dominokit.domino.apt.client.processors.module.client.listeners.ListenersCollector;
import org.dominokit.domino.apt.client.processors.module.client.presenters.PresentersCollector;
import org.dominokit.domino.apt.client.processors.module.client.requests.CommandsCollector;
import org.dominokit.domino.apt.client.processors.module.client.views.ViewsCollector;
import org.dominokit.domino.apt.commons.BaseProcessor;
import org.dominokit.domino.apt.commons.ProcessorElement;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@AutoService(Processor.class)
public class ClientModuleAnnotationProcessor extends BaseProcessor {

    private static final Logger LOGGER = Logger.getLogger(
            ClientModuleAnnotationProcessor.class.getName());

    private Set<String> presenters;
    private Set<String> views;
    private Set<String> commands;
    private Set<String> initialTasks;
    private Set<Element> clientModules = new HashSet<>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        try {
            clientModules.addAll(roundEnv.getElementsAnnotatedWith(ClientModule.class));

            roundEnv.getElementsAnnotatedWith(Presenter.class);
            roundEnv.getElementsAnnotatedWith(UiView.class);
            roundEnv.getElementsAnnotatedWith(Command.class);
            roundEnv.getElementsAnnotatedWith(StartupTask.class);
            roundEnv.getElementsAnnotatedWith(RequestSender.class);

            Register presentersRegister = new Register("presenters", presenters, messager, processingEnv);
            Register viewsRegister = new Register("views", views, messager, processingEnv);
            Register commandsRegister = new Register("commands", commands, messager, processingEnv);
            Register initialTasksRegister = new Register("initialTasks", initialTasks, messager, processingEnv);

            presenters = presentersRegister.readItems();
            views = viewsRegister.readItems();
            commands = commandsRegister.readItems();
            initialTasks = initialTasksRegister.readItems();

            if (roundEnv.processingOver()) {
                presentersRegister.writeItems();
                viewsRegister.writeItems();
                commandsRegister.writeItems();
                initialTasksRegister.writeItems();
                if (roundEnv.processingOver())
                    clientModules.stream()
                            .filter(e -> validateElementKind(e, ElementKind.CLASS))
                            .forEach(e -> generateModuleConfiguration(newProcessorElement(e)));
                return true;
            }

            new PresentersCollector(messager, typeUtils, elementUtils, elementFactory, presenters).collectPresenters(roundEnv);
            new ViewsCollector(messager, elementFactory, views).collectViews(roundEnv);
            new CommandsCollector(messager, typeUtils, elementFactory, commands).collectCommands(roundEnv);
            new InitialTasksCollector(elementFactory, initialTasks).collectInitialTasks(roundEnv);
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, ExceptionUtils.getFullStackTrace(e));
        }
        return false;
    }

    private void generateModuleConfiguration(ProcessorElement element) {

        new ClientModuleProcessingStep.Builder()
                .setPresenters(presenters)
                .setInitialTasks(initialTasks)
                .setViews(views)
                .setProcessingEnv(processingEnv)
                .build()
                .process(clientModules);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Presenter.class.getCanonicalName());
        annotations.add(UiView.class.getCanonicalName());
        annotations.add(Command.class.getCanonicalName());
        annotations.add(StartupTask.class.getCanonicalName());
        annotations.add(ClientModule.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
