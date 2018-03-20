package org.dominokit.domino.apt.client.processors.module.client;

import org.dominokit.domino.api.client.ModuleConfiguration;
import org.dominokit.domino.api.client.annotations.ClientModule;
import org.dominokit.domino.apt.client.processors.module.client.contributions.RegisterContributionsMethodWriter;
import org.dominokit.domino.apt.client.processors.module.client.initialtasks.RegisterInitialTasksMethodWriter;
import org.dominokit.domino.apt.client.processors.module.client.presenters.RegisterPresentersMethodWriter;
import org.dominokit.domino.apt.client.processors.module.client.requests.RegisterRequestsMethodWriter;
import org.dominokit.domino.apt.client.processors.module.client.requests.sender.RegisterSendersMethodWriter;
import org.dominokit.domino.apt.client.processors.module.client.views.RegisterViewsMethodWriter;
import org.dominokit.domino.apt.commons.DominoTypeBuilder;
import org.dominokit.domino.apt.commons.JavaSourceWriter;
import org.dominokit.domino.apt.commons.ProcessorElement;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Set;

public class ModuleConfigurationSourceWriter extends JavaSourceWriter {

    private final Set<String> senders;
    private final Set<String> contributions;
    private final Set<String> initialTasks;
    private final Set<String> requests;
    private final Set<String> presenters;
    private final Set<String> views;

    public ModuleConfigurationSourceWriter(ProcessorElement processorElement,
                                           Set<String> presenters, Set<String> views, Set<String> requests,
                                           Set<String> initialTasks, Set<String> contributions,
                                           Set<String> senders) {
        super(processorElement);
        this.presenters = presenters;
        this.views = views;
        this.requests = requests;
        this.initialTasks = initialTasks;
        this.contributions = contributions;
        this.senders = senders;
    }

    @Override
    public String write() throws IOException {
        TypeSpec.Builder clientModuleTypeBuilder = DominoTypeBuilder.build(processorElement.getAnnotation(ClientModule.class).name() + "ModuleConfiguration",
                ClientModuleAnnotationProcessor.class)
                .addSuperinterface(ModuleConfiguration.class);
        writeBody(clientModuleTypeBuilder);
        StringBuilder asString = new StringBuilder();
        JavaFile.builder(processorElement.elementPackage(), clientModuleTypeBuilder.build()).skipJavaLangImports(true).build().writeTo(asString);
        return asString.toString();
    }

    private void writeBody(TypeSpec.Builder clientModuleTypeBuilder) {
        new RegisterPresentersMethodWriter(clientModuleTypeBuilder).write(presenters);
        new RegisterViewsMethodWriter(clientModuleTypeBuilder).write(views);
        new RegisterRequestsMethodWriter(clientModuleTypeBuilder).write(requests);
        new RegisterInitialTasksMethodWriter(clientModuleTypeBuilder).write(initialTasks);
        new RegisterContributionsMethodWriter(clientModuleTypeBuilder).write(contributions);
        new RegisterSendersMethodWriter(clientModuleTypeBuilder).write(senders);
    }

}
