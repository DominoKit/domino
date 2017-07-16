package com.progressoft.brix.domino.test.api.client;

import com.progressoft.brix.domino.api.client.ModuleConfiguration;
import com.progressoft.brix.domino.api.client.ModuleConfigurator;
import com.progressoft.brix.domino.api.client.mvp.presenter.Presentable;
import com.progressoft.brix.domino.api.client.mvp.view.View;
import com.progressoft.brix.domino.api.server.config.ServerConfiguration;
import com.progressoft.brix.domino.api.server.config.ServerConfigurationLoader;
import com.progressoft.brix.domino.api.server.config.VertxConfiguration;
import com.progressoft.brix.domino.api.server.entrypoint.ServerEntryPointContext;
import com.progressoft.brix.domino.api.server.entrypoint.VertxContext;
import com.progressoft.brix.domino.api.server.entrypoint.VertxEntryPointContext;
import com.progressoft.brix.domino.api.shared.extension.Contribution;
import com.progressoft.brix.domino.api.shared.request.ServerResponse;
import com.progressoft.brix.domino.service.discovery.VertxServiceDiscovery;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.progressoft.brix.domino.api.client.ClientApp.make;
import static org.easymock.EasyMock.createMock;

public class DominoTestClient
        implements CanCustomizeClient, CanStartClient,
        ClientContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DominoTestClient.class);

    private ModuleConfiguration[] modules;
    private Map<String, PresenterReplacement> presentersReplacements = new HashMap<>();
    private Map<String, ViewReplacement> viewsReplacements = new HashMap<>();
    private List<ViewOf> viewsOf = new ArrayList<>();
    private List<ContributionOf> contributionsOf = new ArrayList<>();

    private VertxEntryPointContext testEntryPointContext;
    private Vertx vertx = Vertx.vertx();
    private BeforeStarted beforeStarted = context -> LOGGER.info("Before start client...");
    private StartCompleted startCompleted = context -> LOGGER.info("Client started...");

    private DominoTestClient(ModuleConfiguration... configurations) {
        this.modules = configurations;
    }

    public static CanCustomizeClient useModules(ModuleConfiguration... configurations) {
        return new DominoTestClient(configurations);
    }

    @Override
    public CanCustomizeClient replacePresenter(Class<? extends Presentable> original, Presentable presentable, ReplacePresenterHandler handler) {
        presentersReplacements.put(original.getCanonicalName(), new PresenterReplacement(presentable, handler));
        return this;
    }

    @Override
    public CanCustomizeClient replaceView(Class<? extends Presentable> presenter, View view, ReplaceViewHandler handler) {
        viewsReplacements.put(presenter.getCanonicalName(), new ViewReplacement(view, handler));
        return this;
    }

    @Override
    public CanCustomizeClient viewOf(Class<? extends Presentable> presenter, ViewHandler handler) {
        this.viewsOf.add(new ViewOf(presenter.getCanonicalName(), handler));
        return this;
    }

    @Override
    public CanCustomizeClient contributionOf(Class<? extends Contribution> contributionName,
                                             ContributionHandler handler) {
        this.contributionsOf.add(new ContributionOf(contributionName, handler));
        return this;
    }

    @Override
    public CanCustomizeClient onBeforeStart(BeforeStarted beforeStarted) {
        this.beforeStarted = beforeStarted;
        return this;
    }

    @Override
    public CanCustomizeClient onStartCompleted(StartCompleted startCompleted) {
        this.startCompleted = startCompleted;
        return this;
    }

    @Override
    public void start() {
        ServerConfiguration testServerConfiguration = new VertxConfiguration(new JsonObject());
        testEntryPointContext = new VertxEntryPointContext(createMock(RoutingContext.class), testServerConfiguration,
                vertx);
        VertxContext vertxContext = VertxContext.VertxContextBuilder.vertx(vertx)
                .router(Router.router(vertx))
                .serverConfiguration(testServerConfiguration)
                .vertxServiceDiscovery(new VertxServiceDiscovery(vertx)).build();
        new ServerConfigurationLoader(vertxContext).loadModules();

        init(testEntryPointContext);
        Arrays.stream(modules).forEach(this::configureModule);
        presentersReplacements.forEach((key, value) -> replacePresenter(key,
                () -> {
                    value.handler.onReplacePresenter(value.presentable);
                    return value.presentable;
                }));

        viewsReplacements.forEach((key, value) -> replaceView(key, () -> {
            value.handler.onReplaceView(value.viewable);
            return value.viewable;
        }));

        viewsOf.forEach(v -> v.handler.handle(getView(v.presenterName)));
        contributionsOf.forEach(c -> c.handler.handle(getContribution(c.contributionName)));

        beforeStarted.onBeforeStart(this);
        make().run();
        startCompleted.onStarted(this);
    }

    private void init(ServerEntryPointContext entryPointContext) {
        TestClientAppFactory.make(entryPointContext);
    }

    private void configureModule(ModuleConfiguration configuration) {
        new ModuleConfigurator().configureModule(configuration);
    }

    private void replacePresenter(String presenterName, TestPresenterFactory presenterFactory) {
        ((TestInMemoryPresenterRepository) make().getPresentersRepository())
                .replacePresenter(presenterName, presenterFactory);
    }

    private void replaceView(String presenterName, TestViewFactory viewFactory) {
        ((TestInMemoryViewRepository) make().getViewsRepository()).replaceView(presenterName, viewFactory);
    }

    private <T extends View> T getView(String presenterName) {
        return (T) make().getViewsRepository().getView(presenterName);
    }

    public <C extends Contribution> C getContribution(Class<C> contributionClass) {
        return TestClientAppFactory.contributionsRepository.getContribution(contributionClass);
    }

    @Override
    public TestDominoHistory history() {
        return (TestDominoHistory) make().getHistory();
    }

    @Override
    public void setRoutingListener(TestServerRouter.RoutingListener routingListener) {
        TestClientAppFactory.serverRouter.setRoutingListener(routingListener);
    }

    @Override
    public void removeRoutingListener() {
        TestClientAppFactory.serverRouter.removeRoutingListener();
    }

    @Override
    public TestResponse forRequest(String requestKey) {
        return new TestResponse(requestKey);
    }

    @Override
    public Vertx vertx() {
        return vertx;
    }

    @Override
    public VertxEntryPointContext vertxEntryPointContext() {
        return testEntryPointContext;
    }

    private static class PresenterReplacement {
        private final Presentable presentable;
        private final ReplacePresenterHandler handler;

        private PresenterReplacement(Presentable presentable,
                                     ReplacePresenterHandler handler) {
            this.presentable = presentable;
            this.handler = handler;
        }
    }

    private static class ViewReplacement {
        private final View viewable;
        private final ReplaceViewHandler handler;

        private ViewReplacement(View presentable,
                                ReplaceViewHandler handler) {
            this.viewable = presentable;
            this.handler = handler;
        }
    }

    private static class ViewOf {
        private final String presenterName;
        private final ViewHandler handler;

        private ViewOf(String presenterName, ViewHandler handler) {
            this.presenterName = presenterName;
            this.handler = handler;
        }
    }

    private static class ContributionOf {
        private final Class<? extends Contribution> contributionName;
        private final ContributionHandler handler;

        private ContributionOf(Class<? extends Contribution> contributionName, ContributionHandler handler) {
            this.contributionName = contributionName;
            this.handler = handler;
        }
    }

    public static class TestResponse {

        private String request;

        private TestResponse(String request) {
            this.request = request;
        }

        public void returnResponse(ServerResponse response) {
            TestClientAppFactory.serverRouter.fakeResponse(request, new TestServerRouter.SuccessReply(response));
        }

        public void failWith(Throwable throwable) {
            TestClientAppFactory.serverRouter.fakeResponse(request, new TestServerRouter.FailedReply(throwable));
        }
    }

}
