package org.dominokit.domino.client.commons.request;

import org.dominokit.domino.api.shared.request.RequestRouter;
import org.dominokit.domino.api.shared.request.ServerRequest;

public class ServerRouter implements RequestRouter<ServerRequest> {

    private final RequestAsyncSender requestAsyncRunner;

    public ServerRouter(RequestAsyncSender requestAsyncRunner) {
        this.requestAsyncRunner = requestAsyncRunner;
    }

    @Override
    public void routeRequest(final ServerRequest request) {
        requestAsyncRunner.send(request);
    }
}
