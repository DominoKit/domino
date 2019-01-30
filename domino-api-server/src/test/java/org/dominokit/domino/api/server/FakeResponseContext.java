package org.dominokit.domino.api.server;

import org.dominokit.domino.api.server.response.ResponseContext;
import org.dominokit.domino.api.server.response.ResponseEndHandler;

import java.util.List;

public class FakeResponseContext implements ResponseContext<TestResponse> {

    @Override
    public ResponseContext<TestResponse> putHeader(String name, String value) {
        return null;
    }

    @Override
    public ResponseContext<TestResponse> putHeader(String name, Iterable<String> values) {
        return null;
    }

    @Override
    public ResponseContext<TestResponse> statusCode(int statusCode) {
        return null;
    }

    @Override
    public void end() {

    }

    @Override
    public void end(TestResponse body) {

    }

    @Override
    public void end(String body) {

    }

    @Override
    public void end(TestResponse[] bodyArray) {

    }

    @Override
    public void end(List<TestResponse> bodyList) {

    }

    @Override
    public void endHandler(ResponseEndHandler endHandler) {

    }
}
