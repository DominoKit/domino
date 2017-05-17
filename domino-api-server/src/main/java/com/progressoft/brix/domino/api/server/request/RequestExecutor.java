package com.progressoft.brix.domino.api.server.request;

import com.progressoft.brix.domino.api.server.entrypoint.ServerEntryPointContext;
import com.progressoft.brix.domino.api.server.handler.CallbackRequestHandler;
import com.progressoft.brix.domino.api.shared.request.ServerRequest;
import com.progressoft.brix.domino.api.shared.request.ServerResponse;

public interface RequestExecutor {
    ServerResponse executeRequest(ServerRequest request, ServerEntryPointContext context);
    void executeCallbackRequest(ServerRequest request, ServerEntryPointContext context, CallbackRequestHandler.ResponseCallback<ServerResponse> responseCallback);
}
