package com.progressoft.brix.domino.test.api.client;

import com.progressoft.brix.domino.api.client.mvp.presenter.Presentable;

@FunctionalInterface
public interface ReplacePresenterHandler{
    void onReplacePresenter(Presentable presentable);
}