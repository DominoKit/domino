package com.progressoft.brix.domino.test.api.client;

import com.progressoft.brix.domino.api.client.mvp.view.View;

@FunctionalInterface
public interface TestViewFactory {
    View make();
}
