package com.progressoft.brix.domino.client.commons.request;

import com.progressoft.brix.domino.api.client.ClientApp;
import com.progressoft.brix.domino.api.client.async.AsyncRunner;
import com.progressoft.brix.domino.api.client.events.ServerRequestEventFactory;
import com.progressoft.brix.domino.api.client.request.ServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRequestAsyncSender implements RequestAsyncSender {

        private static final Logger LOGGER = LoggerFactory.getLogger(RequestAsyncSender.class);
    private final ServerRequestEventFactory requestEventFactory;

    public AbstractRequestAsyncSender(ServerRequestEventFactory requestEventFactory) {
        this.requestEventFactory = requestEventFactory;
    }

    @Override
    public final void send(final ServerRequest request) {
        ClientApp.make().getAsyncRunner().runAsync(new RequestAsyncTask(request));
    }

    private class RequestAsyncTask implements AsyncRunner.AsyncTask {
        private final ServerRequest request;

        private RequestAsyncTask(ServerRequest request) {
            this.request = request;
        }

        @Override
        public void onSuccess() {
            sendRequest(request, requestEventFactory);
        }

        @Override
        public void onFailed(Throwable error) {
            LOGGER.debug("Could not RunAsync request [" + request + "]", error);
        }
    }

    protected abstract void sendRequest(ServerRequest request, ServerRequestEventFactory requestEventFactory);
}
