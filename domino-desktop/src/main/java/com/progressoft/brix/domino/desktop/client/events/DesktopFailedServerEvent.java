package com.progressoft.brix.domino.desktop.client.events;

import com.progressoft.brix.domino.api.client.ClientApp;
import com.progressoft.brix.domino.api.client.events.Event;
import com.progressoft.brix.domino.api.client.request.ClientServerRequest;
import com.progressoft.brix.domino.api.client.request.Request;
import com.progressoft.brix.domino.api.shared.request.FailedServerResponse;

public class DesktopFailedServerEvent implements Event {
    private final ClientServerRequest request;
    private final Throwable error;

    public DesktopFailedServerEvent(ClientServerRequest request, Throwable error) {
        this.request = request;
        this.error = error;
    }

    @Override
    public void fire() {
        ClientApp.make().getAsyncRunner().runAsync(this::process);
    }

    @Override
    public void process() {
        request.applyState(new Request.ServerResponseReceivedStateContext(makeFailedContext()));
    }

    private Request.ServerFailedRequestStateContext makeFailedContext() {
        return new Request.ServerFailedRequestStateContext(new FailedServerResponse(error));
    }
}
