package org.dominokit.domino.test.api.client;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.dominokit.domino.api.client.ClientApp;
import org.dominokit.domino.api.client.events.Event;
import org.dominokit.domino.api.client.events.EventsBus;
import org.dominokit.domino.api.client.events.ServerRequestEventFactory;
import org.dominokit.domino.api.server.resource.ResourcesRepository;
import org.dominokit.domino.api.shared.request.*;
import org.dominokit.domino.client.commons.request.RequestAsyncSender;
import org.dominokit.domino.gwt.client.request.GwtRequestAsyncSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

public class TestServerRouter implements RequestRouter<ServerRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestServerRouter.class);

    private Map<String, ResponseReply> fakeResponses = new HashMap<>();
    private final RequestAsyncSender requestAsyncRunner;
    private TestRoutingListener defaultListener = new TestRoutingListener();
    private RoutingListener listener = defaultListener;

    private final ServerRequestEventFactory eventFactory = new ServerRequestEventFactory() {
        @Override
        public <T> Event makeSuccess(ServerRequest request, T responseBean) {
            defaultListener.onRouteRequest(request, responseBean);
            return new TestServerSuccessEvent(request, responseBean);
        }

        @Override
        public Event makeFailed(ServerRequest request, FailedResponseBean failedResponseBean) {
            defaultListener.onRouteRequest(request, failedResponseBean);
            return new TestServerFailedEvent(request, failedResponseBean);
        }
    };

    private Supplier<TestContext> testContextSupplier;
    private Map<String, Deque<Async>> requestsAsync = new HashMap<>();

    public TestServerRouter(Supplier<TestContext> testContextSupplier) {
        this.requestAsyncRunner = new GwtRequestAsyncSender(eventFactory);
        this.testContextSupplier = testContextSupplier;
    }

    public void setRoutingListener(RoutingListener listener) {
        this.listener = listener;
    }

    public void removeRoutingListener() {
        this.listener = defaultListener;
    }

    @Override
    public void routeRequest(ServerRequest request) {
        ResponseBean response;
        try {
            if (fakeResponses.containsKey(getRequestKey(request))) {
                response = fakeResponses.get(getRequestKey(request)).reply();
                eventFactory.makeSuccess(request, response).fire();
            } else {
                if (nonNull(testContextSupplier.get())) {
                    Async async = testContextSupplier.get().async();
                    if (!requestsAsync.containsKey(getRequestKey(request))) {
                        requestsAsync.put(getRequestKey(request), new ConcurrentLinkedDeque<>());
                    }
                    requestsAsync.get(getRequestKey(request)).push(async);
                }
                requestAsyncRunner.send(request);
            }
        } catch (ResourcesRepository.RequestHandlerNotFound ex) {
            LOGGER.error("Request resource not found for request [" + request.getClass().getSimpleName() + "]! either fake the request or start an actual server");
            eventFactory.makeFailed(request, new FailedResponseBean(ex)).fire();
        } catch (Exception ex) {
            LOGGER.error("could not execute request : ", ex);
            if (ex instanceof FakeRequestFailure) {
                eventFactory.makeFailed(request, ((FakeRequestFailure) ex).failedResponseBean).fire();
            } else {
                eventFactory.makeFailed(request, new FailedResponseBean(ex)).fire();
            }
        }
    }

    private String getRequestKey(ServerRequest request) {
        return request.getClass().getCanonicalName();
    }

    public void fakeResponse(String requestKey, ResponseReply reply) {
        fakeResponses.put(requestKey, reply);
    }

    public TestRoutingListener getDefaultRoutingListener() {
        return defaultListener;
    }

    public class TestServerSuccessEvent<T> implements Event {
        protected final ServerRequest request;
        protected final T responseBean;
        private final ClientApp clientApp = ClientApp.make();

        public TestServerSuccessEvent(ServerRequest request, T responseBean) {
            this.request = request;
            this.responseBean = responseBean;
        }

        @Override
        public void fire() {
            clientApp.getEventsBus().publishEvent(new TestSuccessRequestEvent(this));
        }

        @Override
        public void process() {
            if (nonNull(testContextSupplier.get())) {
                testContextSupplier.get().verify(event -> {
                    request.applyState(new Request.ServerResponseReceivedStateContext(makeSuccessContext()));
                    completeIfAsync(request);
                });
            } else {
                request.applyState(new Request.ServerResponseReceivedStateContext(makeSuccessContext()));
            }
        }

        private Request.ServerSuccessRequestStateContext makeSuccessContext() {
            return new Request.ServerSuccessRequestStateContext(responseBean);
        }
    }

    private void completeIfAsync(ServerRequest request) {
        if (requestsAsync.containsKey(getRequestKey(request))) {
            Deque<Async> deque = requestsAsync.get(getRequestKey(request));
            if (!deque.isEmpty()) {
                Async async = deque.poll();
                async.complete();
            }
            if (deque.isEmpty()) {
                requestsAsync.remove(getRequestKey(request));
            }
        }
    }

    public class TestSuccessRequestEvent implements EventsBus.RequestEvent<TestServerSuccessEvent> {

        private final TestServerSuccessEvent event;

        public TestSuccessRequestEvent(TestServerSuccessEvent event) {
            this.event = event;
        }

        @Override
        public TestServerSuccessEvent asEvent() {
            return event;
        }
    }

    public class TestServerFailedEvent implements Event {
        protected final ServerRequest request;
        protected final FailedResponseBean failedResponseBean;
        private final ClientApp clientApp = ClientApp.make();

        public TestServerFailedEvent(ServerRequest request, FailedResponseBean failedResponseBean) {
            this.request = request;
            this.failedResponseBean = failedResponseBean;
        }

        @Override
        public void fire() {
            clientApp.getEventsBus().publishEvent(new TestFailedRequestEvent(this));
        }

        @Override
        public void process() {
            if (nonNull(testContextSupplier.get())) {
                testContextSupplier.get().verify(event -> {
                    request.applyState(new Request.ServerResponseReceivedStateContext(makeFailedContext()));
                    completeIfAsync(request);
                });
            } else {
                request.applyState(new Request.ServerResponseReceivedStateContext(makeFailedContext()));
            }
        }

        private Request.ServerFailedRequestStateContext makeFailedContext() {
            return new Request.ServerFailedRequestStateContext(failedResponseBean);
        }
    }

    public class TestFailedRequestEvent implements EventsBus.RequestEvent<TestServerFailedEvent> {

        private final TestServerFailedEvent event;

        public TestFailedRequestEvent(TestServerFailedEvent event) {
            this.event = event;
        }

        @Override
        public TestServerFailedEvent asEvent() {
            return event;
        }
    }

    public interface RoutingListener {
        void onRouteRequest(ServerRequest request,
                            Object response);
    }

    public interface ResponseReply {
        ResponseBean reply() throws Exception;
    }

    public static class SuccessReply implements ResponseReply {
        private final ResponseBean response;

        public SuccessReply(ResponseBean response) {
            this.response = response;
        }

        @Override
        public ResponseBean reply() {
            return response;
        }
    }

    public static class FailedReply implements ResponseReply {
        private final FailedResponseBean failedResponseBean;

        public FailedReply(FailedResponseBean failedResponseBean) {
            this.failedResponseBean = failedResponseBean;
        }

        @Override
        public ResponseBean reply() throws Exception {
            throw new FakeRequestFailure(failedResponseBean);
        }
    }

    private static class FakeRequestFailure extends Exception {
        private final FailedResponseBean failedResponseBean;

        public FakeRequestFailure(FailedResponseBean failedResponseBean) {
            this.failedResponseBean = failedResponseBean;
        }
    }
}
