package org.dominokit.domino.api.client.request;

import org.dominokit.domino.api.client.ClientApp;
import org.dominokit.domino.api.shared.request.FailedResponseBean;
import org.dominokit.domino.api.shared.request.RequestBean;
import org.dominokit.domino.api.shared.request.ResponseBean;
import org.dominokit.domino.api.shared.request.VoidResponse;
import org.dominokit.jacksonapt.AbstractObjectMapper;
import org.dominokit.rest.client.RequestTimeoutException;
import org.dominokit.rest.shared.Response;
import org.dominokit.rest.shared.RestfulRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractRequestSender<R extends RequestBean, S extends ResponseBean> implements RequestRestSender<R, S> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRequestSender.class);

    private final List<String> SEND_BODY_METHODS = Arrays.asList("POST", "PUT", "PATCH");

    @Override
    public void send(ServerRequest<R, S> request, ServerRequestCallBack callBack) {
        new RequestPathHandler<>(request, getPath(), getCustomRoot()).process(serverRequest -> serverRequest.setUrl(replaceRequestParameters(serverRequest.getUrl(), serverRequest.requestBean())));
        final int[] retriesCounter = new int[]{0};
        ClientApp.make().dominoOptions().getRequestInterceptor()
                .interceptRequest(request, () -> {
                    RestfulRequest restfulRequest = RestfulRequest.request(request.getUrl(), getMethod().toUpperCase());
                    restfulRequest.putHeaders(request.headers())
                            .putParameters(request.parameters())
                            .onSuccess(response -> handleResponse(callBack, response)
                            ).onError(throwable -> {
                                if (throwable instanceof RequestTimeoutException && retriesCounter[0] < request.getRetries()) {
                                    retriesCounter[0]++;
                                    LOGGER.info("Retrying request : " + retriesCounter[0]);
                                    doSend(request, restfulRequest);
                                } else {
                                    FailedResponseBean failedResponseBean = new FailedResponseBean(throwable);
                                    LOGGER.info("Failed to execute request : ", failedResponseBean);
                                    callBack.onFailure(failedResponseBean);
                                }
                            });

                    setTimeout(request, restfulRequest);
                    doSend(request, restfulRequest);
                });
    }

    private void handleResponse(ServerRequestCallBack callBack, Response response) {
        if (Arrays.stream(getSuccessCodes()).anyMatch(code -> code.equals(response.getStatusCode()))) {
            if (isVoidResponse()) {
                callBack.onSuccess(new VoidResponse());
            } else {
                try {
                    readResponse(callBack, response);
                } catch (Exception ex) {
                    LOGGER.error("Could not read response for request ", ex);
                    FailedResponseBean failedResponse = new FailedResponseBean(response.getStatusCode(), response.getStatusText(), response.getBodyAsString(), response.getHeaders());
                    failedResponse.setThrowable(ex);
                    callBack.onFailure(failedResponse);
                }
            }
        } else {
            callBack.onFailure(new FailedResponseBean(response.getStatusCode(), response.getStatusText(), response.getBodyAsString(), response.getHeaders()));
        }
    }

    private void setTimeout(ServerRequest<R, S> request, RestfulRequest restfulRequest) {
        if (request.getTimeout() > 0) {
            restfulRequest.timeout(request.getTimeout());
        }
    }

    private void doSend(ServerRequest<R, S> request, RestfulRequest restfulRequest) {
        if (SEND_BODY_METHODS.contains(getMethod().toUpperCase())) {
            restfulRequest.sendJson(getRequestMapper().write(request.requestBean()));
        } else {
            restfulRequest.send();
        }
    }

    protected abstract void readResponse(ServerRequestCallBack callBack, Response response);

    protected abstract String getPath();

    protected abstract Integer[] getSuccessCodes();

    protected abstract String replaceRequestParameters(String path, R request);

    protected abstract AbstractObjectMapper<R> getRequestMapper();

    protected abstract String getMethod();

    protected abstract String getCustomRoot();

    protected abstract boolean isVoidResponse();

    protected abstract boolean isVoidRequest();

}

